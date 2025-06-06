package hr.pet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun initPager() {
        dogs = fetchDogs()
        dogPosition = intent.getIntExtra(DOG_POSITION, 0)

        binding.dogViewPager.adapter = DogPagerAdapter(this, dogs)
        binding.dogViewPager.currentItem = dogPosition
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}