package hr.pet.model

data class Organization(
    val id: Int,
    val name: String,
    val email: String?,
    val phone: String?,
    val address: String,
    val state: String,
    val postcode: String,
    val country: String,
    val photoPath: String
)
