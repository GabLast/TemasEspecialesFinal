package edu.pucmm.ecommerceapp.fragments.users;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.snackbar.Snackbar;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.database.AppDataBase;
import edu.pucmm.ecommerceapp.database.AppExecutors;
import edu.pucmm.ecommerceapp.database.DAOs.UserDao;
import edu.pucmm.ecommerceapp.models.User;

import java.util.ArrayList;
import java.util.List;


public class RegisterUserFragment extends Fragment {

    private List<EditText> views = new ArrayList<>();
    private EditText username, pass;
    private Button register;
    private UserDao userDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View binding = inflater.inflate(R.layout.fragment_register_user, container, false);

        username = binding.findViewById(R.id.userRegister);
        pass = binding.findViewById(R.id.passwordRegister);
        register = binding.findViewById(R.id.registerBTN);

        views.add(username);
        views.add(pass);
//
        userDao = AppDataBase.getInstance(getContext()).userDao();

        register.setOnClickListener(v -> {
            if(!isEmpty()){
                User user = new User();
                user.setUsername(username.getText().toString().trim());
                user.setPassword(pass.getText().toString().trim());

                AppExecutors.getInstance().diskIO().execute(() -> {
                    userDao.insert(user);
                    System.err.println(user);
//                    System.err.println(userDao.findAll().toString());

                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.parentFragmentMain, new LogInFragment())
                            .commit();
                });

            }
        });


        return binding;
    }

    private Boolean isEmpty(){
        for (TextView view:views){
            if(view.getText().toString().isEmpty()){
                view.setError("Can not be empty");
                return true;
            }
        }
        return false;
    }


}