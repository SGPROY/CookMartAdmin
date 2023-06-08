package com.cookmartadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.cookmartadmin.fragments.HomeFragment;
import com.cookmartadmin.fragments.PedidosFragment;
import com.cookmartadmin.fragments.CodPostalFragment;
import com.cookmartadmin.fragments.ProductosFragment;
import com.cookmartadmin.fragments.AjustesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("admins");
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.apptheme));
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, null,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        TextView navName = findViewById(R.id.nav_bar_nombre);
        TextView navEmail = findViewById(R.id.nav_bar_correo);
        navName.setText(SplashScreen.sharedPreferences.getString("nombre", "Nombre"));
        navEmail.setText(SplashScreen.sharedPreferences.getString("correo", "ejemplo@gmail.com"));
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_main);
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_layout, new CodPostalFragment())
                .commit();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_nav_codPostal:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new CodPostalFragment())
                            .commit();
                    break;

                case R.id.bottom_nav_products:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new ProductosFragment())
                            .commit();
                    break;

                case R.id.bottom_nav_orders:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new PedidosFragment())
                            .commit();
                    break;
                case R.id.bottom_nav_settings:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new AjustesFragment())
                            .commit();
                    break;
                case R.id.bottom_nav_home:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.frame_layout, new HomeFragment())
                            .commit();
                    break;

            }
            return true;
        });
        addNavListener();
    }

    private void addNavListener() {
        findViewById(R.id.nav_bar_home_btn).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new HomeFragment())
                    .commit();
        });
        findViewById(R.id.nav_bar_productos_btn).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new ProductosFragment())
                    .commit();
        });
        findViewById(R.id.nav_bar_codPostal_btn).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new CodPostalFragment())
                    .commit();
        });
        findViewById(R.id.nav_bar_pedidos_btn).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new PedidosFragment())
                    .commit();
        });
        findViewById(R.id.nav_bar_ajustes_btn).setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_layout, new AjustesFragment())
                    .commit();
        });
        findViewById(R.id.nav_bar_logout_btn).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }


    public void openNavDrawer(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}