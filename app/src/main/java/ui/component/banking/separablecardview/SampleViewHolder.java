package ui.component.banking.separablecardview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import ui.component.banking.separablecardview.ui.component.CardView;
import ui.component.banking.separablecardview.ui.component.CardViewImpl;

/**
 * Created by banking on 17/1/6.
 */
public class SampleViewHolder extends RecyclerView.ViewHolder {

    TextView titleTv,startTv,endTv;
    CardView mContainer;
    public SampleViewHolder(ViewGroup parent, int layoutID) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false));
        mContainer = (CardView) itemView.findViewById(R.id.home_container);
        titleTv = (TextView) itemView.findViewById(R.id.home_route_time);
        startTv = (TextView) itemView.findViewById(R.id.home_route_start);
        endTv = (TextView) itemView.findViewById(R.id.home_route_end);
    }

    public void showView(ListDataBean data) {
        switch (data.bgType) {
            case ListDataBean.BG_TYPE_ALL_ROUND:
                setMarginBottom(40);
                setContainerBg(mContainer, CardViewImpl.TYPE_ALL_ROUND);
                break;
            case ListDataBean.BG_TYPE_TOP_ROUND:
                setMarginBottom(0);
                setContainerBg(mContainer, CardViewImpl.TYPE_ONLY_TOP);
                break;
            case ListDataBean.BG_TYPE_BOTTOM_ROUND:
                setMarginBottom(0);
                setContainerBg(mContainer, CardViewImpl.TYPE_ONLY_BOTTOM);
                break;
            case ListDataBean.BG_TYPE_NO_ROUND:
                setMarginBottom(0);
                setContainerBg(mContainer, CardViewImpl.TYPE_NO_ROUND);
                break;
        }
    }

    protected void setContainerBg(ViewGroup mContainer ,int cardType) {
        if(mContainer instanceof CardView) {
            ((CardView) mContainer).setCardType(cardType);
        }
    }

    private void setMarginBottom(int pxSize) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mContainer.getLayoutParams();
        params.setMargins(20, 0, 20, pxSize);
        mContainer.setLayoutParams(params);
    }
}
