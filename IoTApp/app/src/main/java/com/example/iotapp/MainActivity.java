package com.example.iotapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //DEFAULT FRAGMENT
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SettingFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_setting);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SettingFragment()).commit();
                break;
            case R.id.nav_pote:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PoteFragment()).commit();
                break;
            case R.id.nav_temp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TempFragment()).commit();
                break;
            case R.id.nav_hall:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HallFragment()).commit();
                break;
            case R.id.nav_acc:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AccFragment()).commit();
                break;
            case R.id.nav_bright:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new BrightFragment()).commit();
                break;
            case R.id.nav_button:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ButtonFragment()).commit();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
