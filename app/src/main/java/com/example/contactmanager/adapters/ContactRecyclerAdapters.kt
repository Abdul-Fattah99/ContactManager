package com.example.contactmanager.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactmanager.R
import com.example.contactmanager.data.entities.Contact
import kotlinx.android.synthetic.main.contact_item.view.*
import java.util.*

class ContactsRecyclerAdapter: ListAdapter<Contact, ContactsRecyclerAdapter.ContactHolder>(
    DIFF_CALLBACK), Filterable {

    private var listener: ItemOnClickListener ?= null
    private lateinit var searchableList: List<Contact>

    companion object {
        private val DIFF_CALLBACK = object: DiffUtil.ItemCallback<Contact>() {
            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.first_name == newItem.first_name &&
                        oldItem.last_name == newItem.last_name &&
                        oldItem.phone_home == newItem.phone_home &&
                        oldItem.phone_cell == newItem.phone_cell &&
                        oldItem.phone_work == newItem.phone_work &&
                        oldItem.email == newItem.email &&
                        oldItem.address == newItem.address &&
                        oldItem.birthday ==  newItem.birthday &&
                        oldItem.profileImageName == newItem.profileImageName
            }

            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val contact: Contact = getItem(position)

        if (contact.first_name != "" && contact.last_name != "") {
            holder.nameTextView.text = "${contact.first_name} ${contact.last_name}"
        } else if (contact.first_name != "") {
            holder.nameTextView.text = contact.first_name.toString()
        } else if (contact.last_name != "") {
            holder.nameTextView.text = contact.last_name.toString()
        } else if (contact.email != "") {
            holder.nameTextView.text = contact.email.toString()
        } else {
            if (contact.phone_home != "") {
                holder.nameTextView.text = contact.phone_home
            } else if (contact.phone_cell != "") {
                holder.nameTextView.text = contact.phone_cell
            } else if (contact.phone_work != "") {
                holder.nameTextView.text = contact.phone_work
            } else {
                //If nothing is available, set it to nothing
                holder.nameTextView.text = ""
            }
        }

//        holder.imageView
//            .setImageBitmap(ImageDataHelper
//                .readImage(ImageDataHelper.IMAGE_PATH, contact.profileImageName!!))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return ContactHolder(itemView)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setSearchableList(list: List<Contact>) {
        searchableList = list
    }

    fun getContact(position: Int): Contact {
        return getItem(position)
    }

    inner class ContactHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(getItem(position))
                }
            }
        }

        var nameTextView = view.name_text_view
        var imageView = view.contact_profile_image_view
    }

    interface ItemOnClickListener {
        fun onItemClick(contact: Contact) //use this in activity
    }

    fun setItemOnClickListener(listener: ItemOnClickListener) {
        this.listener = listener
    }

    override fun getFilter(): Filter {
        return searchFilter
    }

    private var searchFilter = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var filteredList = mutableListOf<Contact>()
            if (constraint!!.isEmpty()) {
                filteredList = searchableList.toMutableList()
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()
                searchableList.forEach {
                    if (it.first_name?.toLowerCase(Locale.getDefault())!!.contains(filterPattern)
                        || it.last_name?.toLowerCase(Locale.getDefault())!!.contains(filterPattern)) {
                        filteredList.add(it)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            submitList(filteredList)
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }}
