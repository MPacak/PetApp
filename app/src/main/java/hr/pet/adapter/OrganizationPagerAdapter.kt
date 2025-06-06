package hr.pet.adapter

import android.app.Activity
import android.os.Build
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
//za map permission samo ako hocu i svoju lokacij uvidit u suprotnom ne treba
class OrganizationPagerAdapter(
    private val context: FragmentActivity,
    private val organizations: List<Organization>
) : RecyclerView.Adapter<OrganizationPagerAdapter.ViewHolder>() {

    inner class ViewHolder(orgView: View) : RecyclerView.ViewHolder(orgView) {

        val ivOrgPhoto   = orgView.findViewById<ImageView>(R.id.ivOrgPhoto)
        val tvOrgName    = orgView.findViewById<TextView>(R.id.tvOrgName)
        val tvOrgEmail   = orgView.findViewById<TextView>(R.id.tvOrgEmail)
        val tvOrgPhone   = orgView.findViewById<TextView>(R.id.tvOrgPhone)
        val tvOrgAddress = orgView.findViewById<TextView>(R.id.tvOrgAddress)
        val mapContainer = orgView.findViewById<FrameLayout>(R.id.mapContainer)
        val btnOpenMaps  = orgView.findViewById<MaterialButton>(R.id.btnOpenMaps)

        var googleMap: GoogleMap? = null

        var pendingLatLng: LatLng? = null
        var pendingTitle: String? = null

        var bindPosition: Int = -1
        var bindAddress: String = ""
        var bindTitle: String = ""

        fun bind(org: Organization, position: Int) {

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

            bindPosition = position
            bindAddress = fullAddress
            bindTitle = org.name


        }


        fun onViewAttached() {

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

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttached()
    }
}