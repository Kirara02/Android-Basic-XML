package com.kirara.contactapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.kirara.contactapp.database.ContactAppDatabase;
import com.kirara.contactapp.database.dao.ContactDao;
import com.kirara.contactapp.database.entity.ContactEntity;

import java.util.List;

public class ContactRepository {
    private ContactDao contactDao;
    private LiveData<List<ContactEntity>> allContacts;

    public ContactRepository(Application application){
        ContactAppDatabase database = ContactAppDatabase.getInstance(application);
        contactDao = database.contactDao();
        allContacts = contactDao.getAllContacts();
    }

    public void insert(ContactEntity contact){
        new InsertContactAsyncTask(contactDao).execute(contact);
    }

    public void delete(ContactEntity contact){
        new DeleteContactAsyncTask(contactDao).execute(contact);
    }

    public void update(ContactEntity contact){
        new UpdateContactAsyncTask(contactDao).execute(contact);
    }

    public LiveData<List<ContactEntity>> getContacts(){
        return allContacts;
    }

    private static class InsertContactAsyncTask extends AsyncTask<ContactEntity, Void, Void> {

        private ContactDao contactDao;

        private InsertContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(ContactEntity... contactEntities) {
            contactDao.insert(contactEntities[0]);
            return null;
        }
    }


    private static class DeleteContactAsyncTask extends AsyncTask<ContactEntity, Void, Void> {

        private ContactDao contactDao;

        private DeleteContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(ContactEntity... contactEntities) {
            contactDao.delete(contactEntities[0]);
            return null;
        }
    }

    private static class UpdateContactAsyncTask extends AsyncTask<ContactEntity, Void, Void> {

        private ContactDao contactDao;

        private UpdateContactAsyncTask(ContactDao contactDao) {
            this.contactDao = contactDao;
        }

        @Override
        protected Void doInBackground(ContactEntity... contactEntities) {
            contactDao.update(contactEntities[0]);
            return null;
        }
    }
}
