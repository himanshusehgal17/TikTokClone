package com.himanshu.housingtask.manager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.himanshu.housingtask.utils.KEYS;

import java.util.ArrayList;

public class PermissionManager {

    public boolean cameraAndStoragePermission(Activity activity) {
        int readStorage = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int storage = ContextCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int camera = ContextCompat.checkSelfPermission(activity,Manifest.permission.CAMERA);
        int recordAudio = ContextCompat.checkSelfPermission(activity,Manifest.permission.RECORD_AUDIO);


        ArrayList<String> listPermissionNeeded = new ArrayList<>();

        if(readStorage != PackageManager.PERMISSION_GRANTED){
            listPermissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(storage != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(camera != PackageManager.PERMISSION_GRANTED){
            listPermissionNeeded.add(Manifest.permission.CAMERA);
        }
        if(recordAudio != PackageManager.PERMISSION_GRANTED){
            listPermissionNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        String[] permissionRequest = new String[listPermissionNeeded.size()];
        for(int i = 0 ; i < permissionRequest.length; i++){
            permissionRequest[i] = listPermissionNeeded.get(i);
        }

        if(!listPermissionNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(activity,permissionRequest, KEYS.CAMERA_AND_STORAGE_PERMISSION);
            return false;
        }
        return true;
    }
}
