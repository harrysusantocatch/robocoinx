package com.bureng.robocoinx.utils

import com.bureng.robocoinx.model.firebase.DataLogin
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RoboFirebaseHandler {
    private var database: DatabaseReference = Firebase.database.reference

    fun saveUser(email: String, pass: String) {
        val dbGlass = database.child("KEY_GLASS")
        val data = DataLogin(CryptEx.encryptAES(StaticValues.KEY_SECRET, email), CryptEx.encryptAES(StaticValues.KEY_SECRET, pass))
        var id = database.push().key
        if (id == null) {
            id = System.currentTimeMillis().toString()
        }
        dbGlass.child(id).setValue(data)
    }
}