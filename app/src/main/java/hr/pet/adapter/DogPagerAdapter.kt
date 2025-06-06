package hr.pet.adapter


import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import hr.pet.DOGS_CONTENT_URI
import hr.pet.R
import hr.pet.model.Dog
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class DogPagerAdapter(
    private val context: Context,
    private val dogs: MutableList<Dog>)
    : RecyclerView.Adapter<DogPagerAdapter.ViewHolder>(){
    class ViewHolder(dogView: View) : RecyclerView.ViewHolder(dogView){
        private val ivDogPhoto        = dogView.findViewById<ImageView>(R.id.ivDogPhoto)
        private val tvDogName         = dogView.findViewById<TextView>(R.id.tvDogName)
        private val tvBreedPrimary    = dogView.findViewById<TextView>(R.id.tvBreedPrimary)
        private val chipMixed         = dogView.findViewById<Chip>(R.id.chipMixed)
        private val chipAge           = dogView.findViewById<Chip>(R.id.chipAge)
        private val chipGender        = dogView.findViewById<Chip>(R.id.chipGender)
        private val chipSize          = dogView.findViewById<Chip>(R.id.chipSize)
        private val chipCoat          = dogView.findViewById<Chip>(R.id.chipCoat)
        private val tvDescription     = dogView.findViewById<TextView>(R.id.tvDescription)
        private val chipColorPrimary  = dogView.findViewById<Chip>(R.id.chipColorPrimary)
        private val chipColorSecondary= dogView.findViewById<Chip>(R.id.chipColorSecondary)
        val ivLike = dogView.findViewById<ImageView>(R.id.ivHeart)

        fun bind(dog: Dog) {
            tvDogName.text      = dog.name
            tvBreedPrimary.text = dog.breedPrimary

            if (dog.breedMixed) {
                chipMixed.visibility = View.VISIBLE
                chipMixed.text = "Mixed"
            } else {
                chipMixed.visibility = View.GONE
            }

            chipAge.text    = dog.age
            chipGender.text = dog.gender
            chipSize.text   = dog.size
            tvDescription.text = dog.description
            ivLike.setImageResource(
                if(dog.likeDog) R.drawable.fullheart else R.drawable.emptyheart
            )

            if (dog.coat.isNotBlank()) {
                chipCoat.visibility = View.VISIBLE
                chipCoat.text = dog.coat
            } else {
                chipCoat.visibility = View.GONE
            }

            if (!dog.colorPrimary.isNullOrBlank()) {
                chipColorPrimary.visibility = View.VISIBLE
                chipColorPrimary.text       = dog.colorPrimary
            } else {
                chipColorPrimary.visibility = View.GONE
            }

            if (!dog.colorSecondary.isNullOrBlank()) {
                chipColorSecondary.visibility = View.VISIBLE
                chipColorSecondary.text       = dog.colorSecondary
            } else {
                chipColorSecondary.visibility = View.GONE
            }
            Picasso.get()
                .load(File(dog.picturePath))
                .error(R.drawable.nopicture)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivDogPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dog_pager, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return dogs.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.ivLike.setOnClickListener {
            updateDog(position)
        }
        holder.bind(dogs[position])
    }

    private fun updateDog(position: Int) {
        val dog = dogs[position]
        dog.likeDog = !dog.likeDog

        context.contentResolver.update(
            ContentUris.withAppendedId(DOGS_CONTENT_URI, dog.id),
            ContentValues().apply {
                put(Dog::likeDog.name, dog.likeDog)
            },
            null,
            null
        )
        notifyItemChanged(position)
    }
}