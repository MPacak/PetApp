package hr.pet.framework

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import hr.pet.DOGS_CONTENT_URI
import hr.pet.ORGS_CONTENT_URI
import hr.pet.model.*

fun View.applyAnimation(id: Int) {
    startAnimation(AnimationUtils.loadAnimation(context, id))
}
inline fun <reified T : Activity> Context.startActivity() {
    startActivity(
        Intent(this, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}
inline fun <reified T : Activity> Context.startActivity(
    key: String,
    value: Int
) {
    startActivity(
        Intent(this, T::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(key, value)
        }
    )
}
inline fun<reified T: BroadcastReceiver> Context.sendBroadcast() {
    sendBroadcast(Intent(this, T::class.java))
}
inline fun<reified T: BroadcastReceiver> Context.sendBroadcast(intent : Intent) {
    val explicit = Intent(intent).apply {
        setClass(this@sendBroadcast, T::class.java)
    }
    sendBroadcast(explicit)
}

fun Context.setBooleanPreference(key: String, value: Boolean = true) {
    PreferenceManager.getDefaultSharedPreferences(this)
        .edit()
        .putBoolean(key, value)
        .apply()
}

fun Context.getBooleanPreference(key: String) =
    PreferenceManager.getDefaultSharedPreferences(this)
        .getBoolean(key, false)
fun callDelayed(delay: Long, work: Runnable) {
    Handler(Looper.getMainLooper()).postDelayed(
        work,
        delay
    )
}
fun Context.isOnline() : Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>()
    connectivityManager?.activeNetwork?.let { network ->
        connectivityManager.getNetworkCapabilities(network)?.let { cap ->
            return cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
    }

    return false
}
@SuppressLint("Range")
fun Context.fetchDogs(): MutableList<Dog> {
    val dogs = mutableListOf<Dog>()
    val cursor = contentResolver.query(
        DOGS_CONTENT_URI,
        null,
        null,
        null,
        null
    )
    while(cursor != null && cursor.moveToNext()) {
            dogs.add(
                Dog(
                    id            = cursor.getLong(cursor.getColumnIndex(Dog::id.name)),
                    breedPrimary  = cursor.getString(cursor.getColumnIndex(Dog::breedPrimary.name)),
                    breedMixed    = cursor.getInt(cursor.getColumnIndex(Dog::breedMixed.name)) == 1,
                    age           = cursor.getString(cursor.getColumnIndex(Dog::age.name)),
                    gender        = cursor.getString(cursor.getColumnIndex(Dog::gender.name)),
                    size          = cursor.getString(cursor.getColumnIndex(Dog::size.name)),
                    coat          = cursor.getString(cursor.getColumnIndex(Dog::coat.name)),
                    name          = cursor.getString(cursor.getColumnIndex(Dog::name.name)),
                    description   = cursor.getString(cursor.getColumnIndex(Dog::description.name)),
                    colorPrimary  = cursor.getString(cursor.getColumnIndex(Dog::colorPrimary.name)),
                    colorSecondary = cursor.getString(cursor.getColumnIndex(Dog::colorSecondary.name)),
                    picturePath   = cursor.getString(cursor.getColumnIndex(Dog::picturePath.name)),
                    likeDog = cursor.getInt(cursor.getColumnIndex(Dog::likeDog.name)) == 1
                )
            )
        }
    return dogs
}
@SuppressLint("Range")
fun Context.fetchOrganizations(): MutableList<Organization> {
    val orgs = mutableListOf<Organization>()
    val cursor = contentResolver.query(
        ORGS_CONTENT_URI,
        null,
        null,
        null,
        null
    )
    cursor?.use { c ->
        while (c.moveToNext()) {
            orgs.add(Organization(
                id         = c.getLong(c.getColumnIndex(Organization::id.name)),
                officialId = c.getInt(c.getColumnIndex(Organization::officialId.name)),
                name       = c.getString(c.getColumnIndex(Organization::name.name)),
                email      = c.getString(c.getColumnIndex(Organization::email.name)),
                phone      = c.getString(c.getColumnIndex(Organization::phone.name)),
                address    = c.getString(c.getColumnIndex(Organization::address.name)),
                state      = c.getString(c.getColumnIndex(Organization::state.name)),
                city = c.getString(c.getColumnIndex(Organization::city.name)),
                postcode   = c.getString(c.getColumnIndex(Organization::postcode.name)),
                country    = c.getString(c.getColumnIndex(Organization::country.name)),
                photoPath  = c.getString(c.getColumnIndex(Organization::photoPath.name))
            ))
        }
    }
    return orgs
}