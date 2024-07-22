package com.kirara.contactapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kirara.contactapp.database.entity.ContactEntity;
import com.kirara.contactapp.repository.ContactRepository;

import java.util.List;

public class ContactViewModel extends AndroidViewModel {
    private ContactRepository repository;
    private LiveData<List<ContactEntity>> allContacts;

    public ContactViewModel(@NonNull Application application){
        super(application);
        repository = new ContactRepository(application);
        allContacts = repository.getContacts();
    }

    public void insert(ContactEntity contact){
        repository.insert(contact);
    }

    public void delete(ContactEntity contact){
        repository.delete(contact);
    }

    public void update(ContactEntity contact){
        repository.update(contact);
    }

    public LiveData<List<ContactEntity>> getAllContacts(){
        return allContacts;
    }
}
