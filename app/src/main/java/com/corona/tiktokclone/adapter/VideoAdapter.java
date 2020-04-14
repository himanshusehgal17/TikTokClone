package com.corona.tiktokclone.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.corona.tiktokclone.R;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context mContext;
    private ArrayList<String> urls;


    public VideoAdapter(Context mContext, ArrayList<String> urls) {
        this.mContext = mContext;
        this.urls = urls;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.video_layout, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        holder.videoView.setVideoURI(Uri.parse(urls.get(position)));
        holder.videoView.setOnPreparedListener(mp -> {
            holder.videoView.requestFocus();
            holder.videoView.start();
            holder.progressBar.setVisibility(View.GONE);

        });
        holder.videoView.setOnClickListener(v -> {
            if(!holder.videoView.isPlaying()) {
                holder.videoView.start();
            }else holder.videoView.stopPlayback();
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        private VideoView videoView;
        private ProgressBar progressBar;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
