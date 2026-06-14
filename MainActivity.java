package com.example.musicplayerexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicplayerexam.player.MusicService;
import com.example.musicplayerexam.ui.adapters.MainPagerAdapter;
import com.example.musicplayerexam.ui.fragments.NowPlayingFragment;
import com.example.musicplayerexam.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private View miniPlayer;
    private TextView miniTitle, miniArtist;
    private ImageButton miniPlayPause, miniNext;
    private FloatingActionButton fabSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadSavedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Запускаем foreground-сервис
        startService(new Intent(this, MusicService.class));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
        ).attach();

        miniPlayer = findViewById(R.id.miniPlayer);
        miniTitle = miniPlayer.findViewById(R.id.miniTitle);
        miniArtist = miniPlayer.findViewById(R.id.miniArtist);
        miniPlayPause = miniPlayer.findViewById(R.id.miniPlayPause);
        miniNext = miniPlayer.findViewById(R.id.miniNext);
        fabSettings = findViewById(R.id.fabSettings);

        viewModel.getCurrentTrack().observe(this, track -> {
            if (track != null) {
                miniPlayer.setVisibility(View.VISIBLE);
                miniTitle.setText(track.getTitle());
                miniArtist.setText(track.getArtist());
            } else {
                miniPlayer.setVisibility(View.GONE);
            }
        });

        viewModel.isPlaying().observe(this, isPlaying -> {
            miniPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        });

        miniPlayPause.setOnClickListener(v -> viewModel.playPause());
        miniNext.setOnClickListener(v -> viewModel.next());
        miniPlayer.setOnTouchListener((v, e) -> true); // блокируем свайп

        miniPlayer.setOnClickListener(v -> {
            NowPlayingFragment fragment = new NowPlayingFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
            viewPager.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
        });

        fabSettings.setOnClickListener(v -> showThemeDialog());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    findViewById(R.id.viewPager).setVisibility(View.VISIBLE);
                    findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
                } else {
                    finish();
                }
            }
        });
    }

    private void loadSavedTheme() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        String theme = prefs.getString("theme", "dark");
        switch (theme) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void showThemeDialog() {
        String[] themes = {"Светлая", "Тёмная", "Системная"};
        new AlertDialog.Builder(this)
                .setTitle("Выберите тему")
                .setItems(themes, (dialog, which) -> {
                    SharedPreferences.Editor editor = getSharedPreferences("app_settings", MODE_PRIVATE).edit();
                    int nightMode;
                    String modeStr;
                    switch (which) {
                        case 0:
                            modeStr = "light";
                            nightMode = AppCompatDelegate.MODE_NIGHT_NO;
                            break;
                        case 1:
                            modeStr = "dark";
                            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
                            break;
                        default:
                            modeStr = "auto";
                            nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    }
                    editor.putString("theme", modeStr);
                    editor.apply();
                    AppCompatDelegate.setDefaultNightMode(nightMode);
                    recreate();
                })
                .show();
    }
}