package ui.component.banking.separablecardview.ui.component;

import android.graphics.drawable.Drawable;

/**
 * Created by banking on 16/10/14.
 */
interface CardViewDelegate {
    void setBackgroundDrawable(Drawable paramDrawable);
    Drawable getBackground ();
    boolean getUseCompatPadding ();
    boolean getPreventCornerOverlap ();
    float getRadius ();
    void setShadowPadding (int left, int top, int right, int bottom);
}
