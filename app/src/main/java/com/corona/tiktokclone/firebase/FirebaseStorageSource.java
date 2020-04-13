package com.corona.tiktokclone.firebase;

import android.content.Context;
import android.net.Uri;

import com.corona.tiktokclone.utils.UtilsFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class FirebaseStorageSource {
    private Context context;
    private StorageReference mStorageRef;

    public FirebaseStorageSource(Context context) {
        this.context = context;
        if (mStorageRef == null)
            mStorageRef = FirebaseStorage.getInstance().getReference("images/");

    }
    public void uploadVideo(String folderName, String imageUrl, final Interfaces.OnVideoUploaded onVideoUploaded) {
        Uri uri = Uri.parse(imageUrl);
        StorageReference uploadImagePath = mStorageRef.child(folderName + "/" + System.currentTimeMillis());
        byte[] finalImage;
        try {
            finalImage = UtilsFunctions.getByteArray(context,uri);
            UploadTask uploadTask = uploadImagePath.putBytes(finalImage);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                uploadImagePath.getDownloadUrl().addOnSuccessListener(uri1 -> {
                    onVideoUploaded.onSuccess(String.valueOf(uri1));
                });
            }).addOnFailureListener(e -> onVideoUploaded.onError(e.getMessage()));
        } catch (IOException e) {
            onVideoUploaded.onError(e.getMessage());
            e.printStackTrace();
        }



    }
}
