package edu.pucmm.ecommerceapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import edu.pucmm.ecommerceapp.MainActivity;
import edu.pucmm.ecommerceapp.R;
import edu.pucmm.ecommerceapp.databinding.ActivityMainMenuBinding;
import androidx.navigation.ui.AppBarConfiguration;
import edu.pucmm.ecommerceapp.helpers.GlobalVariables;
import edu.pucmm.ecommerceapp.networks.Firebase;
import edu.pucmm.ecommerceapp.networks.NetResponse;

public class MainMenu extends AppCompatActivity {

    private static final String TAG = "MainMenu";

    private ActivityMainMenuBinding binding;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.topNavBar.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_category, R.id.nav_product)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.main_menu_fragment);
        //this puts the button to show the left navbar in the top navbar
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(item -> {
            //After logout redirect user to Login Activity
            Intent intent = new Intent(MainMenu.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        });

        View headerLayout = navigationView.getHeaderView(0);
        CircularImageView profile = headerLayout.findViewById(R.id.profile);
        TextView titleMain = headerLayout.findViewById(R.id.left_TXT_View);
        if(GlobalVariables.getUSERSESSION() != null) {
//            System.out.println(GlobalVariables.getUSERSESSION().toString());
            titleMain.setText(GlobalVariables.getUSERSESSION().getFirstName() + " " + GlobalVariables.getUSERSESSION().getLastName());
            if (GlobalVariables.getUSERSESSION().getPhoto() != null && !GlobalVariables.getUSERSESSION().getPhoto().isEmpty()) {
                Firebase.getConstantInstance().download(GlobalVariables.getUSERSESSION().getPhoto(), new NetResponse<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        profile.setImageBitmap(response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, t.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = binding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_top_nav_bar_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_notifications) {
            return true;
        } else if (id == R.id.action_cart) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.main_menu_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}