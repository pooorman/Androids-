package com.example.ggnews.activity;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ggnews.Constants;
import com.example.ggnews.R;
import com.example.ggnews.UploadData;
import com.example.ggnews.response.BaseResponse;
import com.example.ggnews.response.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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

  private void senRequest(String path,RequestBody requestBody){
    new Thread(new Runnable() {
      @Override
      public void run() {
        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+path)
          .addHeader("appId","37baffe1646a4411a338eb820a131176")
          .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
          .post(requestBody).build();
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
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      new AlertDialog.Builder(PublishActivity.this)
                        .setTitle("提示")
                        .setMessage("分享成功")
                        .setPositiveButton("确定", null)
                        .show();
                    }
                  });


              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(PublishActivity.this)
                      .setTitle("提示")
                      .setMessage(Response.getMsg())
                      .setPositiveButton("确定", null)
                      .show();
                  }
                });
              }
              Intent intent = new Intent(PublishActivity.this, CreateActivity.class);
              //Intent存登录内容
              LoginResponse loginData = (LoginResponse) getIntent().getSerializableExtra("LoginData");
              intent.putExtra("LoginData", loginData);
              intent.putExtra("id", loginData.getId());
              startActivity(intent);
            }
          });
        } catch (NetworkOnMainThreadException ex) {
          ex.printStackTrace();
        }
      }
    }).start();
  }

  private void initView() {
    Button publish = findViewById(R.id.create_publish);
    EditText head = findViewById(R.id.create_head);
    EditText content = findViewById(R.id.create_content);
    ImageView showEdit = findViewById(R.id.create_showEdit);
    Glide.with(PublishActivity.this).load(getIntent().getStringExtra("imageUrl"))
      .into(showEdit);
    CheckBox save = findViewById(R.id.create_save);

    publish.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String cHead = head.getText().toString();
        String  cContent= content.getText().toString();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("title",cHead);
          jsonObject.put("content", cContent);
          jsonObject.put("pUserId", getIntent().getStringExtra("id"));
          jsonObject.put("imageCode", getIntent().getStringExtra("imageCode"));
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
        if(!save.isChecked()){
          senRequest("share/add",requestBody);
        }else {
          senRequest("share/save",requestBody);
        }



      }
    });
  }

}
