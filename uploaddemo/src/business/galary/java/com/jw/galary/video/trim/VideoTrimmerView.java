package com.jw.galary.video.trim;

import android.animation.ValueAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.*;import com.jw.uploaddemo.R;
import iknow.android.utils.thread.BackgroundExecutor;
import iknow.android.utils.thread.UiThreadExecutor;

import static com.jw.galary.video.trim.VideoTrimmerUtil.VIDEO_FRAMES_WIDTH;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoTrimmerView extends FrameLayout implements IVideoTrimmerView {

  private static final String TAG = VideoTrimmerView.class.getSimpleName();

  private int mMaxWidth = VIDEO_FRAMES_WIDTH;
  private Context mContext;
  private FrameLayout mLinearVideo;
  private ZVideoView mVideoView;
  private ImageView mPlayView;
  private RecyclerView mVideoThumbRecyclerView;
  private RangeSeekBarView mRangeSeekBarView;
  private LinearLayout mSeekBarLayout;
  private ImageView mRedProgressIcon;
  private TextView mVideoShootTipTv;
  private TextView mTvReset;
  private float mAverageMsPx;//每毫秒所占的px
  private float averagePxMs;//每px所占用的ms毫秒
  private Uri mSourceUri;
  private VideoTrimListener mOnTrimVideoListener;
  private int mDuration = 0;
  private VideoTrimmerAdapter mVideoThumbAdapter;
  private boolean isFromRestore = false;
  //new
  private long mLeftProgressPos, mRightProgressPos;
  private long mRedProgressBarPos = 0;
  private long scrollPos = 0;
  private int mScaledTouchSlop;
  private int lastScrollX;
  private boolean isSeeking;
  private boolean isOverScaledTouchSlop;
  private int mThumbsTotalCount;
  private ValueAnimator mRedProgressAnimator;
  private Handler mAnimationHandler = new Handler();

  public VideoTrimmerView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VideoTrimmerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    this.mContext = context;
    LayoutInflater.from(context).inflate(R.layout.video_trimmer_view, this, true);

    mLinearVideo = findViewById(R.id.layout_surface_view);
    mVideoView = findViewById(R.id.video_loader);
    mPlayView = findViewById(R.id.icon_video_play);
    mSeekBarLayout = findViewById(R.id.seekBarLayout);
    mRedProgressIcon = findViewById(R.id.positionIcon);
    mVideoShootTipTv = findViewById(R.id.video_shoot_tip);
    mTvReset = findViewById(R.id.tv_reset);
    mVideoThumbRecyclerView = findViewById(R.id.video_frames_recyclerView);
    mVideoThumbRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
    mVideoThumbAdapter = new VideoTrimmerAdapter(mContext);
    mVideoThumbRecyclerView.setAdapter(mVideoThumbAdapter);
    mVideoThumbRecyclerView.addOnScrollListener(mOnScrollListener);
    setUpListeners();
  }

  private void initRangeSeekBarView() {
    if(mRangeSeekBarView != null) return;
    int rangeWidth;
    mLeftProgressPos = 0;
    if (mDuration <= VideoTrimmerUtil.MAX_SHOOT_DURATION) {
      mThumbsTotalCount = VideoTrimmerUtil.MAX_COUNT_RANGE;
      rangeWidth = mMaxWidth;
      mRightProgressPos = mDuration;
    } else {
      mThumbsTotalCount = (int) (mDuration * 1.0f / (VideoTrimmerUtil.MAX_SHOOT_DURATION * 1.0f) * VideoTrimmerUtil.MAX_COUNT_RANGE);
      rangeWidth = mMaxWidth / VideoTrimmerUtil.MAX_COUNT_RANGE * mThumbsTotalCount;
      mRightProgressPos = VideoTrimmerUtil.MAX_SHOOT_DURATION;
    }
    mVideoThumbRecyclerView.addItemDecoration(new SpacesItemDecoration2(VideoTrimmerUtil.RECYCLER_VIEW_PADDING, mThumbsTotalCount));
    mRangeSeekBarView = new RangeSeekBarView(mContext, mLeftProgressPos, mRightProgressPos);
    mRangeSeekBarView.setSelectedMinValue(mLeftProgressPos);
    mRangeSeekBarView.setSelectedMaxValue(mRightProgressPos);
    mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
    mRangeSeekBarView.setMinShootTime(VideoTrimmerUtil.MIN_SHOOT_DURATION);
    mRangeSeekBarView.setNotifyWhileDragging(true);
    mRangeSeekBarView.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener);
    mSeekBarLayout.addView(mRangeSeekBarView);

    mAverageMsPx = mDuration * 1.0f / rangeWidth * 1.0f;
    averagePxMs = (mMaxWidth * 1.0f / (mRightProgressPos - mLeftProgressPos));
  }

  public void initVideoByURI(final Uri videoURI) {
    mSourceUri = videoURI;
    mVideoView.setVideoURI(videoURI);
    mVideoView.requestFocus();
  }

  private void startShootVideoThumbs(final Context context, final Uri videoUri, int totalThumbsCount, long startPosition, long endPosition) {
    VideoTrimmerUtil.shootVideoThumbInBackground(context, videoUri, totalThumbsCount, startPosition, endPosition,
            (bitmap, interval) -> {
              if (bitmap != null) {
                UiThreadExecutor.runTask("", () -> mVideoThumbAdapter.addBitmaps(bitmap), 0L);
              }
            });
  }

  public void onCancelClicked() {
    mOnTrimVideoListener.onCancel();
  }

  private void videoPrepared(MediaPlayer mp) {
    ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
    int videoWidth = mp.getVideoWidth();
    int videoHeight = mp.getVideoHeight();

    float videoProportion = (float) videoWidth / (float) videoHeight;
    int screenWidth = mLinearVideo.getWidth();
    int screenHeight = mLinearVideo.getHeight();

    if (videoHeight > videoWidth) {
      lp.width = screenWidth;
      lp.height = screenHeight;
    } else {
      lp.width = screenWidth;
      float r = videoHeight / (float) videoWidth;
      lp.height = (int) (lp.width * r);
    }
    mVideoView.setLayoutParams(lp);
    mDuration = mVideoView.getDuration();
    mVideoShootTipTv.setText(String.format(mContext.getResources().getString(R.string.video_shoot_tip), mDuration/1000));
    if (!getRestoreState()) {
      seekTo((int) mRedProgressBarPos);
    } else {
      setRestoreState(false);
      seekTo((int) mRedProgressBarPos);
    }
    initRangeSeekBarView();
    startShootVideoThumbs(mContext, mSourceUri, mThumbsTotalCount, 0, mDuration);
  }

  private void videoCompleted() {
    seekTo(mLeftProgressPos);
    setPlayPauseViewIcon(false);
  }

  private void onVideoReset() {
    mVideoView.pause();
    setPlayPauseViewIcon(false);
  }

  private void playVideoOrPause() {
    mRedProgressBarPos = mVideoView.getCurrentPosition();
    if (mVideoView.isPlaying()) {
      mVideoView.pause();
      pauseRedProgressAnimation();
    } else {
      mVideoView.start();
      playingRedProgressAnimation();
    }
    setPlayPauseViewIcon(mVideoView.isPlaying());
  }

  public void onVideoPause() {
    if (mVideoView.isPlaying()) {
      seekTo(mLeftProgressPos);//复位
      mVideoView.pause();
      setPlayPauseViewIcon(false);
      mRedProgressIcon.setVisibility(GONE);
    }
  }

  public void setOnTrimVideoListener(VideoTrimListener onTrimVideoListener) {
    mOnTrimVideoListener = onTrimVideoListener;
  }

  private void setUpListeners() {
    mVideoView.setOnPreparedListener(mp -> {
      mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
      videoPrepared(mp);
    });
    mVideoView.setOnCompletionListener(mp -> videoCompleted());
    mPlayView.setOnClickListener(v -> playVideoOrPause());
    mTvReset.setOnClickListener(v -> init(getContext()));
  }

  public void onSaveClicked() {
    if (mRightProgressPos - mLeftProgressPos < VideoTrimmerUtil.MIN_SHOOT_DURATION) {
      Toast.makeText(mContext, "视频长不足3秒,无法上传", Toast.LENGTH_SHORT).show();
    } else {
      mVideoView.pause();
      VideoTrimmerUtil.trim(mContext,
          mSourceUri.getPath(),
          StorageUtil.getCacheDir(),
          mLeftProgressPos,
          mRightProgressPos,
          mOnTrimVideoListener);
    }
  }

  private void seekTo(long msec) {
    mVideoView.seekTo((int) msec);
    Log.d(TAG, "seekTo = " + msec);
  }

  private boolean getRestoreState() {
    return isFromRestore;
  }

  public void setRestoreState(boolean fromRestore) {
    isFromRestore = fromRestore;
  }

  private void setPlayPauseViewIcon(boolean isPlaying) {
    mPlayView.setImageResource(isPlaying ? R.drawable.ic_video_pause_black : R.drawable.ic_video_play_black);
  }

  private final RangeSeekBarView.OnRangeSeekBarChangeListener mOnRangeSeekBarChangeListener = new RangeSeekBarView.OnRangeSeekBarChangeListener() {
    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBarView bar, long minValue, long maxValue, int action, boolean isMin,
                                            RangeSeekBarView.Thumb pressedThumb) {
      Log.d(TAG, "-----minValue----->>>>>>" + minValue);
      Log.d(TAG, "-----maxValue----->>>>>>" + maxValue);
      mLeftProgressPos = minValue + scrollPos;
      mRedProgressBarPos = mLeftProgressPos;
      mRightProgressPos = maxValue + scrollPos;
      mVideoShootTipTv.setText("裁剪后时长"+(mRightProgressPos-mLeftProgressPos)/1000+"秒");
      Log.d(TAG, "-----mLeftProgressPos----->>>>>>" + mLeftProgressPos);
      Log.d(TAG, "-----mRightProgressPos----->>>>>>" + mRightProgressPos);
      switch (action) {
        case MotionEvent.ACTION_DOWN:
          isSeeking = false;
          break;
        case MotionEvent.ACTION_MOVE:
          isSeeking = true;
          seekTo((int) (pressedThumb == RangeSeekBarView.Thumb.MIN ? mLeftProgressPos : mRightProgressPos));
          break;
        case MotionEvent.ACTION_UP:
          isSeeking = false;
          seekTo((int) mLeftProgressPos);
          break;
        default:
          break;
      }

      mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
    }
  };

  private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
      Log.d(TAG, "newState = " + newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
      isSeeking = false;
      int scrollX = calcScrollXDistance();
      //达不到滑动的距离
      if (Math.abs(lastScrollX - scrollX) < mScaledTouchSlop) {
        isOverScaledTouchSlop = false;
        return;
      }
      isOverScaledTouchSlop = true;
      //初始状态,why ? 因为默认的时候有35dp的空白！
      if (scrollX == -VideoTrimmerUtil.RECYCLER_VIEW_PADDING) {
        scrollPos = 0;
      } else {
        isSeeking = true;
        scrollPos = (long) (mAverageMsPx * (VideoTrimmerUtil.RECYCLER_VIEW_PADDING + scrollX));
        mLeftProgressPos = mRangeSeekBarView.getSelectedMinValue() + scrollPos;
        mRightProgressPos = mRangeSeekBarView.getSelectedMaxValue() + scrollPos;
        Log.d(TAG, "onScrolled >>>> mLeftProgressPos = " + mLeftProgressPos);
        mRedProgressBarPos = mLeftProgressPos;
        if (mVideoView.isPlaying()) {
          mVideoView.pause();
          setPlayPauseViewIcon(false);
        }
        mRedProgressIcon.setVisibility(GONE);
        seekTo(mLeftProgressPos);
        mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
        mRangeSeekBarView.invalidate();
      }
      lastScrollX = scrollX;
    }
  };

  /**
   * 水平滑动了多少px
   */
  private int calcScrollXDistance() {
    LinearLayoutManager layoutManager = (LinearLayoutManager) mVideoThumbRecyclerView.getLayoutManager();
    int position = layoutManager.findFirstVisibleItemPosition();
    View firstVisibleChildView = layoutManager.findViewByPosition(position);
    int itemWidth = firstVisibleChildView.getWidth();
    return (position) * itemWidth - firstVisibleChildView.getLeft();
  }

  private void playingRedProgressAnimation() {
    pauseRedProgressAnimation();
    playingAnimation();
    mAnimationHandler.post(mAnimationRunnable);
  }

  private void playingAnimation() {
    if (mRedProgressIcon.getVisibility() == View.GONE) {
      mRedProgressIcon.setVisibility(View.VISIBLE);
    }
    final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mRedProgressIcon.getLayoutParams();
    int start = (int) (VideoTrimmerUtil.RECYCLER_VIEW_PADDING + (mRedProgressBarPos - scrollPos) * averagePxMs);
    int end = (int) (VideoTrimmerUtil.RECYCLER_VIEW_PADDING + (mRightProgressPos - scrollPos) * averagePxMs);
    mRedProgressAnimator = ValueAnimator.ofInt(start, end).setDuration((mRightProgressPos - scrollPos) - (mRedProgressBarPos - scrollPos));
    mRedProgressAnimator.setInterpolator(new LinearInterpolator());
    mRedProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        params.leftMargin = (int) animation.getAnimatedValue();
        mRedProgressIcon.setLayoutParams(params);
        Log.d(TAG, "----onAnimationUpdate--->>>>>>>" + mRedProgressBarPos);
      }
    });
    mRedProgressAnimator.start();
  }

  private void pauseRedProgressAnimation() {
    mRedProgressIcon.clearAnimation();
    if (mRedProgressAnimator != null && mRedProgressAnimator.isRunning()) {
      mAnimationHandler.removeCallbacks(mAnimationRunnable);
      mRedProgressAnimator.cancel();
    }
  }

  private Runnable mAnimationRunnable = new Runnable() {

    @Override
    public void run() {
      updateVideoProgress();
    }
  };

  private void updateVideoProgress() {
    long currentPosition = mVideoView.getCurrentPosition();
    Log.d(TAG, "updateVideoProgress currentPosition = " + currentPosition);
    if (currentPosition >= (mRightProgressPos)) {
      mRedProgressBarPos = mLeftProgressPos;
      pauseRedProgressAnimation();
      onVideoPause();
    } else {
      mAnimationHandler.post(mAnimationRunnable);
    }
  }

  /**
   * Cancel trim thread execut action when finish
   */
  @Override
  public void onDestroy() {
    BackgroundExecutor.cancelAll("", true);
    UiThreadExecutor.cancelAll("");
  }
}
