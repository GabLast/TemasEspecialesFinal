package edu.pucmm.ecommerceapp.retrofit;

import com.google.gson.JsonObject;
import edu.pucmm.ecommerceapp.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import java.util.List;

public interface UserApiService {
    @GET("users")
    Call<List<User>> getAll();

    @PUT("users")
    Call<User> update(@Body User user);

    @POST("users")
    Call<User> create(@Body User user);

    @PUT("users/change")
    Call<Void> changePassword(@Body User user);

    @POST("users/login")
    Call<User> login(@Body JsonObject user);
}
