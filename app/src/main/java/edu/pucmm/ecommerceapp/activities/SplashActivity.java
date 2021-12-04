package edu.pucmm.ecommerceapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import edu.pucmm.ecommerceapp.MainActivity;
import edu.pucmm.ecommerceapp.R;

public class SplashActivity extends AppCompatActivity {

    private final static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String email = "yaroaconpapafrita@gmail.com";
        String password = "contraseniaParaFireBase";


        System.err.println("Testing");
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Log.e("SplashActivity", "signInWithEmailAndPassword:success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.err.println(e);
                        Log.e("SplashActivity", "signInWithEmailAndPassword:failure");
                    }
                });

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();

        }, SPLASH_TIME_OUT);
    }
}
