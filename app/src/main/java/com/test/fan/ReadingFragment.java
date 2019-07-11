package com.test.fan;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.test.adapter.ReadingsRecycleAdapter;
import com.test.model.entity.Readings;
import com.test.util.ACache;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.test.util.Constant.BUWEI;
import static com.test.util.Constant.DIFANG;
import static com.test.util.Constant.JINGMAO;
import static com.test.util.Constant.LIANGAN;
import static com.test.util.Constant.LILUN;
import static com.test.util.Constant.LVYOU;
import static com.test.util.Constant.PINGLUN;
import static com.test.util.Constant.SHISHI;
import static com.test.util.Constant.TAISHANG;
import static com.test.util.Constant.TAIWAN;
import static com.test.util.Constant.THIRTY_ONE;
import static com.test.util.Constant.WENHUA;
import static com.test.util.Constant.ZUIXIN;

public class ReadingFragment extends Fragment {
    private ACache aCache;
    private String currentUrlStart;
    private String currentUrl;
    private TextView loading;
    private View toast_view;
    private Context context;
    private int index = 0;
    private RefreshLayout refreshLayout;
    private Handler handler;
    private RecyclerView recyclerView;
    private View view;
    private ReadingsRecycleAdapter adapter;
    private ArrayList<Readings> readingsList;

    public ReadingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_readings_recycleview, null);
        toast_view = inflater.inflate(R.layout.toast_view, null);
        loading = (TextView) view.findViewById(R.id.loading);
        recyclerView = (RecyclerView) view.findViewById(R.id.readings_rv);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.smartLayout);
        aCache = ACache.get(context);
        initDisplay();
        return view;
    }

    private void initDisplay() {
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        setTextViewListener();
        setRecyclerViewRefreshListener();
        setRecyclerViewLoadMoreListener();
        //提取网址
        currentUrlStart=ZUIXIN;
        String pageIndex = ((index == 0) ? "" : "_") + ((index == 0) ? "" : index);
        currentUrl=currentUrlStart + pageIndex + ".htm";
        readingsList= (ArrayList<Readings>) aCache.getAsObject(currentUrl);
        if (readingsList != null) {
            loading.setVisibility(View.INVISIBLE);
            adapter=new ReadingsRecycleAdapter(context,readingsList);
            recyclerView.setAdapter(adapter);
        } else {
            readingsList = new ArrayList<>();
            loading.setVisibility(View.VISIBLE);
            loading.setText("加载中...");
            handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        aCache.put(currentUrl,readingsList,43200);
                        loading.setVisibility(View.INVISIBLE);
                        adapter=new ReadingsRecycleAdapter(context,readingsList);
                        recyclerView.setAdapter(adapter);
                    }
                    if (msg.what == 2) {
                        loading.setText("网络连接超时(；′⌒`)");
                    }
                    if (msg.what == 3) {
                        loading.setText("加载失败(；′⌒`)");
                    }
                    msg.what = 0;
                }
            };
            getReadingsInfoToList(readingsList);
        }

    }

    private void setTextViewListener() {
        final Map<TextView, String> map = new HashMap<>();
        map.put((TextView) view.findViewById(R.id.zuixin), ZUIXIN);
        map.put((TextView) view.findViewById(R.id.taiwan), TAIWAN);
        map.put((TextView) view.findViewById(R.id.pinglun), PINGLUN);
        map.put((TextView) view.findViewById(R.id.lilun), LILUN);
        map.put((TextView) view.findViewById(R.id.liangan), LIANGAN);
        map.put((TextView) view.findViewById(R.id.thirty_one), THIRTY_ONE);
        map.put((TextView) view.findViewById(R.id.shishi), SHISHI);
        map.put((TextView) view.findViewById(R.id.jingmao), JINGMAO);
        map.put((TextView) view.findViewById(R.id.taishang), TAISHANG);
        map.put((TextView) view.findViewById(R.id.wenhua), WENHUA);
        map.put((TextView) view.findViewById(R.id.lvyou), LVYOU);
        map.put((TextView) view.findViewById(R.id.difang), DIFANG);
        map.put((TextView) view.findViewById(R.id.buwei), BUWEI);
        for (final TextView tv : map.keySet()) {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv.setBackgroundColor(Color.WHITE);
                    for (final TextView tv_other : map.keySet()) {
                        if (tv_other != tv)
                            tv_other.setBackgroundColor(0xdddddd);
                    }
                    index = 0;
                    currentUrlStart = map.get(tv);
                    String pageIndex = ((index == 0) ? "" : "_") + ((index == 0) ? "" : index);
                    currentUrl=currentUrlStart + pageIndex + ".htm";
                    readingsList= (ArrayList<Readings>) aCache.getAsObject(currentUrl);
                    if (readingsList != null) {
                        loading.setVisibility(View.INVISIBLE);
                        adapter.refresh(readingsList);
                        recyclerView.setAdapter(adapter);
                        showToast("加载成功");
                    }
                    else {
                        readingsList = new ArrayList<>();
                        loading.setVisibility(View.VISIBLE);
                        loading.setText("加载中...");
                        handler = new Handler() {
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    aCache.put(currentUrl,readingsList,43200);
                                    adapter.refresh(readingsList);
                                    loading.setVisibility(View.INVISIBLE);
                                    showToast("加载成功");
                                }
                                if (msg.what == 2) {
                                    loading.setText("网络连接超时(；′⌒`)");
                                }
                                if (msg.what == 3) {
                                    loading.setText("加载失败(；′⌒`)");
                                }
                                msg.what = 0;

                            }
                        };
                        getReadingsInfoToList(readingsList);
                    }

                }
            });
        }
    }

    private void setRecyclerViewRefreshListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                index=0;
                String pageIndex = ((index == 0) ? "" : "_") + ((index == 0) ? "" : index);
                currentUrl=currentUrlStart + pageIndex + ".htm";
                readingsList=new ArrayList<>();
                handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            adapter.refresh(readingsList);
                            aCache.put(currentUrl,readingsList,43200);
                            refreshlayout.finishRefresh();
                            loading.setVisibility(View.INVISIBLE);
                            showToast("刷新成功");
                        }
                        else if(msg.what!=0)
                        {
                            refreshlayout.finishRefresh();
                            showToast("刷新失败");
                        }
                        msg.what = 0;
                    }
                };
                getReadingsInfoToList(readingsList);
            }
        });
    }

    private void setRecyclerViewLoadMoreListener() {
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                index++;
                String pageIndex = ((index == 0) ? "" : "_") + ((index == 0) ? "" : index);
                currentUrl=currentUrlStart + pageIndex + ".htm";
                readingsList= (ArrayList<Readings>) aCache.getAsObject(currentUrl);
                if (readingsList != null) {
                    adapter.load(readingsList);
                    refreshlayout.finishLoadmore();
                    recyclerView.setAdapter(adapter);
                }
                else {
                    readingsList = new ArrayList<Readings>();
                    handler = new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                refreshlayout.finishLoadmore();
                                adapter.load(readingsList);
                                showToast("已为你加载了10条更新");
                                loading.setVisibility(View.INVISIBLE);
                                aCache.put(currentUrl,readingsList,43200);
                            }
                            else if(msg.what!=0)
                            {
                                refreshlayout.finishLoadmore();
                                showToast("加载失败");
                            }
                            msg.what = 0;
                        }
                    };
                    getReadingsInfoToList(readingsList);
                }
            }
        });
    }

    private void showToast(String s) {
        Toast toast = new Toast(context);
        TextView toast_tv = (TextView) toast_view.findViewById(R.id.toast_tv);
        toast_tv.setText(s);
        toast.setView(toast_view);
        toast.setGravity(Gravity.TOP, 0, 75);
        toast.show();
    }

    private void getReadingsInfoToList(final List<Readings> readingsList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                try {
                    String title, content_url, img_url, date, digest, group;
                    digest = "";
                    Connection conn = Jsoup.connect(currentUrl);
                    conn.userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50");
                    Document readings_info = conn.get();
                    Elements titleLinks = readings_info.select("ul.list>li");    //解析来获取每条新闻的标题与链接地址
                    int size = titleLinks.size() > 10 ? 10 : titleLinks.size();
                    for (int j = 0; j < size; j++) {
                        Element titleLink = titleLinks.get(j);
                        title = titleLink.select("h2").text();
                        content_url = titleLink.select("a").attr("href");
                        img_url = titleLink.select("img").attr("src");
                        date = titleLink.select("p.info>span").text();
                        group = titleLink.select("p.info>a").text();
                        Elements p_elements = titleLink.select("p>a");
                        if (p_elements.size() > 0)
                            digest = p_elements.get(0).text();
                        if (p_elements.size() > 1)
                            group = p_elements.get(1).text();
                        Readings readings = new Readings(title, content_url, group, digest, img_url, date);
                        readingsList.add(readings);
                    }
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    if (e instanceof TimeoutException) {
                        message.what = 2;
                    } else {
                        message.what = 3;
                    }
                    handler.sendMessage(message);
                }
            }
        }).start();

    }

}





