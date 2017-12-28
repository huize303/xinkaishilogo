package com.tangyi.view.xinkaishilogo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * Created by tangyi on 7/28/16.
 */
public class LogoProgressView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CircleProgressView";

    //0.8 1.5 0.9 1.0
    // 0.05  0.2  0.6  1.0

    private PaintFlagsDrawFilter paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG);
    private static final int INVALIDATE = 1;

    private int bgColor;
    private int strokeColor;
    private int centerCircleColor;

    private long startTime;
    private long endTime;
    private long frequency = 15;

    private boolean hasFinishOnce;

    private int mCircleLineStrokeWidth = 0;

    private long mDelayTime = 800;
    //animation time
    public long mProgressAnimationTime = 700;

    private float scale_1 = 0.8f;
    private float scale_2 = 1.5f;
    private float scale_3 = 0.9f;
    private float scale_4 = 1.0f;

    private long scale_0_1_time =  50;
    private long scale_1_2_time = 200;
    private long scale_2_3_time = 600;
    private long scale_3_4_time = 1000;

    private long mCircleTime = scale_3_4_time;

    private long mTextTime = 350;

    private int mPadding = 0;

    private final RectF mRectF;
    private final Paint mPaint;

    private Drawable mLogoDrawabel;


    private final Context mContext;
    private SurfaceHolder holder;

    private EndInterface mEndInterface;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(INVALIDATE == msg.what) {

                if(System.currentTimeMillis() > endTime) {
                    if(hasFinishOnce) {
                    } else {
                        drawView();
                        if(mEndInterface != null) {
                            mEndInterface.end();
                        }
                    }
                    hasFinishOnce = true;
                } else {
                    drawView();
                    mHandler.sendEmptyMessageDelayed(INVALIDATE,frequency);
                }
            }
        }
    };

    public LogoProgressView(Context context) {
        this(context,null);
    }

    public LogoProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LogoProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();
        holder = this.getHolder();//get holder

        mCircleLineStrokeWidth = (int)context.getResources().getDimension(R.dimen.animation_stroke_width);
        mPadding = (int)context.getResources().getDimension(R.dimen.xinkaishi_logo_padding);
        bgColor = context.getResources().getColor(R.color.white);
        strokeColor = context.getResources().getColor(R.color.logo_stroke);
        centerCircleColor = context.getResources().getColor(R.color.logo_center_circle);

        Drawable drawable = context.getResources().getDrawable(R.drawable.xinkaishi_logo);
        int width = (int)context.getResources().getDimension(R.dimen.xinkaishi_logo_width);
        int height = (int)(drawable.getIntrinsicHeight() * ((float)width/drawable.getIntrinsicWidth()));
        mLogoDrawabel = ImageUtil.zoomDrawable(context.getResources(),drawable,width,height);

        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    protected synchronized  void drawView() {
        Canvas canvas = holder.lockCanvas();
        if(canvas != null) {
            canvas.setDrawFilter(paintFlagsDrawFilter);
            int width = this.getWidth();
            int height = this.getHeight();

            if (width != height) {
                int min = Math.min(width, height);
                width = min;
                height = min;
            }
            canvas.drawColor(bgColor);

            if(startTime != 0) {
                float progress = 0.0f;
                mPaint.reset();
                mPaint.setAntiAlias(true);
                mPaint.setColor(strokeColor);
                mPaint.setStrokeWidth(mCircleLineStrokeWidth);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                // position
                mRectF.left = mCircleLineStrokeWidth / 2 + mPadding; // 左上角x
                mRectF.top = mCircleLineStrokeWidth / 2 + mPadding; // 左上角y
                mRectF.right = width - mCircleLineStrokeWidth / 2 - mPadding; // 左下角x
                mRectF.bottom = height - mCircleLineStrokeWidth / 2 - mPadding; // 右下角y

                long progressTime = System.currentTimeMillis() - startTime;
                if (progressTime >= 0) {
                    progress = -((float) progressTime / mProgressAnimationTime) * 360;
                }
                if (progress < -360) {
                    canvas.drawArc(mRectF, 0, -360, false, mPaint);
                } else {
                    canvas.drawArc(mRectF, 0, progress, false, mPaint);
                }
            }

            if (startTime != 0) {
                //canvas
                mPaint.reset();
                mPaint.setColor(centerCircleColor);
                mPaint.setStyle(Paint.Style.FILL);
                int center_x = width - mCircleLineStrokeWidth / 2 - mPadding;
                int center_y = height / 2;
                //canvas.translate(center_x,center_y);
                float mCircleRadius = getCircleRadius(mCircleLineStrokeWidth / 2);
                canvas.drawCircle(center_x, center_y, mCircleRadius, mPaint);
            }

            if (startTime != 0) {
                if (mLogoDrawabel != null) {
                    int totalwidth = mLogoDrawabel.getIntrinsicWidth();
                    long currentTime = System.currentTimeMillis() - startTime - mProgressAnimationTime;
                    int top = (height - mLogoDrawabel.getIntrinsicHeight()) / 2;
                    int right = width - mCircleLineStrokeWidth - mPadding;
                    int button = (height + mLogoDrawabel.getIntrinsicHeight()) / 2;
                    if (currentTime <= 0) {

                    } else if (currentTime > 0 && currentTime <= mTextTime) {
                        float scale = (float) (mTextTime - currentTime) / mTextTime;
                        int left = width - mCircleLineStrokeWidth - mPadding - ((int) (totalwidth - totalwidth * scale));
                        mLogoDrawabel.setBounds(left, top, right, button);
                        mLogoDrawabel.draw(canvas);
                    } else if (currentTime > mTextTime) {
                        int left = width - mCircleLineStrokeWidth - mPadding - totalwidth;
                        mLogoDrawabel.setBounds(left, top, right, button);
                        mLogoDrawabel.draw(canvas);
                    }
                }
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public  void startAnimation(EndInterface endInterface) {
        this.mEndInterface = endInterface;
        startTime = System.currentTimeMillis() + mDelayTime;
        endTime = startTime + mProgressAnimationTime + mCircleTime;
        mHandler.sendEmptyMessageDelayed(INVALIDATE,frequency);
    }

    public void stopAnimation() {
        mHandler.removeMessages(INVALIDATE);
        resetResource();
    }

    private void resetResource() {
        startTime = 0;
        endTime = 0;
        mEndInterface = null;
        hasFinishOnce = false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        return super.onDragEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public interface EndInterface {
        public void end();
    }

    public float getCircleRadius(float nomalRadius) {
        if(startTime != 0) {
            long currentTime = System.currentTimeMillis()-startTime - mProgressAnimationTime;
            if(currentTime <=0 ) {
                return 0;
            } else if(currentTime >0 && currentTime <=scale_0_1_time) {
                float scale = scale_1 * ((float) currentTime/scale_0_1_time);
                return scale* nomalRadius;
            } else if(currentTime > scale_0_1_time && currentTime <= scale_1_2_time) {
                currentTime = currentTime - scale_0_1_time;
                float scale =scale_1 + (scale_2 - scale_1)* ((float) currentTime/(scale_1_2_time-scale_0_1_time));
                return scale * nomalRadius;
            } else if(currentTime > scale_1_2_time && currentTime <= scale_2_3_time) {
                currentTime = currentTime - scale_1_2_time;
                float scale =scale_2 - (scale_2 - scale_3)* ((float) currentTime/(scale_2_3_time-scale_1_2_time));
                return scale * nomalRadius;
            } else if(currentTime > scale_2_3_time && currentTime <= scale_3_4_time) {
                currentTime = currentTime - scale_2_3_time;
                float scale =scale_3 + (scale_4 - scale_3)* ((float) currentTime/(scale_3_4_time-scale_2_3_time));
                return scale * nomalRadius;
            } else if(currentTime > scale_3_4_time) {
                return  nomalRadius;
            }
        }
        return 0;
    }
}
