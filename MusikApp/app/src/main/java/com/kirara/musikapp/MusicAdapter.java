package com.kirara.musikapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kirara.musikapp.model.Song;
import com.kirara.musikapp.util.Formatters;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends ArrayAdapter<Song> {
    public MusicAdapter(Context context, List<Song> songs){
        super(context, 0, songs);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Song song = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_song, parent, false);
        }

        TextView title = convertView.findViewById(R.id.tvTitle);
        TextView artis = convertView.findViewById(R.id.tvArtist);
        TextView duration = convertView.findViewById(R.id.tvDuration);


        title.setText(song.getTitle());
        artis.setText(song.getArtist());
        duration.setText(Formatters.formatTime(song.getDuration()));

        return convertView;
    }
}
