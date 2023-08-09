package com.example.ggnews;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
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

public class RecordsAdapter extends ArrayAdapter<Records> {
  private List<Records> mNewsData;
  private Context mContext;
  private Handler mHandler = new Handler(Looper.getMainLooper());
  private int resourceId;

  private ImageView btnLike; // 添加成员变量


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

//  private void sendUnLikeRequest(int position){
//    Records record = mNewsData.get(position);
//    new Thread(new Runnable() {
//      FormBody formBody = new FormBody.Builder()
//        .add("likeId",record.getLikeId())
//        .build();
//      @Override
//      public void run() {
//        Request request = new Request.Builder()
//          .url(Constants.SERVER_URL2+"/cancel")
//          .addHeader("appId","37baffe1646a4411a338eb820a131176")
//          .addHeader("appSecret","37609f4e6965cf9384d88bfd237a20b5aa666")
//          .post(formBody).build();
//        try {
//          OkHttpClient client = new OkHttpClient();
//          client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//              Log.e(TAG, "Failed to connect server!");
//              e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response)
//              throws IOException {
//              final String body = response.body().string();
//              Gson gson = new Gson();
//              Type jsonType =
//                new TypeToken<BaseResponse<Objects>>() {}.getType();
//              BaseResponse<Objects> Response =
//                gson.fromJson(body, jsonType);
//              if (true) {
////        无返回，所以我不管
//                mHandler.post(new Runnable() {
//                  @Override
//                  public void run() {
//                    btnLike.setBackgroundResource(R.drawable.baseline_handshake_24);
//                    // 更新records对象的hasLike字段和likeId字段
//                    record.setHasLike(false);
//                    record.setLikeId(null);
//                    // 更新适配器中的数据列表
//                    updateData(mNewsData);
//                  }
//                });
//
//              } else {
//
//              }
//            }
//          });
//        } catch (NetworkOnMainThreadException ex) {
//          ex.printStackTrace();
//        }
//      }
//    }).start();
////    自己的id先写死


//  }

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
            requestObj.setUserId(1);
            requestObj.setSize(Constants.NEWS_NUM);
            requestObj.setCurrent(1);
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


  private void sendLikeRequest(int position) {
    Records record = mNewsData.get(position);
    String id = record.getId();
    new Thread(new Runnable() {
      FormBody formBody = new FormBody.Builder()
        .add("userId","1688921375286366208")
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
//        无返回，所以我不管
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    //变喜欢
                    btnLike.setImageResource(R.drawable.baseline_redhand_24);
                    new  AlertDialog.Builder(mContext)
                      .setTitle("提示")
                      .setMessage("点赞成功" )
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
//      vh.tvFollow = view.findViewById(R.id.tvFollow);
      vh.tvImageUrl = view.findViewById(R.id.tvImageUrl);
      vh.tvLikesCount = view.findViewById(R.id.tvLikesCount);
      vh.tvCollectCount = view.findViewById(R.id.tvCollectCount);
      vh.btnLike = view.findViewById(R.id.btnLike);


      view.setTag(vh);
    } else {
      view = convertView;
      vh = (RecordsAdapter.ViewHolder) view.getTag();
    }


    ImageView btnLike = view.findViewById(R.id.btnLike);
    btnLike.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        boolean hasLike = records.isHasLike();
        RecordsAdapter.this.btnLike = btnLike;
//        if (!hasLike) {
          // 还没喜欢
          sendLikeRequest(position);
//        } else {
          // 已经喜欢，执行取消点赞操作
//          sendUnLikeRequest(position);
//        }
      }
    });
    if (!records.isHasLike()){
      vh.btnLike.setImageResource(R.drawable.baseline_redhand_24);
    }else {
      vh.btnLike.setImageResource(R.drawable.baseline_handshake_24);
    }

    vh.tvTitle.setText(records.getTitle());
    vh.tvCollectCount.setText(records.getCollectNum()+"次点赞");
    vh.tvLikesCount.setText("共"+String.valueOf(records.getLikeNum())+"条评论");
    vh.tvUsername.setText(records.getUsername());
//    头像没处理
    //为什么要记录位置
//    vh.ivDelete.setTag(position);

    Glide.with(mContext).load(records.getImageUrlList())
      .into(vh.tvImageUrl);

    return view;
  }

  class ViewHolder {
    ImageView tvHeadImage;
    TextView tvUsername;
//    Button tvFollow;
    ImageView tvImageUrl;
    ImageView btnLike;
//    ImageView btnComment;
//    ImageView btnCollection;

    TextView tvLikesCount;
    TextView  tvCollectCount;
    TextView tvTitle;
  }

}
