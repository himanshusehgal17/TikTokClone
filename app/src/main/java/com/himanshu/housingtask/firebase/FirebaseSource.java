package com.himanshu.housingtask.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;

public class FirebaseSource {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorageSource storageSource;

    public FirebaseSource(FirebaseStorageSource storageSource) {
        this.storageSource = storageSource;
        if (mAuth == null)
            mAuth = FirebaseAuth.getInstance();
        if (firestore == null)
            firestore = FirebaseFirestore.getInstance();
    }


    public void uploadNewVideo(String url, Interfaces.OnUploadResult onUploadResult) {
        HashMap<String,Object> object = new HashMap<>();
        object.put("url",url);
        object.put("timeStamp",System.currentTimeMillis());

        firestore.collection("Videos").document()
                .set(object)
                .addOnCompleteListener(task -> onUploadResult.onSuccess()).addOnFailureListener(e -> {
                    onUploadResult.onError(e.getMessage());
                });
    }
    public void getUploadedVideos(Interfaces.OnGetUploadedVideosList onGetUploadedVideosList) {
        ArrayList<String> urls = new ArrayList<>();
        Query query = firestore.collection("Videos");
        query = query.orderBy("timeStamp", Query.Direction.DESCENDING);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc: queryDocumentSnapshots) {
                 urls.add((String) doc.get("url"));
            }
            onGetUploadedVideosList.onGetUrls(urls);
        });

    }
}
