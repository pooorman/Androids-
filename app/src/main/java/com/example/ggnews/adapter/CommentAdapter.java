package com.example.ggnews.adapter;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggnews.response.BaseResponse;
import com.example.ggnews.javabean.Comment;
import com.example.ggnews.response.CommentResponse;
import com.example.ggnews.Constants;
import com.example.ggnews.response.LoginResponse;
import com.example.ggnews.response.NewBaseResponse;
import com.example.ggnews.R;
import com.example.ggnews.javabean.Records;
import com.example.ggnews.request.RecordsRequest;
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

public class CommentAdapter extends ArrayAdapter<Comment> {
  private int page = 1;
  private String userId;
  private EditText editText;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  private String userName;
  private ListView lvNewsList;

  private SecondCommentAdapter adapter;

  public LoginResponse getLoginResponse() {
    return loginResponse;
  }

  public void setLoginResponse(LoginResponse loginResponse) {
    this.loginResponse = loginResponse;
  }

  private LoginResponse loginResponse;
  private List<Comment> mNewsData;
  private Context mContext;
  private Button resButton;
  private Handler mHandler = new Handler(Looper.getMainLooper());
  private int resourceId;



  public void setUserId(String userId) {
    this.userId = userId;
  }

  public CommentAdapter(Context context,
                        int resourceId, List<Comment> data, EditText activityLayout,Button resButton) {
    super(context, resourceId, data);
    this.mContext = context;
    this.mNewsData = data;
    this.resourceId = resourceId;
    this.resButton = resButton;
    this.editText = activityLayout;
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
              .url(Constants.SERVER_URL2+"comment/first"+ urlParams)
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
                      new TypeToken<BaseResponse<CommentResponse>>() {}.getType();
                    NewBaseResponse<List<Records>> newsListResponse =
                      gson.fromJson(body, jsonType);
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
        if (totalItemCount < mNewsData.size()) {
          // End has been reached, load more data
          loadMoreData();
          loading = true;
        }
      }
    }
  }

  @Override
  public View getView(int position,
                      View convertView, ViewGroup parent) {
    Comment item = getItem(position);

    View view ;

    //列表设置滚动监听
    ListView listView = (ListView) parent;
    listView.setOnScrollListener(new CommentAdapter.EndlessScrollListener());
    final CommentAdapter.ViewHolder vh;




    if (convertView == null) {
      view = LayoutInflater.from(getContext())
        .inflate(resourceId, parent, false);

      vh = new CommentAdapter.ViewHolder();
      vh.coComment = view.findViewById(R.id.co_comment);
      vh.coTime = view.findViewById(R.id.time);
      vh.coShowSecond = view.findViewById(R.id.co_showSecond);
      vh.coUsername = view.findViewById(R.id.co_username);
      vh.coBtn = view.findViewById(R.id.co_btn);
      vh.coHeadImage = view.findViewById(R.id.coHeadImage);
      view.setTag(vh);
    } else {
      view = convertView;
      vh = (CommentAdapter.ViewHolder) view.getTag();
    }

   vh.coUsername.setText(item.getUserName());
   vh.coComment.setText(item.getContent());
   vh.coTime.setText(item.getCreateTime());
    Glide.with(mContext).load("https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/08/30/ee2927bf-6b67-4040-957c-b26c5a5343ab.jpg")
      .into(vh.coHeadImage);
   vh.coBtn.setOnClickListener(new View.OnClickListener() {
     //回复按钮
     @Override
     public void onClick(View v) {
//        1.改变hint，正在回复？？？的评论,按钮改变回复中
          editText.setHint("正在回复"+item.getUserName()+"的评论");
          vh.coBtn.setText("回复中");
          resButton.setText("回复");
          resButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //发送回复请求
               sendAnswerRequest(item);

            }
          });
//          2.焦点保护
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             editText.setHint("请输入评论");
             vh.coBtn.setText("回复");
             resButton.setText("发表");
             listView.setOnItemClickListener(null);
         }
       });


     }
   });



     //移除展开全部回复


     vh.coShowSecond.setVisibility(View.VISIBLE);
     vh.coShowSecond.setOnClickListener(new View.OnClickListener() {
       //展示全部回复
       @Override
       public void onClick(View v) {
         //看情况来绑定
         lvNewsList = view.findViewById(R.id.co_secondList);
         mNewsData = new ArrayList<>();
         SecondCommentAdapter commentAdapter = new SecondCommentAdapter(mContext,
           R.layout.comment_item, mNewsData);
         lvNewsList.setAdapter(commentAdapter);
         sendSecondCommentRequest(item,commentAdapter);
       }
     });




    return view;
  }


  private void sendSecondCommentRequest(Comment comment,SecondCommentAdapter adapter) {
    //获取点击的评论对象


    new Thread(new Runnable() {
      @Override
      public void run() {
        RecordsRequest requestObj = new RecordsRequest();
        requestObj.setSize(Constants.ADD_NUM);
        requestObj.setCurrent(1);

        String urlParams = requestObj.toString();

        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"/comment/second"+ urlParams+"&commentId="+comment.getId()+"&shareId="+comment.getShareId())
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

              final String body = response.body().string();
              Gson gson = new Gson();
              Type jsonType =
                new TypeToken<BaseResponse<CommentResponse>>() {}.getType();
              BaseResponse<CommentResponse> newsListResponse =
                gson.fromJson(body, jsonType);
              if (newsListResponse.getCode()==200) {

                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    for (Comment comment:newsListResponse.getData().getRecords()) {
                      adapter.add(comment);
                    }
                   adapter.notifyDataSetChanged();
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
//    自己的id先写死

  }



  private void sendAnswerRequest(Comment comment){
    new Thread(new Runnable() {
      @Override
      public void run() {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put("content", editText.getText());
          jsonObject.put("parentCommentId", comment.getId());
          jsonObject.put("parentCommentUserId", comment.getPUserId());
          jsonObject.put("replyCommentId", comment.getId());
          jsonObject.put("replyCommentUserId", comment.getPUserId());
          jsonObject.put("shareId", comment.getShareId());
         jsonObject.put("userId",loginResponse.getId());
          jsonObject.put("userName", loginResponse.getUsername());
        } catch (JSONException e) {
          e.printStackTrace();
        }
        RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());


        Request request = new Request.Builder()
          .url(Constants.SERVER_URL2+"comment/second")
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
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    //1.构造一个自己加 2.重新请求
                    new AlertDialog.Builder(mContext)
                      .setMessage("回复成功")
                      .show();

                  }
                });


              } else {
                mHandler.post(new Runnable() {
                  @Override
                  public void run() {
                    new AlertDialog.Builder(mContext)
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


  class ViewHolder {
    TextView coUsername;
    TextView coTime;
    ImageView coHeadImage;
    TextView  coComment;
    TextView coBtn;

    TextView coShowSecond;
  }

}

