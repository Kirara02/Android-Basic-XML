package com.kirara.contactapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kirara.contactapp.database.entity.ContactEntity;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert
    void insert(ContactEntity contact);

    @Update
    void update(ContactEntity contact);

    @Delete
    void delete(ContactEntity contact);

    @Query("SELECT * FROM contacts")
    LiveData<List<ContactEntity>> getAllContacts();
}
