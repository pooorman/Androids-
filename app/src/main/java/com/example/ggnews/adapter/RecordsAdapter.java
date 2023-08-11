package com.example.ggnews.adapter;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ggnews.Constants;
import com.example.ggnews.GridSpacingItemDecoration;
import com.example.ggnews.R;
import com.example.ggnews.activity.LoginActivity;
import com.example.ggnews.request.RecordsRequest;
import com.example.ggnews.activity.UserDetailActivity;
import com.example.ggnews.activity.CommentActivity;
import com.example.ggnews.javabean.Records;
import com.example.ggnews.response.BaseResponse;
import com.example.ggnews.response.LoginResponse;
import com.example.ggnews.response.NewBaseResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecordsAdapter extends ArrayAdapter<Records> {

  private int page = 1;
  private String userId;
  private Handler mHandler = new Handler(Looper.getMainLooper());
  private boolean isFirstRequest = true;
  private List<Records> mNewsData;
  private Context mContext;
  private LoginResponse loginResponse;

  private RecyclerView mImagesData;





  SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
  private int resourceId;


  public LoginResponse getLoginResponse() {
    return loginResponse;
  }

  public void setLoginResponse(LoginResponse loginResponse) {
    this.loginResponse = loginResponse;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  private void updateData(List<Records> newData) {
    mNewsData.clear();
    mNewsData.addAll(newData);
    notifyDataSetChanged();
  }






  public RecordsAdapter(Context context,
                     int resourceId, List<Records> data) {
    super(context, resourceId, data);
    this.mContext = context;
    this.mNewsData = data;
    this.resourceId = resourceId;
  }

  private String CalTimeFormat(Long timestamp){
    long currentTimestamp = System.currentTimeMillis();

    // 获取当天0点的时间戳
//    long todayTimestamp = currentTimestamp - (currentTimestamp % (24 * 60 * 60 * 1000));

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

  public void loadMoreData() {
    //加载新的数据
    new Thread(new Runnable() {
      @Override
      public void run() {
        // 发送网络请求获取新数据
        // ...

        new Thread(new Runnable() {
          @Override
          public void run() {
            RecordsRequest requestObj = new RecordsRequest();
            requestObj.setUserId(Long.parseLong(userId));
            requestObj.setSize(Constants.ADD_NUM);
            requestObj.setCurrent(++page);
            String urlParams = requestObj.toString();

            Request request = new Request.Builder()
              .url(Constants.SERVER_URL2+"/share"+ urlParams)
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
                          new TypeToken<NewBaseResponse<List<Records>>>() {}.getType();
                        NewBaseResponse<List<Records>> newsListResponse =
                          gson.fromJson(body, jsonType);
                    mNewsData.addAll(newsListResponse.getData().getRecords());

                    // 通知适配器数据已更新
                    mHandler.post(new Runnable() {
                      @Override
                      public void run() {
                        notifyDataSetChanged();
                      }
                    });
                  } else {
                  }
                }
              });
            } catch (NetworkOnMainThreadException ex) {

              ex.printStackTrace();
            }
          }
        }).start();

        // 解析返回的数据

        // 将新数据添加到现有数据列表的末尾

      }
    }).start();
  }


  private void sendColRequest(int position, ViewHolder vh){
    final Records record = mNewsData.get(position);
    String id = record.getId();
    new Thread(new Runnable() {
      FormBody formBody = new FormBody.Builder()
        .add("userId",userId)
        .add("shareId",id)
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
                UpdateIdRequest(record.getId(),record,vh);
//        无返回，所以我不管
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    //变喜欢
                    record.setCollectNum(record.getCollectNum()+1);
                    vh.tvCollectCount.setText("共"+record.getCollectNum()+"次收藏");
                    vh.btnCollection.setImageResource(R.drawable.baseline_yellow_star_24);
                    new  AlertDialog.Builder(mContext)
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
                    new AlertDialog.Builder(mContext)
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

  private void sendUnColRequest(int position, ViewHolder vh){
    final Records record = mNewsData.get(position);
    String id = record.getId();
    new Thread(new Runnable() {
      FormBody formBody = new FormBody.Builder()
        .add("collectId",record.getCollectId())
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
                UpdateIdRequest(record.getId(),record,vh);
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    //变喜欢
                    record.setCollectNum(record.getCollectNum()-1);
                    vh.tvCollectCount.setText("共"+record.getCollectNum()+"次收藏");
                    vh.btnCollection.setImageResource(R.drawable.baseline_gray_star_24);
                    new  AlertDialog.Builder(mContext)
                      .setTitle("提示")
                      .setMessage("取消收藏" )
                      .show();
                  }

                });

              } else {
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(mContext)
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


  private void UpdateIdRequest(String shareID,Records records,ViewHolder vh) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        RecordsRequest requestObj = new RecordsRequest();
        requestObj.setUserId(Long.parseLong(userId));
        requestObj.setSize(Constants.ADD_NUM);
        requestObj.setCurrent(++page);
        String urlParams = requestObj.toString();

        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"/share/detail"+ urlParams+"&shareId="+shareID)
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
                records.setLikeNum(newsListResponse.getData().getLikeNum());
                records.setCollectNum(newsListResponse.getData().getCollectNum());
//                vh.tvLikesCount.setText(records.getLikeNum());
//                vh.tvLikesCount.setText(records.getCollectNum());
                // 通知适配器数据已更新
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    notifyDataSetChanged();
                  }
                });
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


  private void sendLikeRequest(int position, ViewHolder vh) {
    Records record = mNewsData.get(position);
    String id = record.getId();
    new Thread(new Runnable() {
      FormBody formBody = new FormBody.Builder()
        .add("userId",userId)
        .add("shareId",id)
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
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    vh.btnLike.setImageResource(R.drawable.baseline_redhand_24);
                    new  AlertDialog.Builder(mContext)
                      .setTitle("提示")
                      .setMessage("点赞成功" )
                      .show();
                  }
                });
                UpdateIdRequest(record.getId(),record,vh);
//        无返回，所以我不管


              } else {
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(mContext)
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


  private void sendUnLikeRequest(int position, ViewHolder vh) {
    Records record = mNewsData.get(position);
    String id = record.getId();
    new Thread(new Runnable() {
      FormBody formBody = new FormBody.Builder()
        .add("likeId",record.getLikeId())
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
                UpdateIdRequest(record.getId(),record,vh);
//        无返回，所以我不管vh
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    //变喜欢
                    vh.btnLike.setImageResource(R.drawable.baseline_handshake_24);
                    new  AlertDialog.Builder(mContext)
                      .setTitle("提示")
                      .setMessage("取消点赞" )
                      .show();
                  }
                });

              } else {
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(mContext)
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






  private class EndlessScrollListener implements AbsListView.OnScrollListener {
    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
      // Do nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
      if (loading) {
        if (totalItemCount > previousTotalItemCount) {
          loading = false;
          previousTotalItemCount = totalItemCount;
          currentPage++;
        }
      }
      if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
        // End has been reached, load more data
        loadMoreData();
        loading = true;
      }
    }
  }

  private int calGridLayout(int sum){
    if(sum==1)return 1;
    else if (sum==2 || sum==4) return 2;
    else return 3;

  }

  @Override
  public View getView(int position,
                      View convertView, ViewGroup parent) {
    Records records = getItem(position);

    View view ;

    //列表设置滚动监听
    ListView listView = (ListView) parent;
    listView.setOnScrollListener(new EndlessScrollListener());


    final RecordsAdapter.ViewHolder vh;


    if (convertView == null) {
      view = LayoutInflater.from(getContext())
        .inflate(resourceId, parent, false);

      vh = new RecordsAdapter.ViewHolder();
      vh.tvTitle  = view.findViewById(R.id.tvTitle);
      vh.tvHeadImage = view.findViewById(R.id.tvHeadImage);
      vh.tvUsername = view.findViewById(R.id.tvUsername);
      vh.tvFollow = view.findViewById(R.id.tvFollow);
      vh.tvLikesCount = view.findViewById(R.id.tvLikesCount);
      vh.tvCollectCount = view.findViewById(R.id.tvCollectCount);
      vh.btnLike = view.findViewById(R.id.btnLike);
      vh.btnCollection = view.findViewById(R.id.btnCollection);
      vh.tvRealTitle = view.findViewById(R.id.tvRealTitle);
      vh.tvSecondTitle = view.findViewById(R.id.tvSecondTitle);
      vh.tvTime = view.findViewById(R.id.tvTime);

      //每个record初始化recycleview
      vh.reImageList = view.findViewById(R.id.re_image_list);



      view.setTag(vh);
    } else {
      view = convertView;
      vh = (RecordsAdapter.ViewHolder) view.getTag();
    }


    ImageView btnLike = view.findViewById(R.id.btnLike);
    ImageView btnComment = view.findViewById(R.id.btnComment);

    Glide.with(mContext).load("https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/08/30/ee2927bf-6b67-4040-957c-b26c5a5343ab.jpg")
      .into(vh.tvHeadImage);

  //初始化recycleView的数据
    int spacing = mContext.getResources().getDimensionPixelSize(R.dimen.item_spacing);
    int borderSize = mContext.getResources().getDimensionPixelSize(R.dimen.item_border_size);
    int borderColor = ContextCompat.getColor(mContext, R.color.item_border_color);
    int num = records.getImageAllUrlList().size();
    int columnNum = calGridLayout(num);
    int rowNum = (int)(Math.ceil(num/columnNum));
    int height = 1050 / rowNum;
    GridLayoutManager layoutManager = new GridLayoutManager(mContext, columnNum);
    layoutManager.setAutoMeasureEnabled(true); // 添加这行代码
//    vh.reImageList.setNestedScrollingEnabled(false);
    vh.reImageList.setLayoutManager(layoutManager);
    vh.reImageList.addItemDecoration(new GridSpacingItemDecoration(spacing,borderSize, borderColor));

    //如何判断富裕，还有大小如何设置
    List<String> imageAllUrlList = records.getImageAllUrlList();
    ImageItemReAdapter adapter = new ImageItemReAdapter(mContext, R.layout.image_item, imageAllUrlList);
    adapter.setHeight(height);
    ViewGroup.LayoutParams params = vh.reImageList.getLayoutParams();
    params.height = height; // 设置高度为500像素
    vh.reImageList.setLayoutParams(params);
    vh.reImageList.setAdapter(adapter);


    vh.tvFollow.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //点击发送关注的请求
        if (!records.isHasFocus()) {
          // 发送第一种请求
          sendFirstRequest("focus",records.getpUserId(),vh);
        } else {
          // 发送第二种请求
          sendFirstRequest("focus/cancel",records.getpUserId(),vh);
        }
        // 切换请求类型
        records.setHasFocus(!records.isHasFocus());
      }
    });

    vh.tvHeadImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, UserDetailActivity.class);
        intent.putExtra("name",vh.tvUsername.getText());
        intent.putExtra("pUserId",records.getpUserId());
        mContext.startActivity(intent);
      }
    });

    btnComment.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, CommentActivity.class);
        //存入shareId
        intent.putExtra("shareId",records.getId());
        intent.putExtra("loginData1",loginResponse);
        mContext.startActivity(intent);

      }
    });

    btnLike.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(!records.isHasLike()){
          sendLikeRequest(position,vh);
        }else {
          sendUnLikeRequest(position,vh);
        }
        //相反
          records.setHasLike(!records.isHasLike());
      }
    });

    //绑定收藏事件
    ImageView btnCol = view.findViewById(R.id.btnCollection);
    btnCol.setOnClickListener(
      new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(!records.isHasCollect()){
            sendColRequest(position,vh);
          }else {
            sendUnColRequest(position,vh);
          }
          //相反
          records.setHasCollect(!records.isHasCollect());
        }
      }
    );


    //第一次的数据
    if (records.isHasLike()){
      vh.btnLike.setImageResource(R.drawable.baseline_redhand_24);
    }

    if(records.isHasCollect()){
        vh.btnCollection.setImageResource(R.drawable.baseline_yellow_star_24);
    }

    if(records.isHasFocus()){
      vh.tvFollow.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.black));
      vh.tvFollow.setTextSize(10);
      vh.tvFollow.setText("已关注");
    }else {
      vh.tvFollow.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.purple_700));
      vh.tvFollow.setTextSize(10);
      vh.tvFollow.setText("关注");
    }
    vh.tvTitle.setText(records.getContent());
    vh.tvRealTitle.setText(records.getTitle());
    vh.tvSecondTitle.setText(records.getUsername());
    vh.tvLikesCount.setText(records.getCollectNum()+"次赞");
    vh.tvCollectCount.setText("共"+String.valueOf(records.getCollectNum())+"次收藏");
    vh.tvUsername.setText(records.getUsername());



    vh.tvTime.setText(CalTimeFormat(Long.parseLong(records.getCreateTime())));

    return view;
  }

  private void sendFirstRequest(String path, String focus, ViewHolder vh) {
    new Thread(() -> {

      FormBody formBody = new FormBody.Builder()
        .add("userId",userId)
        .add("focusUserId",focus)//被关注的用户id
        .build();

      //请求路径
      Request request = new Request.Builder()
        .addHeader("appId","37baffe1646a4411a338eb820a131176")
        .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
        .url(Constants.SERVER_URL2 + path)
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
              new TypeToken<BaseResponse<Object>>() {}.getType();
            BaseResponse<Object> Response =
              gson.fromJson(body, jsonType);

            if (Response.getCode()==200) {
              //改变关注的颜色
              mHandler.post(new Runnable() {
                @Override
                public void run() {
                  // 在这里执行UI更新操作
                  if(path=="focus"){
                    vh.tvFollow.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.black));
                    vh.tvFollow.setText("已关注");
                    vh.tvFollow.setTextSize(10);
                    new AlertDialog.Builder(mContext)
                      .setTitle("提示")
                      .setMessage("关注成功，可以及时推送关注人的消息！")
                      .show();

                  }else {
                    vh.tvFollow.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.purple_700));
                    vh.tvFollow.setText("关注");
                    vh.tvFollow.setTextSize(10);
                    new AlertDialog.Builder(mContext)
                      .setTitle("提示")
                      .setMessage("已取关，不会推送关注该人的消息了！")
                      .show();
                  }

                }
              });

            } else {

            }
          }
        });
      } catch (NetworkOnMainThreadException ex) {
//            主线程网络错误
        ex.printStackTrace();
      }
    }).start();


  }



  class ViewHolder {
    ImageView tvHeadImage;
    TextView tvUsername;
//    关注按钮
    Button tvFollow;
    ImageView tvImageUrl;
    RecyclerView reImageList;
    ImageView btnLike;
    ImageView btnComment;
    ImageView btnCollection;

    TextView tvLikesCount;
    TextView  tvCollectCount;
    TextView tvTitle;
    TextView tvTime;
    TextView tvRealTitle;
    TextView tvSecondTitle;
  }




}
