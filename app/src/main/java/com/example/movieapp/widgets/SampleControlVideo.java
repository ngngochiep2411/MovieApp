package com.example.movieapp.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.R;
import com.example.movieapp.ui.detailmovie.ChooseSpeedAdapter;
import com.example.movieapp.ui.detailmovie.OnChangeTypeClick;
import com.example.movieapp.ui.detailmovie.Speed;
import com.example.movieapp.util.SharedViewModel;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


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
    private ImageView imgMenu;
    private TextView seekTime;
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
    private Dialog dialog;
    private SharedViewModel sharedViewModel;
    private SampleControlVideo sampleVideo;
    private Boolean chooseType = true;

    public void setSharedViewModel(SharedViewModel viewModel) {
        this.sharedViewModel = viewModel;
    }

    List<Speed> speedList = Arrays.asList(new Speed("0.5x", false), new Speed("0.75x", false), new Speed("1x", true), new Speed("1.25x", false), new Speed("1.5x", false), new Speed("2x", false));

    List<Speed> typeList;

    public List<Speed> getTypeList() {
        return typeList;
    }

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

    public void setupURL(String url) {
        if (mIfCurrentIsFullscreen) {
            sampleVideo.setUp(url, true, "");
            sampleVideo.startPlayLogic();
        } else {
            setUp(url, true, "");
            startPlayLogic();
        }

    }

    public void setTypeList(List<Speed> typeList) {
        this.typeList = typeList;
    }

    private void initView() {
        imgForward = findViewById(R.id.forward);
        imgNext = findViewById(R.id.next);
        imgPrevious = findViewById(R.id.previous);
        imgReplay = findViewById(R.id.replay);
        mPreviewLayout = (RelativeLayout) findViewById(R.id.preview_layout);
        mPreView = (ImageView) findViewById(R.id.preview_image);
        imgMenu = findViewById(R.id.setting);
        seekTime = findViewById(R.id.seekTime);

        dialog = new Dialog(getContext());

        imgMenu.setOnClickListener(v -> {
            dialog.setContentView(R.layout.layout_setting_dialog);
            dialog.findViewById(R.id.chooseSpeed).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogChooseSpeed();
                }
            });
            dialog.findViewById(R.id.chooseType).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogChooseType();
                }
            });
            dialog.findViewById(R.id.chooseType).setVisibility(chooseType ? View.VISIBLE : View.GONE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.BOTTOM);
            }
            dialog.show();
        });


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


    OnChangeTypeClick onChangeType;

    public void setOnChangeType(OnChangeTypeClick onChangeType) {
        this.onChangeType = onChangeType;
    }

    private void showDialogChooseType() {
        dialog.setContentView(R.layout.layout_choose_speed_dialog);
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Chế độ xem");
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        ChooseSpeedAdapter adapter = new ChooseSpeedAdapter(position -> {
            if (!typeList.get(position).isSelected()) {
                typeList.get(position).setSelected(true);
                for (int i = 0; i < typeList.size(); i++) {
                    if (i != position) {
                        typeList.get(i).setSelected(false);
                    }
                }
                if (position == 0) {
                    onChangeType.onChangeType(SharedViewModel.PlayType.LONG_TIENG);
                } else if (position == 1) {
                    onChangeType.onChangeType(SharedViewModel.PlayType.VIETSUB);
                }
            }
            dialog.dismiss();
            return null;
        });
        recyclerView.setAdapter(adapter);
        adapter.submitList(typeList);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
        dialog.show();
    }


    private void showDialogChooseSpeed() {
        dialog.setContentView(R.layout.layout_choose_speed_dialog);
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Tốc độ phát");
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        ChooseSpeedAdapter adapter = new ChooseSpeedAdapter(position -> {
            speedList.get(position).setSelected(true);
            for (int i = 0; i < speedList.size(); i++) {
                if (i != position) {
                    speedList.get(i).setSelected(false);
                }
            }
            dialog.dismiss();
            String value = speedList.get(position).getValue();
            float speed = Float.parseFloat(value.replace("x", ""));
            setSpeedPlaying(speed, true);
            return null;
        });
        recyclerView.setAdapter(adapter);
        adapter.submitList(speedList);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
        dialog.show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);

        seekTime.setVisibility(View.VISIBLE);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        seekTime.setVisibility(View.GONE);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
        if (fromUser) {
            long duration = getDuration();
            long newPos = duration * progress / 100;
            String currentStr = CommonUtil.stringForTime((int) newPos);
            seekTime.setText(currentStr);
        }
    }


    private void showPreView(String url, long time) {
        int width = CommonUtil.dip2px(getContext(), 150);
        int height = CommonUtil.dip2px(getContext(), 100);
        Glide.with(getContext().getApplicationContext()).setDefaultRequestOptions(new RequestOptions()
                //这里限制了只从缓存读取
                .onlyRetrieveFromCache(true).frame(1000 * time).override(width, height).dontAnimate().centerCrop()).load(url).into(mPreView);
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
        sampleVideo = (SampleControlVideo) super.startWindowFullscreen(context, actionBar, statusBar);
        sampleVideo.mSourcePosition = mSourcePosition;
        sampleVideo.mType = mType;
        sampleVideo.mTransformSize = mTransformSize;
        //sampleVideo.resolveTransform();
        //sampleVideo.resolveRotateUI();
        //这个播放器的demo配置切换到全屏播放器
        //这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        //比如已旋转角度之类的等等
        //可参考super中的实现
        sampleVideo.setOnChangeType(onChangeType);
        sampleVideo.setSharedViewModel(sharedViewModel);
        sampleVideo.setVideoControlListener(videoControlListener);
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

            setOnChangeType(sampleVideo.onChangeType);
            setSharedViewModel(sampleVideo.sharedViewModel);
            setVideoControlListener(sampleVideo.videoControlListener);
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


    public void hideChooseTypeList() {
        chooseType = false;
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
