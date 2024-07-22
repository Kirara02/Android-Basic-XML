package com.kirara.contactapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirara.contactapp.MainActivity;
import com.kirara.contactapp.R;
import com.kirara.contactapp.database.entity.ContactEntity;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> implements Filterable {
    private List<ContactEntity> contacts = new ArrayList<>();
    private List<ContactEntity> contactsFull;
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
        contactsFull = new ArrayList<>(contacts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    private final Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ContactEntity> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(contactsFull);
            }else{
                String stringPattern = constraint.toString().toLowerCase().trim();
                for(ContactEntity item : contactsFull){
                    if(item.getName().toLowerCase().contains(stringPattern) ||
                        item.getPhoneNumber().toLowerCase().contains(stringPattern)){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults result = new FilterResults();
            result.values = filteredList;
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contacts.clear();
            contacts.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class ContactHolder extends RecyclerView.ViewHolder {
        private TextView name, phoneNumber;
        private ImageView more;
        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_name);
            phoneNumber = itemView.findViewById(R.id.text_view_phone_number);
            more = itemView.findViewById(R.id.btn_more);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    Log.d(MainActivity.class.getName(), "Clicked item-"+position);
                }
            });

            more.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.menu_item_options);
                popupMenu.setOnMenuItemClickListener(item -> {
                    int position = getAbsoluteAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        if (item.getItemId() == R.id.action_update) {
                            if (listener != null) {
                                listener.onItemClick(contacts.get(position));
                            }
                            return true;
                        } else if (item.getItemId() == R.id.action_delete) {
                            if (deleteListener != null) {
                                deleteListener.onDeleteClick(contacts.get(position));
                            }
                            return true;
                        }
                    }
                    return false;
                });
                popupMenu.show();
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
