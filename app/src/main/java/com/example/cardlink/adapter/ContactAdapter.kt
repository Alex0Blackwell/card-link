package com.example.cardlink.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.cardlink.R
import com.example.cardlink.dataLayer.Person


class ContactAdapter(
    private val context: Context,
    private var contactList: ArrayList<Person>
): BaseAdapter() {
    private var myViewConverter: View? = null
    private var viewHolder: ViewHolder? = null

    data class ViewHolder(
        val name: TextView,
        val occupation: TextView,
        val photo: ImageView,
        var primaryKey: String = "",
        var person: Person = Person()
    )

    override fun getCount(): Int {
        return contactList.size
    }

    override fun getItem(position: Int): Person {
        return contactList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        myViewConverter = convertView

        updateViewHolder()

        val contact = getItem(position)

        viewHolder?.primaryKey = contact.primaryKey
        viewHolder?.name?.text = contact.name
        viewHolder?.occupation?.text = contact.occupation
        viewHolder?.person = contact
        // TODO: Add profile photo when data is not mocked

        return myViewConverter!!
    }

    /**
     * List adapters should use the "view holder" pattern.
     * https://stackoverflow.com/a/25381936
     * We want to recycle the view holder as much as possible.
     * So, store it in the "tag" of the view converter to avoid inflating it on every call.
     */
    private fun updateViewHolder() {
        var _myViewConverter = myViewConverter

        if(_myViewConverter == null) {
            // Then we need to update the view holder.
            _myViewConverter = View.inflate(context, R.layout.item_listview_contact, null)
            viewHolder = ViewHolder(
                _myViewConverter.findViewById(R.id.my_contacts_list_name),
                _myViewConverter.findViewById(R.id.my_contacts_list_occupation),
                _myViewConverter.findViewById(R.id.my_contacts_list_photo),
            )
            _myViewConverter.tag = viewHolder
        }
        else {
            // The view already exists, let's recycle it.
            viewHolder = _myViewConverter.tag as ViewHolder
        }

        myViewConverter = _myViewConverter
    }
}