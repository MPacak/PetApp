package hr.pet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import hr.pet.api.ACTION_INITIAL_FETCH_DONE
import hr.pet.api.PetOrgFetcher
import hr.pet.api.PetOrgFetcher.Companion
import hr.pet.framework.setBooleanPreference
import hr.pet.framework.startActivity

class PetFinderReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "PetReceiverClient"
    }
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(PetFinderReceiver.TAG, "arrived at on receive")
        if (intent.action == ACTION_INITIAL_FETCH_DONE) {
            Log.d(PetFinderReceiver.TAG, "intent happened $ACTION_INITIAL_FETCH_DONE")
        context.setBooleanPreference(DATA_IMPORTED)
        context.startActivity<MainActivity>()
        }
       //TODO kad samo pse rjesavamo - tako da opet vuce iz baze i prikaze. Drugi splash screen
       else {
            context.setBooleanPreference(DATA_IMPORTED)
                context.startActivity<MainActivity>()
        }
    }
}