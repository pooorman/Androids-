package com.example.ggnews;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ListView lvNewsList;
    private List<Records> newsData;

    private RecordsAdapter adapter;


    private int page = 1;

    private int mCurrentColIndex = 0;

    private int[] mCols = new int[]{Constants.NEWS_COL5,
            Constants.NEWS_COL7, Constants.NEWS_COL8,
            Constants.NEWS_COL10, Constants.NEWS_COL11};
    private okhttp3.Callback callback = new okhttp3.Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "Failed to connect server!");
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response)
                throws IOException {
            if (response.isSuccessful()) {
                final String body = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
//                        Type jsonType =
//                                new TypeToken<BaseResponse<List<News>>>() {}.getType();
//                        BaseResponse<List<News>> newsListResponse =
//                                gson.fromJson(body, jsonType);
                      Type jsonType =
                        new TypeToken<NewBaseResponse<List<Records>>>() {}.getType();
                      NewBaseResponse<List<Records>> newsListResponse =
                        gson.fromJson(body, jsonType);
                        for (Records records:newsListResponse.getData().getRecords()) {
                            adapter.add(records);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            } else {
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

    }
    private void initData() {
        newsData = new ArrayList<>();
        adapter = new RecordsAdapter(MainActivity.this,
                R.layout.record_item, newsData);
        lvNewsList.setAdapter(adapter);

        refreshData(1);
    }

    private void refreshData(final int page) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                RecordsRequest requestObj = new RecordsRequest();
//                requestObj.setCol(mCols[mCurrentColIndex]);
                requestObj.setUserId(1);
                requestObj.setSize(Constants.NEWS_NUM);
                requestObj.setCurrent(page);
                String urlParams = requestObj.toString();

                Request request = new Request.Builder()
                        .url(Constants.SERVER_URL2+"/share"+ urlParams)
                        .addHeader("appId","37baffe1646a4411a338eb820a131176")
                        .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
                        .get().build();
                try {
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(callback);
                } catch (NetworkOnMainThreadException ex) {

                    ex.printStackTrace();
                }
            }
        }).start();
    }
    private void initView() {
        lvNewsList = findViewById(R.id.lv_news_list);
//        lvNewsList.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView,
//                                            View view, int i, long l) {
////                        跳转
//                        Intent intent = new Intent(MainActivity.this,
//                                DetailActivity.class);
//
//                        News news = adapter.getItem(i);
//                        intent.putExtra(Constants.NEWS_DETAIL_URL_KEY,
//                                news.getContentUrl());
//
//                        startActivity(intent);
//                    }
//                });
    }
}
