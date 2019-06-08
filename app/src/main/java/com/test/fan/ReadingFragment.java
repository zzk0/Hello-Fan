package com.test.fan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.test.adapter.ReadingsRecycleAdapter;
import com.test.model.Readings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadingFragment extends Fragment {
    private String currentUrl="http://big5.taiwan.cn/taiwan/jsxw/index";
    private View toast_view;
    private Context context;
    private int index=0;
    private RefreshLayout refreshLayout;
    private Handler handler;
    private RecyclerView recyclerView;
    private View view;
    private ReadingsRecycleAdapter adapter;
    private List<Readings> readingsList;
    public ReadingFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=getContext();
        view = inflater.inflate(R.layout.fragment_readings_recycleview, null);
        toast_view=inflater.inflate(R.layout.toast_view,null);
        recyclerView=(RecyclerView)view.findViewById(R.id.readings_rv);
        refreshLayout=(RefreshLayout)view.findViewById(R.id.smartLayout);
        readingsList =new ArrayList<Readings>();
        initDisplay();
        return view;
    }
    private void initDisplay()
    {
        recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        setTextViewListener();
        setRecyclerViewRefreshListener();
        setRecyclerViewLoadMoreListener();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getReadingsInfoToList(readingsList);
            }
        }).start();
        adapter = new ReadingsRecycleAdapter(getContext(), readingsList);
        handler=new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    recyclerView.setAdapter(adapter);
                    msg.what=0;
                }
            }
        };
    }
    private void setTextViewListener()
    {
        final Map<TextView,String> map=new HashMap<>();
        map.put((TextView)view.findViewById(R.id.zuixin),"http://big5.taiwan.cn/taiwan/jsxw/index");
        map.put((TextView)view.findViewById(R.id.taiwan),"http://big5.taiwan.cn/taiwan/index");
        map.put((TextView)view.findViewById(R.id.pinglun),"http://big5.taiwan.cn/plzhx/index");
        map.put((TextView)view.findViewById(R.id.lilun),"http://big5.taiwan.cn/lilunpindao/index");
        map.put((TextView)view.findViewById(R.id.liangan),"http://big5.taiwan.cn/xwzx/la/index");
        map.put((TextView)view.findViewById(R.id.thirty_one),"http://big5.taiwan.cn/31t/index");
        map.put((TextView)view.findViewById(R.id.shishi),"http://big5.taiwan.cn/xwzx/index");
        map.put((TextView)view.findViewById(R.id.jingmao),"http://big5.taiwan.cn/gate/big5/econ.taiwan.cn/index");
        map.put((TextView)view.findViewById(R.id.taishang),"http://big5.taiwan.cn/gate/big5/taishang.taiwan.cn/index");
        map.put((TextView)view.findViewById(R.id.wenhua),"http://big5.taiwan.cn/gate/big5/culture.taiwan.cn/index");
        map.put((TextView)view.findViewById(R.id.lvyou),"http://big5.taiwan.cn/gate/big5/travel.taiwan.cn/index");
        map.put((TextView)view.findViewById(R.id.difang),"http://big5.taiwan.cn/local/index");
        map.put((TextView)view.findViewById(R.id.buwei),"http://big5.taiwan.cn/gate/big5/depts.taiwan.cn/index");
        for(final TextView tv:map.keySet())
        {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv.setBackgroundColor(Color.WHITE);
                    for(final TextView tv_other:map.keySet())
                    {
                        if(tv_other!=tv)
                            tv_other.setBackgroundColor(0xdddddd);
                    }
                    index=0;
                    currentUrl=map.get(tv);
                    final List<Readings> newReadingsList=new ArrayList<>();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getReadingsInfoToList(newReadingsList);
                        }
                    }).start();
                    handler=new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                adapter.refresh(newReadingsList);
                                showToast("加载成功");
                                msg.what=0;
                            }
                        }
                    };

                }
            });
        }
    }

    private void setRecyclerViewRefreshListener()
    {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                final List<Readings> newReadingsList=new ArrayList<>();
                index++;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getReadingsInfoToList(newReadingsList);
                    }
                }).start();
                handler=new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            adapter.recommend(newReadingsList);
                            refreshlayout.finishRefresh();
                            showToast("已为你推荐了10条更新");
                            msg.what=0;

                        }
                    }
                };
            }
        });
    }
    private void setRecyclerViewLoadMoreListener()
    {
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(final RefreshLayout refreshlayout) {
                final List<Readings> newReadingsList=new ArrayList<Readings>();
                index++;
                System.out.println(index);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getReadingsInfoToList(newReadingsList);
                    }
                }).start();
                handler=new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            adapter.load(newReadingsList);
                            showToast("已为你加载了10条更新");
                            refreshlayout.finishLoadmore();
                            msg.what=0;

                        }
                    }
                };
            }
        });
    }
    private void showToast(String s)
    {
        Toast toast=new Toast(context);
        TextView toast_tv=(TextView)toast_view.findViewById(R.id.toast_tv);
        toast_tv.setText(s);
        toast.setView(toast_view);
        toast.setGravity(Gravity.TOP,0,75);
        toast.show();
    }
    private void getReadingsInfoToList(List<Readings> readingsList){
        try{
                    String title,content_url,img_url,date,content,digest,group;
                    title=content_url=img_url=date=content=digest=group="";
                    String pageIndex=((index==0)?"":"_")+((index==0)?"":index);
                    Document readings_info = Jsoup.connect(currentUrl+pageIndex+".htm").get();
                    Elements titleLinks = readings_info.select("ul.list>li");    //解析来获取每条新闻的标题与链接地址
                    int size=titleLinks.size()>10?10:titleLinks.size();
                    for(int j = 0;j <size ;j++){
                        Element titleLink=titleLinks.get(j);
                        title = titleLink.select("h2").text();
                        content_url = titleLink.select("a").attr("href");
                        img_url=titleLink.select("img").attr("src");
                        date=titleLink.select("p.info>span").text();
                        group=titleLink.select("p.info>a").text();
                        Elements p_elements=titleLink.select("p>a");
                        if(p_elements.size()>0)
                            digest=p_elements.get(0).text();
                        if(p_elements.size()>1)
                        group=p_elements.get(1).text();
                        Readings readings = new Readings(title,content_url,group,digest,content,img_url,date);
                        readingsList.add(readings);
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
    }

}





