package edu.pucmm.ecommerceapp.fragments.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.databinding.FragmentRegisterUserBinding;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.helpers.Functions;
import edu.pucmm.ecommerceapp.helpers.KProgressHUDUtils;
import edu.pucmm.ecommerceapp.models.User;
import edu.pucmm.ecommerceapp.networks.Firebase;
import edu.pucmm.ecommerceapp.networks.NetResponse;
import edu.pucmm.ecommerceapp.retrofit.UserApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;


public class RegisterUserFragment extends Fragment {

    private User user = new User();
    private Uri uri;
    private FragmentRegisterUserBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterUserBinding.inflate(inflater, container, false);
//

        List<String> rols = new ArrayList();
        for (User.ROL rol : User.ROL.values()) {
            rols.add(rol.toString());
        }
//        rols.add(User.ROL.CUSTOMER.toString());
//        rols.add(User.ROL.SELLER.toString());

        //Creating the ArrayAdapter instance
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.pretty_spinner_item, rols);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.rolSPINNER.setAdapter(arrayAdapter);
        binding.rolSPINNER.setSelection(0);


        binding.register.setOnClickListener(v -> {
            registerAccount();
        });

        binding.profile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imageResultLauncher.launch(intent);
        });

        autoFill();

        return binding.getRoot();
    }

    private void registerAccount() {
        if (Functions.isEmpty(binding.firstnameTXT, binding.lastnameTXT, binding.emailTXT, binding.passwordTXT, binding.confirmPassTXT, binding.contactTXT)) {
            return;
        }

        String email = this.binding.emailTXT.getText().toString().trim();
        String password = this.binding.passwordTXT.getText().toString();
        String confirmPass = this.binding.confirmPassTXT.getText().toString();

        if (!password.equals(confirmPass)) {
            FancyToast.makeText(getContext(), "Passwords do not match", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return;
        }

        if (!Functions.isEmailValid(binding.emailTXT, email)) {
            FancyToast.makeText(getContext(), "Please enter a valid mail", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return;
        }
        if(binding.rolSPINNER.getText().toString().isEmpty()){
            FancyToast.makeText(getContext(), "Please select a rol", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return;
        }

        user.setFirstName(binding.firstnameTXT.getText().toString().trim());
        user.setLastName(binding.lastnameTXT.getText().toString().trim());
        user.setEmail(binding.emailTXT.getText().toString().trim());
        user.setRol(User.ROL.valueOf(binding.rolSPINNER.getText().toString()));
        user.setContact(binding.contactTXT.getText().toString());
        user.setPassword(password);

        int day = binding.birthdayDATE.getDayOfMonth();
        int month = binding.birthdayDATE.getMonth();
        int year = binding.birthdayDATE.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        user.setBirthday(dateFormat.format(calendar.getTime()));

        final KProgressHUD progressDialog = new KProgressHUDUtils(getContext()).showConnecting();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalVariables.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<User> userCreateCall = retrofit.create(UserApiService.class).create(user);

        call(userCreateCall, error -> {
            if (error) {
                progressDialog.dismiss();
                return;
            }

            if(uri != null){
                Firebase.getConstantInstance().upload(uri, String.format("profile/%s.jpg", user.getUid()),
                        new NetResponse<String>() {
                            @Override
                            public void onResponse(String response) {
                                FancyToast.makeText(getContext(), "Image uploaded successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                                user.setPhoto(response);
                                final Call<User> userUpdateCall = retrofit.create(UserApiService.class).update(user);

                                call(userUpdateCall, res1 -> progressDialog.dismiss());
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                progressDialog.dismiss();
                                System.err.println("error on onFailure" + t.getMessage());
                                FancyToast.makeText(getContext(), t.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }
                        });
            }else {
                progressDialog.dismiss();
            }

        });
    }

    private void call(Call<User> call, Consumer<Boolean> error) {
        call.enqueue(new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                switch (response.code()) {
                    case 201:
                        FancyToast.makeText(getContext(), "User has been registered", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        user.setUid(response.body().getUid());
                        goToLogin();
                        error.accept(false);
                        break;
                    case 204:
                        FancyToast.makeText(getContext(), "User has been updated", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                        error.accept(false);
                        break;
                    default:
                        try {
                            System.err.println("ERROR on onResponse" + response.errorBody().string());
//                            ^ this doesn't appear on the toast for some reason when the mail is registered
                            FancyToast.makeText(getActivity(), "Mail is already registered", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            error.accept(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                error.accept(true);
                FancyToast.makeText(getContext(), t.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
        });

    }

    private ActivityResultLauncher<Intent> imageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            uri = result.getData().getData();
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            binding.profile.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void autoFill() {
        binding.firstnameTXT.setText("gab");
        binding.lastnameTXT.setText("lastname");
        binding.emailTXT.setText("gab1@mail.com");
        binding.passwordTXT.setText("12345678");
        binding.confirmPassTXT.setText("12345678");
        binding.contactTXT.setText("809-242-2323");
        binding.rolSPINNER.setSelection(0);
        binding.birthdayDATE.updateDate(1999, 11, 17);

    }

    public void goToLogin(){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.parentFragmentMain, new LogInFragment())
                .addToBackStack(null)
                .commit();
    }

}