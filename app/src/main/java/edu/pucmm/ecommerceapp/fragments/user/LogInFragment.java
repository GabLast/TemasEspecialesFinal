package edu.pucmm.ecommerceapp.fragments.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.activities.MainMenu;
import edu.pucmm.ecommerceapp.databinding.FragmentLogInBinding;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.helpers.Functions;
import edu.pucmm.ecommerceapp.helpers.KProgressHUDUtils;
import edu.pucmm.ecommerceapp.models.User;
import edu.pucmm.ecommerceapp.retrofit.UserApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class LogInFragment extends Fragment {

    private TextView registerTxt;
    private EditText mail, password;
    private Button login;
    private List<View> views = new ArrayList<>();
    private FragmentLogInBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLogInBinding.inflate(inflater, container, false);

        registerTxt = binding.registerTXT;
        login = binding.loginBTN;

        mail = binding.mailInput;
        password = binding.passwordInput;

        views.add(mail);
        views.add(password);


        registerTxt.setOnClickListener(v -> {

            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.parentFragmentMain, new RegisterUserFragment())
                    .addToBackStack(null)
                    .commit();
        });

        login.setOnClickListener(v -> {
            userLogIn();
        });

        autoFill();

        return binding.getRoot();
    }

    private void userLogIn(){

        if (Functions.isEmpty((View) views)) {
            return;
        }

        KProgressHUD progressDialog = new KProgressHUDUtils(getContext()).showAuthenticating();

        //transforming to json
        JsonObject user = new JsonObject();
        user.addProperty("email", binding.mailInput.getText().toString().trim());
        user.addProperty("password", binding.passwordInput.getText().toString().trim());

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(User.class, dateJsonDeserializer);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalVariables.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();

        Call<User> userCall = retrofit.create(UserApiService.class).login(user);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                switch (response.code()) {
                    case 200:

                        FancyToast.makeText(getContext(), "Successfully Logged In", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                        GlobalVariables.setUSERSESSION(response.body());

                        System.err.println(response.body().toString());

                        goToMainMenu();
                        break;
                    default:
                        try {
                            FancyToast.makeText(getContext(), response.errorBody().string(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable error) {
                progressDialog.dismiss();
                FancyToast.makeText(getContext(), error.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
        });
    }

    private void goToMainMenu() {
        Intent intent = new Intent(getContext(), MainMenu.class);
        startActivity(intent);
        getActivity().finish();
    }

    private final JsonDeserializer<User> dateJsonDeserializer = (json, typeOfT, context) -> {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        final JsonObject jo = (JsonObject) json;

        User user = new User();
        user.setUid(jo.get("uid").getAsInt());
        user.setFirstName(jo.get("firstName").getAsString());
        user.setLastName(jo.get("lastName").getAsString());
        user.setEmail(jo.get("email").getAsString());
        user.setRol(User.ROL.valueOf(jo.get("rol").getAsString()));
        user.setContact(jo.get("contact").getAsString());
        user.setPhoto(jo.get("photo").getAsString());
        user.setBirthday(dateFormat.format(new Date(jo.get("birthday").getAsLong())));

        return user;
    };

    private void autoFill() {
        binding.mailInput.setText("gab1@mail.com");
        binding.passwordInput.setText("12345678");
    }

}