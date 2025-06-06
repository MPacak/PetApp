package hr.pet.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

const val API_URL = "https://api.petfinder.com/v2/"
const val API = "I3K4rfViRRkD9BbFZxrB1RgPMUqlzP72xVDr5m09UGl93w6UrN"
const val SECRET = "qe2D2Guoglh4650shhnbcMTEKEP58n5sbAdF6LC5"
interface PetApi {
    @FormUrlEncoded
    @POST("oauth2/token")
    fun getToken(
        @Field("grant_type")     grantType: String = "client_credentials",
        @Field("client_id")      clientId: String = API,
        @Field("client_secret")  clientSecret: String = SECRET
    ): Call<Token>

    @GET("animals")
    fun fetchAnimals(
        //   @Header("Authorization") bearer: String,
        @Query("type") type: String = "dog",
        @Query("page") page: Int     = 1,
        @Query("limit") limit: Int = 10,
        @Query("organization") organization : Int? = null
    ): Call<PetAnimals>

    @GET("organizations")
    fun fetchOrganizations(
        //   @Header("Authorization") bearer: String,
        @Query("state") state: String = "CA",
        @Query("page")  page: Int     = 1,
        @Query("limit") limit: Int = 10
    ): Call<PetOrganization>
}