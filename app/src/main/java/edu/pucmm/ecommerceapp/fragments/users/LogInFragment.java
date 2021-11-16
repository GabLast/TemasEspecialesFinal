package edu.pucmm.ecommerceapp.fragments.users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.pucmm.ecommerceapp.MainMenu;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.database.AppExecutors;
import edu.pucmm.ecommerceapp.database.DAOs.UserDao;
import edu.pucmm.ecommerceapp.database.sessions.UserSession;
import edu.pucmm.ecommerceapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class LogInFragment extends Fragment {

    private TextView registerTxtClick;
    private EditText username, password;
    private Button login;
    private List<EditText> views = new ArrayList<>();
    private UserDao userDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View binding = inflater.inflate(R.layout.fragment_log_in, container, false);

        UserSession session = new UserSession(getContext());
        if(session.isLoggedIn()){
            goToMainMenu();
        }

        registerTxtClick = binding.findViewById(R.id.registerTxtClick);
        username = binding.findViewById(R.id.usernameCredentials);
        password = binding.findViewById(R.id.passwordCredentials);
        login = binding.findViewById(R.id.loginBTN);

        views.add(username);
        views.add(password);


        registerTxtClick.setOnClickListener(v -> {

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.parentFragmentMain, new RegisterUserFragment())
                    .addToBackStack(null)
                    .commit();
        });

        login.setOnClickListener(v -> {
            if(!isEmpty()){
                AppExecutors.getInstance().diskIO().execute(() -> {

                    User user = userDao.login(username.getText().toString().trim(), password.getText().toString().trim());
                    System.err.println(user);

                    if(user != null){
                        session.saveUserLocally(session.USER, user.getUsername());
                        goToMainMenu();
                    }else {
                        Toast.makeText(getContext(), "The credentials are wrong", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

        return binding;
    }

    private Boolean isEmpty(){
        for (TextView view:views){
            if(view.getText().toString().isEmpty()){
                view.setError("Field can't be empty");
                return true;
            }
        }
        return false;
    }

    private void goToMainMenu()
    {
        Intent intent = new Intent(getContext(), MainMenu.class);
        startActivity(intent);
    }

}