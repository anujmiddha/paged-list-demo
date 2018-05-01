package com.moldedbits.pagedlistdemo.ui.contact

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.arch.paging.PositionalDataSource
import android.content.ContentResolver
import android.provider.ContactsContract
import com.moldedbits.pagedlistdemo.data.Contact

class ContactsViewModel(private val contentResolver: ContentResolver) : ViewModel() {

    lateinit var contactsList: LiveData<PagedList<Contact>>

    fun loadContacts() {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build()
        contactsList = LivePagedListBuilder<Int, Contact>(
                ContactsDataSourceFactory(contentResolver), config).build()
    }
}

class ContactsDataSourceFactory(private val contentResolver: ContentResolver) :
        DataSource.Factory<Int, Contact>() {

    override fun create(): DataSource<Int, Contact> {
        return ContactsDataSource(contentResolver)
    }
}

class ContactsDataSource(private val contentResolver: ContentResolver) :
        PositionalDataSource<Contact>() {

    companion object {
        private val PROJECTION = arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        )
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Contact>) {
        callback.onResult(getContacts(params.requestedLoadSize, params.requestedStartPosition), 0)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Contact>) {
        callback.onResult(getContacts(params.loadSize, params.startPosition))
    }

    private fun getContacts(limit: Int, offset: Int): MutableList<Contact> {
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY +
                        " ASC LIMIT " + limit + " OFFSET " + offset)

        cursor.moveToFirst()
        val contacts: MutableList<Contact> = mutableListOf()
        while (!cursor.isAfterLast) {
            val id = cursor.getLong(cursor.getColumnIndex(PROJECTION[0]))
            val lookupKey = cursor.getString(cursor.getColumnIndex(PROJECTION[0]))
            val name = cursor.getString(cursor.getColumnIndex(PROJECTION[2]))
            contacts.add(Contact(id, lookupKey, name))
            cursor.moveToNext()
        }
        cursor.close()

        return contacts
    }
}