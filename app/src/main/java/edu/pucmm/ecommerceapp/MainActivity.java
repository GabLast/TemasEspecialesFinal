package edu.pucmm.ecommerceapp;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import edu.pucmm.ecommerceapp.databinding.ActivityMainBinding;
import edu.pucmm.ecommerceapp.fragments.user.LogInFragment;

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