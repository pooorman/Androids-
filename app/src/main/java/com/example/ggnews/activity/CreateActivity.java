package com.example.ggnews.activity;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ggnews.GridSpacingItemDecoration;
import com.example.ggnews.adapter.ImageItemAdapter;
import com.example.ggnews.response.BaseResponse;
import com.example.ggnews.Constants;
import com.example.ggnews.R;
import com.example.ggnews.UploadData;
import com.example.ggnews.adapter.MediaCursorAdapter;
import com.example.ggnews.response.LoginResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateActivity extends AppCompatActivity {

  private ContentResolver mContentResolver;
  public static final int REQUEST_CODE_OPEN_DOCUMENT = 100;
  private RecyclerView mPlaylist;
  private List<Uri> uriList;
  private  BaseResponse<UploadData> Response1;
  private String ImageCode;
  private String userID;
  private String url;

  public String getImageCode() {
    return ImageCode;
  }

  public void setImageCode(String imageCode) {
    ImageCode = imageCode;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  private ArrayList<File> arrayList = new ArrayList<>();
  private MediaCursorAdapter mCursorAdapter;
  private Button addImg;
  private ImageView showImage;



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




//    mCursorAdapter = new MediaCursorAdapter(CreateActivity.this);
//    mCursorAdapter.setUserID(getIntent().getStringExtra("id"));
//    mPlaylist.setAdapter(mCursorAdapter);
    initView();


//    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//      if (ActivityCompat.shouldShowRequestPermissionRationale(CreateActivity.this,
//        Manifest.permission.READ_EXTERNAL_STORAGE)) {
//        //
//      } else {
//        requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
//      }
//    } else {
//      initView();
//      initPlaylist();
//    }
  }

  private void sendRequest(){
    new Thread(new Runnable() {
      @Override
      public void run() {
    String url = "http://47.107.52.7:88/member/photo/image/upload";
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");


    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
      .setType(MultipartBody.FORM);

    for (File file : arrayList) {
      RequestBody fileRequestBody = RequestBody.create(MEDIA_TYPE_PNG, file);
      multipartBuilder.addFormDataPart("fileList", file.getName(), fileRequestBody);
    }

    MultipartBody multipartBody = multipartBuilder.build();



        Request request = new Request.Builder()
          .url(url)
          .addHeader("appId","37baffe1646a4411a338eb820a131176")
          .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
          .post(multipartBody).build();
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
              Log.d("imageBody",body);
              Gson gson = new Gson();
              Type jsonType =
                new TypeToken<BaseResponse<UploadData>>() {}.getType();
              BaseResponse<UploadData> Response =
                gson.fromJson(body, jsonType);
                Response1 = Response;
              if (Response.getCode()==200) {
                //传递下一步所需的参数
                setImageCode(Response.getData().getImageCode());
                //传入下一步所需的图片地址
                setUrl(Response.getData().getImageUrlList().get(0));
                Intent intent = new Intent(CreateActivity.this, PublishActivity.class);
                //Intent存登录内容
                LoginResponse loginData = (LoginResponse) getIntent().getSerializableExtra("LoginData");
                intent.putExtra("LoginData", loginData);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("imageCode",getImageCode());
                intent.putExtra("imageUrl",uriList.get(0).toString());
                startActivity(intent);


              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(CreateActivity.this)
                      .setTitle("上传提示")
                      .setMessage(Response.getMsg())
                      .setPositiveButton("确定", null)
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


  private void initView() {
    mPlaylist = findViewById(R.id.ce_photo_list);
    GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
    mPlaylist.setLayoutManager(layoutManager);

    int spacing = getResources().getDimensionPixelSize(R.dimen.item_spacing);
    int borderSize = getResources().getDimensionPixelSize(R.dimen.item_border_size);
    int borderColor = ContextCompat.getColor(this, R.color.item_border_color);
    mPlaylist.addItemDecoration(new GridSpacingItemDecoration(spacing, borderSize, borderColor));

    uriList = new ArrayList<>();
    mCursorAdapter = new MediaCursorAdapter(this, R.layout.image_item, uriList);
    mPlaylist.setAdapter(mCursorAdapter);


    BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewc);
    bottomNavigationView.setSelectedItemId(R.id.bottom2);
    bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.bottom1) {
          // do nothing
          //flash ur data and view
          //登录成功跳转
          Intent intent = new Intent(CreateActivity.this, MainActivity.class);

          //存储数据
          intent.putExtra("id", getIntent().getStringExtra("id"));
          intent.putExtra("LoginData", (LoginResponse) getIntent().getSerializableExtra("LoginData"));



          startActivity(intent);


          return true;
        } else if (item.getItemId() == R.id.bottom2) {
          // 新增activity
          return true;
        } else if (item.getItemId() == R.id.bottom3) {
          // 主页activity
          Intent intent = new Intent(CreateActivity.this, SelfActivity.class);
          //传递LoginData
          LoginResponse loginData = (LoginResponse) getIntent().getSerializableExtra("LoginData");
          intent.putExtra("LoginData", loginData);
          intent.putExtra("id", loginData.getId());
          //跳转主页
          startActivity(intent);
          return true;
        }else if (item.getItemId() == R.id.bottom4){
          Intent intent = new Intent(CreateActivity.this, CareActivity.class);

          //存储数据
          intent.putExtra("id", getIntent().getStringExtra("id"));
          intent.putExtra("LoginData", (LoginResponse) getIntent().getSerializableExtra("LoginData"));

          startActivity(intent);
          return true;
        }
        return false;
      }
    });
    showImage = findViewById(R.id.ce_showImage);
    Glide.with(this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQa8MwpRkpN8mzt04Y4gpQdRxLfmGu_hc-_46OYJ2agKYZV63cXkEQz5d_k8cuQ27WN8-E&usqp=CAU")
      .into(showImage);

    Button continues = findViewById(R.id.create_continue);
    showImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 2);
      }
    });
    //未获取先写死


//    绑定事件
    continues.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //点击继续按钮，上传文件（发送请求）
        sendRequest();

        //跳转发布activity

      }
    });

  }




  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 2) {
      // 从相册返回的数据
      if (data != null && data.getClipData() != null) {
        // 得到图片组的全路径
        int count = data.getClipData().getItemCount();
        for (int i = 0; i < count; i++) {
          Uri uri = data.getClipData().getItemAt(i).getUri();
          try {
            File file = createTempFileFromUri(uri);
            arrayList.add(file);
            uriList.add(uri);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        mCursorAdapter.notifyDataSetChanged();
      } else if (data != null) {
        Uri uri = data.getData();
        try {
          File file = createTempFileFromUri(uri);
          arrayList.add(file);
          uriList.add(uri);
        } catch (IOException e) {
          e.printStackTrace();
        }
        mCursorAdapter.notifyDataSetChanged();
      }
    }
  }



  private File createTempFileFromUri(Uri uri) throws IOException {
    File tempFile = File.createTempFile("temp", getFileExtension(uri), getCacheDir());
    InputStream inputStream = getContentResolver().openInputStream(uri);
    FileOutputStream outputStream = new FileOutputStream(tempFile);
    byte[] buffer = new byte[1024];
    int length;
    while ((length = inputStream.read(buffer)) > 0) {
      outputStream.write(buffer, 0, length);
    }
    outputStream.close();
    inputStream.close();
    return tempFile;
  }

  private String getFileExtension(Uri uri) {
    ContentResolver contentResolver = getContentResolver();
    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    if (extension != null) {
      extension = "." + extension;
    }
    return extension;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case REQUEST_EXTERNAL_STORAGE:
        if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }
        break;
      default:
        break;
    }
  }





}
