package com.example.ggnews.activity;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ggnews.Constants;
import com.example.ggnews.R;
import com.example.ggnews.UploadData;
import com.example.ggnews.response.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PublishActivity  extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_publish);

    initView();
    initData();

  }

  private void initData() {
  }


  private void initView() {
    Button publish = findViewById(R.id.create_publish);
    EditText head = findViewById(R.id.create_head);
    EditText content = findViewById(R.id.create_content);
    ImageView showEdit = findViewById(R.id.create_showEdit);
    Glide.with(PublishActivity.this).load(getIntent().getStringExtra("imageUrl"))
      .into(showEdit);
    TextView save = findViewById(R.id.create_save);
    publish.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //点击发布按钮
        String cHead = head.getText().toString();
        String  cContent= content.getText().toString();

        new Thread(new Runnable() {


          FormBody formBody = new FormBody.Builder()
            .add("title",cHead)
            .add("content",cContent)
            .add("pUserId",getIntent().getStringExtra("id"))
            .add("imageCode",getIntent().getStringExtra("imageCode"))
            .build();
          @Override
          public void run() {
            Request request = new Request.Builder()
              .url(Constants.SERVER_URL2+"share/add")
              .addHeader("appId","37baffe1646a4411a338eb820a131176")
              .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
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
                    new TypeToken<BaseResponse<UploadData>>() {}.getType();
                  BaseResponse<UploadData> Response =
                    gson.fromJson(body, jsonType);
                  if (Response.getCode()==200) {



                  } else {

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
    save.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //点击发布按钮
        String cHead = head.getText().toString();
        String  cContent= content.getText().toString();

        new Thread(new Runnable() {


          FormBody formBody = new FormBody.Builder()
            .add("title",cHead)
            .add("content",cContent)
            .add("pUserId",getIntent().getStringExtra("id"))
            .add("imageCode",getIntent().getStringExtra("imageCode"))
            .build();
          @Override
          public void run() {
            Request request = new Request.Builder()
              .url(Constants.SERVER_URL2+"share/save")
              .addHeader("appId","37baffe1646a4411a338eb820a131176")
              .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
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
                    new TypeToken<BaseResponse<UploadData>>() {}.getType();
                  BaseResponse<UploadData> Response =
                    gson.fromJson(body, jsonType);
                  if (Response.getCode()==200) {



                  } else {

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
