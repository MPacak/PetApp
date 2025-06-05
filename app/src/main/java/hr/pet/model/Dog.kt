package hr.pet.model

data class Dog(
    val id: Long,
    val breedPrimary: String,
    val breedMixed: Boolean,
    val age: String,
    val gender: String,
    val size: String,
    val coat: String,
    val name: String,
    val description: String,
    val colorPrimary: String?,
    val colorSecondary: String?,
    val picturePath: String
)
