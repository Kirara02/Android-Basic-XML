package com.kirara.fragmentact.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kirara.fragmentact.R;
import com.kirara.fragmentact.models.Student;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private ArrayList<Student> listStudent;

    public StudentAdapter(ArrayList<Student> listStudent) {
        this.listStudent = listStudent;
    }

    @NonNull
    @Override
    public StudentAdapter.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.StudentViewHolder holder, int position) {
        holder.nim.setText(listStudent.get(position).getNim());
        holder.nama.setText(listStudent.get(position).getNama());
        holder.kelas.setText(listStudent.get(position).getKelas());
    }

    @Override
    public int getItemCount() {
        return (listStudent != null)? listStudent.size() : 0;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder{

        private TextView nim,nama,kelas;
        public StudentViewHolder(View view){
            super(view);
            nim = view.findViewById(R.id.nim);
            nama = view.findViewById(R.id.nama);
            kelas = view.findViewById(R.id.kelas);
        }

    }
}
