package hr.pet.api

import com.google.gson.annotations.SerializedName

data class PetOrganization(
    @SerializedName("organizations") val organizations: List<Organization>,
    @SerializedName("pagination")    val pagination: Pagination
){
    data class Organization(
        @SerializedName("id")                val id: String,
        @SerializedName("name")              val name: String,
        @SerializedName("email")             val email: String?,
        @SerializedName("phone")             val phone: String?,
        @SerializedName("address")           val address: Address,
        @SerializedName("hours")             val hours: Hours,
        @SerializedName("url")               val url: String,
        @SerializedName("website")           val website: String?,
        @SerializedName("mission_statement") val missionStatement: String?,
        @SerializedName("adoption")          val adoption: Adoption,
        @SerializedName("social_media")      val socialMedia: SocialMedia,
        @SerializedName("photos")            val photos: List<Photo>,
        @SerializedName("distance")          val distance: Double?,
        @SerializedName("_links")            val links: Links
    ) {
        data class Address(
            @SerializedName("address1") val address1: String?,
            @SerializedName("address2") val address2: String?,
            @SerializedName("city")     val city: String,
            @SerializedName("state")    val state: String,
            @SerializedName("postcode") val postcode: String,
            @SerializedName("country")  val country: String
        )

        data class Hours(
            @SerializedName("monday")    val monday:    String?,
            @SerializedName("tuesday")   val tuesday:   String?,
            @SerializedName("wednesday") val wednesday: String?,
            @SerializedName("thursday")  val thursday:  String?,
            @SerializedName("friday")    val friday:    String?,
            @SerializedName("saturday")  val saturday:  String?,
            @SerializedName("sunday")    val sunday:    String?
        )

        data class Adoption(
            @SerializedName("policy") val policy: String?,
            @SerializedName("url")    val url:    String?
        )

        data class SocialMedia(
            @SerializedName("facebook")  val facebook:  String?,
            @SerializedName("twitter")   val twitter:   String?,
            @SerializedName("youtube")   val youtube:   String?,
            @SerializedName("instagram") val instagram: String?,
            @SerializedName("pinterest") val pinterest: String?
        )

        data class Photo(
            @SerializedName("small")  val small:  String,
            @SerializedName("medium") val medium: String,
            @SerializedName("large")  val large:  String,
            @SerializedName("full")   val full:   String
        )

        data class Links(
            @SerializedName("self")    val self: Link,
            @SerializedName("animals") val animals: Link
        ) {
            data class Link(@SerializedName("href") val href: String)
        }
    }

    data class Pagination(
        @SerializedName("count_per_page") val countPerPage: Int,
        @SerializedName("total_count")    val totalCount: Int,
        @SerializedName("current_page")   val currentPage: Int,
        @SerializedName("total_pages")    val totalPages: Int,
        @SerializedName("_links")         val links: Map<String, PetOrganization.Organization.Links.Link>?
    )
}

