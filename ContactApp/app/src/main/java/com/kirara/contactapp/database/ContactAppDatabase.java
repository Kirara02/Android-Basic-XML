package com.kirara.contactapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kirara.contactapp.database.dao.ContactDao;
import com.kirara.contactapp.database.entity.ContactEntity;

@Database(
        entities = {
            ContactEntity.class
        },
        version = 1
)
public abstract class ContactAppDatabase extends RoomDatabase {
    private static ContactAppDatabase instance;
    public abstract ContactDao contactDao();
    public static synchronized ContactAppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ContactAppDatabase.class, "contact_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
