package com.example.ggnews.activity;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ggnews.response.BaseResponse;
import com.example.ggnews.javabean.Comment;
import com.example.ggnews.response.CommentResponse;
import com.example.ggnews.Constants;
import com.example.ggnews.response.LoginResponse;
import com.example.ggnews.R;
import com.example.ggnews.request.RecordsRequest;
import com.example.ggnews.adapter.CommentAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

  private ListView lvNewsList;
  private List<Comment> newsData;
  private CommentAdapter adapter;
  //定义变量
  private SwipeRefreshLayout mSwipeLayout;

  private TextView coSum;


  @Override
  public void onRefresh() {
    initData();
    mSwipeLayout.setRefreshing(false);
  }
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
              new TypeToken<BaseResponse<CommentResponse>>() {}.getType();
            BaseResponse<CommentResponse>newsListResponse =
              gson.fromJson(body, jsonType);
            //获取每一个records，添加进adapter
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                coSum.setText("全部评论（共"+newsListResponse.getData().getTotal()+"条）");
              }
            });
            for (Comment comment:newsListResponse.getData().getRecords()) {
              adapter.add(comment);
            }

            adapter.notifyDataSetChanged();
          }
        });
      } else {
//        暂时没有评论界面

      }
    }
  };




  private int page = 1;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comment);

    initView();
    initData();

  }
  private void initData() {
    newsData = new ArrayList<>();
    adapter = new CommentAdapter(CommentActivity.this,
      R.layout.comment_item, newsData,findViewById(R.id.co_edit),findViewById(R.id.co_publish));

    //保存userId
    Intent intent = getIntent();
    if (intent != null) {
      String userId = intent.getStringExtra("id");
      adapter.setUserId(userId);
      LoginResponse loginData1 = (LoginResponse) intent.getSerializableExtra("loginData1");
      adapter.setLoginResponse(loginData1);
    }
    lvNewsList.setAdapter(adapter);

    refreshData(1);
  }

  private void refreshData(final int page) {

    new Thread(new Runnable() {
      @Override
      public void run() {
        String userId;
        RecordsRequest requestObj = new RecordsRequest();
//                requestObj.setCol(mCols[mCurrentColIndex]);
        Intent intent = getIntent();
//                追加数量
        requestObj.setSize(Constants.NEWS_NUM);
        requestObj.setCurrent(page);
        String urlParams = requestObj.toString();
        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"comment/first"+ urlParams+"&shareId="+intent.getStringExtra("shareId"))
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
    //下拉刷新
    mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
    mSwipeLayout.setOnRefreshListener(this);
//设置加载动画背景颜色
    mSwipeLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(android.R.color.background_light));
//设置进度动画的颜色
    mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    lvNewsList = findViewById(R.id.co_list);
    Button viewById = findViewById(R.id.co_publish);
    EditText editText = findViewById(R.id.co_edit);
    coSum = findViewById(R.id.co_sum);
    //这里为什么取用不了,大小写！
    LoginResponse loginData = (LoginResponse) getIntent().getSerializableExtra("loginData1");
    //头像显示，加圆形处理，记得。


    viewById.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Editable text = editText.getText();
        new Thread(new Runnable() {
          @Override
          public void run() {
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            try {
              jsonObject.put("content", text.toString());
              jsonObject.put("shareId", getIntent().getStringExtra("shareId"));
              jsonObject.put("userId", loginData.getId());
              jsonObject.put("userName", loginData.getUsername());
            } catch (JSONException e) {
              e.printStackTrace();
            }
            RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());


            Request request = new Request.Builder()
              .url(Constants.SERVER_URL2+"comment/first")
              .addHeader("appId","37baffe1646a4411a338eb820a131176")
              .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
              .addHeader("Accept", "application/json, text/plain, */*")
              .post(formBody).build();
            try {
              OkHttpClient client = new OkHttpClient();
              client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                  Log.e(TAG, "Failed to connect server!");
                  e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response)
                  throws IOException {
                  final String body = response.body().string();
                  Gson gson = new Gson();
                  Type jsonType =
                    new TypeToken<BaseResponse<Objects>>() {}.getType();
                  BaseResponse<Objects> Response =
                    gson.fromJson(body, jsonType);
                  if (Response.getCode()==200) {
                    editText.setHint("留下你精彩的评论吧！");
                    editText.setText(null);
                    runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                        //1.构造一个自己加 2.重新请求
                        initData();
                        new AlertDialog.Builder(CommentActivity.this)
                          .setMessage("添加成功")
                          .show();

                      }
                    });


                  } else {
                    runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                        new AlertDialog.Builder(CommentActivity.this)
                          .setMessage(Response.getMsg())
                          .show();
                      }
                    });

                  }
                }
              });
            } catch (NetworkOnMainThreadException ex) {
              ex.printStackTrace();
            }
          }
        }).start();


      }
    });

  }








}
