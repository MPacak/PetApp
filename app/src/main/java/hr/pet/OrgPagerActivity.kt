package hr.pet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import hr.pet.adapter.DogPagerAdapter
import hr.pet.adapter.OrganizationPagerAdapter
import hr.pet.databinding.ActivityDogPagerBinding
import hr.pet.databinding.ActivityOrgPagerBinding
import hr.pet.framework.fetchDogs
import hr.pet.framework.fetchOrganizations
import hr.pet.model.Dog
import hr.pet.model.Organization

const val ORG_POSITION = "hr.pet.org_position"
class OrgPagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrgPagerBinding
    private lateinit var orgs: MutableList<Organization>

    private var orgPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrgPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPager()
        //ima predvideno mejsto za back button i sad override da kad klikne ide na home, samo je treba uapliti
        //jer zasada nezna da na klik ide nazad
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun initPager() {
        orgs = fetchOrganizations()
        orgPosition = intent.getIntExtra(ORG_POSITION, 0)
//activity already implements lifecycleowner
        binding.orgViewPager.adapter = OrganizationPagerAdapter(this, orgs, this)

        binding.orgViewPager.currentItem = orgPosition
    }

    override fun onSupportNavigateUp(): Boolean {
        //rucno okida event koji je vec ugraden u njemu. Samo mu moram reci da je to to i da ode back
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
    override fun onLowMemory() {
        super.onLowMemory()

        // ViewPager2 internally hosts a RecyclerView as its first child.
        // We grab that RecyclerView, iterate over its ViewHolders, and call mapView.onLowMemory().
        (binding.orgViewPager.getChildAt(0) as? RecyclerView)?.let { rv ->
            for (i in 0 until rv.childCount) {
                val holder = rv.findViewHolderForAdapterPosition(i)
                if (holder is OrganizationPagerAdapter.ViewHolder) {
                    holder.mapView.onLowMemory()
                }
            }
        }
    }
}