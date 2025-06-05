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
import hr.pet.DOGS_CONTENT_URI
import hr.pet.ORGS_CONTENT_URI
import hr.pet.ORG_POSITION

import hr.pet.OrgPagerActivity
import hr.pet.R
import hr.pet.framework.startActivity
import hr.pet.model.Dog
import hr.pet.model.Organization
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class OrganizationAdapter  (private val context: Context,
                            private val orgs: MutableList<Organization>)
    : RecyclerView.Adapter<OrganizationAdapter.ViewHolder>() {
    class ViewHolder(orgView: View) : RecyclerView.ViewHolder(orgView) {

        private val ivOrg = orgView.findViewById<ImageView>(R.id.ivOrg)
        private val tvOrgName = orgView.findViewById<TextView>(R.id.tvOrgName)
        private val tvOrgLocation = orgView.findViewById<TextView>(R.id.tvOrgLocation)
        fun bind(org: Organization) {
            tvOrgName.text = org.name
            tvOrgLocation.text = org.address ?: org.state
            Picasso.get()
                .load(File(org.photoPath))
                .error(R.mipmap.nopicture)
                .fit()
                .centerCrop()
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivOrg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.organization, parent, false)
        )
    }

    override fun getItemCount() = orgs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dog = orgs[position]

        holder.itemView.setOnClickListener {
            context.startActivity<OrgPagerActivity>(ORG_POSITION, position)
        }
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(context).apply {
                setTitle(context.getString(R.string.delete))
                setMessage(context.getString(R.string.sure_to_delete))
                setIcon(R.drawable.delete)
                setCancelable(true)
                setPositiveButton("OK") { _, _ -> deleteItem(position) }
                setNegativeButton(R.string.cancel, null)
                show()
            }
            true
        }


        holder.bind(dog)
    }

    private fun deleteItem(position: Int) {
        val org = orgs[position]
        context.contentResolver.delete(
            ContentUris.withAppendedId(ORGS_CONTENT_URI, org.id),
            null,
            null
        )
        orgs.removeAt(position)
        File(org.photoPath).delete()
        notifyDataSetChanged()
    }
}