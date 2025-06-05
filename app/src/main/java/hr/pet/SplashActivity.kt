package hr.pet

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hr.pet.api.PetWorker
import hr.pet.databinding.ActivitySplashBinding
import hr.pet.framework.applyAnimation
import hr.pet.framework.callDelayed
import hr.pet.framework.getBooleanPreference
import hr.pet.framework.isOnline
import hr.pet.framework.startActivity

private const val DELAY = 3000L
const val DATA_IMPORTED = "hr.pet.data_imported"
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
        if (getBooleanPreference(DATA_IMPORTED)) {
            callDelayed(DELAY) { startActivity<MainActivity>() }

        } else {
            if (isOnline()) {
                WorkManager.getInstance(this).apply {
                    enqueueUniqueWork(
                        DATA_IMPORTED,
                        ExistingWorkPolicy.KEEP,
                        OneTimeWorkRequest.Companion.from(PetWorker::class.java)
                    )
                }
            }
        }
    }
    }
