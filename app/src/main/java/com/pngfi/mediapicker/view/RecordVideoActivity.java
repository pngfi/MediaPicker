package com.pngfi.mediapicker.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pngfi.mediapicker.R;
import com.pngfi.mediapicker.utils.DialogUtils;

/**
 * Created by lucky-django on 16/3/16.
 * 录视频
 */
public class RecordVideoActivity extends AppCompatActivity
        implements View.OnClickListener, SurfaceHolder.Callback {


    public static final String EXTRA_VIDEO_PATH="extra_video_path";

    private VideoRecorder mVideoRecorder;
    private SurfaceView mPreview;
    private ImageView mRecordImageView;
    private ImageView mSwitchImageView;
    private TextView mTimeTextView;
    private boolean mIsRecording = false;
    private boolean mInited = false;
    private boolean mSurfaceCreated = false;
    private int mSeconds = 20;//最大20秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_record_video);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            DialogUtils.showSingleButtonDialog(RecordVideoActivity.this, getString(R.string.tip),
                    getString(R.string.no_camera), getString(R.string.sure),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RecordVideoActivity.this.finish();
                        }
                    });
        }
        initView();
    }

    private void initView() {
        mPreview = (SurfaceView) findViewById(R.id.surface_view);
        SurfaceHolder holder = mPreview.getHolder();
        holder.addCallback(this);
        //don't need this line since api 11
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSwitchImageView = (ImageView) findViewById(R.id.iv_switch);
        mRecordImageView = (ImageView) findViewById(R.id.iv_record);
        mSwitchImageView.setOnClickListener(this);
        mRecordImageView.setOnClickListener(this);
        mTimeTextView = (TextView) findViewById(R.id.tv_time);
        mTimeTextView.setText(getFormattedTime(mSeconds));
        if (VideoRecorder.hasFrontCamera()) {
            mSwitchImageView.setVisibility(View.VISIBLE);
        }
        mVideoRecorder = new VideoRecorder(this, mPreview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_record:
                if (!mIsRecording) {
                    mSwitchImageView.setVisibility(View.INVISIBLE);
                    mVideoRecorder.startRecording(new VideoRecorder.OnVideoRecordListener() {
                        @Override
                        public void onRecordStart(final String filePath) {
                            mRecordImageView.setImageResource(R.drawable.stop_record);
                            mIsRecording = true;
                        }

                        @Override
                        public void onTimeUpgrade(long timeMills) {
                            mTimeTextView.setText(getFormattedTime(mSeconds - (int) (timeMills / 1000)));
                        }

                        @Override
                        public void onRecordFinished(final String filePath) {
                            mIsRecording = false;
                            mRecordImageView.setImageResource(R.drawable.start_record);
                            DialogUtils.showDoubleButtonDialog(RecordVideoActivity.this, getString(R.string.tip),
                                    getString(R.string.choose_current_video), getString(R.string.sure),
                                    getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.putExtra(EXTRA_VIDEO_PATH, filePath);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            mSwitchImageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onRecordFailed(Exception e) {
                            mTimeTextView.setText(getFormattedTime(mSeconds));
                            mVideoRecorder.releaseRecorder();
                            mIsRecording = false;
                            mSwitchImageView.setVisibility(View.VISIBLE);
                            Toast.makeText(RecordVideoActivity.this.getApplicationContext(),getString(R.string.record_video_error),Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                } else {
                    mVideoRecorder.stopRecording();
                }
                break;
            case R.id.iv_switch:
                mVideoRecorder.switchCamera(new VideoRecorder.OnCameraSwitchListener() {
                    @Override
                    public void onSwitchStart() {
                        mSwitchImageView.setClickable(false);
                    }

                    @Override
                    public void onSwitchFinish() {
                        mSwitchImageView.setClickable(true);
                    }
                });
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceCreated = true;
        if (mVideoRecorder != null && !mInited) {
            mInited = true;
            mVideoRecorder.onSurfaceCreated();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceCreated = false;
        if (mVideoRecorder != null) {
            mInited = false;
            mVideoRecorder.onSurfaceDestroyed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoRecorder != null) {
            mVideoRecorder.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVideoRecorder != null) {
            mVideoRecorder.onStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoRecorder != null) {
            mVideoRecorder.onResume();
            if (!mInited && mSurfaceCreated) {
                mInited = true;
                mVideoRecorder.onSurfaceCreated();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mVideoRecorder.isRecording()) {
            mVideoRecorder.stopRecording();
        } else {
            super.onBackPressed();
        }
    }

    private String getFormattedTime(int leftSeconds) {
        if (leftSeconds >= 0 && leftSeconds <= 9) {
            return "00:0" + leftSeconds;
        } else {
            return "00:" + leftSeconds;
        }
    }
}
