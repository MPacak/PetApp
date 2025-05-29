package hr.pet

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import hr.pet.databinding.ActivitySplashBinding

private const val DELAY = 3000L
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startAnimations()
        redirect()
        }
    private fun startAnimations() {
        binding.tvLoading.applyAnimation(R.anim.fade_in_out)
        binding.ivWaiting.setImageResource(R.drawable.splash_frames)
        val frameAnim = binding.ivWaiting.drawable as AnimationDrawable
        frameAnim.start()
    }
    private fun redirect() {
        if (isOnline()) {
            callDelayed(DELAY) { startActivity<MainActivity>() }
        }
    }
    }
