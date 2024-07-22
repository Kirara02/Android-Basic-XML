package com.kirara.contactapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirara.contactapp.R;
import com.kirara.contactapp.database.entity.ContactEntity;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {
    private List<ContactEntity> contacts = new ArrayList<>();
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;

    @NonNull
    @Override
    public ContactAdapter.ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactHolder holder, int position) {
        ContactEntity currentContact = contacts.get(position);
        holder.name.setText(currentContact.getName());
        holder.phoneNumber.setText(currentContact.getPhoneNumber());
    }

    public void setContacts(List<ContactEntity> contacts){
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ContactHolder extends RecyclerView.ViewHolder {
        private TextView name, phoneNumber;
        private ImageView imgDelete;
        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_name);
            phoneNumber = itemView.findViewById(R.id.text_view_phone_number);
            imgDelete = itemView.findViewById(R.id.image_view_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(contacts.get(position));
                    }
                }
            });

            imgDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if(deleteListener != null && position != RecyclerView.NO_POSITION){
                    deleteListener.onDeleteClick(contacts.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ContactEntity contact);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(ContactEntity contact);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener deleteListener){
        this.deleteListener = deleteListener;
    }
}
