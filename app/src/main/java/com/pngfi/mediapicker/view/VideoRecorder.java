package com.pngfi.mediapicker.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.pngfi.mediapicker.utils.DialogUtils;
import com.pngfi.mediapicker.utils.FileUtils;
import com.pngfi.mediapicker.utils.ScreenUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lucky-django on 16/3/16.
 * 视频录制器
 */
public class VideoRecorder {

    private static final int MESSAGE_START = 1;
    private static final int MESSAGE_UPDATE = 2;
    private static final int MESSAGE_FINISH = 3;
    private static final int MESSAGE_EXCEPTION = 4;
    public MediaRecorder mRecorder;
    private Activity mActivity;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private File mFile;
    private long mTimeMills;
    private CamcorderProfile mProfile;
    private boolean mUseFrontCamera = false;
    private boolean mIsRecording = false;
    private OnVideoRecordListener mListener;
    private OnCameraSwitchListener mSwitchListener;
    private Timer mTimer;
    private Handler mHandler;
    private MediaScannerConnection mMediaScannerConnection;
    private boolean mMaxFlag = false;
    private Camera.Size mOptimalSize;

    public VideoRecorder(final Activity activity, SurfaceView surfaceView) {
        mActivity = activity;
        mPreview = surfaceView;
        mHolder = mPreview.getHolder();
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_START:
                        mListener.onRecordStart(mFile.getAbsolutePath());
                        break;
                    case MESSAGE_UPDATE:
                        if (mTimeMills > 0) {
                            mListener.onTimeUpgrade(mTimeMills);
                        }
                        break;
                    case MESSAGE_FINISH:
                        mListener.onRecordFinished(mFile.getAbsolutePath());
                        break;
                    case MESSAGE_EXCEPTION:
                        releaseRecorder();
                        releaseCamera();
                        mListener.onRecordFailed((Exception) msg.obj);
                        break;
                }
                return false;
            }
        });
    }

    public static boolean hasFrontCamera() {
        return Camera.getNumberOfCameras() >= 2;
    }

    /**
     * Iterate over supported camera preview sizes to see which one best fits the
     * dimensions of the given view while maintaining the aspect ratio. If none can,
     * be lenient with the aspect ratio.
     *
     * @param sizes Supported camera preview sizes.
     * @param w     The width of the view.
     * @param h     The height of the view.
     * @return Best match camera preview size to fit in the view.
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available preview sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Target view height
        int targetHeight = h;

        // Try to find a preview size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find preview size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private int getOptimalFrameRate(List<int[]> supportedPreviewFpsRange) {
        //默认25
        int frameRate = 25000;
        int minAbsRate = 0;
        for (int i = 0; i < supportedPreviewFpsRange.size(); i++) {
            int[] range = supportedPreviewFpsRange.get(i);
            if (frameRate >= range[0] && frameRate <= range[1]) {
                return frameRate / 1000;
            }
            if (Math.abs(frameRate - range[0]) < Math.abs(frameRate - minAbsRate)) {
                minAbsRate = range[0];
            }
            if (Math.abs(frameRate - range[1]) < Math.abs(frameRate - minAbsRate)) {
                minAbsRate = range[1];
            }
        }
        return minAbsRate / 1000;
    }

    private Camera.Size getMinPreviewSize(List<Camera.Size> sizes) {
        int minHeight = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            if (size.height < minHeight) {
                minHeight = size.height;
                minIndex = i;
            }
        }
        return sizes.get(minIndex);
    }

    private Camera.Size getMidPreviewSize(List<Camera.Size> sizes) {
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            if (size.width == 640 && size.height == 480) {
                return size;
            }
        }
        int mediumIndex = sizes.size() / 2;
        if (mediumIndex >= sizes.size()) {
            mediumIndex = sizes.size() - 1;
        }
        return sizes.get(mediumIndex);
    }

    private int getMinFrameRate(List<int[]> supportedPreviewFpsRange) {
        int minFrameRate = Integer.MAX_VALUE;
        for (int i = 0; i < supportedPreviewFpsRange.size(); i++) {
            int[] range = supportedPreviewFpsRange.get(i);
            if (minFrameRate > range[0]) {
                minFrameRate = range[0];
            }
        }
        return minFrameRate / 1000;
    }

    private int getMaxFrameRate(List<int[]> supportedPreviewFpsRange) {
        int maxFrameRate = -1;
        for (int i = 0; i < supportedPreviewFpsRange.size(); i++) {
            int[] range = supportedPreviewFpsRange.get(i);
            if (maxFrameRate < range[1]) {
                maxFrameRate = range[1];
            }
        }
        return maxFrameRate / 1000;
    }

    private void getCameraInstance() {
        try {
            if (mUseFrontCamera) {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        } catch (Exception e) {
            onException();
        }
    }

    private void prepareRecording() {
        if (mRecorder == null && mCamera == null) {
            getCameraInstance();
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                mProfile = CamcorderProfile.get(mUseFrontCamera ? Camera.CameraInfo.CAMERA_FACING_FRONT
                        : Camera.CameraInfo.CAMERA_FACING_BACK, CamcorderProfile.QUALITY_480P);
                mOptimalSize = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(),
                        ScreenUtil.getScreenPix(mActivity).widthPixels, ScreenUtil.getScreenPix(mActivity).heightPixels);

                parameters.setRecordingHint(true);
                if (parameters.getSupportedFocusModes()
                        .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                parameters.setPreviewSize(mOptimalSize.width, mOptimalSize.height);
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                try {
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    onException();
                }
                prepareRecorder();
            } catch (Exception e) {
                onException();
            }
        }
    }

    public void onSurfaceCreated() {
        prepareRecording();
    }

    private void prepareRecorder() {
        mRecorder = new MediaRecorder();
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mRecorder.setCamera(mCamera);
        mRecorder.setPreviewDisplay(mHolder.getSurface());

        // Step 2: Set sources
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        //Step3: SetArgs
        mRecorder.setOutputFormat(mProfile.fileFormat);
        mRecorder.setVideoFrameRate(mProfile.videoFrameRate);
        mRecorder.setVideoSize(mOptimalSize.width, mOptimalSize.height);
        mRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
        mRecorder.setVideoEncoder(mProfile.videoCodec);
        mRecorder.setAudioEncodingBitRate(mProfile.audioBitRate);
        mRecorder.setAudioChannels(mProfile.audioChannels);
        mRecorder.setAudioSamplingRate(mProfile.audioSampleRate);
        mRecorder.setAudioEncoder(mProfile.audioCodec);
        mRecorder.setMaxFileSize(20 * 1024 * 1024);
        mRecorder.setMaxDuration(20 * 1000);
        if (mUseFrontCamera) {
            mRecorder.setOrientationHint(270);
        } else {
            mRecorder.setOrientationHint(90);
        }
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED
                        || what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                    if (!mMaxFlag) {
                        mMaxFlag = true;
                        mHandler.obtainMessage(MESSAGE_FINISH).sendToTarget();
                        stopHandle(false, true);
                    }
                }
            }
        });
        mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                mHandler.obtainMessage(MESSAGE_EXCEPTION, new Exception("record failed")).sendToTarget();
                stopHandle(false, false);
            }
        });
    }

    public void startRecording(OnVideoRecordListener listener) {
        if (!mIsRecording) {
            mListener = listener;
            if (listener == null) {
                throw new IllegalArgumentException("Recorder listener shouldn't be null!");
            }
            mMaxFlag = false;
            if (mCamera == null) {
                prepareRecording();
            }
            if (mRecorder == null) {
                prepareRecorder();
            }
            mFile = FileUtils.getOutputMediaFile(mActivity, FileUtils.MEDIA_TYPE_VIDEO);
            mRecorder.setOutputFile(mFile.getAbsolutePath());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mRecorder.prepare();
                        mRecorder.start();
                        Message msg = mHandler.obtainMessage();
                        msg.what = MESSAGE_START;
                        mHandler.sendMessage(msg);
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                Message message = mHandler.obtainMessage();
                                message.what = MESSAGE_UPDATE;
                                mTimeMills = mTimeMills + 1000;
                                mHandler.sendMessage(message);
                            }
                        };
                        mTimer = new Timer();
                        mTimer.schedule(timerTask, 1000, 1000);
                        mIsRecording = true;
                    } catch (Exception e) {
                        mHandler.obtainMessage(MESSAGE_EXCEPTION, e).sendToTarget();
                    }
                }
            }).start();
        }
    }

    public void stopRecording() {
        stopHandle(true, true);
    }

    public void switchCamera(OnCameraSwitchListener listener) {
        if (!mIsRecording) {
            mSwitchListener = listener;
            mSwitchListener.onSwitchStart();
            releaseRecorder();
            releaseCamera();
            mUseFrontCamera = !mUseFrontCamera;
            prepareRecording();
            mSwitchListener.onSwitchFinish();
        }
    }

    public void releaseRecorder() {
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
            mCamera.lock();
            mTimeMills = 0;
        }
    }

    public void onPause() {
        if (mIsRecording) {
            stopRecording();
            return;
        }
        releaseRecorder();
    }

    private void releaseCamera() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
        }
    }

    public void onStop() {
        mPreview.setVisibility(View.INVISIBLE);
        releaseCamera();
    }

    public void onResume() {
        mPreview.setVisibility(View.VISIBLE);
    }

    public void onSurfaceDestroyed() {
        releaseRecorder();
        releaseCamera();
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    private void onException() {
        DialogUtils.showSingleButtonDialog(mActivity, "提示", "打开摄像头失败，请稍后重试", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.finish();
                    }
                });
    }

    private void stopHandle(boolean release, boolean scan) {
        if (mIsRecording) {
            try {
                if (mTimer != null) {
                    mTimer.cancel();
                }
                if (mRecorder != null && release) {
                    mRecorder.stop();
                    mListener.onRecordFinished(mFile.getAbsolutePath());
                }
                releaseRecorder();
                if (scan) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mMediaScannerConnection =
                                    new MediaScannerConnection(mActivity.getApplicationContext(),
                                            new MediaScannerConnection.MediaScannerConnectionClient() {

                                                @Override
                                                public void onMediaScannerConnected() {
                                                    mMediaScannerConnection.scanFile(mFile.getAbsolutePath(), "audio/*");
                                                }

                                                @Override
                                                public void onScanCompleted(String path, Uri uri) {
                                                    mMediaScannerConnection.disconnect();
                                                    mMediaScannerConnection = null;
                                                }
                                            });
                            mMediaScannerConnection.connect();
                        }
                    }).start();
                }
            } catch (Exception e) {
                mListener.onRecordFailed(e);
            } finally {
                mRecorder = null;
                mIsRecording = false;
                mTimeMills = 0;
            }
        }
    }

    public interface OnVideoRecordListener {

        /**
         * 录制开始
         *
         * @param filePath 录音文件路径
         */
        public void onRecordStart(final String filePath);

        /**
         * 更新录制时间
         *
         * @param timeMills 当前录音时间
         */
        public void onTimeUpgrade(long timeMills);

        /**
         * 录制结束
         *
         * @param filePath 录音文件路径
         */
        public void onRecordFinished(final String filePath);

        /**
         * 录制失败
         *
         * @param e 抛出的异常
         */
        public void onRecordFailed(Exception e);
    }

    public interface OnCameraSwitchListener {

        public void onSwitchStart();

        public void onSwitchFinish();
    }
}
