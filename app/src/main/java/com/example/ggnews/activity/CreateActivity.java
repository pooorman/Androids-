package com.example.ggnews.activity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.ggnews.response.BaseResponse;
import com.example.ggnews.Constants;
import com.example.ggnews.R;
import com.example.ggnews.UploadData;
import com.example.ggnews.adapter.MediaCursorAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateActivity extends AppCompatActivity {

  private ContentResolver mContentResolver;
  private ListView mPlaylist;
  private MediaCursorAdapter mCursorAdapter;


  private final String SELECTION =
    MediaStore.Images.Media.MIME_TYPE + " LIKE ? ";
  private final String[] SELECTION_ARGS = {
    "image/%"
  };

  private final int REQUEST_EXTERNAL_STORAGE = 1;

  private static String[] PERMISSIONS_STORAGE = {
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create);

    mPlaylist = findViewById(R.id.ce_photo_list);
    mContentResolver = getContentResolver();

    mCursorAdapter = new MediaCursorAdapter(CreateActivity.this);
    mCursorAdapter.setUserID(getIntent().getStringExtra("id"));
    mPlaylist.setAdapter(mCursorAdapter);




    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(CreateActivity.this,
        Manifest.permission.READ_EXTERNAL_STORAGE)) {
        //
      } else {
        requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
      }
    } else {
      initView();
      initPlaylist();
    }
  }

  private void sendRequest(){
    new Thread(new Runnable() {

      Gson gson = new Gson();
      String fileListJson = gson.toJson(mCursorAdapter.getArrayList());

      FormBody formBody = new FormBody.Builder()
        .add("fileList",fileListJson)
        .build();
      @Override
      public void run() {
        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"image/upload")
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
                //传递下一步所需的参数
                mCursorAdapter.setImageCode(Response.getData().getImageCode());
                //传入下一步所需的图片地址
                mCursorAdapter.setUrl(Response.getData().getImageUrlList().get(0));
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


  private void initView() {
    ImageView showImage = findViewById(R.id.ce_showImage);
    Button continues = findViewById(R.id.create_continue);
    //未获取先写死
    Glide.with(this).load("")
      .into(showImage);

    //绑定事件
    continues.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //点击继续按钮，上传文件（发送请求）
        sendRequest();

        //跳转发布activity
        Intent intent = new Intent(CreateActivity.this, PublishActivity.class);
        //Intent存登录内容
        intent.putExtra("id", mCursorAdapter.getUserID());
        intent.putExtra("imageCode",mCursorAdapter.getImageCode());
        intent.putExtra("imageUrl",mCursorAdapter.getUrl());
        startActivity(intent);
      }
    });
  }

  private void initPlaylist() {
    String cameraPath = "internal storage"; // 相机相册的路径
    String selection = MediaStore.Images.Media.DATA + " LIKE ?";
    String[] selectionArgs = new String[]{ "%camera%" };

    Cursor mCursor = mContentResolver.query(
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      null,
      selection,
      selectionArgs,
      MediaStore.Images.Media.DEFAULT_SORT_ORDER
    );


    if (mCursor != null) {
      int count = mCursor.getCount();
      if (count > 0) {
        // 查询到元素
      } else {
        // 没有查询到元素
      }
    } else {
      // 查询失败
    }
      mCursorAdapter.swapCursor(mCursor);
      mCursorAdapter.notifyDataSetChanged();
    }



  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case REQUEST_EXTERNAL_STORAGE:
        if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          initPlaylist();
        }
        break;
      default:
        break;
    }
  }





}
