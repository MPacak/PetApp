package hr.pet.adapter

import android.app.Activity
import android.os.Build
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import hr.pet.model.Organization
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
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


//FINISH ACTIVITIY!!!
class OrganizationPagerAdapter (
    private val context: Context,
    private val organizations: List<Organization>,
    private val lifecycleOwner: LifecycleOwner  // we’ll use this to observe lifecycles if needed
) : RecyclerView.Adapter<OrganizationPagerAdapter.ViewHolder>() {

    inner class ViewHolder(orgView: View) : RecyclerView.ViewHolder(orgView) {
        // 1) Basic data fields
        private val ivOrgPhoto = orgView.findViewById<ImageView>(R.id.ivOrgPhoto)
        private val tvOrgName = orgView.findViewById<TextView>(R.id.tvOrgName)
        private val tvOrgEmail = orgView.findViewById<TextView>(R.id.tvOrgEmail)
        private val tvOrgPhone = orgView.findViewById<TextView>(R.id.tvOrgPhone)
        private val tvOrgAddress = orgView.findViewById<TextView>(R.id.tvOrgAddress)
        val mapView = orgView.findViewById<MapView>(R.id.mapView)
        private val btnOpenMaps = orgView.findViewById<MaterialButton>(R.id.btnOpenMaps)

        // We’ll keep a GoogleMap reference once the MapView is ready
        private var googleMap: GoogleMap? = null

        // Bind data into all fields
        fun bind(org: Organization) {
            Picasso.get()
                .load(File(org.photoPath))
                .error(R.drawable.nopicture)   // your fallback drawable
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivOrgPhoto)

            tvOrgName.text = org.name

            if (!org.email.isNullOrBlank()) {
                tvOrgEmail.visibility = View.VISIBLE
                tvOrgEmail.text = org.email
            } else {
                tvOrgEmail.visibility = View.GONE
            }

            // ne treba persmission jer samo otvara phone i onda korisnik sam odlucuje hoce li zvati ili ne
            //kako imam to za email mozda da za ovo dodam permissions pa da zovem odmah direktno
            if (!org.phone.isNullOrBlank()) {
                tvOrgPhone.visibility = View.VISIBLE
                tvOrgPhone.text = org.phone
                //ovo actually radi android sam - skenira za broj telefona parsira i poziva intent
//                tvOrgPhone.setOnClickListener {
//                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
//                        data = Uri.parse("tel:${org.phone}")
//                    }
//                    context.startActivity(dialIntent)
//                }
            } else {
                tvOrgPhone.visibility = View.GONE
            }

            val fullAddress = buildFullAddress(org)
            if (fullAddress.isNotBlank()) {
                tvOrgAddress.visibility = View.VISIBLE
                tvOrgAddress.text = fullAddress
            } else {
                tvOrgAddress.visibility = View.GONE
            }


            initMapView(fullAddress, org.name)

            // 7. “Open in Google Maps” button
            if (fullAddress.isNotBlank()) {
                btnOpenMaps.visibility = View.VISIBLE
                btnOpenMaps.setOnClickListener {
                    openInGoogleMaps(fullAddress)
                }
            } else {
                btnOpenMaps.visibility = View.GONE
            }
        }

        /** Build a single-line “street, state postcode, country” string (skipping nulls) */
        private fun buildFullAddress(org: Organization): String {
            val parts = mutableListOf<String>()
            org.address?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
            // state + postcode
            val stateZip = listOf(org.state, org.postcode).joinToString(" ").trim()
            if (stateZip.isNotBlank()) parts.add(stateZip)
            parts.add(org.country)      // country is non-null in your data class
            return parts.joinToString(", ")
        }

        /** Initialize MapView, geocode the `fullAddress`, and drop a marker */
        private fun initMapView(fullAddress: String, name:String) {
            // 1) MapView’s lifecycle must be forwarded manually
            mapView.onCreate(null)  // if you want to restore state, pass savedInstanceState
            mapView.getMapAsync { map ->
                googleMap = map

                // Center map on a default position until geocode finishes
                val defaultLatLng = LatLng(0.0, 0.0)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 2f))

                // 2) Geocode the address (synchronously or asynchronously)
                //    For simplicity, use Android’s Geocoder (requires INTERNET permission).
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(
                        fullAddress,
                        /* maxResults = */ 1,
                        /* executor = */
                        /* listener = */ object : Geocoder.GeocodeListener{
                            override fun onGeocode(addresses: MutableList<Address>) {
                                if (addresses.isNotEmpty()) {
                                    val addr = addresses[0]
                                    val latLng = LatLng(addr.latitude, addr.longitude)

                                    map.clear()
                                    map.addMarker(
                                        MarkerOptions()
                                            .position(latLng)
                                            .title(name)
                                    )
                                    map.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                                    )
                                }
                                // else: leave the default camera alone
                            }
                        }
                    )
                }
                // 5) Fallback for older Android versions: do geocoding on a background thread
                else {
                    @Suppress("DEPRECATION")
                    Executors.newSingleThreadExecutor().execute {
                        val addresses: List<Address>? = try {
                            geocoder.getFromLocationName(fullAddress, /* maxResults */ 1)
                        } catch (e: IOException) {
                            null
                        }

                        if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            val latLng = LatLng(addr.latitude, addr.longitude)

                            // Switch back to the main thread to update the map UI
                            (context as Activity).runOnUiThread {
                                map.clear()
                                map.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(name)
                                )
                                map.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                                )
                            }
                        }
                    }
                }
            }
        }

        /** Call this from the Activity’s lifecycle methods:
         *  onResume() → mapView.onResume()
         *  onPause()  → mapView.onPause()
         *  onDestroy()→ mapView.onDestroy()
         *  etc. */
        fun forwardLifecycle(event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> {
                    mapView.onDestroy()
                    googleMap = null
                }
                else -> { /* no-op */}
            }
        }

        /** Launch an Intent to open the full address in the Google Maps app */
        private fun openInGoogleMaps(address: String) {
            // “geo:0,0?q=<address>” will let Android choose Maps and drop a pin
            val uri = Uri.parse("geo:0,0?q=" + Uri.encode(address))
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            // If Maps isn’t installed, fallback to browser
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Try a generic ACTION_VIEW
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val orgView = LayoutInflater.from(parent.context)
            .inflate(R.layout.org_pager, parent, false)
        return ViewHolder(orgView)
    }

    override fun getItemCount(): Int = organizations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(organizations[position])
        // Forward lifecycle events to the MapView inside the holder:
        // We assume the Activity implements LifecycleOwner (it does by default).
        lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            holder.forwardLifecycle(event)
        })
    }
}