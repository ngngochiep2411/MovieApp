package com.example.movieapp.widgets;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.movieapp.R;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;


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

    //记住切换数据源类型
    private int mType = 0;

    private int mTransformSize = 0;

    //数据源
    private int mSourcePosition = 0;

    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
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
