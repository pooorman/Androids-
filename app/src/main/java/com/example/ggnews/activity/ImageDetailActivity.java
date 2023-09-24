package com.example.ggnews.activity;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ggnews.adapter.ImageItemAdapter;
import com.example.ggnews.adapter.RecordsAdapter;
import com.example.ggnews.response.BaseResponse;
import com.example.ggnews.javabean.Comment;
import com.example.ggnews.Constants;
import com.example.ggnews.GridSpacingItemDecoration;
import com.example.ggnews.response.LoginResponse;
import com.example.ggnews.R;
import com.example.ggnews.javabean.Records;
import com.example.ggnews.request.RecordsRequest;
//import com.example.ggnews.adapter.SimpleCommentAdapter;
import com.example.ggnews.adapter.ImageItemReAdapter;
import com.example.ggnews.response.CommentResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageDetailActivity extends AppCompatActivity {

  private ImageView headImage;
  private TextView userName;
  private TextView time;
  private TextView title;
  private ImageView like;
  private ImageView coll;
  private ImageItemReAdapter adapter;
//  private SimpleCommentAdapter simpleCommentAdapter;
  private ImageView comment;
  private RecyclerView newImageData;
  private RecyclerView newCommentData;
  private List<Comment> commentList;
  private List<String> recordList;
  private LoginResponse loginResponse;
  private Records records;
  private EditText publish;
  private ImageView bottomImage;
  private Handler mHandler = new Handler(Looper.getMainLooper());

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_imagedetial);

    initView();
    initData();
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
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                commentList = newsListResponse.getData().getRecords();
//                simpleCommentAdapter = new SimpleCommentAdapter(ImageDetailActivity.this,R.id.id_comment_list,commentList);
//                newCommentData.setAdapter(simpleCommentAdapter);

              }
            });
            adapter.notifyDataSetChanged();
          }
        });
      } else {
//        暂时没有评论界面

      }
    }
  };


  private okhttp3.Callback updateHeadImage = new okhttp3.Callback() {
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
            String avatar = Response.getData().getAvatar();
            Glide.with(ImageDetailActivity.this).load((avatar)==null?"https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/08/30/ee2927bf-6b67-4040-957c-b26c5a5343ab.jpg":avatar)
              .into(headImage);
          }
        });

      } else {


      }
    }
  };


  private void UpdateIdRequest() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        RecordsRequest requestObj = new RecordsRequest();
        requestObj.setUserId(Long.parseLong(loginResponse.getId()));
        requestObj.setSize(Constants.ADD_NUM);
        String urlParams = requestObj.toString();

        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"/share/detail"+ urlParams+"&shareId="+records.getId())
          .addHeader("appId","37baffe1646a4411a338eb820a131176")
          .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
          .get().build();
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
              if (response.isSuccessful()) {
                final String body = response.body().string();

                Gson gson = new Gson();
                Type jsonType =
                  new TypeToken<BaseResponse<Records>>() {}.getType();
                BaseResponse<Records> newsListResponse =
                  gson.fromJson(body, jsonType);

                //更新likeId
                records.setLikeId(newsListResponse.getData().getLikeId());
                records.setCollectId(newsListResponse.getData().getCollectId());


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

  private void sendUnLikeRequest() {
    new Thread(new Runnable() {
      FormBody formBody = new FormBody.Builder()
        .add("likeId",records.getLikeId())
        .build();
      @Override
      public void run() {
        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"/like/cancel")
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
                new TypeToken<BaseResponse<Objects>>() {}.getType();
              BaseResponse<Objects> Response =
                gson.fromJson(body, jsonType);
              if (Response.getCode()==200) {
                UpdateIdRequest();
//        无返回，所以我不管
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    //变喜欢
                    like.setImageResource(R.drawable.baseline_handshake_24);
                    new  AlertDialog.Builder(ImageDetailActivity.this)
                      .setTitle("提示")
                      .setMessage("取消点赞" )
                      .show();
                  }
                });

              } else {
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(ImageDetailActivity.this)
                      .setTitle("提示")
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
//    自己的id先写死

  }

  private void sendColRequest(){
    new Thread(new Runnable() {
      FormBody formBody = new FormBody.Builder()
        .add("userId",loginResponse.getId())
        .add("shareId",records.getId())
        .build();
      @Override
      public void run() {
        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"/collect")
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
                new TypeToken<BaseResponse<Objects>>() {}.getType();
              BaseResponse<Objects> Response =
                gson.fromJson(body, jsonType);
              if (Response.getCode()==200) {
                UpdateIdRequest();
//        无返回，所以我不管
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    //变喜欢
                    coll.setImageResource(R.drawable.baseline_yellow_star_24);
                    new  AlertDialog.Builder(ImageDetailActivity.this)
                      .setTitle("提示")
                      .setMessage("收藏成功" )
                      .show();
                  }
                  //更改CollectId

                });

              } else {
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(ImageDetailActivity.this)
                      .setTitle("提示")
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
//    自己的id先写死
  }


  private void sendUnColRequest(){
    new Thread(new Runnable() {
      FormBody formBody = new FormBody.Builder()
        .add("collectId",records.getCollectId())
        .build();
      @Override
      public void run() {
        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"/collect/cancel")
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
                new TypeToken<BaseResponse<Objects>>() {}.getType();
              BaseResponse<Objects> Response =
                gson.fromJson(body, jsonType);
              if (Response.getCode()==200) {
//        无返回，所以我不管
                UpdateIdRequest();
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    //变喜欢
                    coll.setImageResource(R.drawable.baseline_gray_star_24);
                    new  AlertDialog.Builder(ImageDetailActivity.this)
                      .setTitle("提示")
                      .setMessage("取消收藏" )
                      .show();
                  }

                });

              } else {
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(ImageDetailActivity.this)
                      .setTitle("提示")
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
//    自己的id先写死
  }

  private void sendLikeRequest() {
    new Thread(new Runnable() {
      FormBody formBody = new FormBody.Builder()
        .add("userId",loginResponse.getId())
        .add("shareId",records.getId())
        .build();
      @Override
      public void run() {
        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"/like")
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
                new TypeToken<BaseResponse<Objects>>() {}.getType();
              BaseResponse<Objects> Response =
                gson.fromJson(body, jsonType);
              if (Response.getCode()==200) {
                UpdateIdRequest();
//        无返回，所以我不管
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    //变喜欢
                    like.setImageResource(R.drawable.baseline_redhand_24);
                    new  AlertDialog.Builder(ImageDetailActivity.this)
                      .setTitle("提示")
                      .setMessage("点赞成功" )
                      .show();
                  }
                });

              } else {
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(ImageDetailActivity.this)
                      .setTitle("提示")
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
//    自己的id先写死

  }


  private void getHeadImage(String userName){
    new Thread(new Runnable() {
      @Override
      public void run() {
        //请求路径
        Request request = new Request.Builder()
          .addHeader("appId", "37baffe1646a4411a338eb820a131176")
          .addHeader("appSecret", "37609f4e6965cf9384d88bfd237a20b5aa666")
          .url(Constants.SERVER_URL2 + "user/getUserByName?username=" + userName)
          .get().build();

        try {
          OkHttpClient client = new OkHttpClient();
          client.newCall(request).enqueue(updateHeadImage);
        } catch (NetworkOnMainThreadException ex) {
//            主线程网络错误
          ex.printStackTrace();
        }
      }
    }).start();
  }


  private void initData() {
    //传入所需
    loginResponse = (LoginResponse) getIntent().getSerializableExtra("loginData1");
    records = (Records) getIntent().getSerializableExtra("record");
    //获取头像
    getHeadImage(records.getUsername());
    userName.setText(records.getUsername());
    time.setText(RecordsAdapter.CalTimeFormat(Long.parseLong(records.getCreateTime())));

    title.setText(records.getTitle());

    headImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(ImageDetailActivity.this, UserDetailActivity.class);
        intent.putExtra("name",userName.getText());
        intent.putExtra("pUserId",loginResponse.getId());
        startActivity(intent);
      }
    });

//    按钮们
    if (records.isHasLike()){
      like.setImageResource(R.drawable.baseline_redhand_24);
    }

    if(records.isHasCollect()){
      coll.setImageResource(R.drawable.baseline_yellow_star_24);
    }

    like.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(!records.isHasLike()){
          sendLikeRequest();
        }else {
          sendUnLikeRequest();
        }
        //相反
        records.setHasLike(!records.isHasLike());
      }
    });


    coll.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(!records.isHasCollect()){
            sendColRequest();
          }else {
            sendUnColRequest();
          }
          //相反
          records.setHasCollect(!records.isHasCollect());
        }
      }
    );

  //评论要显示输入框有点难



//    初始化recycle数据

    List<String> imageAllUrlList = records.getImageAllUrlList();
    recordList = imageAllUrlList;


    int spacing = this.getResources().getDimensionPixelSize(R.dimen.item_spacing);
    int borderSize = this.getResources().getDimensionPixelSize(R.dimen.item_border_size);
    int borderColor = ContextCompat.getColor(this, R.color.item_border_color);
    int num = recordList.size();
    int columnNum = calGridLayout(num);
    int rowNum = (int)(Math.ceil(num/columnNum));
    //更新一级评论
    if(num!=0){
      int height = 1050 / rowNum;
      GridLayoutManager layoutManager = new GridLayoutManager(this, columnNum);
      layoutManager.setAutoMeasureEnabled(true); // 添加这行代码
      newImageData.setLayoutManager(layoutManager);
      newImageData.addItemDecoration(new GridSpacingItemDecoration(spacing,borderSize, borderColor));

      ImageItemReAdapter adapter = new ImageItemReAdapter(this, R.layout.image_item, recordList);
      adapter.setHeight(height);
      ViewGroup.LayoutParams params = newImageData.getLayoutParams();
      params.height = height; // 设置高度为500像素
      newImageData.setLayoutParams(params);
      newImageData.setAdapter(adapter);
    }

    comment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(ImageDetailActivity.this, CommentActivity.class);
        //存入shareId
        intent.putExtra("shareId",records.getId());
        intent.putExtra("loginData1",loginResponse);
        startActivity(intent);

      }
    });


  }




  private int calGridLayout(int sum){
    if(sum==1)return 1;
    else if (sum==2 || sum==4) return 2;
    else return 3;

  }




  private void initView() {
    headImage = findViewById(R.id.idHeadImage);
    userName = findViewById(R.id.idUsername);
    time = findViewById(R.id.idTime);
    title = findViewById(R.id.idTitle);
    newImageData = findViewById(R.id.id_image_list);
    like = findViewById(R.id.btnLike1);
    coll = findViewById(R.id.btnCollection1);
    comment = findViewById(R.id.btnComment1);




    //ImageItem的距离注意!
    //更新距离
    GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
    newImageData.setLayoutManager(layoutManager);



  }

}
