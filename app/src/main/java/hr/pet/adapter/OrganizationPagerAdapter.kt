package hr.pet.adapter

import android.app.Activity
import android.os.Build
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import hr.pet.model.Organization
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import hr.pet.R
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.concurrent.Executors

class OrganizationPagerAdapter(
    private val context: FragmentActivity,
    private val organizations: List<Organization>
) : RecyclerView.Adapter<OrganizationPagerAdapter.ViewHolder>() {

    inner class ViewHolder(orgView: View) : RecyclerView.ViewHolder(orgView) {
        // 1) Your other views
        val ivOrgPhoto   = orgView.findViewById<ImageView>(R.id.ivOrgPhoto)
        val tvOrgName    = orgView.findViewById<TextView>(R.id.tvOrgName)
        val tvOrgEmail   = orgView.findViewById<TextView>(R.id.tvOrgEmail)
        val tvOrgPhone   = orgView.findViewById<TextView>(R.id.tvOrgPhone)
        val tvOrgAddress = orgView.findViewById<TextView>(R.id.tvOrgAddress)
        val mapContainer = orgView.findViewById<FrameLayout>(R.id.mapContainer)
        val btnOpenMaps  = orgView.findViewById<MaterialButton>(R.id.btnOpenMaps)

        // 2) We keep a reference to GoogleMap so we can drop a marker once ready
        var googleMap: GoogleMap? = null

        // 3) If geocoding finishes before the map is “ready,” stash lat/lng here
        var pendingLatLng: LatLng? = null
        var pendingTitle: String? = null

        // 4) We remember the “bind‐time” data so we can attach the fragment later in onViewAttachedToWindow
        var bindPosition: Int = -1
        var bindAddress: String = ""
        var bindTitle: String = ""

        fun bind(org: Organization, position: Int) {
            // A) Bind your image/text/email/phone/address exactly as before
            Picasso.get()
                .load(File(org.photoPath))
                .error(R.drawable.nopicture)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivOrgPhoto)

            tvOrgName.text = org.name
            if (!org.email.isNullOrBlank()) {
                tvOrgEmail.visibility = View.VISIBLE
                tvOrgEmail.text = org.email
            } else {
                tvOrgEmail.visibility = View.GONE
            }

            if (!org.phone.isNullOrBlank()) {
                tvOrgPhone.visibility = View.VISIBLE
                tvOrgPhone.text = org.phone
            } else {
                tvOrgPhone.visibility = View.GONE
            }

            val fullAddress = buildFullAddress(org)
            if (fullAddress.isNotBlank()) {
                tvOrgAddress.visibility = View.VISIBLE
                tvOrgAddress.text = fullAddress
                btnOpenMaps.visibility = View.VISIBLE
                btnOpenMaps.setOnClickListener { openInGoogleMaps(fullAddress) }
            } else {
                tvOrgAddress.visibility = View.GONE
                btnOpenMaps.visibility = View.GONE
            }

            // B) Record the data needed for “attaching” in onViewAttachedToWindow
            bindPosition = position
            bindAddress = fullAddress
            bindTitle = org.name

            // IMPORTANT: Do NOT perform fragment transactions here anymore!
            // We will do them in onViewAttachedToWindow, where mapContainer is guaranteed to exist.
        }

        /** Called when the ViewHolder’s view is attached to the window hierarchy. */
        fun onViewAttached() {
            // If bindPosition < 0, it means bind() hasn’t been called yet, so skip.
            if (bindPosition < 0) return
            Log.w("OrgPagerAdapter", "I am going to start the fragment again from on view attached")
            initOrAttachMapFragment(bindPosition, bindAddress, bindTitle)
        }

        private fun initOrAttachMapFragment(position: Int, address: String, title: String) {
            val fragmentTag = "orgMapFragment_$position"
            val fm = context.supportFragmentManager
            val existingFrag = fm.findFragmentByTag(fragmentTag) as? SupportMapFragment

            if (existingFrag != null) {
                Log.d("OrgPagerAdapter", "Re-using existing fragment for tag=$fragmentTag")

                // Replace (re-parent) the existing fragment into the current mapContainer
                fm.beginTransaction()
                    .replace(mapContainer.id, existingFrag, fragmentTag)
                    .commitAllowingStateLoss()

                existingFrag.getMapAsync { map ->
                    Log.d("OrgPagerAdapter", ">> onMapReady() for existing tag=$fragmentTag")
                    googleMap = map
                    pendingLatLng?.let { latlng ->
                        dropMarker(latlng, pendingTitle ?: "")
                        pendingLatLng = null
                        pendingTitle = null
                    }
                }

                geocodeAddress(address, title)

            } else {
                Log.d("OrgPagerAdapter", "Creating new fragment for tag=$fragmentTag")
                val newFrag = SupportMapFragment.newInstance()
                fm.beginTransaction()
                    .add(mapContainer.id, newFrag, fragmentTag)
                    .commitAllowingStateLoss()

                newFrag.getMapAsync { map ->
                    Log.d("OrgPagerAdapter", ">> onMapReady() for new tag=$fragmentTag")
                    googleMap = map
                    geocodeAddress(address, title)
                }
            }
        }

        private fun geocodeAddress(fullAddress: String, title: String) {
            if (fullAddress.isBlank()) return

            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(fullAddress, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isNotEmpty()) {
                            val addr = addresses[0]
                            val latlng = LatLng(addr.latitude, addr.longitude)
                            (context as Activity).runOnUiThread {
                                if (googleMap != null) {
                                    dropMarker(latlng, title)
                                } else {
                                    pendingLatLng = latlng
                                    pendingTitle = title
                                }
                            }
                        } else {
                            Log.w("OrgPagerAdapter", "No geocoding results for '$fullAddress'")
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        Log.e("OrgPagerAdapter", "Geocoding failed: $errorMessage")
                    }
                })
            } else {
                Executors.newSingleThreadExecutor().execute {
                    val list = try {
                        geocoder.getFromLocationName(fullAddress, 1)
                    } catch (e: IOException) {
                        null
                    }
                    if (!list.isNullOrEmpty()) {
                        val addr = list[0]
                        val latlng = LatLng(addr.latitude, addr.longitude)
                        (context as Activity).runOnUiThread {
                            if (googleMap != null) {
                                dropMarker(latlng, title)
                            } else {
                                pendingLatLng = latlng
                                pendingTitle = title
                            }
                        }
                    } else {
                        Log.w("OrgPagerAdapter", "Deprecated geocoding returned no results for '$fullAddress'")
                    }
                }
            }
        }

        private fun dropMarker(latlng: LatLng, title: String) {
            googleMap?.let { map ->
                map.clear()
                map.addMarker(MarkerOptions().position(latlng).title(title))
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14f))
            }
        }

        private fun openInGoogleMaps(address: String) {
            val uri = Uri.parse("geo:0,0?q=" + Uri.encode(address))
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, uri))
            }
        }

        private fun buildFullAddress(org: Organization): String {
            val parts = mutableListOf<String>()
            org.address?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
            parts.add(org.city)
            val stateZip = listOf(org.state, org.postcode).joinToString(" ").trim()
            if (stateZip.isNotBlank()) parts.add(stateZip)
            parts.add(org.country)
            return parts.joinToString(", ")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val orgView = LayoutInflater.from(parent.context)
            .inflate(R.layout.org_pager, parent, false)
        return ViewHolder(orgView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(organizations[position], position)
    }

    override fun getItemCount(): Int = organizations.size

    /**
     * This is the critical change: when a ViewHolder’s view is attached to the window,
     * we know that `mapContainer` is now part of the Activity’s view hierarchy.
     * Therefore we can safely do fragment transactions that target mapContainer.id.
     */
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttached()
    }
}