package com.lifestyle.retail_dashboard.customview.newSlideConcept.ios;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.lifestyle.retail_dashboard.R;
import com.lifestyle.retail_dashboard.customview.newSlideConcept.slideToUnlock.SlideLayout;
import com.lifestyle.retail_dashboard.customview.newSlideConcept.slideToUnlock.sliders.Direction;
import com.lifestyle.retail_dashboard.customview.newSlideConcept.slideToUnlock.sliders.HorizontalSlider;


public class IosSlider extends SlideLayout {
    private Paint mTextPaint;

    private int mPaddingRight;
    private Rect mBounds = new Rect();

    private String mTextToBeDrawn;

    LinearGradient[] mShaders;
    int mIndex = 0;

    boolean mActive = false;

    public IosSlider(Context context) {
        super(context);
        init();
    }

    public IosSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IosSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        try {
            View.inflate(getContext(), R.layout.ios_slider, this);
            setBackgroundResource(R.drawable.ios_back);

            setSlider(new HorizontalSlider(Direction.FORWARD));
            setRenderer(new BtnRenderer(this));
            setAllowEventsAfterFinishing(true);
            setChildId(R.id.ios_child);

            mTextToBeDrawn = "Login";

            mTextPaint = new Paint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.ios_text_size));

            mPaddingRight = getResources().getDimensionPixelSize(R.dimen.ios_padding_right);

            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    initShaders();
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }catch (Exception e){
            Log.d("Abrar","Exception: "+e.getLocalizedMessage());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mActive = true;
        postDelayed(mRunnable, 100);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mActive = false;
        removeCallbacks(mRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        try {
            mTextPaint.getTextBounds(mTextToBeDrawn, 0, mTextToBeDrawn.length(), mBounds);
            int startX = getWidth() / 2 - mPaddingRight;
            int startY = (int) (getHeight() - mTextPaint.descent() - mTextPaint.ascent()) / 2;

            assert mShaders != null;
            if (mShaders.length > 0) {
                mTextPaint.setShader(mShaders[mIndex]);
            }
            canvas.drawText(mTextToBeDrawn, startX, startY, mTextPaint);
        }catch (Exception e){
            Log.d("Abrar","Exception: "+e);
        }
    }

    private void initShaders() {
        final int COUNT = 100;
        mShaders = new LinearGradient[COUNT];
        float stepX = getWidth() * 3 / COUNT;
        int[] colors = new int[]{Color.WHITE, Color.GRAY, Color.WHITE};
        for (int i = 0; i < COUNT; i++) {
            mShaders[i] = new LinearGradient(stepX * i, 0, stepX * (i + 5), 0, colors, null, Shader.TileMode.CLAMP);
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mIndex++;
                if (mIndex >= mShaders.length) {
                    mIndex = 0;
                }
                invalidate();
                if (mActive) {
                    postDelayed(mRunnable, 20);
                }
            } catch (Exception e) {
                Log.d("Abrar", e.getLocalizedMessage());
            }
        }
    };

    public void setComponentName(String componentName) {
        mTextToBeDrawn = componentName;
        mTextPaint.getTextBounds(componentName, 0, componentName.length(), mBounds);
    }
}
