package hr.pet.adapter

import android.content.ContentUris
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.pet.DogPagerActivity
import hr.pet.framework.startActivity
import hr.pet.R
import hr.pet.model.Dog
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File
import hr.pet.DOG_POSITION
import hr.pet.DOGS_CONTENT_URI

class DogAdapter  (private val context: Context,
private val dogs: MutableList<Dog>)
: RecyclerView.Adapter<DogAdapter.ViewHolder>() {
    class ViewHolder(dogView: View) : RecyclerView.ViewHolder(dogView){

        private val ivDog = dogView.findViewById<ImageView>(R.id.ivDog)
    private val tvDogName = dogView.findViewById<TextView>(R.id.tvDogName)
    private val tvDogInfo = dogView.findViewById<TextView>(R.id.tvDogInfo)
    fun bind(dog : Dog) {
        tvDogName.text = dog.name
        tvDogInfo.text = dog.description
        Picasso.get()
            .load(File(dog.picturePath))
            .error(R.mipmap.nopicture)
            .fit()
            .centerCrop()
            .transform(RoundedCornersTransformation(50, 5))
            .into(ivDog)
    }
}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.dog, parent, false)
    )
}

override fun getItemCount() = dogs.size

override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val dog = dogs[position]

    holder.itemView.setOnClickListener {
        context.startActivity<DogPagerActivity>(DOG_POSITION, position)
    }
    holder.itemView.setOnLongClickListener {
        AlertDialog.Builder(context).apply {
            setTitle(context.getString(R.string.delete))
            setMessage(context.getString(R.string.sure_to_delete))
            setIcon(R.drawable.delete)
            setCancelable(true)
            setPositiveButton("OK") { _, _ -> deleteItem(position)}
            setNegativeButton(R.string.cancel, null)
            show()
        }
        true
    }


    holder.bind(dog)
}

private fun deleteItem(position: Int) {
    val dog = dogs[position]
    context.contentResolver.delete(
        ContentUris.withAppendedId(DOGS_CONTENT_URI, dog.id),
        null,
        null
    )
    dogs.removeAt(position)
    File(dog.picturePath).delete()
    notifyDataSetChanged()
}
}