package com.example.movieapp.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;
import java.util.HashMap;


/**
 * Created by shuyu on 2016/12/7.
 * 注意
 * 这个播放器的demo配置切换到全屏播放器
 * 这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
 */

public class SampleControlVideo extends StandardGSYVideoPlayer {
    private ImageView imgNext;
    private ImageView imgPrevious;
    private ImageView imgForward;
    private ImageView imgReplay;
    private int mType = 0;
    private int mTransformSize = 0;
    private int mSourcePosition = 0;

    private RelativeLayout mPreviewLayout;

    private ImageView mPreView;

    //是否因为用户点击
    private boolean mIsFromUser;

    //是否打开滑动预览
    private boolean mOpenPreView = true;

    private int mPreProgress = -2;

    public SampleControlVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SampleControlVideo(Context context) {
        super(context);
    }

    public SampleControlVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
    }

    private void initView() {
        imgForward = findViewById(R.id.forward);
        imgNext = findViewById(R.id.next);
        imgPrevious = findViewById(R.id.previous);
        imgReplay = findViewById(R.id.replay);
        mPreviewLayout = (RelativeLayout) findViewById(R.id.preview_layout);
        mPreView = (ImageView) findViewById(R.id.preview_image);


        imgForward.setOnClickListener(view -> {
            if (mHadPlay) {
                long current = getCurrentPositionWhenPlaying();
                seekTo(current + 10000);
            }
        });

        imgReplay.setOnClickListener(view -> {
            if (mHadPlay) {
                long current = getCurrentPositionWhenPlaying();
                seekTo(current - 10000);
            }
        });

        imgNext.setOnClickListener(v -> {
            if (videoControlListener != null) {
                videoControlListener.onNextVideo();
            }
        });

        imgPrevious.setOnClickListener(v -> {
            if (videoControlListener != null) {
                videoControlListener.onPreviousVideo();
            }
        });
    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        super.onStartTrackingTouch(seekBar);
//        if (mOpenPreView) {
//            mIsFromUser = true;
//            mPreviewLayout.setVisibility(VISIBLE);
//            mPreProgress = -2;
//        }
//    }


//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        if (mOpenPreView) {
//            if (mPreProgress >= 0) {
//                seekBar.setProgress(mPreProgress);
//            }
//            super.onStopTrackingTouch(seekBar);
//            mIsFromUser = false;
//            mPreviewLayout.setVisibility(GONE);
//        } else {
//            super.onStopTrackingTouch(seekBar);
//        }
//    }


//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        super.onProgressChanged(seekBar, progress, fromUser);
//        if (fromUser && mOpenPreView) {
//            int width = seekBar.getWidth();
//            long time = progress * getDuration() / 100;
//            int offset = (int) (width - (getResources().getDimension(R.dimen.dp150) / 2)) / 100 * progress;
//            Debuger.printfError("***************** " + progress);
//            Debuger.printfError("***************** " + time);
//            showPreView(mOriginUrl, time);
//            Log.d("mOriginUrl", mOriginUrl);
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPreviewLayout.getLayoutParams();
//            layoutParams.leftMargin = offset;
//            //设置帧预览图的显示位置
//            mPreviewLayout.setLayoutParams(layoutParams);
//            if (mHadPlay && mOpenPreView) {
//                mPreProgress = progress;
//            }
//        }
//    }


    private void showPreView(String url, long time) {
        int width = CommonUtil.dip2px(getContext(), 150);
        int height = CommonUtil.dip2px(getContext(), 100);
        Glide.with(getContext().getApplicationContext())
                .setDefaultRequestOptions(
                        new RequestOptions()
                                //这里限制了只从缓存读取
                                .onlyRetrieveFromCache(true)
                                .frame(1000 * time)
                                .override(width, height)
                                .dontAnimate()
                                .centerCrop())
                .load(url)
                .into(mPreView);
    }

    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
        imgNext.setVisibility(View.INVISIBLE);
        imgPrevious.setVisibility(View.INVISIBLE);
        imgReplay.setVisibility(View.INVISIBLE);
        imgForward.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void lockTouchLogic() {
        super.lockTouchLogic();
        updateNextPrevVisibility();
    }

    @Override
    protected void onClickUiToggle(MotionEvent e) {
        super.onClickUiToggle(e);
    }

    @Override
    protected void changeUiToPlayingShow() {
        imgNext.setVisibility(View.VISIBLE);
        imgPrevious.setVisibility(View.VISIBLE);
        imgReplay.setVisibility(View.VISIBLE);
        imgForward.setVisibility(View.VISIBLE);
        super.changeUiToPlayingShow();

    }

    @Override
    protected void changeUiToPlayingClear() {
        imgNext.setVisibility(View.INVISIBLE);
        imgPrevious.setVisibility(View.INVISIBLE);
        imgReplay.setVisibility(View.INVISIBLE);
        imgForward.setVisibility(View.INVISIBLE);
        super.changeUiToPlayingClear();

    }


    private void updateNextPrevVisibility() {
        if (mLockCurScreen) {
            findViewById(R.id.start).setVisibility(View.INVISIBLE);
            imgNext.setVisibility(View.INVISIBLE);
            imgPrevious.setVisibility(View.INVISIBLE);
            imgReplay.setVisibility(View.INVISIBLE);
            imgForward.setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.start).setVisibility(View.VISIBLE);
            imgNext.setVisibility(View.VISIBLE);
            imgPrevious.setVisibility(View.VISIBLE);
            imgReplay.setVisibility(View.VISIBLE);
            imgForward.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 处理显示逻辑
     */
    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
        resolveRotateUI();
    }

    @Override
    public int getLayoutId() {
        return R.layout.sample_video;
    }


    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        SampleControlVideo sampleVideo = (SampleControlVideo) super.startWindowFullscreen(context, actionBar, statusBar);
        sampleVideo.mSourcePosition = mSourcePosition;
        sampleVideo.mType = mType;
        sampleVideo.mTransformSize = mTransformSize;
        //sampleVideo.resolveTransform();
        //sampleVideo.resolveRotateUI();
        //这个播放器的demo配置切换到全屏播放器
        //这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        //比如已旋转角度之类的等等
        //可参考super中的实现
        return sampleVideo;
    }

    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            SampleControlVideo sampleVideo = (SampleControlVideo) gsyVideoPlayer;
            mSourcePosition = sampleVideo.mSourcePosition;
            mType = sampleVideo.mType;
            mTransformSize = sampleVideo.mTransformSize;
        }
    }

    /**
     * 旋转逻辑
     */
    private void resolveRotateUI() {
        if (!mHadPlay) {
            return;
        }
        mTextureView.setRotation(mRotate);
        mTextureView.requestLayout();
    }

    public interface OnVideoControlListener {
        void onNextVideo();

        void onPreviousVideo();
    }

    private OnVideoControlListener videoControlListener;

    public void setVideoControlListener(OnVideoControlListener listener) {
        this.videoControlListener = listener;
    }
}
