package hr.pet.api

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import hr.pet.handler.downloadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import hr.pet.model.*
import hr.pet.DOGS_CONTENT_URI
import hr.pet.ORGS_CONTENT_URI
import hr.pet.PetFinderReceiver
import hr.pet.framework.sendBroadcast


const val ACTION_INITIAL_FETCH_DONE = "hr.pet.ACTION_INITIAL_FETCH_DONE"
class PetOrgFetcher(private val context: Context) {
    private var petApi: PetApi
    private var remainingInitialLoads = 0

    companion object {
        private const val TAG = "PetApiClient"
    }

    init {
        val withoutAuth = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val petApiAuth = withoutAuth.create(PetApi::class.java)
        val tokenRepo = TokenRepository(petApiAuth)
        val okClient = OkHttpClient.Builder()
            .addInterceptor(AuthenticationInterceptor(tokenRepo))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        petApi = retrofit.create(PetApi::class.java)
    }

    fun fetchInitalPetOrg(type: String, page: Int, limit: Int, state: String) {
        remainingInitialLoads = 2
        fetchDogs(type, page, limit, true)
        fetchOrganizations(state, page, limit, true)
    }

    fun fetchDogs(type: String, page: Int, limit: Int,  isInitial: Boolean = false) {
        val request = petApi.fetchAnimals(type, page, limit)
        request.enqueue(object : Callback<PetAnimals> {
            override fun onResponse(
                call: Call<PetAnimals>,
                response: Response<PetAnimals>,
            ) {
                response.body()?.let { populateDogs(it, isInitial) }

            }

            override fun onFailure(call: Call<PetAnimals>, t: Throwable) {
                Log.d(javaClass.name, t.message, t)
            }

        })
    }

    fun fetchOrganizations(state: String, page: Int, limit: Int,  isInitial: Boolean = false) {
        val request = petApi.fetchOrganizations(state, page, limit)
        request.enqueue(object : Callback<PetOrganization> {
            override fun onResponse(
                call: Call<PetOrganization>,
                response: Response<PetOrganization>
            ) {
                response.body()?.let { populateOrganizations(it) }
                if (isInitial) checkInitialDone()
            }

            override fun onFailure(call: Call<PetOrganization>, t: Throwable) {
                Log.d(javaClass.name, t.message, t)
            }

        })
    }

    private fun populateOrganizations(petOrganizations: PetOrganization) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            petOrganizations.organizations.forEach {
                val url = it.photos.firstOrNull()?.small
                    ?: it.photos.firstOrNull()?.medium
                    ?: it.photos.firstOrNull()?.large
                    ?: ""

                val picturePath = downloadImage(context, url)

                val address = it.address.address1
                    ?.takeIf { it.isNotBlank() }
                    ?: it.address.address2
                        ?.takeIf { it.isNotBlank() }
                    ?: ""

                val values = ContentValues().apply {
                    put(Organization::name.name, it.name)
                    put(Organization::email.name, it.email)
                    put(Organization::phone.name, it.phone)
                    put(Organization::address.name, address)
                    put(Organization::state.name, it.address.state)
                    put(Organization::city.name, it.address.city)
                    put(Organization::postcode.name, it.address.postcode)
                    put(Organization::country.name, it.address.country)
                    put(Organization::photoPath.name, picturePath ?: "")
                }
                context.contentResolver.insert(
                    ORGS_CONTENT_URI,
                    values
                )
            }

        }
    }

    private fun populateDogs(petAnimals: PetAnimals, isInital : Boolean = false) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            var isInitial :Boolean = isInital
            petAnimals.animals.forEach {
                val url = it.photos.firstOrNull()?.small
                    ?: it.photos.firstOrNull()?.medium
                    ?: it.photos.firstOrNull()?.large
                    ?: ""

                val picturePath = downloadImage(context, url)
                val values = ContentValues().apply {
                    put(Dog::breedPrimary.name, it.breeds.primary)
                    put(Dog::breedMixed.name, it.breeds.mixed)
                    put(Dog::age.name, it.age)
                    put(Dog::gender.name, it.gender)
                    put(Dog::size.name, it.size)
                    put(Dog::coat.name, it.coat ?: "")
                    put(Dog::name.name, it.name)
                    put(Dog::description.name, it.description ?: "No description given")
                    put(Dog::colorPrimary.name, it.colors.primary ?: "")
                    put(Dog::colorSecondary.name, it.colors.secondary ?: "")
                    put(Dog::picturePath.name, picturePath ?: "")
                }
                context.contentResolver.insert(
                    DOGS_CONTENT_URI,
                    values
                )

            }
            if (isInitial) {checkInitialDone()} else {
                context.sendBroadcast<PetFinderReceiver>()
            }
        }

    }
    private fun checkInitialDone() {
        remainingInitialLoads -= 1
        if (remainingInitialLoads == 0) {
            Log.d(TAG, "ready to trasnfer = $remainingInitialLoads")
            context.sendBroadcast<PetFinderReceiver>(Intent(ACTION_INITIAL_FETCH_DONE))
        }
    }
    // context.sendBroadcast(Intent(ACTION_INITIAL_FETCH_DONE))
    // context.sendBroadcast<PetFinderReceiver>(Intent(ACTION_INITIAL_FETCH_DONE))
}