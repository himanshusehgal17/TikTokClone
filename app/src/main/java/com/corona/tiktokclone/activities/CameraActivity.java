package com.corona.tiktokclone.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.corona.tiktokclone.AppController;
import com.corona.tiktokclone.R;
import com.corona.tiktokclone.base.BaseActivity;
import com.corona.tiktokclone.firebase.FirebaseSource;
import com.corona.tiktokclone.firebase.FirebaseStorageSource;
import com.corona.tiktokclone.firebase.Interfaces;
import com.corona.tiktokclone.manager.PermissionManager;
import com.corona.tiktokclone.utils.CameraPreview;
import com.corona.tiktokclone.utils.KEYS;
import com.corona.tiktokclone.utils.UtilsFunctions;

import java.io.IOException;
import java.util.Objects;

import static com.corona.tiktokclone.utils.KEYS.MEDIA_TYPE_VIDEO;

@SuppressLint("Registered")
public class CameraActivity extends BaseActivity {

    private static final String TAG = "";
    private PermissionManager permissionManager = AppController.getInstance().getPermissionManager();

    private FirebaseStorageSource firebaseStorageSource = AppController.getInstance().getFirebaseStorageSource();
    private FirebaseSource firebaseSource = AppController.getInstance().getFirebaseSource();

    private Camera mCamera;
    private MediaRecorder mediaRecorder;

    private CameraPreview mPreview;

    private boolean isRecording = false;

    private Button captureButton, uploadButton;

    private long currentTimeStamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        permissionManager.cameraAndStoragePermission(this);
        // Create an instance of Camera
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);

        focusOnCamera();


        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        captureButton = findViewById(R.id.button_capture);
        uploadButton = findViewById(R.id.upload_video);

        uploadButton.setOnClickListener(v -> {
            uploadVideoOnServer();
        });

        captureButton.setOnClickListener(
                v -> {
                    if (isRecording) {
                        // stop recording and release camera
                        mediaRecorder.stop();  // stop the recording
                        releaseMediaRecorder(); // release the MediaRecorder object
                        mCamera.lock();         // take camera access back from MediaRecorder
                        focusOnCamera();
                        // inform the user that recording has stopped
                        setCaptureButtonText("Capture");
                        uploadButton.setVisibility(View.VISIBLE);
                        isRecording = false;
                    } else {
                        // initialize video camera
                        if (prepareVideoRecorder()) {
                            // Camera is available and unlocked, MediaRecorder is prepared,
                            // now you can start recording

                            mediaRecorder.start();

                            // inform the user that recording has started
                            setCaptureButtonText("Stop");
                            uploadButton.setVisibility(View.GONE);
                            isRecording = true;
                        } else {
                            // prepare didn't work, release the camera
                            releaseMediaRecorder();
                            // inform user
                        }
                    }
                }
        );
    }

    private void uploadVideoOnServer() {
        if(isNetworkAvailable()) {
            showProgressDialog("Uploading...");
            firebaseStorageSource.uploadVideo(KEYS.DEVICE_ID(this), UtilsFunctions.getOutputMediaFileUri(MEDIA_TYPE_VIDEO, currentTimeStamp).toString(), new Interfaces.OnVideoUploaded() {
                @Override
                public void onSuccess(String url) {
                    firebaseSource.uploadNewVideo(url, new Interfaces.OnUploadResult() {
                        @Override
                        public void onSuccess() {
                            showToast("Video Uploaded");
                            hideProgressDialog();
                            goBackWithData();
                        }

                        @Override
                        public void onError(String error) {
                            showToast(error);
                            hideProgressDialog();
                        }
                    });

                }

                @Override
                public void onError(String error) {
                    showToast(error);
                    hideProgressDialog();
                }
            });
        }else showToast("No network connection. Upload again !!");
    }

    private void goBackWithData() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


    private void focusOnCamera() {
        Camera.Parameters params = mCamera.getParameters();
        // set the focus mode
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        // set Camera parameters
        mCamera.setParameters(params);
    }

    private void setCaptureButtonText(String text) {
        captureButton.setText(text);
    }

    private boolean prepareVideoRecorder() {
        currentTimeStamp = System.currentTimeMillis();
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mediaRecorder = new MediaRecorder();
        focusOnCamera();
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mediaRecorder.setOutputFile(Objects.requireNonNull(UtilsFunctions.getOutputMediaFile(MEDIA_TYPE_VIDEO,currentTimeStamp)).toString());

        // Step 5: Set the preview output
        mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock();// lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


}
