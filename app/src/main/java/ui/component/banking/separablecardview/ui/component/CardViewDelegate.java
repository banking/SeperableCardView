package ui.component.banking.separablecardview.ui.component;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by banking on 16/10/14.
 */
interface CardViewDelegate {
    void setCardBackground(Drawable drawable);
    Drawable getCardBackground();
    boolean getUseCompatPadding();
    boolean getPreventCornerOverlap();
    void setShadowPadding(int left, int top, int right, int bottom);
    void setMinWidthHeightInternal(int width, int height);
    View getCardView();
}
