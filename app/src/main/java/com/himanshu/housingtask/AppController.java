package com.himanshu.housingtask;

import android.app.Application;
import android.os.StrictMode;

import com.himanshu.housingtask.firebase.FirebaseSource;
import com.himanshu.housingtask.firebase.FirebaseStorageSource;
import com.himanshu.housingtask.manager.PermissionManager;

public class AppController extends Application {

    private static AppController instance;


    private FirebaseSource firebaseSource;
    private FirebaseStorageSource firebaseStorageSource;
    private PermissionManager permissionManager;

    public static synchronized AppController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public FirebaseSource getFirebaseSource() {
        if (firebaseSource == null) {
            firebaseSource = new FirebaseSource(getFirebaseStorageSource());
        }
        return firebaseSource;
    }


    public PermissionManager getPermissionManager() {
        if (permissionManager == null) {
            permissionManager = new PermissionManager();
        }
        return permissionManager;
    }

    public FirebaseStorageSource getFirebaseStorageSource() {
        if (firebaseStorageSource == null) {
            firebaseStorageSource = new FirebaseStorageSource(this);
        }
        return firebaseStorageSource;
    }


}
