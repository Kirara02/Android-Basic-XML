package com.kirara.contactapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kirara.contactapp.adapter.ContactAdapter;
import com.kirara.contactapp.database.entity.ContactEntity;
import com.kirara.contactapp.viewmodel.ContactViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ContactViewModel viewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ContactAdapter contactAdapter;
    private Toolbar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        contactAdapter = new ContactAdapter();
        recyclerView.setAdapter(contactAdapter);

        viewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        viewModel.getAllContacts().observe(this, new Observer<List<ContactEntity>>() {
            @Override
            public void onChanged(List<ContactEntity> contactEntities) {
                contactAdapter.setContacts(contactEntities);
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            openAddContactDialog(null, DialogType.ADD);
        });

        contactAdapter.setOnItemClickListener(contact -> openAddContactDialog(contact, DialogType.EDIT));
        contactAdapter.setOnDeleteClickListener(this::showDeleteConfirmationDialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    contactAdapter.getFilter().filter(newText);
                    return false;
                }
            });

            searchView.setOnCloseListener(() -> {
                contactAdapter.setContacts(viewModel.getAllContacts().getValue());
                return false;
            });
        }

        return true;
    }

    private void openAddContactDialog(@Nullable final ContactEntity contact, DialogType dialogType) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_contact, null);

        final EditText editTextName = dialogView.findViewById(R.id.edit_text_name);
        final EditText editTextPhoneNumber = dialogView.findViewById(R.id.edit_text_phone_number);

        if (dialogType == DialogType.EDIT && contact != null) {
            editTextName.setText(contact.getName());
            editTextPhoneNumber.setText(contact.getPhoneNumber());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(dialogType == DialogType.ADD ? "Add Contact" : "Edit Contact")
                .setView(dialogView)
                .setPositiveButton(dialogType == DialogType.ADD ? "Add" : "Update", (dialog1, which) -> {
                    String name = editTextName.getText().toString();
                    String phoneNumber = editTextPhoneNumber.getText().toString();
                    if (!name.isEmpty() && !phoneNumber.isEmpty()) {
                        if (dialogType == DialogType.ADD) {
                            ContactEntity newContact = new ContactEntity(name, phoneNumber);
                            viewModel.insert(newContact);
                            Toast.makeText(MainActivity.this, "Add Contact successfully", Toast.LENGTH_SHORT).show();
                        } else if (dialogType == DialogType.EDIT && contact != null) {
                            contact.setName(name);
                            contact.setPhoneNumber(phoneNumber);
                            viewModel.update(contact);
                            Toast.makeText(MainActivity.this, "Contact updated", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showDeleteConfirmationDialog(ContactEntity contact) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Are you sure you want to delete this contact?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    viewModel.delete(contact);
                    Toast.makeText(MainActivity.this, "Contact deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    public enum DialogType {
        ADD,
        EDIT
    }
}