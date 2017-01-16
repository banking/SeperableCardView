package ui.component.banking.separablecardview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by banking on 17/1/6.
 */
public class SampleAdapter extends RecyclerView.Adapter<SampleViewHolder> {

    protected List<ListDataBean> mDataList = new ArrayList<>();
    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SampleViewHolder(parent, R.layout.sample_item_layout);
    }

    public void setDataList(List<ListDataBean> dataList) {
        mDataList = dataList;
    }
    @Override
    public void onBindViewHolder (SampleViewHolder holder, int position) {

        ListDataBean data = mDataList.get(position);
        SampleViewHolder viewHolder = (SampleViewHolder) holder;
        viewHolder.showView(data);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount () {
        return mDataList.size();
    }
}
