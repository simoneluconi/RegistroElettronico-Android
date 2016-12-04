/*
 * Copyright (C) 2015 Basil Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package devlight.io.library;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by GIGAMOLE on 04.03.2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ArcProgressStackView extends View {

    // Default values
    private final static float DEFAULT_START_ANGLE = 270.0F;
    private final static float DEFAULT_SWEEP_ANGLE = 360.0F;
    private final static float DEFAULT_DRAW_WIDTH_FRACTION = 0.7F;
    private final static float DEFAULT_MODEL_OFFSET = 5.0F;
    private final static float DEFAULT_SHADOW_RADIUS = 30.0F;
    private final static float DEFAULT_SHADOW_DISTANCE = 15.0F;
    private final static float DEFAULT_SHADOW_ANGLE = 90.0F;
    private final static int DEFAULT_ANIMATION_DURATION = 350;
    private final static int DEFAULT_ACTION_MOVE_ANIMATION_DURATION = 150;

    // Max and min progress values
    private final static float MAX_PROGRESS = 100.0F;
    private final static float MIN_PROGRESS = 0.0F;

    // Max and min fraction values
    private final static float MAX_FRACTION = 1.0F;
    private final static float MIN_FRACTION = 0.0F;

    // Max and min end angle
    private final static float MAX_ANGLE = 360.0F;
    private final static float MIN_ANGLE = 0.0F;

    // Min shadow
    private final static float MIN_SHADOW = 0.0F;

    // Action move constants
    private final static float POSITIVE_ANGLE = 90.0F;
    private final static float NEGATIVE_ANGLE = 270.0F;
    private final static int POSITIVE_SLICE = 1;
    private final static int NEGATIVE_SLICE = -1;
    private final static int DEFAULT_SLICE = 0;
    private final static int ANIMATE_ALL_INDEX = -2;
    private final static int DISABLE_ANIMATE_INDEX = -1;

    // Default colors
    private final static int DEFAULT_SHADOW_COLOR = Color.parseColor("#8C000000");

    // Start and end angles
    private float mStartAngle;
    private float mSweepAngle;

    // Progress models
    private List<Model> mModels = new ArrayList<>();

    // Progress and text paints
    private final Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setStyle(Style.STROKE);
        }
    };
    private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setTextAlign(Align.LEFT);
        }
    };
    private final Paint mLevelPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setStyle(Paint.Style.FILL_AND_STROKE);
            setPathEffect(new CornerPathEffect(0.5F));
        }
    };

    // ValueAnimator and interpolator for progress animating
    private final ValueAnimator mProgressAnimator = new ValueAnimator();
    private ValueAnimator.AnimatorListener mAnimatorListener;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;
    private Interpolator mInterpolator;
    private int mAnimationDuration;
    private float mAnimatedFraction;

    // Square size of view
    private int mSize;

    // Offsets for handling and radius of progress models
    private float mProgressModelSize;
    private float mProgressModelOffset;
    private float mDrawWidthFraction;
    private float mDrawWidthDimension;

    // Shadow variables
    private float mShadowRadius;
    private float mShadowDistance;
    private float mShadowAngle;

    // Boolean variables
    private boolean mIsAnimated;
    private boolean mIsShadowed;
    private boolean mIsRounded;
    private boolean mIsDragged;
    private boolean mIsModelBgEnabled;
    private boolean mIsLeveled;

    // Colors
    private int mShadowColor;
    private int mTextColor;
    private int mPreviewModelBgColor;

    // Action move variables
    private int mActionMoveModelIndex = DISABLE_ANIMATE_INDEX;
    private int mActionMoveLastSlice = 0;
    private int mActionMoveSliceCounter;
    private boolean mIsActionMoved;

    // Text typeface
    private Typeface mTypeface;

    // Indicator orientation
    private IndicatorOrientation mIndicatorOrientation;

    // Is >= VERSION_CODES.HONEYCOMB
    private boolean mIsFeaturesAvailable;

    public ArcProgressStackView(final Context context) {
        this(context, null);
    }

    public ArcProgressStackView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressStackView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Init CPSV

        // Always draw
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, null);

        // Detect if features available
        mIsFeaturesAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

        // Retrieve attributes from xml
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressStackView);
        try {
            setIsAnimated(
                    typedArray.getBoolean(R.styleable.ArcProgressStackView_apsv_animated, true)
            );
            setIsShadowed(
                    typedArray.getBoolean(R.styleable.ArcProgressStackView_apsv_shadowed, true)
            );
            setIsRounded(
                    typedArray.getBoolean(R.styleable.ArcProgressStackView_apsv_rounded, false)
            );
            setIsDragged(
                    typedArray.getBoolean(R.styleable.ArcProgressStackView_apsv_dragged, false)
            );
            setIsLeveled(
                    typedArray.getBoolean(R.styleable.ArcProgressStackView_apsv_leveled, false)
            );
            setTypeface(
                    typedArray.getString(R.styleable.ArcProgressStackView_apsv_typeface)
            );
            setTextColor(
                    typedArray.getColor(
                            R.styleable.ArcProgressStackView_apsv_text_color,
                            Color.WHITE
                    )
            );
            setShadowRadius(
                    typedArray.getDimension(
                            R.styleable.ArcProgressStackView_apsv_shadow_radius,
                            DEFAULT_SHADOW_RADIUS
                    )
            );
            setShadowDistance(
                    typedArray.getDimension(
                            R.styleable.ArcProgressStackView_apsv_shadow_distance,
                            DEFAULT_SHADOW_DISTANCE
                    )
            );
            setShadowAngle(
                    typedArray.getInteger(
                            R.styleable.ArcProgressStackView_apsv_shadow_angle,
                            (int) DEFAULT_SHADOW_ANGLE
                    )
            );
            setShadowColor(
                    typedArray.getColor(
                            R.styleable.ArcProgressStackView_apsv_shadow_color,
                            DEFAULT_SHADOW_COLOR
                    )
            );
            setAnimationDuration(
                    typedArray.getInteger(
                            R.styleable.ArcProgressStackView_apsv_animation_duration,
                            DEFAULT_ANIMATION_DURATION
                    )
            );
            setStartAngle(
                    typedArray.getInteger(
                            R.styleable.ArcProgressStackView_apsv_start_angle,
                            (int) DEFAULT_START_ANGLE
                    )
            );
            setSweepAngle(
                    typedArray.getInteger(
                            R.styleable.ArcProgressStackView_apsv_sweep_angle,
                            (int) DEFAULT_SWEEP_ANGLE
                    )
            );
            setProgressModelOffset(
                    typedArray.getDimension(
                            R.styleable.ArcProgressStackView_apsv_model_offset,
                            DEFAULT_MODEL_OFFSET
                    )
            );
            setModelBgEnabled(
                    typedArray.getBoolean(
                            R.styleable.ArcProgressStackView_apsv_model_bg_enabled, false
                    )
            );

            // Set orientation
            final int orientationOrdinal =
                    typedArray.getInt(R.styleable.ArcProgressStackView_apsv_indicator_orientation, 0);
            setIndicatorOrientation(
                    orientationOrdinal == 0 ? IndicatorOrientation.VERTICAL : IndicatorOrientation.HORIZONTAL
            );

            // Retrieve interpolator
            Interpolator interpolator = null;
            try {
                final int interpolatorId = typedArray.getResourceId(
                        R.styleable.ArcProgressStackView_apsv_interpolator, 0
                );
                interpolator = interpolatorId == 0 ? null :
                        AnimationUtils.loadInterpolator(context, interpolatorId);
            } catch (Resources.NotFoundException exception) {
                interpolator = null;
                exception.printStackTrace();
            } finally {
                setInterpolator(interpolator);
            }

            // Set animation info if is available
            if (mIsFeaturesAvailable) {
                mProgressAnimator.setFloatValues(MIN_FRACTION, MAX_FRACTION);
                mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(final ValueAnimator animation) {
                        mAnimatedFraction = (float) animation.getAnimatedValue();
                        if (mAnimatorUpdateListener != null)
                            mAnimatorUpdateListener.onAnimationUpdate(animation);

                        postInvalidate();
                    }
                });
            }

            // Check whether draw width dimension or fraction
            if (typedArray.hasValue(R.styleable.ArcProgressStackView_apsv_draw_width)) {
                final TypedValue drawWidth = new TypedValue();
                typedArray.getValue(R.styleable.ArcProgressStackView_apsv_draw_width, drawWidth);
                if (drawWidth.type == TypedValue.TYPE_DIMENSION)
                    setDrawWidthDimension(
                            drawWidth.getDimension(context.getResources().getDisplayMetrics())
                    );
                else setDrawWidthFraction(drawWidth.getFraction(MAX_FRACTION, MAX_FRACTION));
            } else setDrawWidthFraction(DEFAULT_DRAW_WIDTH_FRACTION);

            // Set preview models
            if (isInEditMode()) {
                String[] preview = null;
                try {
                    final int previewId = typedArray.getResourceId(
                            R.styleable.ArcProgressStackView_apsv_preview_colors, 0
                    );
                    preview = previewId == 0 ? null : typedArray.getResources().getStringArray(previewId);
                } catch (Exception exception) {
                    preview = null;
                    exception.printStackTrace();
                } finally {
                    if (preview == null)
                        preview = typedArray.getResources().getStringArray(R.array.default_preview);

                    final Random random = new Random();
                    for (String previewColor : preview)
                        mModels.add(
                                new Model("", random.nextInt((int) MAX_PROGRESS), Color.parseColor(previewColor))
                        );
                    measure(mSize, mSize);
                }

                // Set preview model bg color
                mPreviewModelBgColor = typedArray.getColor(
                        R.styleable.ArcProgressStackView_apsv_preview_bg,
                        Color.LTGRAY
                );
            }
        } finally {
            typedArray.recycle();
        }
    }

    public ValueAnimator getProgressAnimator() {
        return mProgressAnimator;
    }

    public long getAnimationDuration() {
        return mAnimationDuration;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setAnimationDuration(final long animationDuration) {
        mAnimationDuration = (int) animationDuration;
        mProgressAnimator.setDuration(animationDuration);
    }

    public ValueAnimator.AnimatorListener getAnimatorListener() {
        return mAnimatorListener;
    }

    public void setAnimatorListener(final ValueAnimator.AnimatorListener animatorListener) {
        if (mAnimatorListener != null) mProgressAnimator.removeListener(mAnimatorListener);

        mAnimatorListener = animatorListener;
        mProgressAnimator.addListener(animatorListener);
    }

    public ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener() {
        return mAnimatorUpdateListener;
    }

    public void setAnimatorUpdateListener(final ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        mAnimatorUpdateListener = animatorUpdateListener;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    @SuppressLint("SupportAnnotationUsage")
    @FloatRange
    public void setStartAngle(@FloatRange(from = MIN_ANGLE, to = MAX_ANGLE) final float startAngle) {
        mStartAngle = Math.max(MIN_ANGLE, Math.min(startAngle, MAX_ANGLE));
        postInvalidate();
    }

    public float getSweepAngle() {
        return mSweepAngle;
    }

    @SuppressLint("SupportAnnotationUsage")
    @FloatRange
    public void setSweepAngle(@FloatRange(from = MIN_ANGLE, to = MAX_ANGLE) final float sweepAngle) {
        mSweepAngle = Math.max(MIN_ANGLE, Math.min(sweepAngle, MAX_ANGLE));
        postInvalidate();
    }

    public List<Model> getModels() {
        return mModels;
    }

    public void setModels(final List<Model> models) {
        mModels.clear();
        mModels = models;
        requestLayout();
    }

    public int getSize() {
        return mSize;
    }

    public float getProgressModelSize() {
        return mProgressModelSize;
    }

    public boolean isAnimated() {
        return mIsAnimated;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setIsAnimated(final boolean isAnimated) {
        mIsAnimated = mIsFeaturesAvailable && isAnimated;
    }

    public boolean isShadowed() {
        return mIsShadowed;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setIsShadowed(final boolean isShadowed) {
        mIsShadowed = mIsFeaturesAvailable && isShadowed;
        resetShadowLayer();
        requestLayout();
    }

    public boolean isModelBgEnabled() {
        return mIsModelBgEnabled;
    }

    public void setModelBgEnabled(final boolean modelBgEnabled) {
        mIsModelBgEnabled = modelBgEnabled;
        postInvalidate();
    }

    public boolean isRounded() {
        return mIsRounded;
    }

    public void setIsRounded(final boolean isRounded) {
        mIsRounded = isRounded;
        if (mIsRounded) {
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        } else {
            mProgressPaint.setStrokeCap(Paint.Cap.BUTT);
            mProgressPaint.setStrokeJoin(Paint.Join.MITER);
        }
        requestLayout();
    }

    public boolean isDragged() {
        return mIsDragged;
    }

    public void setIsDragged(final boolean isDragged) {
        mIsDragged = isDragged;
    }

    public boolean isLeveled() {
        return mIsLeveled;
    }

    public void setIsLeveled(final boolean isLeveled) {
        mIsLeveled = mIsFeaturesAvailable && isLeveled;
        requestLayout();
    }

    public Interpolator getInterpolator() {
        return (Interpolator) mProgressAnimator.getInterpolator();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setInterpolator(final Interpolator interpolator) {
        mInterpolator = interpolator == null ? new AccelerateDecelerateInterpolator() : interpolator;
        mProgressAnimator.setInterpolator(mInterpolator);
    }

    public float getProgressModelOffset() {
        return mProgressModelOffset;
    }

    public void setProgressModelOffset(final float progressModelOffset) {
        mProgressModelOffset = progressModelOffset;
        requestLayout();
    }

    public float getDrawWidthFraction() {
        return mDrawWidthFraction;
    }

    @SuppressLint("SupportAnnotationUsage")
    @FloatRange
    public void setDrawWidthFraction(@FloatRange(from = MIN_FRACTION, to = MAX_FRACTION) final float drawWidthFraction) {
        // Divide by half for radius and reset
        mDrawWidthFraction = Math.max(MIN_FRACTION, Math.min(drawWidthFraction, MAX_FRACTION)) * 0.5F;
        mDrawWidthDimension = MIN_FRACTION;
        requestLayout();
    }

    public float getDrawWidthDimension() {
        return mDrawWidthDimension;
    }

    public void setDrawWidthDimension(final float drawWidthDimension) {
        mDrawWidthFraction = MIN_FRACTION;
        mDrawWidthDimension = drawWidthDimension;
        requestLayout();
    }

    public float getShadowDistance() {
        return mShadowDistance;
    }

    public void setShadowDistance(final float shadowDistance) {
        mShadowDistance = shadowDistance;
        resetShadowLayer();
        requestLayout();
    }

    public float getShadowAngle() {
        return mShadowAngle;
    }

    @SuppressLint("SupportAnnotationUsage")
    @FloatRange
    public void setShadowAngle(@FloatRange(from = MIN_ANGLE, to = MAX_ANGLE) final float shadowAngle) {
        mShadowAngle = Math.max(MIN_ANGLE, Math.min(shadowAngle, MAX_ANGLE));
        resetShadowLayer();
        requestLayout();
    }

    public float getShadowRadius() {
        return mShadowRadius;
    }

    public void setShadowRadius(final float shadowRadius) {
        mShadowRadius = shadowRadius > MIN_SHADOW ? shadowRadius : MIN_SHADOW;
        resetShadowLayer();
        requestLayout();
    }

    public int getShadowColor() {
        return mShadowColor;
    }

    public void setShadowColor(final int shadowColor) {
        mShadowColor = shadowColor;
        resetShadowLayer();
        postInvalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(final int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(textColor);
        postInvalidate();
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public void setTypeface(final String typeface) {
        Typeface tempTypeface;
        try {
            if (isInEditMode()) return;
            tempTypeface = Typeface.createFromAsset(getContext().getAssets(), typeface);
        } catch (Exception e) {
            tempTypeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        }

        setTypeface(tempTypeface);
    }

    public void setTypeface(final Typeface typeface) {
        mTypeface = typeface;
        mTextPaint.setTypeface(typeface);
        postInvalidate();
    }

    public IndicatorOrientation getIndicatorOrientation() {
        return mIndicatorOrientation;
    }

    public void setIndicatorOrientation(final IndicatorOrientation indicatorOrientation) {
        mIndicatorOrientation = indicatorOrientation;
    }

    // Reset shadow layer
    private void resetShadowLayer() {
        if (isInEditMode()) return;

        final float newDx =
                (float) ((mShadowDistance) * Math.cos((mShadowAngle - mStartAngle) / 180.0F * Math.PI));
        final float newDy =
                (float) ((mShadowDistance) * Math.sin((mShadowAngle - mStartAngle) / 180.0F * Math.PI));

        if (mIsShadowed)
            mProgressPaint.setShadowLayer(mShadowRadius, newDx, newDy, mShadowColor);
        else mProgressPaint.clearShadowLayer();
    }

    // Set start elevation pin if gradient round progress
    private void setLevelShadowLayer() {
        if (isInEditMode()) return;

        if (mIsShadowed || mIsLeveled) {
            final float shadowOffset = mShadowRadius * 0.5f;
            mLevelPaint.setShadowLayer(
                    shadowOffset, 0.0f, -shadowOffset, adjustColorAlpha(mShadowColor, 0.5f)
            );
        } else mLevelPaint.clearShadowLayer();
    }

    // Adjust color alpha(used for shadow reduce)
    private int adjustColorAlpha(final int color, final float factor) {
        return Color.argb(
                Math.round(Color.alpha(color) * factor),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    // Animate progress
    public void animateProgress() {
        if (!mIsAnimated || mProgressAnimator == null) return;
        if (mProgressAnimator.isRunning()) {
            if (mAnimatorListener != null) mProgressAnimator.removeListener(mAnimatorListener);
            mProgressAnimator.cancel();
        }
        // Set to animate all models
        mActionMoveModelIndex = ANIMATE_ALL_INDEX;
        mProgressAnimator.setDuration(mAnimationDuration);
        mProgressAnimator.setInterpolator(mInterpolator);
        if (mAnimatorListener != null) {
            mProgressAnimator.removeListener(mAnimatorListener);
            mProgressAnimator.addListener(mAnimatorListener);
        }
        mProgressAnimator.start();
    }

    // Animate progress
    private void animateActionMoveProgress() {
        if (!mIsAnimated || mProgressAnimator == null) return;
        if (mProgressAnimator.isRunning()) return;

        mProgressAnimator.setDuration(DEFAULT_ACTION_MOVE_ANIMATION_DURATION);
        mProgressAnimator.setInterpolator(null);
        if (mAnimatorListener != null) mProgressAnimator.removeListener(mAnimatorListener);
        mProgressAnimator.start();
    }

    // Get the angle of action move model
    private float getActionMoveAngle(final float x, final float y) {
        //Get radius
        final float radius = mSize * 0.5F;

        // Get degrees without offset
        float degrees = (float) ((Math.toDegrees(Math.atan2(y - radius, x - radius)) + 360.0F) % 360.0F);
        if (degrees < 0) degrees += 2.0F * Math.PI;

        // Get point with offset relative to start angle
        final float newActionMoveX =
                (float) (radius * Math.cos((degrees - mStartAngle) / 180.0F * Math.PI));
        final float newActionMoveY =
                (float) (radius * Math.sin((degrees - mStartAngle) / 180.0F * Math.PI));

        // Set new angle with offset
        degrees = (float) ((Math.toDegrees(Math.atan2(newActionMoveY, newActionMoveX)) + 360.0F) % 360.0F);
        if (degrees < 0) degrees += 2.0F * Math.PI;

        return degrees;
    }

    private void handleActionMoveModel(final MotionEvent event) {
        if (mActionMoveModelIndex == DISABLE_ANIMATE_INDEX) return;

        // Get current move angle
        float currentAngle = getActionMoveAngle(event.getX(), event.getY());

        // Check if angle in slice zones
        final int actionMoveCurrentSlice;
        if (currentAngle > MIN_ANGLE && currentAngle < POSITIVE_ANGLE)
            actionMoveCurrentSlice = POSITIVE_SLICE;
        else if (currentAngle > NEGATIVE_ANGLE && currentAngle < MAX_ANGLE)
            actionMoveCurrentSlice = NEGATIVE_SLICE;
        else actionMoveCurrentSlice = DEFAULT_SLICE;

        // Check for handling counter
        if (actionMoveCurrentSlice != 0 &&
                ((mActionMoveLastSlice == NEGATIVE_SLICE && actionMoveCurrentSlice == POSITIVE_SLICE) ||
                        (actionMoveCurrentSlice == NEGATIVE_SLICE && mActionMoveLastSlice == POSITIVE_SLICE))) {
            if (mActionMoveLastSlice == NEGATIVE_SLICE) mActionMoveSliceCounter++;
            else mActionMoveSliceCounter--;

            // Limit counter for 1 and -1, we don`t need take the race
            if (mActionMoveSliceCounter > 1) mActionMoveSliceCounter = 1;
            else if (mActionMoveSliceCounter < -1) mActionMoveSliceCounter = -1;
        }
        mActionMoveLastSlice = actionMoveCurrentSlice;

        // Set total traveled angle
        float actionMoveTotalAngle = currentAngle + (MAX_ANGLE * mActionMoveSliceCounter);
        final Model model = mModels.get(mActionMoveModelIndex);

        // Check whether traveled angle out of limit
        if (actionMoveTotalAngle < MIN_ANGLE || actionMoveTotalAngle > MAX_ANGLE) {
            actionMoveTotalAngle =
                    actionMoveTotalAngle > MAX_ANGLE ? MAX_ANGLE + 1.0F : -1.0F;
            currentAngle = actionMoveTotalAngle;
        }

        // Set model progress and invalidate
        float touchProgress = Math.round(MAX_PROGRESS / mSweepAngle * currentAngle);
        model.setProgress(touchProgress);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (!mIsDragged) return super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionMoveModelIndex = DISABLE_ANIMATE_INDEX;
                // Get current move angle and check whether touched angle is in sweep angle zone
                float currentAngle = getActionMoveAngle(event.getX(), event.getY());
                if (currentAngle > mSweepAngle && currentAngle < MAX_ANGLE) break;

                for (int i = 0; i < mModels.size(); i++) {
                    final Model model = mModels.get(i);
                    // Check if our model contains touch points
                    if (model.mBounds.contains(event.getX(), event.getY())) {
                        // Check variables for handle touch in progress model zone
                        float modelRadius = model.mBounds.width() * 0.5F;
                        float modelOffset = mProgressModelSize * 0.5F;
                        float mainRadius = mSize * 0.5F;

                        // Get distance between 2 points
                        final float distance = (float) Math.sqrt(Math.pow(event.getX() - mainRadius, 2) +
                                Math.pow(event.getY() - mainRadius, 2));
                        if (distance > modelRadius - modelOffset && distance < modelRadius + modelOffset) {
                            mActionMoveModelIndex = i;
                            mIsActionMoved = true;
                            handleActionMoveModel(event);
                            animateActionMoveProgress();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActionMoveModelIndex == DISABLE_ANIMATE_INDEX && !mIsActionMoved) break;
                if (mProgressAnimator.isRunning()) break;
                handleActionMoveModel(event);
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            default:
                // Reset values
                mActionMoveLastSlice = DEFAULT_SLICE;
                mActionMoveSliceCounter = 0;
                mIsActionMoved = false;
                break;
        }

        // If we have parent, so requestDisallowInterceptTouchEvent
        if (event.getAction() == MotionEvent.ACTION_MOVE && getParent() != null)
            getParent().requestDisallowInterceptTouchEvent(true);

        return true;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        // Get measured sizes
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        // Get size for square dimension
        if (width > height) mSize = height;
        else mSize = width;

        // Get progress offsets
        final float divider = mDrawWidthFraction == 0 ? mDrawWidthDimension : mSize * mDrawWidthFraction;
        mProgressModelSize = divider / mModels.size();
        final float paintOffset = mProgressModelSize * 0.5F;
        final float shadowOffset = mIsShadowed ? (mShadowRadius + mShadowDistance) : 0.0F;

        // Set bound with offset for models
        for (int i = 0; i < mModels.size(); i++) {
            final Model model = mModels.get(i);
            final float modelOffset = (mProgressModelSize * i) +
                    (paintOffset + shadowOffset) - (mProgressModelOffset * i);

            // Set bounds to progress
            model.mBounds.set(
                    modelOffset, modelOffset,
                    mSize - modelOffset, mSize - modelOffset
            );

            // Set sweep gradient shader
            if (model.getColors() != null)
                model.mSweepGradient = new SweepGradient(
                        model.mBounds.centerX(), model.mBounds.centerY(), model.getColors(), null
                );
        }

        // Set square measured dimension
        setMeasuredDimension(mSize, mSize);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        // Save and rotate to start angle
        canvas.save();
        final float radius = mSize * 0.5F;
        canvas.rotate(mStartAngle, radius, radius);

        // Draw all of progress
        for (int i = 0; i < mModels.size(); i++) {
            final Model model = mModels.get(i);
            // Get progress for current model
            float progressFraction = mIsAnimated && !isInEditMode() ? (model.mLastProgress + (mAnimatedFraction *
                    (model.getProgress() - model.mLastProgress))) / MAX_PROGRESS :
                    model.getProgress() / MAX_PROGRESS;
            if (i != mActionMoveModelIndex && mActionMoveModelIndex != ANIMATE_ALL_INDEX)
                progressFraction = model.getProgress() / MAX_PROGRESS;
            final float progress = progressFraction * mSweepAngle;

            // Check if model have gradient
            final boolean isGradient = model.getColors() != null;
            // Set width of progress
            mProgressPaint.setStrokeWidth(mProgressModelSize);

            // Set model arc progress
            model.mPath.reset();
            model.mPath.addArc(model.mBounds, 0.0F, progress);

            // Draw gradient progress or solid
            resetShadowLayer();
            mProgressPaint.setShader(null);
            mProgressPaint.setStyle(Paint.Style.STROKE);

            if (mIsModelBgEnabled) {
                //noinspection ResourceAsColor
                mProgressPaint.setColor(isInEditMode() ? mPreviewModelBgColor : model.getBgColor());
                canvas.drawArc(model.mBounds, 0.0F, mSweepAngle, false, mProgressPaint);
                if (!isInEditMode()) mProgressPaint.clearShadowLayer();
            }

            // Check if gradient for draw shadow at first and then gradient progress
            if (isGradient) {
                if (!mIsModelBgEnabled) {
                    canvas.drawPath(model.mPath, mProgressPaint);

                    if (!isInEditMode()) mProgressPaint.clearShadowLayer();
                }

                mProgressPaint.setShader(model.mSweepGradient);
            } else mProgressPaint.setColor(model.getColor());

            // Here we draw main progress
            mProgressPaint.setAlpha(255);
            canvas.drawPath(model.mPath, mProgressPaint);

            // Preview mode
            if (isInEditMode()) continue;

            // Get model title bounds
            mTextPaint.setTextSize(mProgressModelSize * 0.5F);
            mTextPaint.getTextBounds(
                    model.getTitle(),
                    0, model.getTitle().length(),
                    model.mTextBounds
            );

            // Draw title at start with offset
            final float titleHorizontalOffset = model.mTextBounds.height() * 0.5F;
            final float progressLength =
                    (float) (Math.PI / 180.0F) * progress * model.mBounds.width() * 0.5F;
            final String title = (String) TextUtils.ellipsize(
                    model.getTitle(), mTextPaint,
                    progressLength - titleHorizontalOffset * 2, TextUtils.TruncateAt.END
            );
            canvas.drawTextOnPath(
                    title,
                    model.mPath,
                    mIsRounded ? 0.0F : titleHorizontalOffset, titleHorizontalOffset,
                    mTextPaint
            );

            // Get pos and tan at final path point
            model.mPathMeasure.setPath(model.mPath, false);
            model.mPathMeasure.getPosTan(model.mPathMeasure.getLength(), model.mPos, model.mTan);

            // Get title width
            final float titleWidth = model.mTextBounds.width();

            // Create model progress like : 23%
            final String percentProgress = String.format("%d%%", (int) model.getProgress());
            // Get progress text bounds
            mTextPaint.setTextSize(mProgressModelSize * 0.35f);
            mTextPaint.getTextBounds(
                    percentProgress, 0, percentProgress.length(), model.mTextBounds
            );

            // Get pos tan with end point offset and check whether the rounded corners for offset
            final float progressHorizontalOffset =
                    mIndicatorOrientation == IndicatorOrientation.VERTICAL ?
                            model.mTextBounds.height() * 0.5F : model.mTextBounds.width() * 0.5F;
            final float indicatorProgressOffset = (mIsRounded ? progressFraction : 1.0F) *
                    (-progressHorizontalOffset - titleHorizontalOffset
                            - (mIsRounded ? model.mTextBounds.height() * 2.0F : 0.0F));
            model.mPathMeasure.getPosTan(
                    model.mPathMeasure.getLength() + indicatorProgressOffset, model.mPos,
                    mIndicatorOrientation == IndicatorOrientation.VERTICAL && !mIsRounded ?
                            new float[2] :
                            model.mTan
            );

            // Check if there available place for indicator
            if ((titleWidth + model.mTextBounds.height() + titleHorizontalOffset * 2.0F) -
                    indicatorProgressOffset < progressLength) {
                // Get rotate indicator progress angle for progress value
                float indicatorProgressAngle =
                        (float) (Math.atan2(model.mTan[1], model.mTan[0]) * (180.0F / Math.PI));
                // Get arc angle of progress indicator
                final float indicatorLengthProgressAngle = ((progressLength + indicatorProgressOffset) /
                        (model.mBounds.width() * 0.5F)) * (float) (180.0F / Math.PI);

                // Detect progress indicator position : left or right and then rotate
                if (mIndicatorOrientation == IndicatorOrientation.VERTICAL) {
                    // Get X point of arc angle progress indicator
                    final float x = (float) (model.mBounds.width() * 0.5F *
                            (Math.cos((indicatorLengthProgressAngle + mStartAngle) *
                                    Math.PI / 180.0F))) + model.mBounds.centerX();
                    indicatorProgressAngle += (x > radius) ? -90.0F : 90.0F;
                } else {
                    // Get Y point of arc angle progress indicator
                    final float y = (float) (model.mBounds.height() * 0.5F *
                            (Math.sin((indicatorLengthProgressAngle + mStartAngle) *
                                    Math.PI / 180.0F))) + model.mBounds.centerY();
                    indicatorProgressAngle += (y > radius) ? 180.0F : 0.0F;
                }

                // Draw progress value
                canvas.save();
                canvas.rotate(indicatorProgressAngle, model.mPos[0], model.mPos[1]);
                canvas.drawText(
                        percentProgress,
                        model.mPos[0] - model.mTextBounds.exactCenterX(),
                        model.mPos[1] - model.mTextBounds.exactCenterY(),
                        mTextPaint
                );
                canvas.restore();
            }

            // Check if gradient and have rounded corners, because we must to create elevation effect
            // for start progress corner
            if ((isGradient || mIsLeveled) && mIsRounded && progress != 0) {
                model.mPathMeasure.getPosTan(0.0F, model.mPos, model.mTan);

                // Set paint for overlay rounded gradient with shadow
                setLevelShadowLayer();
                //noinspection ResourceAsColor
                mLevelPaint.setColor(isGradient ? model.getColors()[0] : model.getColor());

                // Get bounds of start pump
                final float halfSize = mProgressModelSize * 0.5F;
                final RectF arcRect = new RectF(
                        model.mPos[0] - halfSize, model.mPos[1] - halfSize,
                        model.mPos[0] + halfSize, model.mPos[1] + halfSize + 2.0F
                );
                canvas.drawArc(arcRect, 0.0F, -180.0F, true, mLevelPaint);
            }
        }

        // Restore after drawing
        canvas.restore();
    }

    public static class Model {

        private String mTitle;
        private float mLastProgress;
        private float mProgress;

        private int mColor;
        private int mBgColor;
        private int[] mColors;

        private final RectF mBounds = new RectF();
        private final Rect mTextBounds = new Rect();

        private final Path mPath = new Path();
        private SweepGradient mSweepGradient;

        private final PathMeasure mPathMeasure = new PathMeasure();
        private final float[] mPos = new float[2];
        private final float[] mTan = new float[2];

        public Model(final String title, final float progress, final int color) {
            setTitle(title);
            setProgress(progress);
            setColor(color);
        }

        public Model(final String title, final float progress, final int[] colors) {
            setTitle(title);
            setProgress(progress);
            setColors(colors);
        }

        public Model(final String title, final float progress, final int bgColor, final int color) {
            setTitle(title);
            setProgress(progress);
            setColor(color);
            setBgColor(bgColor);
        }

        public Model(final String title, final float progress, final int bgColor, final int[] colors) {
            setTitle(title);
            setProgress(progress);
            setColors(colors);
            setBgColor(bgColor);
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(final String title) {
            mTitle = title;
        }

        public float getProgress() {
            return mProgress;
        }

        @FloatRange
        public void setProgress(@FloatRange(from = MIN_PROGRESS, to = MAX_PROGRESS) final float progress) {
            mLastProgress = mProgress;
            mProgress = (int) Math.max(MIN_PROGRESS, Math.min(progress, MAX_PROGRESS));
        }

        public int getColor() {
            return mColor;
        }

        public void setColor(final int color) {
            mColor = color;
        }

        public int getBgColor() {
            return mBgColor;
        }

        public void setBgColor(final int bgColor) {
            mBgColor = bgColor;
        }

        public int[] getColors() {
            return mColors;
        }

        public void setColors(final int[] colors) {
            if (colors != null && colors.length >= 2) mColors = colors;
            else mColors = null;
        }
    }

    public enum IndicatorOrientation {
        HORIZONTAL, VERTICAL
    }
}
