package com.example.ggnews.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ggnews.R;
import com.example.ggnews.javabean.Comment;

import java.util.List;

public class SecondCommentAdapter extends ArrayAdapter<Comment> {
  private int page = 1;
  private String userId;
  private List<Comment> mNewsData;
  private Context mContext;
  private Handler mHandler = new Handler(Looper.getMainLooper());
  private int resourceId;



  public void setUserId(String userId) {
    this.userId = userId;
  }

  public SecondCommentAdapter(Context context,
                        int resourceId, List<Comment> data) {
    super(context, resourceId, data);
    this.mContext = context;
    this.mNewsData = data;
    this.resourceId = resourceId;
  }

  public void loadMoreData() {

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
    listView.setOnScrollListener(new EndlessScrollListener());
    final ViewHolder vh;




    if (convertView == null) {
      view = LayoutInflater.from(getContext())
        .inflate(resourceId, parent, false);

      vh = new ViewHolder();
      vh.coComment = view.findViewById(R.id.co_comment);
      vh.coTime = view.findViewById(R.id.time);
      vh.coShowSecond = view.findViewById(R.id.co_showSecond);
      vh.coUsername = view.findViewById(R.id.co_username);
      vh.coBtn = view.findViewById(R.id.co_btn);
      vh.coHeadImage = view.findViewById(R.id.coHeadImage);
      view.setTag(vh);
    } else {
      view = convertView;
      vh = (ViewHolder) view.getTag();
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
          //二级回复

      }
    });

    vh.coShowSecond.setVisibility(View.GONE);
    return view;
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
