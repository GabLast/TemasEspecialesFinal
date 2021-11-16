package edu.pucmm.ecommerceapp;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import edu.pucmm.ecommerceapp.databinding.ActivityMainBinding;
import edu.pucmm.ecommerceapp.fragments.users.LogInFragment;
import edu.pucmm.ecommerceapp.fragments.users.RegisterUserFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.parentFragmentMain, new LogInFragment())
                .addToBackStack(null)
                .commit();


    }
}