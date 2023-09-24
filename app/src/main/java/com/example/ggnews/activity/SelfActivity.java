package com.example.ggnews.activity;

import static android.content.ContentValues.TAG;

import static com.example.ggnews.adapter.RecordsAdapter.CalTimeFormat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ggnews.Constants;
import com.example.ggnews.GridSpacingItemDecoration;
import com.example.ggnews.R;
import com.example.ggnews.UploadData;
import com.example.ggnews.request.RecordsRequest;
import com.example.ggnews.adapter.ImageItemAdapter;
import com.example.ggnews.javabean.Records;
import com.example.ggnews.response.BaseResponse;
import com.example.ggnews.response.LikeListResponse;
import com.example.ggnews.response.LoginResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SelfActivity extends AppCompatActivity {
  private List<Records> newsData;
  private TextView introduce;
  private TextView sex;
  private TextView username;
  private RecyclerView lvLikeList;
  private int choiceFlag2;
  private ImageItemAdapter adapter;
  private EditText passwordEdit;
  private int page = 1;
  private int choiceFlag;
  private String passwordFlag;

  private Button works;
  private Button privates;
  private   Button collections;
  private Button loves;
  private LoginResponse loginData;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_self);

    initView();
    loginData = (LoginResponse) getIntent().getSerializableExtra("LoginData");
    getUserDetail(loginData.getUsername());
  }
  public void clearAll() {
    newsData.clear();
    adapter.notifyDataSetChanged();
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
            new AlertDialog.Builder(SelfActivity.this)
              .setTitle("提示")
              .setMessage("修改成功")
              .setPositiveButton("确定", null)
              .show();
            if(choiceFlag==1){
              getUserDetail(loginData.getUsername());
            } else if (choiceFlag==3) {
              getUserDetail(passwordFlag);
            }
          }
        });
      } else {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            new AlertDialog.Builder(SelfActivity.this)
              .setTitle("提示")
              .setMessage("修改失败！/n"+Response.getMsg())
              .setPositiveButton("确定", null)
              .show();

          }
        });
      }
    }
  };
  public void addAll(List<Records> data) {
    if (data != null) {
      clearAll();
      newsData.addAll(data);
      adapter.notifyDataSetChanged();
    }

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
              new TypeToken<LikeListResponse>() {}.getType();
            LikeListResponse newsListResponse =
              gson.fromJson(body, jsonType);

            //存入数据
            if(newsListResponse.getData()!=null) {
              List<Records> records = newsListResponse.getData().getRecords();
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  if(choiceFlag2==0){
                    for (Records record : records) {
                      record.setUsername(loginData.getUsername());
                    }
                    addAll(records);
                    works.setText("作品\t"+newsListResponse.getData().getTotal());
                    works.setBackgroundTintList(ContextCompat.getColorStateList(SelfActivity.this, R.color.black));
                  }else if(choiceFlag2==1){
                    for (Records record : records) {
                      record.setUsername(loginData.getUsername());
                    }
                    addAll(records);
                    privates.setText("私密\t"+newsListResponse.getData().getTotal());
                    privates.setBackgroundTintList(ContextCompat.getColorStateList(SelfActivity.this, R.color.black));
                  } else if (choiceFlag2==2) {
                    addAll(records);
                    collections.setText("收藏\t"+newsListResponse.getData().getTotal());
                    collections.setBackgroundTintList(ContextCompat.getColorStateList(SelfActivity.this, R.color.black));
                  }else if(choiceFlag2==3){
                    addAll(records);
                    loves.setText("喜欢\t"+newsListResponse.getData().getTotal());
                    loves.setBackgroundTintList(ContextCompat.getColorStateList(SelfActivity.this, R.color.black));
                  }
                }
              });

            }else {
              addAll(new ArrayList<Records>());
            }

          }
        });
      } else {
      }
    }
  };

  public void showLoginDialog(String changeName,String parameterName,int choice) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelfActivity.this);
        //修改的名字
        builder.setTitle("修改"+changeName);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_login_dialog, null);
        builder.setView(dialogView);

        EditText usernameEditText = dialogView.findViewById(R.id.iv_username);
        passwordEdit = dialogView.findViewById(R.id.iv_password);
        usernameEditText.setVisibility(View.GONE);
        passwordEdit.setInputType(View.AUTOFILL_TYPE_TEXT);
        passwordEdit.setHint("在这你想要更改的"+changeName);

        builder.setPositiveButton("确定更改",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int id) {
                String password = passwordEdit.getText().toString();
                passwordFlag = password;
                choiceFlag = choice;
                //发注册请求
                new Thread(() -> {
                  MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                  JSONObject jsonObject = new JSONObject();
                  try {
                    jsonObject.put("id", getIntent().getStringExtra("id"));
                    jsonObject.put(parameterName, password);
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

                  //FormBody录入失败？
                  Request request = new Request.Builder()
                    .addHeader("appId","37baffe1646a4411a338eb820a131176")
                    .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
                    .url(Constants.SERVER_URL2 + "user/update")
                    .post(formBody).build();


                  try {
                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(signup);



                  } catch (NetworkOnMainThreadException ex) {
//            主线程网络错误
                    ex.printStackTrace();
                  }
                }).start();
              }
            })
          .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();

      }
    });
  }

  public void changeHeadImage(String uri) {
    new Thread(() -> {
      MediaType JSON = MediaType.parse("application/json; charset=utf-8");
      JSONObject jsonObject = new JSONObject();
      try {
        jsonObject.put("id", getIntent().getStringExtra("id"));
        jsonObject.put("avatar", uri);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

      //FormBody录入失败？
      Request request = new Request.Builder()
        .addHeader("appId","37baffe1646a4411a338eb820a131176")
        .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
        .url(Constants.SERVER_URL2 + "user/update")
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
                  new AlertDialog.Builder(SelfActivity.this)
                    .setTitle("提示")
                    .setMessage("修改成功")
                    .setPositiveButton("确定", null)
                    .show();
                  getUserDetail(loginData.getUsername());
                }
              });
            } else {
              runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  new AlertDialog.Builder(SelfActivity.this)
                    .setTitle("提示")
                    .setMessage("修改失败！/n"+Response.getMsg())
                    .setPositiveButton("确定", null)
                    .show();

                }
              });
            }
          }
        });



      } catch (NetworkOnMainThreadException ex) {
//            主线程网络错误
        ex.printStackTrace();
      }
    }).start();

  }

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
            Response.getData().setId(getIntent().getStringExtra("id"));
            initData(Response.getData());
            loginData=Response.getData();
          }
        });

      } else {


      }
    }
  };
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }


  private void getUserDetail(String detailName){
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


    //初始化上半部分的数据
    ImageView headImage = findViewById(R.id.se_headImage);
    Glide.with(this).load(loginData.getAvatar()==null?"https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/08/30/ee2927bf-6b67-4040-957c-b26c5a5343ab.jpg":loginData.getAvatar())
      .into(headImage);



    headImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          // 创建一个Dialog对话框
          Dialog dialog = new Dialog(SelfActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
          dialog.setContentView(R.layout.dialog_image_zoom_button);

          // 获取对话框中的ImageView和背景View
          ImageView zoomImage = dialog.findViewById(R.id.zoom_image2);
          View background = dialog.findViewById(R.id.background);
        Button button = dialog.findViewById(R.id.change_avatar_button);

        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, 2);
          }
        });

        // 设置放大后的图片
          Glide.with(SelfActivity.this).load(loginData.getAvatar()==null?"https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/08/30/ee2927bf-6b67-4040-957c-b26c5a5343ab.jpg":loginData.getAvatar())
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


    introduce=findViewById(R.id.se_introduce);
    introduce.setText("自我介绍："+(loginData.getIntroduce()==null?"这个人很懒，什么也没说":loginData.getIntroduce()));

    introduce.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showLoginDialog("简介","introduce",1);
      }
    });

    TextView number = findViewById(R.id.se_number);
    number.setText("上次修改："+CalTimeFormat(Long.parseLong(loginData.getLastUpdateTime())));


    sex=findViewById(R.id.se_sex);
    sex.setText("性别："+(loginData.getSex()==null?"暂未填写":(loginData.getSex()=="0"?"男":"女")));

    sex.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showLoginDialogSex();
      }
    });


    username=findViewById(R.id.se_username);
    username.setText(loginData.getUsername());


    username.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showLoginDialog("昵称","username",3);
      }
    });

    newsData = new ArrayList<>();
    adapter = new ImageItemAdapter(this, R.layout.self_item, newsData);
    adapter.setLoginData(loginData);
    lvLikeList.setAdapter(adapter);

    refreshData(1);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 2) {
      // 从相册返回的数据
      if (data != null && data.getClipData() == null) {
        Uri uri = data.getData();
        try {
        File file = createTempFileFromUri(uri);
        sendRequest(file);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else if (data!=null) {
        int count = data.getClipData().getItemCount();
        Uri uri = data.getClipData().getItemAt(0).getUri();
        try {
          File file = createTempFileFromUri(uri);
          sendRequest(file);
        } catch (IOException e) {
          e.printStackTrace();
        }
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
  private void sendRequest(File file){
    new Thread(new Runnable() {
      @Override
      public void run() {
        String url = "http://47.107.52.7:88/member/photo/image/upload";
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
        MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

        MultipartBody multipartBody = new MultipartBody.Builder()
          .setType(MultipartBody.FORM)
          .addFormDataPart("fileList",file.getName(),RequestBody.create(MEDIA_TYPE_JSON, file))
          .build();



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
              if (Response.getCode()==200) {
                //传递下一步所需的参数
                changeHeadImage(Response.getData().getImageUrlList().get(0));
              } else {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(SelfActivity.this)
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



  private void showLoginDialogSex() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelfActivity.this);
        //修改的名字
        builder.setTitle("修改性别");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.frage_sex_dialog, null);
        builder.setView(dialogView);

        RadioButton radioButton = dialogView.findViewById(R.id.radio_female);
        RadioButton radioButton1 = dialogView.findViewById(R.id.radio_male);

        radioButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //点击时候如果对方有
            if(radioButton1.isChecked()){
              radioButton1.setChecked(!radioButton1.isChecked());
            }
          }
        });

        radioButton1.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            //点击时候如果对方有
            if(radioButton.isChecked()){
              radioButton.setChecked(!radioButton.isChecked());
            }
          }
        });

        //0和1是自己定义的
        builder.setPositiveButton("确定",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int id) {
                //发注册请求
                new Thread(() -> {
                  MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                  JSONObject jsonObject = new JSONObject();
                  try {
                    jsonObject.put("id", getIntent().getStringExtra("id"));
                    jsonObject.put("sex",radioButton1.isChecked()==true?0:1);
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                  RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());

                  //FormBody录入失败？
                  Request request = new Request.Builder()
                    .addHeader("appId","37baffe1646a4411a338eb820a131176")
                    .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
                    .url(Constants.SERVER_URL2 + "user/update")
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
                              new AlertDialog.Builder(SelfActivity.this)
                                .setTitle("提示")
                                .setMessage("修改成功")
                                .setPositiveButton("确定", null)
                                .show();
                                getUserDetail(loginData.getUsername());
                            }
                          });
                        } else {
                          runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              new AlertDialog.Builder(SelfActivity.this)
                                .setTitle("提示")
                                .setMessage("修改失败！\n"+Response.getMsg())
                                .setPositiveButton("确定", null)
                                .show();

                            }
                          });
                        }
                      }
                    });



                  } catch (NetworkOnMainThreadException ex) {
//            主线程网络错误
                    ex.printStackTrace();
                  }
                }).start();
              }
            });
        AlertDialog dialog = builder.create();
        dialog.show();

      }
    });
  }


  public void sendRequest(String path) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        String userId;
        RecordsRequest requestObj = new RecordsRequest();
        Intent intent = getIntent();
        userId = intent != null ? intent.getStringExtra("id") : null;
        requestObj.setUserId(Long.parseLong(userId));
        requestObj.setSize(Constants.NEWS_NUM);
        requestObj.setCurrent(page);
        String urlParams = requestObj.toString();
        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2 + path + urlParams)
          .addHeader("appId", "37baffe1646a4411a338eb820a131176")
          .addHeader("appSecret", "37609f4e6965cf9384d88bfd237a20b5aa666")
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

  private void updateButton(){
    works.setText("作品");
    works.setBackgroundTintList(ContextCompat.getColorStateList(SelfActivity.this, R.color.purple_700));
    privates.setText("私密");
    privates.setBackgroundTintList(ContextCompat.getColorStateList(SelfActivity.this, R.color.purple_700));
    collections.setText("收藏");
    collections.setBackgroundTintList(ContextCompat.getColorStateList(SelfActivity.this, R.color.purple_700));
    loves.setText("喜欢");
    loves.setBackgroundTintList(ContextCompat.getColorStateList(SelfActivity.this, R.color.purple_700));
  }

  private void refreshData(final int page) {
    //图片集合还有主页信息
    sendRequest("share/myself");
  }

  private void initView() {
    //登录的组件的信息录入
    lvLikeList = findViewById(R.id.lv_image_list);

    //更新距离
    GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
    lvLikeList.setLayoutManager(layoutManager);

    // Create and set the ItemDecoration
    int spacing = getResources().getDimensionPixelSize(R.dimen.item_spacing);
    int borderSize = getResources().getDimensionPixelSize(R.dimen.item_border_size);
    int borderColor = ContextCompat.getColor(this, R.color.item_border_color);
    lvLikeList.addItemDecoration(new GridSpacingItemDecoration(spacing, borderSize, borderColor));



    //分别给四个按钮注册事件
    works = findViewById(R.id.se_works);
    privates = findViewById(R.id.se_private);
    collections = findViewById(R.id.se_collecte);
    loves = findViewById(R.id.se_love);


    works.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateButton();
        choiceFlag2=0;
        sendRequest("share/myself");
      }
    });

    updateButton();
    privates.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateButton();
        choiceFlag2=1;
        sendRequest("share/save");
      }
    });


    collections.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateButton();
        choiceFlag2=2;
        sendRequest("collect");
      }
    });


    loves.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateButton();
        choiceFlag2=3;
        sendRequest("like");
      }
    });



    BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView1);
    bottomNavigationView.setSelectedItemId(R.id.bottom3);
    bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.bottom1) {
          // do nothing
          //flash ur data and view
          //登录成功跳转
          Intent intent = new Intent(SelfActivity.this, MainActivity.class);

          //存储数据
          intent.putExtra("id", getIntent().getStringExtra("id"));
          intent.putExtra("LoginData", (LoginResponse) getIntent().getSerializableExtra("LoginData"));



          startActivity(intent);


          return true;
        } else if (item.getItemId() == R.id.bottom2) {
          // 新增activity
          Intent intent = new Intent(SelfActivity.this, CreateActivity.class);
          LoginResponse loginData = (LoginResponse) getIntent().getSerializableExtra("LoginData");
          intent.putExtra("LoginData", loginData);
          intent.putExtra("id", loginData.getId());
          startActivity(intent);
          return true;
        } else if (item.getItemId() == R.id.bottom3) {
          // 主页activity
          return true;
        }else if (item.getItemId() == R.id.bottom4){
          Intent intent = new Intent(SelfActivity.this, CareActivity.class);

          //存储数据
          intent.putExtra("id", getIntent().getStringExtra("id"));
          intent.putExtra("LoginData", (LoginResponse) getIntent().getSerializableExtra("LoginData"));

          startActivity(intent);
          return true;
        }
        return false;
      }
    });

  }


}
