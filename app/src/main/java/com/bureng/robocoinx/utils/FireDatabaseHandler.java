package com.bureng.robocoinx.utils;

import com.bureng.robocoinx.model.firebase.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireDatabaseHandler {

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public static boolean registerUser(User user){
        return true;
    }
    public static User getUserByEmail(String email){
        return null;
    }
    public static boolean isRegister(String email){
        User userByEmail = getUserByEmail(email);
        return userByEmail != null;
    }

    public static boolean updatePassword(User user){
        return true;
    }
}
