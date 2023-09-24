package com.example.ggnews.activity;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ggnews.Constants;
import com.example.ggnews.GridSpacingItemDecoration;
import com.example.ggnews.R;
import com.example.ggnews.request.RecordsRequest;
import com.example.ggnews.adapter.ImageItemAdapter;
import com.example.ggnews.javabean.Records;
import com.example.ggnews.response.BaseResponse;
import com.example.ggnews.response.LikeListResponse;
import com.example.ggnews.response.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserDetailActivity extends AppCompatActivity {
  private TextView introduce;
  private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
  private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private TextView sex;
  private TextView username;

  private TextView number;
  private TextView create;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_detail);
    sendUserDetail();
  }

  private okhttp3.Callback signup = new okhttp3.Callback() {
    @Override
    public void onFailure(Call call, IOException e) {
      Log.e(TAG, "Failed to connect server!");
      e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response)
      throws IOException {
      //接口是看内部的code,解析他的code
      final String body = response.body().string();
      Gson gson = new Gson();
      Type jsonType =
        new TypeToken<BaseResponse<Objects>>() {}.getType();
      BaseResponse<Objects> Response =
        gson.fromJson(body, jsonType);

      if (Response.getCode()==200) {

        runOnUiThread(new Runnable() {
          @Override
          public void run() {



          }
        });
      } else {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {

          }
        });
      }
    }
  };




  private okhttp3.Callback login = new okhttp3.Callback() {
    @Override
    public void onFailure(Call call, IOException e) {
      Log.e(TAG, "Failed to connect server!");
      e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response)
      throws IOException {
      //接口是看内部的code,解析他的code
      final String body = response.body().string();
      Gson gson = new Gson();
      Type jsonType =
        new TypeToken<BaseResponse<LoginResponse>>() {}.getType();
      BaseResponse<LoginResponse> Response =
        gson.fromJson(body, jsonType);

      if (Response.getCode()==200) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            initData(Response.getData());
          }
        });

      } else {


      }
    }
  };


  private void sendUserDetail(){
    String detailName = getIntent().getStringExtra("name");
      new Thread(new Runnable() {
        @Override
        public void run() {
          //请求路径
          Request request = new Request.Builder()
            .addHeader("appId", "37baffe1646a4411a338eb820a131176")
            .addHeader("appSecret", "37609f4e6965cf9384d88bfd237a20b5aa666")
            .url(Constants.SERVER_URL2 + "user/getUserByName?username=" + detailName)
            .get().build();

          try {
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(login);
          } catch (NetworkOnMainThreadException ex) {
//            主线程网络错误
            ex.printStackTrace();
          }
        }
      }).start();
  }

  private void initData(LoginResponse loginData) {
    //重新请求获取用户的数据


    //初始化上半部分的数据
    ImageView headImage = findViewById(R.id.de_headImage);
    Glide.with(this).load(loginData.getAvatar()==null?"https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/08/30/ee2927bf-6b67-4040-957c-b26c5a5343ab.jpg":loginData.getAvatar())
      .into(headImage);

    headImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // 创建一个Dialog对话框
        Dialog dialog = new Dialog(UserDetailActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_zoom);

        // 获取对话框中的ImageView和背景View
        ImageView zoomImage = dialog.findViewById(R.id.zoom_image);
        View background = dialog.findViewById(R.id.background);

        // 设置放大后的图片
        Glide.with(UserDetailActivity.this).load(loginData.getAvatar()==null?"https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/08/30/ee2927bf-6b67-4040-957c-b26c5a5343ab.jpg":loginData.getAvatar())
          .into(zoomImage);

        // 设置背景点击事件监听器
        background.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            // 关闭对话框
            dialog.dismiss();
          }
        });

        // 显示对话框
        dialog.show();
      }
    });



    introduce=findViewById(R.id.de_introduce);
    introduce.setText("自我介绍："+(loginData.getIntroduce()==null?"这个人很懒，什么也没说":loginData.getIntroduce()));


    number = findViewById(R.id.de_number);
    number.setText("上次修改："+CalTimeFormat(Long.parseLong(loginData.getLastUpdateTime())));
//    number.setText("上次修改："+sdf.format(new Date(Long.parseLong(loginData.getLastUpdateTime()))));

    create = findViewById(R.id.de_create);
    create.setText("账号创建时间："+sdf1.format(new Date(Long.parseLong(loginData.getCreateTime()))));

    sex=findViewById(R.id.de_sex);
    sex.setText("性别："+(loginData.getSex()==null?"暂未填写":(loginData.getSex()=="0"?"男":"女")));


    username=findViewById(R.id.de_username);
    username.setText(loginData.getUsername());




    refreshData(1);
  }



  private String CalTimeFormat(Long timestamp){
    long currentTimestamp = System.currentTimeMillis();

    long todayTimestamp = currentTimestamp;
    // 计算时间差（单位：毫秒）
    long timeDifference = todayTimestamp - timestamp;

    // 计算时间差对应的天数
    long days = timeDifference / (24 * 60 * 60 * 1000);

    Date date = new Date(timestamp);

    // 使用SimpleDateFormat格式化Date对象为字符串
    String timeString = sdf.format(date);

    // 根据时间差的天数进行判断并显示相应的时间格式
    if (days == 0) {
      return ("今天 " + timeString);
    } else if (days == 1) {
      return ("昨天 " + timeString);
    } else if (days == 2) {
      return ("前天 " + timeString);
    } else {
      return (days + "天前 ");
    }
  }



  private void refreshData(final int page) {
    //图片集合还有主页信息

  }



}
