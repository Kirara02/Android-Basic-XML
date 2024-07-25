package com.kirara.musikapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kirara.musikapp.model.Song;
import com.kirara.musikapp.util.Formatters;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MusicActivity extends AppCompatActivity {
    private ListView listViewSongs;
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private SeekBar seekBar;
    private TextView currentTime, totalTime;

    private ArrayList<Song> songList;
    private int currentSongIndex = 0;
    private MusicService musicService;
    private boolean isBound = false;

    private Timer timer;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;

            if (musicService.isPlaying()) {
                updateSeekBar();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        listViewSongs = findViewById(R.id.listViewSongs);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        seekBar = findViewById(R.id.seekBar);
        currentTime = findViewById(R.id.tvCurrentTime);
        totalTime = findViewById(R.id.tvTotalTime);

        songList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, 1);
            }
        } else {
            loadSongs();
        }


        btnPlayPause.setOnClickListener(v -> {
            if (musicService.isPlaying()) {
                pauseSong();
            } else {
                if (musicService.getPlayingCurrentPosition() > 0) {
                    resumeSong();
                } else {
                    playSong(songList.get(currentSongIndex).getFilePath());
                }
            }
        });

        btnNext.setOnClickListener(v -> nextSong());

        btnPrevious.setOnClickListener(v -> previousSong());

        listViewSongs.setOnItemClickListener((parent, view, position, id) -> {
            currentSongIndex = position;
            playSong(songList.get(currentSongIndex).getFilePath());
        });

        bindService(new Intent(this, MusicService.class), serviceConnection, BIND_AUTO_CREATE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isBound) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadSongs() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.IS_MUSIC + "!= 0",
                null,
                MediaStore.Audio.Media.TITLE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);
                String path = cursor.getString(pathColumn);
                int duration = cursor.getInt(durationColumn);

                songList.add(new Song(title, artist, path, duration));
            } while (cursor.moveToNext());

            cursor.close();
        }

        MusicAdapter adapter = new MusicAdapter(this, songList);
        listViewSongs.setAdapter(adapter);
    }


    private void playSong(String filePath) {
        if (isBound) {
            if (musicService.isPlaying()) {
                musicService.pauseMusic();
                btnPlayPause.setImageResource(R.drawable.play_circle_outline);
            } else {
                musicService.playMusic(filePath);
                btnPlayPause.setImageResource(R.drawable.pause_circle_outline);
                totalTime.setText(Formatters.formatTime(musicService.getSongDuration())); // Update total time
                updateSeekBar();
            }
        }
    }

    private void resumeSong() {
        if (isBound) {
            musicService.resumeMusic();
            btnPlayPause.setImageResource(R.drawable.pause_circle_outline);
            updateSeekBar();
        }
    }


    private void pauseSong() {
        if (isBound) {
            musicService.pauseMusic();
            btnPlayPause.setImageResource(R.drawable.play_circle_outline);
        }
    }

    private void nextSong() {
        currentSongIndex = (currentSongIndex + 1) % songList.size();
        musicService.pauseMusic(); // Pause current song before starting next
        playSong(songList.get(currentSongIndex).getFilePath());
        seekBar.setProgress(0);
        currentTime.setText(Formatters.formatTime(0));
    }


    private void previousSong() {
        currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
        musicService.pauseMusic(); // Pause current song before starting previous
        playSong(songList.get(currentSongIndex).getFilePath());
        seekBar.setProgress(0);
        currentTime.setText(Formatters.formatTime(0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(this, "Please allow storage permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateSeekBar() {
        if (isBound) {
            seekBar.setMax(musicService.getSongDuration());
            totalTime.setText(Formatters.formatTime(musicService.getSongDuration()));

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> {
                        if (isBound && musicService.isPlaying()) {
                            int currentPos = musicService.getCurrentPosition();
                            seekBar.setProgress(currentPos);
                            currentTime.setText(Formatters.formatTime(currentPos));
                        }
                    });
                }
            }, 0, 1000);
        }
    }
}
