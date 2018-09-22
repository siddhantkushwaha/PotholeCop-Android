package com.potholecop.androidapp;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDb {

    private static FirebaseDatabase mData;

    public static FirebaseDatabase getInstance() {
        if (mData == null) {

            mData = FirebaseDatabase.getInstance();
            mData.setPersistenceEnabled(true);
        }
        return mData;
    }
}
