package hr.pet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import hr.pet.databinding.ActivityDogPagerBinding
import hr.pet.framework.fetchDogs
import hr.pet.model.Dog
import hr.pet.adapter.DogPagerAdapter

const val DOG_POSITION = "hr.pet.dog_position"


class DogPagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDogPagerBinding
    private lateinit var dogs: MutableList<Dog>

    private var dogPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDogPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPager()
        //ima predvideno mejsto za back button i sad override da kad klikne ide na home, samo je treba uapliti
        //jer zasada nezna da na klik ide nazad
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun initPager() {
        dogs = fetchDogs()
        dogPosition = intent.getIntExtra(DOG_POSITION, 0)
        //moram naci koji mi je adapter i poslati context i item kako bi ga i odmah prikazao
        binding.dogViewPager.adapter = DogPagerAdapter(this, dogs)
        //i prikazemo item na toj poziciji
        binding.dogViewPager.currentItem = dogPosition
    }

    override fun onSupportNavigateUp(): Boolean {
        //rucno okida event koji je vec ugraden u njemu. Samo mu moram reci da je to to i da ode back
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}