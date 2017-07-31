package ui.component.banking.separablecardview.ui.component;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import ui.component.banking.separablecardview.R;

/**
 * Created by banking on 16/10/14.
 */
class RoundRectDrawableWithShadow extends Drawable {
    // used to calculate content padding
    final static double COS_45 = Math.cos(Math.toRadians(45));

    final static float SHADOW_MULTIPLIER = 1.5f;

    final int mInsetShadow; // extra shadow to avoid gaps between card and shadow

    /*
    * This helper is set by CardView implementations.
    * <p>
    * Prior to API 17, canvas.drawRoundRect is expensive; which is why we need this interface
    * to draw efficient rounded rectangles before 17.
    * */
    static RoundRectHelper sRoundRectHelper;

    Paint mPaint;

    Paint mTestYellowPaint;

    Paint mTestBlackPaint;

    Paint mCornerShadowPaint;

    Paint mEdgeShadowPaint;

    Paint mEdgeShadowPaint2;

    final RectF mCardBounds;

    float mCornerRadius;

    Path mCornerShadowPath;

    // updated value with inset
    float mMaxShadowSize;

    // actual value set by developer
    float mRawMaxShadowSize;

    // multiplied value to account for shadow offset
    float mShadowSize;

    // actual value set by developer
    float mRawShadowSize;

    private ColorStateList mBackground;

    private boolean mDirty = true;

    private final int mShadowStartColor;

    private final int mShadowEndColor;

    private boolean mAddPaddingForCorners = true;

    /**
     * If shadow size is set to a value above max shadow, we print a warning
     */
    private boolean mPrintedShadowClipWarning = false;

    RoundRectDrawableWithShadow(Resources resources, ColorStateList backgroundColor, float radius,
                                float shadowSize, float maxShadowSize) {
        mShadowStartColor = resources.getColor(R.color.cardview_shadow_start_color);
        mShadowEndColor = resources.getColor(R.color.cardview_shadow_end_color);
        mInsetShadow = resources.getDimensionPixelSize(R.dimen.cardview_compat_inset_shadow);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTestYellowPaint = new Paint();
        mTestBlackPaint = new Paint();
        setBackground(backgroundColor);
        mCornerShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mCornerShadowPaint.setStyle(Paint.Style.FILL);
        mCornerRadius = (int) (radius + .5f);
        mCardBounds = new RectF();
        mEdgeShadowPaint = new Paint(mCornerShadowPaint);
        mEdgeShadowPaint.setAntiAlias(false);
        mEdgeShadowPaint2 = new Paint(mCornerShadowPaint);
        mEdgeShadowPaint2.setAntiAlias(false);
        setShadowSize(shadowSize, maxShadowSize);
    }

    private void setBackground(ColorStateList color) {
        mBackground = (color == null) ?  ColorStateList.valueOf(Color.TRANSPARENT) : color;
        mPaint.setColor(mBackground.getColorForState(getState(), mBackground.getDefaultColor()));
        mTestYellowPaint.setColor(Color.parseColor("#ffff00"));
        mTestBlackPaint.setColor(Color.parseColor("#000000"));
    }

    /**
     * Casts the value to an even integer.
     */
    private int toEven(float value) {
        int i = (int) (value + .5f);
        if (i % 2 == 1) {
            return i - 1;
        }
        return i;
    }

    public void setAddPaddingForCorners(boolean addPaddingForCorners) {
        mAddPaddingForCorners = addPaddingForCorners;
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        mCornerShadowPaint.setAlpha(alpha);
        mEdgeShadowPaint.setAlpha(alpha);
        mEdgeShadowPaint2.setAlpha(alpha);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mDirty = true;
    }

    void setShadowSize(float shadowSize, float maxShadowSize) {
        if (shadowSize < 0f) {
            throw new IllegalArgumentException("Invalid shadow size " + shadowSize +
                    ". Must be >= 0");
        }
        if (maxShadowSize < 0f) {
            throw new IllegalArgumentException("Invalid max shadow size " + maxShadowSize +
                    ". Must be >= 0");
        }
        shadowSize = toEven(shadowSize);
        maxShadowSize = toEven(maxShadowSize);
        if (shadowSize > maxShadowSize) {
            shadowSize = maxShadowSize;
            if (!mPrintedShadowClipWarning) {
                mPrintedShadowClipWarning = true;
            }
        }
        if (mRawShadowSize == shadowSize && mRawMaxShadowSize == maxShadowSize) {
            return;
        }
        mRawShadowSize = shadowSize;
        mRawMaxShadowSize = maxShadowSize;
        mShadowSize = (int)(shadowSize * SHADOW_MULTIPLIER + mInsetShadow + .5f);
        mMaxShadowSize = maxShadowSize + mInsetShadow;
        mDirty = true;
        invalidateSelf();
    }

    @Override
    public boolean getPadding(Rect padding) {
        int vOffset = (int) Math.ceil(calculateVerticalPadding(mRawMaxShadowSize, mCornerRadius,
                mAddPaddingForCorners));
        int hOffset = (int) Math.ceil(calculateHorizontalPadding(mRawMaxShadowSize, mCornerRadius,
                mAddPaddingForCorners));
        padding.set(hOffset, vOffset, hOffset, vOffset);
        return true;
    }

    static float calculateVerticalPadding(float maxShadowSize, float cornerRadius,
                                          boolean addPaddingForCorners) {
        if (addPaddingForCorners) {
            return (float) (maxShadowSize * SHADOW_MULTIPLIER + (1 - COS_45) * cornerRadius);
        } else {
            return maxShadowSize * SHADOW_MULTIPLIER;
        }
    }

    static float calculateHorizontalPadding(float maxShadowSize, float cornerRadius,
                                            boolean addPaddingForCorners) {
        if (addPaddingForCorners) {
            return (float) (maxShadowSize + (1 - COS_45) * cornerRadius);
        } else {
            return maxShadowSize;
        }
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        final int newColor = mBackground.getColorForState(stateSet, mBackground.getDefaultColor());
        if (mPaint.getColor() == newColor) {
            return false;
        }
        mPaint.setColor(newColor);
        mDirty = true;
        invalidateSelf();
        return true;
    }

    @Override
    public boolean isStateful() {
        return (mBackground != null && mBackground.isStateful()) || super.isStateful();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    void setCornerRadius(float radius) {
        if (radius < 0f) {
            throw new IllegalArgumentException("Invalid radius " + radius +
                    ". Must be >= 0");
        }
        radius = (int) (radius + .5f);
        if (mCornerRadius == radius) {
            return;
        }
        mCornerRadius = radius;
        mDirty = true;
        invalidateSelf();
    }

    int cardType = CardViewImpl.TYPE_ALL_ROUND;

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }
    @Override
    public void draw(Canvas canvas) {
        if (mDirty) {
            buildComponents(getBounds());
            mDirty = false;
        }
        if (cardType != CardViewImpl.TYPE_NO_ROUND) {
            canvas.translate(0, mRawShadowSize / 2);
        }

        switch (cardType) {
            case CardViewImpl.TYPE_ALL_ROUND:
                drawAllShadow(canvas);
                break;
            case CardViewImpl.TYPE_ONLY_TOP:
                drawShadowOnlyTop(canvas);
                break;
            case CardViewImpl.TYPE_ONLY_BOTTOM:
                drawShadowOnlyBottom(canvas);
                break;
            case CardViewImpl.TYPE_NO_ROUND:
                drawNoCornerShadow(canvas);
                break;
        }
        if (cardType != CardViewImpl.TYPE_NO_ROUND) {
            canvas.translate(0, -mRawShadowSize / 2);
        }
        sRoundRectHelper.drawRoundRect(canvas, mCardBounds, mCornerRadius, mPaint);
    }

    private void drawAllShadow(Canvas canvas) {
        final float edgeShadowTop = -mCornerRadius - mShadowSize;
        final float inset = mCornerRadius + mInsetShadow + mRawShadowSize / 2;
        final boolean drawHorizontalEdges = mCardBounds.width() - 2 * inset > 0;
        final boolean drawVerticalEdges = mCardBounds.height() - 2 * inset > 0;
        Log.d("", "Shadow Config log:mCornerRadius = " + mCornerRadius + ";mCornerRadius = " + mShadowSize
            + ";mInsetShadow = " + mInsetShadow + ";mCardBounds.width = " + mCardBounds.width() +
                ";mCardBuonds.height = " + mCardBounds.height() + ";mCardBounds.left = " + mCardBounds.left
                + ";mCardBounds.top = " + mCardBounds.top);
        // LT
        int saved = canvas.save();
        canvas.translate(mCardBounds.left + inset, mCardBounds.top + inset);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.width() - 2 * inset, -mCornerRadius,
                    mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        // RB
        saved = canvas.save();
        canvas.translate(mCardBounds.right - inset, mCardBounds.bottom - inset);
        canvas.rotate(180f);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.width() - 2 * inset, -mCornerRadius + mShadowSize,
                    mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        // LB
        saved = canvas.save();
        canvas.translate(mCardBounds.left + inset, mCardBounds.bottom - inset);
        canvas.rotate(270f);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.height() - 2 * inset, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        // RT
        saved = canvas.save();
        canvas.translate(mCardBounds.right - inset, mCardBounds.top + inset);
        canvas.rotate(90f);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.height() - 2 * inset, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
    }

    private void drawShadowOnlyTop(Canvas canvas) {
        final float edgeShadowTop = -mCornerRadius - mShadowSize;
        final float inset = mCornerRadius + mInsetShadow + mRawShadowSize / 2;
        final boolean drawHorizontalEdges = mCardBounds.width() - 2 * inset > 0;
        final boolean drawVerticalEdges = mCardBounds.height() - 2 * inset > 0;
        // LT
        int saved = canvas.save();
        canvas.translate(mCardBounds.left + inset, mCardBounds.top + inset);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.width() - 2 * inset, -mCornerRadius,
                    mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);

        // LB //must fix draw path
        saved = canvas.save();
        canvas.translate(mCardBounds.left + inset, mCardBounds.bottom - inset);
        canvas.rotate(270f);
//        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(-mCornerRadius - 2 * inset, edgeShadowTop - mCornerRadius - mInsetShadow,
                    mCardBounds.height() - 2 * inset , -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        // RT
        saved = canvas.save();
        canvas.translate(mCardBounds.right - inset, mCardBounds.top + inset);
        canvas.rotate(90f);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.height() - inset + mCornerRadius , -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);

        // RB  //todo check wrong Shadow
        saved = canvas.save();
        canvas.translate(mCardBounds.right - inset, mCardBounds.bottom - inset);
        canvas.rotate(180f);
//        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(-mShadowSize , edgeShadowTop,
                    mCardBounds.width() - mShadowSize , mCornerRadius + mShadowSize, //- todo check
                    mPaint);
        }
        canvas.restoreToCount(saved);
    }

    private void drawNoCornerShadow(Canvas canvas) {
        final float edgeShadowTop = -mCornerRadius - mShadowSize;
        final float inset = mCornerRadius + mInsetShadow + mRawShadowSize/2;
        final float insetRaw = mCornerRadius + mInsetShadow + mRawShadowSize/2;
        final boolean drawHorizontalEdges = mCardBounds.width() - 2 * inset > 0;
        final boolean drawVerticalEdges = mCardBounds.height() - 2 * inset > 0;



        // LB
        int saved = canvas.save();
        canvas.translate(mCardBounds.left + insetRaw, mCardBounds.bottom - insetRaw);
        canvas.rotate(270f);
        if (drawVerticalEdges) { // 划线方向和预想不一致，done
            canvas.drawRect(- mCornerRadius - 2 * inset, edgeShadowTop - mCornerRadius - mInsetShadow,
                    mCardBounds.height() + mCornerRadius, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);

//        canvas.drawRect(0, 0,
//                mCornerRadius, mCardBounds.height() + mCornerRadius, mTestYellowPaint);

        // RT
        saved = canvas.save();
        canvas.translate(mCardBounds.right - inset, mCardBounds.top + inset);
        canvas.rotate(90f);
        if (drawVerticalEdges) {
//            canvas.drawRect(-inset, edgeShadowTop,
//                    mCardBounds.height(), -mCornerRadius, mEdgeShadowPaint);
            canvas.drawRect(-mCornerRadius - 2 * inset , edgeShadowTop,
                    mCardBounds.height() + mCornerRadius , -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);

//        // LT
//        saved = canvas.save();
//        canvas.translate(mCardBounds.left + inset, mCardBounds.top + inset);
//        if (drawHorizontalEdges) {
//            canvas.drawRect(-mShadowSize, edgeShadowTop - 1.0f,
//                    mCardBounds.width() - mShadowSize, -mCornerRadius,
//                    mPaint);
//        }
//        canvas.restoreToCount(saved);
        // LT
        if (drawHorizontalEdges) {
            canvas.drawRect(mCornerRadius, 0,
                    mCardBounds.width() + mCornerRadius, mShadowSize,
                    mTestYellowPaint);
        }

//        // RB
//        saved = canvas.save();
//        canvas.translate(mCardBounds.right - inset, mCardBounds.bottom - inset);
//        canvas.rotate(180f);
//        if (drawHorizontalEdges) {
//            canvas.drawRect(-mShadowSize , edgeShadowTop,
//                    mCardBounds.width()-mShadowSize , -mCornerRadius + mShadowSize,
//                    mPaint);
//        }
//        canvas.restoreToCount(saved);
        // RB
        if (drawHorizontalEdges) { //todo card radius
            canvas.drawRect(mCornerRadius, mCardBounds.height(), // todo why not + mShadowSize
                    mCardBounds.width() + mCornerRadius, mShadowSize + mCardBounds.height() + mShadowSize,
                    mPaint);
        }

//        canvas.drawRect(mCardBounds.left + insetRaw - 10, mCardBounds.bottom - insetRaw - 10 ,
//                mCardBounds.left + insetRaw + 10, mCardBounds.bottom - insetRaw + 10 ,mTestBlackPaint);
    }

    private void drawShadowOnlyBottom(Canvas canvas){

        final float edgeShadowTop = -mCornerRadius - mShadowSize;
        final float inset = mCornerRadius + mInsetShadow + mRawShadowSize / 2;
        final boolean drawHorizontalEdges = mCardBounds.width() - 2 * inset > 0;
        final boolean drawVerticalEdges = mCardBounds.height() - 2 * inset > 0;

        // LT
        int saved = canvas.save();
        canvas.translate(mCardBounds.left + inset, mCardBounds.top + inset);
        if (drawHorizontalEdges) {
            canvas.drawRect(-2 * inset, edgeShadowTop - 10.0f,  //todo check
                    mCardBounds.width() - inset, -mCornerRadius,
                    mPaint);
        }
        canvas.restoreToCount(saved);
        // RB
        saved = canvas.save();
        canvas.translate(mCardBounds.right - inset, mCardBounds.bottom - inset);
        canvas.rotate(180f);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.drawRect(0, edgeShadowTop,
                    mCardBounds.width() - 2 * inset, -mCornerRadius + mShadowSize,
                    mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        // LB
        saved = canvas.save();
        canvas.translate(mCardBounds.left + inset, mCardBounds.bottom - inset);
        canvas.rotate(270f);
        canvas.drawPath(mCornerShadowPath, mCornerShadowPaint);
        if (drawVerticalEdges) {
//            canvas.drawRect(0, edgeShadowTop,
//                    mCardBounds.height() -  inset , -mCornerRadius, mEdgeShadowPaint);
            canvas.drawRect(- mCornerRadius, edgeShadowTop - mCornerRadius - mInsetShadow,
                    mCardBounds.height() - mCornerRadius, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
        // RT
        saved = canvas.save();
        canvas.translate(mCardBounds.right - inset, mCardBounds.top + inset);
        canvas.rotate(90f);
        if (drawVerticalEdges) {
            canvas.drawRect(-inset, edgeShadowTop,
                    mCardBounds.height()- 2 * inset, -mCornerRadius, mEdgeShadowPaint);
        }
        canvas.restoreToCount(saved);
    }

    private void buildShadowCorners() {
        RectF innerBounds = new RectF(-mCornerRadius, -mCornerRadius, mCornerRadius, mCornerRadius);
        RectF outerBounds = new RectF(innerBounds);
        outerBounds.inset(-mShadowSize, -mShadowSize);

        if (mCornerShadowPath == null) {
            mCornerShadowPath = new Path();
        } else {
            mCornerShadowPath.reset();
        }
        mCornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
        mCornerShadowPath.moveTo(-mCornerRadius, 0);
        mCornerShadowPath.rLineTo(-mShadowSize, 0);
        // outer arc
        mCornerShadowPath.arcTo(outerBounds, 180f, 90f, false);
        // inner arc
        mCornerShadowPath.arcTo(innerBounds, 270f, -90f, false);
        mCornerShadowPath.close();
        float startRatio = mCornerRadius / (mCornerRadius + mShadowSize);
        mCornerShadowPaint.setShader(new RadialGradient(0, 0, mCornerRadius + mShadowSize,
                new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor},
                new float[]{0f, startRatio, 1f}
                , Shader.TileMode.CLAMP));

        // we offset the content shadowSize/2 pixels up to make it more realistic.
        // this is why edge shadow shader has some extra space
        // When drawing bottom edge shadow, we use that extra space.
        mEdgeShadowPaint.setShader(new LinearGradient(0, -mCornerRadius + mShadowSize, 0,
                -mCornerRadius - mShadowSize,
                new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor},
                new float[]{0f, .5f, 1f}, Shader.TileMode.CLAMP));
        mEdgeShadowPaint.setAntiAlias(false);


        mEdgeShadowPaint2.setShader(new LinearGradient(0, -mCornerRadius + mShadowSize, 0,
                -mCornerRadius - mShadowSize,
                new int[]{mShadowStartColor, mShadowStartColor, mShadowEndColor},
                new float[]{0f, .5f, 1f}, Shader.TileMode.CLAMP));
        mEdgeShadowPaint2.setAntiAlias(false);
    }

    private void buildComponents(Rect bounds) {
        // Card is offset SHADOW_MULTIPLIER * maxShadowSize to account for the shadow shift.
        // We could have different top-bottom offsets to avoid extra gap above but in that case
        // center aligning Views inside the CardView would be problematic.
        final float verticalOffset = mRawMaxShadowSize * SHADOW_MULTIPLIER;
        Log.i("", "Shadow Config log buonds -->" + bounds.left + ";" + bounds.top);
        mCardBounds.set(bounds.left + mRawMaxShadowSize, bounds.top + verticalOffset,
                bounds.right - mRawMaxShadowSize, bounds.bottom - verticalOffset);
        buildShadowCorners();
    }

    float getCornerRadius() {
        return mCornerRadius;
    }

    void getMaxShadowAndCornerPadding(Rect into) {
        getPadding(into);
    }

    void setShadowSize(float size) {
        setShadowSize(size, mRawMaxShadowSize);
    }

    void setMaxShadowSize(float size) {
        setShadowSize(mRawShadowSize, size);
    }

    float getShadowSize() {
        return mRawShadowSize;
    }

    float getMaxShadowSize() {
        return mRawMaxShadowSize;
    }

    float getMinWidth() {
        final float content = 2 *
                Math.max(mRawMaxShadowSize, mCornerRadius + mInsetShadow + mRawMaxShadowSize / 2);
        return content + (mRawMaxShadowSize + mInsetShadow) * 2;
    }

    float getMinHeight() {
        final float content = 2 * Math.max(mRawMaxShadowSize, mCornerRadius + mInsetShadow
                + mRawMaxShadowSize * SHADOW_MULTIPLIER / 2);
        return content + (mRawMaxShadowSize * SHADOW_MULTIPLIER + mInsetShadow) * 2;
    }

    void setColor(@Nullable ColorStateList color) {
        setBackground(color);
        invalidateSelf();
    }

    ColorStateList getColor() {
        return mBackground;
    }

    static interface RoundRectHelper {
        void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius, Paint paint);
    }
}