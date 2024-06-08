package com.example.taskkttelematic.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.taskkttelematic.MainActivity
import com.example.taskkttelematic.R
import com.example.taskkttelematic.model.UserData
import io.realm.Realm
import io.realm.kotlin.where

class SwitchUserDialogFragment : DialogFragment() {

    private lateinit var userListAdapter: ArrayAdapter<String>

    @SuppressLint("ResourceType")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val realm = Realm.getDefaultInstance()
            val userList = realm.where<UserData>().findAll().map { it.email }

            userListAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, userList)

        val dialog = AlertDialog.Builder(requireActivity(), R.style.CustomDialog)
            .setTitle("Switch User")
            .setAdapter(userListAdapter) { dialog, which ->
                val selectedUser = userListAdapter.getItem(which)
                selectedUser?.let {
                    (activity as MainActivity).switchUser(it)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        return dialog
    }
}


