package com.example.ggnews.activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ggnews.Constants;
import com.example.ggnews.R;
import com.example.ggnews.adapter.RecordsAdapter;
import com.example.ggnews.javabean.Records;
import com.example.ggnews.request.RecordsRequest;
import com.example.ggnews.response.LoginResponse;
import com.example.ggnews.response.NewBaseResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

public class CareActivity extends AppCompatActivity {
  //数据源不同
  private ListView lvNewsList;
  private List<Records> newsData;
  private RecordsAdapter adapter;



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
            Type jsonType =
              new TypeToken<NewBaseResponse<List<Records>>>() {}.getType();
            NewBaseResponse<List<Records>> newsListResponse =
              gson.fromJson(body, jsonType);
            //获取每一个records，添加进adapter
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
    adapter = new RecordsAdapter(CareActivity.this,
      R.layout.record_item, newsData);

    //保存userId
    Intent intent = getIntent();
    if (intent != null) {
      String userId = intent.getStringExtra("id");
      adapter.setUserId(userId);
    }
    LoginResponse loginData = (LoginResponse) getIntent().getSerializableExtra("LoginData");
    adapter.setLoginResponse(loginData);
    lvNewsList.setAdapter(adapter);

    refreshData(1);
  }

  private void refreshData(final int page) {

    new Thread(new Runnable() {
      @Override
      public void run() {
        //路径变化
        String userId;
        RecordsRequest requestObj = new RecordsRequest();
        Intent intent = getIntent();
        userId = intent!=null?intent.getStringExtra("id"):null;
        requestObj.setUserId(Long.parseLong(userId));
        requestObj.setSize(Constants.NEWS_NUM);
        requestObj.setCurrent(page);
        String urlParams = requestObj.toString();
        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"/focus"+ urlParams)
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
    BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
    bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
      //导航栏增加
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.bottom1) {
          LoginResponse loginData = (LoginResponse) getIntent().getSerializableExtra("LoginData");
          Intent intent = new Intent(CareActivity.this, MainActivity.class);
          //存储数据(与selfActivity有区别)
//           //存入userID，是否loginactivity存入了
          intent.putExtra("id", loginData.getId());
          //存入LoginData
          intent.putExtra("LoginData", (LoginResponse) getIntent().getSerializableExtra("LoginData"));

          startActivity(intent);
          return true;
        } else if (item.getItemId() == R.id.bottom2) {
          Intent intent = new Intent(CareActivity.this, CreateActivity.class);
          //Intent存登录内容
          LoginResponse loginData = (LoginResponse) getIntent().getSerializableExtra("LoginData");
          intent.putExtra("id", loginData.getId());
          startActivity(intent);
          // 新增activity
          return true;
        } else if (item.getItemId() == R.id.bottom3) {
          // 主页activity
          Intent intent = new Intent(CareActivity.this, SelfActivity.class);

          //传递LoginData
          LoginResponse loginData = (LoginResponse) getIntent().getSerializableExtra("LoginData");
          intent.putExtra("LoginData", loginData);
          intent.putExtra("id", loginData.getId());
          //跳转主页
          startActivity(intent);
          return true;
        }else if(item.getItemId() == R.id.bottom4){
          //自己
        }
        return false;
      }
    });
  }
}
