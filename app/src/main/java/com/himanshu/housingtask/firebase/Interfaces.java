package com.himanshu.housingtask.firebase;

import java.util.List;

public class Interfaces {

    public interface OnVideoUploaded{
        void onSuccess(String url);
        void onError(String error);
    }

    public interface OnUploadResult{
        void onSuccess();
        void onError(String error);
    }

    public interface OnGetUploadedVideosList{
        void onGetUrls(List<String> urls);
    }
}
