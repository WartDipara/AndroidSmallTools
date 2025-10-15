package com.tools.easytools.wartdipara.EasyBoostBall;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BoostBall extends View {
    private Paint mPaintWave; // 波浪画笔
    private Paint mPaintProgress; // 进度条画笔
    private float mItemWaveLength; // 波浪的长度
    private float mRadius; // 圆的半径
    private float mWaveHeight; // 波浪的高度
    private float mProgressTextSize; // 进度文字的大小
    private Path mWavePath; // 波浪路径（不透明）
    private Path mWaveAlphaPath; // 波浪透明路径 (半透明）
    private Path mCirclePath; // 圆球路径
    private float mWaveBias = 0; // 波浪的偏移量
    private float mProgress = 0; // 当前进度
    private Paint.FontMetricsInt mFontMetrics; // 文字的测量
    private ObjectAnimator mWaveAnimator; // 波浪动画
    private float haloRadius = 0f; // 光晕
    private int haloColor = Color.WHITE; // 光晕颜色
    private int haloAlpha = 0; // 光晕透明度
    private ObjectAnimator haloAnimator; // 光晕动画

    // 触摸拖拽相关
    private float lastX;
    private float lastY;
    private float downX;
    private float downY;
    private static final int DRAG_THRESHOLD = 10; // 拖拽阈值
    private boolean isDragging = false;
    private static final float HALO_OFFSET = 20f; // 光圈可超出圆圈的距离


    public BoostBall(Context context) {
        this(context, null);
    }

    public BoostBall(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoostBall(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null); // 关闭硬件加速
        mPaintWave = new Paint(Paint.ANTI_ALIAS_FLAG); // 波浪画笔
        mPaintWave.setStyle(Paint.Style.FILL);
        mPaintWave.setColor(0x8800ffff); // 半透明青色

        mPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG); // 进度条画笔
        mPaintProgress.setStrokeWidth(10); // 进度条宽度
        mPaintProgress.setStrokeCap(Paint.Cap.ROUND); // 进度条两端为圆角
        mPaintProgress.setStrokeJoin(Paint.Join.ROUND); // 进度条连接处为圆角
        mPaintProgress.setColor(Color.WHITE); // 白色
        mPaintProgress.setTextAlign(Paint.Align.CENTER); // 文字居中

        mWavePath = new Path(); // 波浪路径
        mWaveAlphaPath = new Path(); // 波浪透明路径
        mCirclePath = new Path(); // 圆球路径
    }

    // 测量宽度
    private int measureWidth(int widthMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200; // 默认宽度
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    // 测量高度
    private int measureHeight(int heightMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200; // 默认高度
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }


    // 设置波浪偏移量，透过反射机制被propertyName锁定
    public void setWaveBias(float waveBias) {
        mWaveBias = waveBias;
        invalidate(); // 刷新
    }

    private void startWaveAnimation() {
        mWaveAnimator = ObjectAnimator.ofFloat(this, "waveBias", 0, mItemWaveLength);
        mWaveAnimator.setDuration(4000);
        mWaveAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mWaveAnimator.setInterpolator(new LinearInterpolator());
        mWaveAnimator.start();
    }

    /**
     * 当View的尺寸发生变化时调用
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (float) (Math.min(w, h) * 0.8 / 2);
        mItemWaveLength = mRadius * 2; // 一段波浪长度
        mCirclePath.addCircle(w / 2, h / 2, mRadius, Path.Direction.CW); // 圆球路径
        mProgressTextSize = mRadius * 0.6f; // 进度文字大小
        mPaintProgress.setTextSize(mProgressTextSize); // 设置进度文字大小
        mFontMetrics = mPaintProgress.getFontMetricsInt(); // 获取文字测量
        mWaveHeight = mRadius / 10; // 波浪高度
        invalidate(); // 刷新
        startWaveAnimation(); // 开始波浪动画
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.clipPath(mCirclePath); // 裁剪成圆形
        canvas.drawColor(Color.parseColor("#FF4081")); // 绘制背景色
        mWavePath.reset();
        mWaveAlphaPath.reset();

        float baseX = getWidth() / 2 - mRadius - mItemWaveLength + mWaveBias;
        float baseY = getHeight() / 2 + mRadius + mWaveHeight - mProgress * (mRadius * 2 + mWaveHeight * 2);

        mWavePath.moveTo(baseX, baseY);
        mWaveAlphaPath.moveTo(baseX + mItemWaveLength / 8, baseY);

        float half = mItemWaveLength / 4;
        for (float x = -mItemWaveLength; x < getWidth() + mItemWaveLength; x += mItemWaveLength) {
            // 贝塞尔曲线绘制波浪
            mWavePath.rQuadTo(half / 2, -mWaveHeight, half, 0);
            mWavePath.rQuadTo(half / 2, mWaveHeight, half, 0);

            mWaveAlphaPath.rQuadTo(half / 2, -mWaveHeight, half, 0);
            mWaveAlphaPath.rQuadTo(half / 2, mWaveHeight, half, 0);
        }

        mWavePath.lineTo(getWidth(), getHeight());
        mWavePath.lineTo(0, getHeight());
        mWavePath.close(); // 闭合路径

        mWaveAlphaPath.lineTo(getWidth(), getHeight());
        mWaveAlphaPath.lineTo(0, getHeight());
        mWaveAlphaPath.close(); // 闭合路径

        mPaintWave.setColor(0x8800ffff); // 设置后面的波浪为半透明青色
        canvas.drawPath(mWaveAlphaPath, mPaintWave); // 绘制不透明波浪

        mPaintWave.setColor(0xff00ffff); // 设置前面的波浪为不透明青色
        canvas.drawPath(mWavePath, mPaintWave); // 绘制半透明

        canvas.drawText((int) (mProgress * 100) + "%", getWidth() / 2, getHeight() / 2 + ((mFontMetrics.bottom - mFontMetrics.top) / 2 - mFontMetrics.bottom), mPaintProgress);
        canvas.restore(); // 恢复画布

        // 光圈绘制：允许超出圆圈一点点
        if (haloAlpha > 0 && haloRadius <= HALO_OFFSET + mRadius) {
            Paint haloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            haloPaint.setStyle(Paint.Style.STROKE);
            haloPaint.setColor(haloColor);
            haloPaint.setStrokeWidth(10);
            haloPaint.setAlpha(haloAlpha);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius + haloRadius, haloPaint);
        }
    }

    private void showHalo() {
        if (haloAnimator != null) haloAnimator.cancel();
        haloRadius = 0f;
        haloAlpha = 180;
        float maxRadius = HALO_OFFSET + mRadius;
        haloAnimator = ObjectAnimator.ofFloat(this, "haloRadius", 0f, maxRadius);
        haloAnimator.setDuration(800);
        haloAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            haloAlpha = 180 - (int) (value * (180 / maxRadius)); // 渐隐
            invalidate();
        });
        haloAnimator.start();
    }

    public void setHaloRadius(float r) {
        haloRadius = r;
        invalidate();
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(float progress) {
        mProgress = progress;
        invalidate();
        if (progress >= 1f) {
            showHalo();
        }
    }

    /**
     * 设置进度并带动画
     *
     * @param progress
     */
    public void setProgressWithAnimation(float progress) {
        ObjectAnimator.ofFloat(this, "progress", 0, progress).setDuration(5000).start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mWaveAnimator != null) {
            mWaveAnimator.cancel();
        }
    }

    /**
     * 处理触摸事件，实现拖拽效果
     *
     * @param event The motion event.
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 仅在触摸点在圆内时响应拖拽，放在actiondown里面可以避免拖动过快造成来不及判定，形成的拖动卡顿问题
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
                float dist = (float) Math.hypot(x - cx, y - cy);
                if (dist > mRadius) {
                    isDragging = false;
                    return false;
                }
                downX = lastX = event.getRawX();
                downY = lastY = event.getRawY();
                isDragging = true;
                return true;
            }
            case MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    float dx = event.getRawX() - lastX;
                    float dy = event.getRawY() - lastY;
                    if (Math.abs(event.getRawX() - downX) > DRAG_THRESHOLD || Math.abs(event.getRawY() - downY) > DRAG_THRESHOLD) {
                        float newX = getX() + dx;
                        float newY = getY() + dy;
                        int parentWidth = ((View) getParent()).getWidth();
                        int parentHeight = ((View) getParent()).getHeight();
                        newX = Math.max(0, Math.min(newX, parentWidth - getWidth()));
                        newY = Math.max(0, Math.min(newY, parentHeight - getHeight()));
                        setX(newX);
                        setY(newY);
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                    }
                }
                return isDragging;
            }
            case MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    isDragging = false;
                    float totalDx = Math.abs(event.getRawX() - downX);
                    float totalDy = Math.abs(event.getRawY() - downY);
                    if (totalDx < DRAG_THRESHOLD && totalDy < DRAG_THRESHOLD) {
                        performClick();
                    }
                    return true;
                }
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        // 回调到MainActivity的OnClickListener
        return true;
    }

}
