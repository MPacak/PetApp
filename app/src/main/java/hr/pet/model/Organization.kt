package hr.pet.model

data class Organization(
    val id: Long,
    val officialId : Int,
    val name: String,
    val email: String?,
    val phone: String?,
    val address: String?,
    val state: String,
    val city: String,
    val postcode: String,
    val country: String,
    val photoPath: String
)
