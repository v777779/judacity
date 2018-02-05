package ru.vpcb.footballassistant.add;

import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.vpcb.footballassistant.R;
import ru.vpcb.footballassistant.RecyclerAdapter;
import ru.vpcb.footballassistant.data.FDFixture;
import ru.vpcb.footballassistant.utils.Config;

import static ru.vpcb.footballassistant.utils.Config.EMPTY_FIXTURE_DATE;

/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 29-Sep-17
 * Email: vadim.v.voronov@gmail.com
 */

public class ViewPagerAdapterDynamic extends PagerAdapter {
    private List<List<FDFixture>> listPages;
    private List<String> listTitles;
    private AppCompatActivity mActivity;

    /**
     * Constructor  created object from List<View>> data source
     *
     * @param listPages input List<View> data source
     */
    public ViewPagerAdapterDynamic(AppCompatActivity activity, List<List<FDFixture>> listPages, List<String> listTitles) {
        this.mActivity = activity;
        this.listPages = listPages;
        this.listTitles = listTitles;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getRecycler(listPages.get(position));
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if(listPages == null) return 0;
        return listPages.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (listTitles == null || position < 0 || position >= listTitles.size())
            return EMPTY_FIXTURE_DATE;
        String title = listTitles.get(position);
        if (title == null) title = EMPTY_FIXTURE_DATE;
        return title;
    }

    public void swap(List<List<FDFixture>> list, List<String> titles) {
        if (list == null || list.isEmpty() || titles == null || titles.isEmpty()) return;

        listPages = list;
        listTitles = titles;
        notifyDataSetChanged();

    }

    private RecyclerView getRecycler(List<FDFixture> list) {
//        Config.Span sp = Config.getDisplayMetrics(mActivity);
        Config.Span sp = null;


        View recyclerLayout = mActivity.getLayoutInflater().inflate(R.layout.recycler_main, null);
        RecyclerView recyclerView = recyclerLayout.findViewById(R.id.recycler_main_container);

        RecyclerAdapter adapter = new RecyclerAdapter(mActivity, sp, list);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(layoutManager);
        return recyclerView;
    }
}
