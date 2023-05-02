package com.example.rulesphere;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rulesphere.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home)
                replaceFragment(new HomeFragment());
            else if (item.getItemId() == R.id.search)
                replaceFragment(new SearchFragment());
            else if (item.getItemId() == R.id.favourites)
                replaceFragment(new DesignFragment());
            else if (item.getItemId() == R.id.myRules)
                replaceFragment(new MyRulesFragment());

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        /*
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
        */

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.frameLayout, fragment)
                .commit();
    }
}