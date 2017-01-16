package ui.component.banking.separablecardview.ui.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by banking on 16/10/14.
 */
class CardViewEclairMr1 implements CardViewImpl {

    final RectF sCornerRect = new RectF();

    int cardType = TYPE_NO_ROUND;


    public void setCardType(int cardType) {
        this.cardType = cardType;
        background.setCardType(cardType);

    }
    @Override
    public void initStatic() {
        // Draws a round rect using 7 draw operations. This is faster than using
        // canvas.drawRoundRect before JBMR1 because API 11-16 used alpha mask textures to draw
        // shapes.
        RoundRectDrawableWithShadow.sRoundRectHelper
                = new RoundRectDrawableWithShadow.RoundRectHelper() {
            @Override
            public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
                                      Paint paint) {
                final float diameter = cornerRadius * 2;
                final float innerWidth = bounds.width() -  diameter - 1;
                final float innerHeight = bounds.height() -  diameter - 1;
                Paint otherPaint = new Paint();
                otherPaint.setColor(0xffff0000);
                // increment it to account for half pixels.
                if (cornerRadius >= 1f) {
                    cornerRadius += .5f;
                    int saved = canvas.save();

                    //left-top radius
                    canvas.translate(bounds.left + cornerRadius, bounds.top + cornerRadius);
                    if (cardType == TYPE_ONLY_BOTTOM || cardType == TYPE_NO_ROUND) {
                        canvas.drawRect(sCornerRect,paint);
                    } else {
                        canvas.drawArc(sCornerRect, 180, 90, true, paint);
                    }
                    canvas.translate(innerWidth, 0);
                    //right-top radius
                        canvas.rotate(90);
                    if (cardType == TYPE_ONLY_BOTTOM || cardType == TYPE_NO_ROUND) {
                        canvas.drawRect(sCornerRect,paint);
                    } else {
                        canvas.drawArc(sCornerRect, 180, 90, true, paint);
                    }
                        canvas.translate(innerHeight, 0);
                    //right-bottom radius
                        canvas.rotate(90);
                    if (cardType == TYPE_ONLY_TOP || cardType == TYPE_NO_ROUND) {
                        canvas.drawRect(sCornerRect,paint);
                    } else {
                        canvas.drawArc(sCornerRect, 180, 90, true, paint);
                    }
                        canvas.translate(innerWidth, 0);
                    //left_bottom radius
                        canvas.rotate(90);
                    if (cardType == TYPE_ONLY_TOP || cardType == TYPE_NO_ROUND) {
                        canvas.drawRect(sCornerRect,paint);
                    } else {
                        canvas.drawArc(sCornerRect, 180, 90, true, paint);
                    }
                    canvas.restoreToCount(saved);

                    Paint blackPaint = new Paint();
                    blackPaint.setColor(0xff000000);


                    //draw top and bottom pieces
                    if (cardType == TYPE_ONLY_TOP || cardType == TYPE_ALL_ROUND) {
                        canvas.drawRect(bounds.left + cornerRadius - 1f, bounds.top,
                                bounds.right - cornerRadius + 1f, bounds.top + cornerRadius,
                                paint);
                    } else {
                        canvas.drawRect(bounds.left + cornerRadius - 1f, bounds.top - cornerRadius,
                                bounds.right - cornerRadius + 1f, bounds.top + cornerRadius,
                                paint);
                    }
                    if (cardType == TYPE_ONLY_BOTTOM || cardType == TYPE_ALL_ROUND) {
                        canvas.drawRect(bounds.left + cornerRadius - 1f,
                                bounds.bottom - cornerRadius + 1f, bounds.right - cornerRadius + 1f,
                                bounds.bottom, paint);
                    } else {
//                        canvas.translate(0, - cornerRadius);
                        canvas.drawRect(bounds.left + cornerRadius - 1f,
                            bounds.bottom - cornerRadius + 1f, bounds.right - cornerRadius + 1f,
                            bounds.bottom + 2 * cornerRadius, paint);
                    }

                }
                canvas.drawRect(bounds.left, bounds.top + Math.max(0, cornerRadius - 1f),
                        bounds.right, bounds.bottom - cornerRadius + 1f, paint);
            }
        };
    }

    RoundRectDrawableWithShadow background;
    @Override
    public void initialize(CardViewDelegate cardView, Context context, int backgroundColor,
                           float radius, float elevation, float maxElevation) {
        background = createBackground(context, backgroundColor, radius,
                elevation, maxElevation);
        background.setAddPaddingForCorners(cardView.getPreventCornerOverlap());
        cardView.setBackgroundDrawable(background);
        updatePadding(cardView);
    }

    RoundRectDrawableWithShadow createBackground(Context context, int backgroundColor,
                                                 float radius, float elevation, float maxElevation) {
        return new RoundRectDrawableWithShadow(context.getResources(), backgroundColor, radius,
                elevation, maxElevation);
    }

    @Override
    public void updatePadding(CardViewDelegate cardView) {
        Rect shadowPadding = new Rect();
        getShadowBackground(cardView).getMaxShadowAndCornerPadding(shadowPadding);
        ((View) cardView).setMinimumHeight((int) Math.ceil(getMinHeight(cardView)));
        ((View) cardView).setMinimumWidth((int) Math.ceil(getMinWidth(cardView)));
        cardView.setShadowPadding(shadowPadding.left, shadowPadding.top,
                shadowPadding.right, shadowPadding.bottom);
    }

    @Override
    public void onCompatPaddingChanged(CardViewDelegate cardView) {
        // NO OP
    }

    @Override
    public void onPreventCornerOverlapChanged(CardViewDelegate cardView) {
        getShadowBackground(cardView).setAddPaddingForCorners(cardView.getPreventCornerOverlap());
        updatePadding(cardView);
    }

    @Override
    public void setBackgroundColor(CardViewDelegate cardView, int color) {
        getShadowBackground(cardView).setColor(color);
    }

    @Override
    public void setRadius(CardViewDelegate cardView, float radius) {
        getShadowBackground(cardView).setCornerRadius(radius);
        updatePadding(cardView);
    }

    @Override
    public float getRadius(CardViewDelegate cardView) {
        return getShadowBackground(cardView).getCornerRadius();
    }

    @Override
    public void setElevation(CardViewDelegate cardView, float elevation) {
        getShadowBackground(cardView).setShadowSize(elevation);
    }

    @Override
    public float getElevation(CardViewDelegate cardView) {
        return getShadowBackground(cardView).getShadowSize();
    }

    @Override
    public void setMaxElevation(CardViewDelegate cardView, float maxElevation) {
        getShadowBackground(cardView).setMaxShadowSize(maxElevation);
        updatePadding(cardView);
    }

    @Override
    public float getMaxElevation(CardViewDelegate cardView) {
        return getShadowBackground(cardView).getMaxShadowSize();
    }

    @Override
    public float getMinWidth(CardViewDelegate cardView) {
        return getShadowBackground(cardView).getMinWidth();
    }

    @Override
    public float getMinHeight(CardViewDelegate cardView) {
        return getShadowBackground(cardView).getMinHeight();
    }

    private RoundRectDrawableWithShadow getShadowBackground(CardViewDelegate cardView) {
        return ((RoundRectDrawableWithShadow) cardView.getBackground());
    }
}
