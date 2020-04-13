package com.corona.tiktokclone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.corona.tiktokclone.AppController;
import com.corona.tiktokclone.R;
import com.corona.tiktokclone.adapter.RecyclerAdapter;
import com.corona.tiktokclone.base.BaseActivity;
import com.corona.tiktokclone.firebase.FirebaseSource;
import com.corona.tiktokclone.firebase.FirebaseStorageSource;
import com.corona.tiktokclone.firebase.Interfaces;
import com.corona.tiktokclone.manager.PermissionManager;
import com.corona.tiktokclone.utils.KEYS;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements Interfaces.OnGetUploadedVideosList {

    private FirebaseSource firebaseSource = AppController.getInstance().getFirebaseSource();
    private PermissionManager permissionManager = AppController.getInstance().getPermissionManager();

    private RecyclerView recyclerView;

    private ArrayList<String> urlData;
    private RecyclerAdapter adapter;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlData =  new ArrayList<>();
        recyclerView = findViewById(R.id.rx);
        button = findViewById(R.id.record_new_video);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new RecyclerAdapter(urlData, MainActivity.this);
        recyclerView.setAdapter(adapter);
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        permissionManager.cameraAndStoragePermission(this);

        if(isNetworkAvailable()) {
            showProgressDialog("Loading...");
            firebaseSource.getUploadedVideos(this);
        }

        button.setOnClickListener(v -> actionIntent());
    }

    private void actionIntent() {
        Intent intent = new Intent(this,CameraActivity.class);
        startActivityForResult(intent, KEYS.UPLOAD_NEW_VIDEO);
    }

    @Override
    public void onGetUrls(List<String> urls) {
        hideProgressDialog();
        urlData.addAll(urls);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == KEYS.UPLOAD_NEW_VIDEO && resultCode == RESULT_OK) {
            showProgressDialog("Updating...");
            firebaseSource.getUploadedVideos(this);
        }
    }
}
