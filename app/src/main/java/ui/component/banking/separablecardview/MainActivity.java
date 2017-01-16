package ui.component.banking.separablecardview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    protected RecyclerView mRecycleView;
    protected LinearLayoutManager mLayoutManager;
    protected SampleAdapter mAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecycleView = (RecyclerView) findViewById(R.id.demo_recycle_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(mLayoutManager);
        mAdapter = new SampleAdapter();
        mAdapter.setDataList(mockData());
        mRecycleView.setAdapter(mAdapter);
        int i = 10;
        int x = i >>> 4;
    }

    public List<ListDataBean> mockData() {
        List<ListDataBean> dataBeanList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ListDataBean listDataBean = new ListDataBean();
            listDataBean.bgType = i;
            listDataBean.title = "数据标题" + i;
            listDataBean.startAddress = "出发";
            listDataBean.endAddress = "到达";
            dataBeanList.add(listDataBean);
        }
        return dataBeanList;
    }


}
