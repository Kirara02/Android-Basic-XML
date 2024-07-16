package com.kirara.fragmentact;


import com.kirara.fragmentact.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.kirara.fragmentact.fragment.AccountFragment;
import com.kirara.fragmentact.fragment.ArticleFragment;
import com.kirara.fragmentact.fragment.GraphFragment;
import com.kirara.fragmentact.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new HomeFragment());

        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        int itemId = item.getItemId();

        if (itemId == R.id.fr_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.fr_article) {
            fragment = new ArticleFragment();
        } else if (itemId == R.id.fr_graph) {
            fragment = new GraphFragment();
        } else if (itemId == R.id.fr_account) {
            fragment = new AccountFragment();
        }

        return loadFragment(fragment);
    }
}
