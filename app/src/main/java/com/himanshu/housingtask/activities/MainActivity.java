package com.himanshu.housingtask.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.himanshu.housingtask.AppController;
import com.himanshu.housingtask.R;
import com.himanshu.housingtask.adapter.VideoAdapter;
import com.himanshu.housingtask.base.BaseActivity;
import com.himanshu.housingtask.firebase.FirebaseSource;
import com.himanshu.housingtask.firebase.Interfaces;
import com.himanshu.housingtask.manager.PermissionManager;
import com.himanshu.housingtask.utils.KEYS;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements Interfaces.OnGetUploadedVideosList {

    private FirebaseSource firebaseSource = AppController.getInstance().getFirebaseSource();
    private PermissionManager permissionManager = AppController.getInstance().getPermissionManager();

    private Button button;
    private LinearLayout videoViewLayout;
    private RelativeLayout mainLayout;

    private ArrayList<String> urls;
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        permissionManager.cameraAndStoragePermission(this);

        if (isNetworkAvailable()) {
            showProgressDialog("Loading...");
            firebaseSource.getUploadedVideos(this);
        }
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        button.setOnClickListener(v -> actionIntent());
    }

    private void init() {
        videoViewLayout = findViewById(R.id.videoViewLayout);
        button = findViewById(R.id.record_new_video);
        mainLayout= findViewById(R.id.mainLayut);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        urls = new ArrayList<>();
        adapter = new VideoAdapter(this,urls);

    }

    private void actionIntent() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, KEYS.UPLOAD_NEW_VIDEO);
    }


    private void setVideoLayout(List<String> urls) {

        for (String url: urls) {
            View view = getLayoutInflater().inflate(R.layout.video_layout,null);
            ProgressBar progressBar = view.findViewById(R.id.progressBar);
            VideoView videoView = view.findViewById(R.id.videoView);
            videoView.setVideoURI(Uri.parse(url));
            videoView.setOnPreparedListener(mp -> {
                progressBar.setVisibility(View.GONE);
                videoView.requestFocus();
                videoView.start();
            });
            videoViewLayout.addView(view);
        }

    }

    @Override
    public void onGetUrls(List<String> url) {
        hideProgressDialog();
        urls.clear();
        urls.addAll(url);
        adapter.notifyDataSetChanged();
       // setVideoLayout(urls);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEYS.UPLOAD_NEW_VIDEO && resultCode == RESULT_OK) {
            showProgressDialog("Updating...");
            firebaseSource.getUploadedVideos(this);
        }
    }

}
