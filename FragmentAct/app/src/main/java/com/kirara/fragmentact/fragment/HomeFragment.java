package com.kirara.fragmentact.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kirara.fragmentact.R;
import com.kirara.fragmentact.adapters.StudentAdapter;
import com.kirara.fragmentact.models.Student;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private ArrayList<Student> studentArrayList;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        prepareStudentData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_home);
        studentAdapter = new StudentAdapter(studentArrayList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(studentAdapter);

        return view;
    }

    private void prepareStudentData() {
        studentArrayList = new ArrayList<>();
        // Add student data to the list
        studentArrayList.add(new Student("John Doe", "Mathematics", "Kelas 10A"));
        studentArrayList.add(new Student("Jane Smith", "Science", "Kelas 10B"));
        studentArrayList.add(new Student("Alice Brown", "History", "Kelas 10C"));
        studentArrayList.add(new Student("Bob Johnson", "Literature", "Kelas 10D"));
        studentArrayList.add(new Student("Charlie Wilson", "Physics", "Kelas 10A"));
        studentArrayList.add(new Student("Emily Davis", "Chemistry", "Kelas 10B"));
        studentArrayList.add(new Student("Frank Harris", "Biology", "Kelas 10C"));
        studentArrayList.add(new Student("Grace Martinez", "Geography", "Kelas 10D"));
        studentArrayList.add(new Student("Henry Clark", "Computer Science", "Kelas 10A"));
        studentArrayList.add(new Student("Isabella Lee", "Art", "Kelas 10B"));
        studentArrayList.add(new Student("Jack White", "Music", "Kelas 10C"));
        studentArrayList.add(new Student("Katherine Young", "Physical Education", "Kelas 10D"));
        studentArrayList.add(new Student("Liam Miller", "Economics", "Kelas 10A"));
        studentArrayList.add(new Student("Mia Taylor", "Psychology", "Kelas 10B"));
        studentArrayList.add(new Student("Noah Moore", "Sociology", "Kelas 10C"));
        studentArrayList.add(new Student("Olivia Garcia", "Anthropology", "Kelas 10D"));
        studentArrayList.add(new Student("Patrick Martinez", "Philosophy", "Kelas 10A"));
        studentArrayList.add(new Student("Quinn Robinson", "Language Arts", "Kelas 10B"));
        studentArrayList.add(new Student("Ryan Hall", "Environmental Science", "Kelas 10C"));
        studentArrayList.add(new Student("Sophia Allen", "Engineering", "Kelas 10D"));
    }
}