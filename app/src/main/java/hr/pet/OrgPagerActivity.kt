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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun initPager() {
        orgs = fetchOrganizations()
        orgPosition = intent.getIntExtra(ORG_POSITION, 0)
        binding.orgViewPager.adapter = OrganizationPagerAdapter(this, orgs)

        binding.orgViewPager.currentItem = orgPosition
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

}