package com.example.familysafety

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private val listContacts:ArrayList<ContactModel> = ArrayList(

    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listMembers = listOf<MemberModel>(
            MemberModel("xyz", "Behind Uco Bank Ramnagar Charoda B.M.Y Durg Chhattisgarh"),
            MemberModel("xyz", "Behind Uco Bank Ramnagar Charoda B.M.Y Durg Chhattisgarh"),
            MemberModel("xyz", "Behind Uco Bank Ramnagar Charoda B.M.Y Durg Chhattisgarh"),
        )
        val adapter = MemberAdapter(listMembers)

        val recycler = requireView().findViewById<RecyclerView>(R.id.recycler_member)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        fetchContacts()

        val inviteAdapter = InviteAdapter(listContacts)

        CoroutineScope(Dispatchers.IO).launch {

            listContacts.addAll(fetchContacts())

            withContext(Dispatchers.Main){
                inviteAdapter.notifyDataSetChanged()

            }
        }



        val inviteRecycler = requireView().findViewById<RecyclerView>(R.id.recycler_invite)
        inviteRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        inviteRecycler.adapter = inviteAdapter


    }

    private fun fetchContacts(): ArrayList<ContactModel> {
        val cr = requireActivity().contentResolver
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null)

        val listContacts:ArrayList<ContactModel> = ArrayList()

        if ((cursor != null) && (cursor.count > 0)){
            while (cursor.moveToNext()){

                val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                if(hasPhoneNumber > 0){
                    val pCur = cr.query((ContactsContract.CommonDataKinds.Phone.CONTENT_URI),
                        null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                            arrayOf(id),
                            ""
                            )
                    if(pCur != null && pCur.count>0){
                        while (pCur.moveToNext()){

                            val phoneNum = pCur.getString(pCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))

                            listContacts.add(ContactModel(name, phoneNum))
                        }
                        pCur.close()
                    }
                }
            }
            cursor.close()
        }

        return listContacts
    }



    companion object {

        @JvmStatic
        fun newInstance() = HomeFragment()
        }
    }