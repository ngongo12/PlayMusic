package com.ps14237;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ps14237.adapter.MainViewPagerAdapter;
import com.ps14237.support.Constants;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    BottomNavigationView bottom_nav;
    MainViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ánh xạ
        viewPager = findViewById(R.id.viewPager);
        bottom_nav = findViewById(R.id.bottom_nav);

        //create channel id
        createNotificationChannel();

        // ViewPagerAdapter
        pagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.item_recent: viewPager.setCurrentItem(0); break;
                    case R.id.item_library: viewPager.setCurrentItem(1); break;
                    case R.id.item_favorite: viewPager.setCurrentItem(2); break;
                    case R.id.item_setting: viewPager.setCurrentItem(3); break;
                }
                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: bottom_nav.setSelectedItemId(R.id.item_recent); break;
                    case 1: bottom_nav.setSelectedItemId(R.id.item_library); break;
                    case 2: bottom_nav.setSelectedItemId(R.id.item_favorite); break;
                    case 3: bottom_nav.setSelectedItemId(R.id.item_setting); break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Upload Song";
            String description = "Upload Song";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}