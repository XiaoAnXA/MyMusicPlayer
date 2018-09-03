package com.example.xa.musicplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {

    private ArrayList<Song> mSongs;

    public SongAdapter(@NonNull Context context, int resource, @NonNull List<Song> objects) {
        super(context, resource, objects);
        mSongs = (ArrayList<Song>) objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         Song song= mSongs.get(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.main_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.VhTitle = view.findViewById(R.id.main_item_tv_title);
            viewHolder.VhAlbum = view.findViewById(R.id.main_item_tv_album);
            viewHolder.VhDuration = view.findViewById(R.id.main_item_tv_duration);
            viewHolder.VhSinger = view.findViewById(R.id.main_item_tv_Singer);
            viewHolder.VhSize = view.findViewById(R.id.main_item_tv_size);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        //viewHolder的属性的实例化是直接获取了控件的对象
        viewHolder.VhSize.setText("大小："+song.getSize());
        viewHolder.VhSinger.setText("歌手"+song.getSinger());
        int m = song.getDuration()/60000;
        int s = (song.getDuration()-m*60000)/1000;
        viewHolder.VhDuration.setText("时长"+m+"分"+s+"秒");
        viewHolder.VhAlbum.setText("专辑："+song.getAlbum());
        viewHolder.VhTitle.setText("歌曲"+song.getTitle());
        return view;
    }

    @Override
    public int getCount() {
        return mSongs!=null?mSongs.size():0;
    }

    class ViewHolder{
         TextView VhTitle,VhSinger,VhAlbum,VhDuration,VhSize;
    }
}
