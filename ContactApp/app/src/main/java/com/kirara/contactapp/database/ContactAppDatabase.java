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
        version = 1,
        exportSchema = false
)
public abstract class ContactAppDatabase extends RoomDatabase {
    private static volatile ContactAppDatabase instance;

    public abstract ContactDao contactDao();

    public static ContactAppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (ContactAppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    ContactAppDatabase.class, "contact_db")
                            .fallbackToDestructiveMigration() // Consider using migration in production
                            .build();
                }
            }
        }
        return instance;
    }
}
