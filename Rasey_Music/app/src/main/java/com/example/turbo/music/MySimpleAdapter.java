package com.example.turbo.music;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by turbo on 2015/9/5.
 */
public class MySimpleAdapter extends SimpleAdapter {
    private LayoutInflater layoutInflater;

    public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,LayoutInflater layoutInflater) {
        super(context, data, resource, from, to);
        this.layoutInflater=layoutInflater;
    }
    @Override
    public int getCount() {
        return PlayActivity.mp3list.size();
    }

    @Override
    public Object getItem(int position) {
        return PlayActivity.mp3list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null)
        {
            convertView=layoutInflater.inflate( R.layout.simple_item,null);
            viewHolder=new ViewHolder();
            viewHolder.title= (TextView) convertView.findViewById(R.id.title);
            viewHolder.header= (ImageView) convertView.findViewById(R.id.musicImage);
            viewHolder.artist= (TextView) convertView.findViewById(R.id.artist);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.header.setImageResource(PlayActivity.mp3Infos.get(position).getIds());
        viewHolder.header.setAdjustViewBounds(true);
        viewHolder.title.setText(PlayActivity.mp3Infos.get(position).getTitle());
        viewHolder.artist.setText(PlayActivity.mp3Infos.get(position).getArtist());
        if(position == PlayActivity.musicNumber && !PlayActivity.if_JustStart){
            viewHolder.title.setTextColor(Color.rgb(237, 28, 36));
        }
        else
        {
            viewHolder.title.setTextColor(Color.BLACK);
        }
        return convertView;
    }

    class ViewHolder{
        public ImageView header;
        public TextView title;
        public TextView artist;
    }
}

