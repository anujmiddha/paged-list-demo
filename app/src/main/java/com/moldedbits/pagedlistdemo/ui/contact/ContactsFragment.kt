package com.moldedbits.pagedlistdemo.ui.contact

import android.arch.lifecycle.Observer
import android.arch.paging.PagedListAdapter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.moldedbits.pagedlistdemo.R
import com.moldedbits.pagedlistdemo.data.Contact
import com.moldedbits.pagedlistdemo.ui.contact.ContactsViewModel
import kotlinx.android.synthetic.main.fragment_contacts.*
import org.koin.android.architecture.ext.viewModel


class ContactsFragment : Fragment() {

    private val viewModel by viewModel<ContactsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        contactsList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false)

        val diffCallback = object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }
        }

        val adapter = ContactsAdapter(diffCallback)
        contactsList.adapter = adapter

        viewModel.loadContacts()
        viewModel.contactsList.observe(this, Observer {
            adapter.submitList(it)
            contactsEmpty.visibility = if (adapter.itemCount > 0) {
                View.GONE
            } else {
                View.VISIBLE
            }
        })
    }

    class ContactsAdapter(diffCallback: DiffUtil.ItemCallback<Contact>) :
            PagedListAdapter<Contact, ContactViewHolder>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            return ContactViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.list_item_group, parent,
                            false))
        }

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            holder.textView.text = getItem(position)?.name
        }

    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.groupName)
    }
}