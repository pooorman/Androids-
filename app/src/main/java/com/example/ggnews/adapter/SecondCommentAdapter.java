package com.example.ggnews.adapter;

import static com.example.ggnews.adapter.RecordsAdapter.CalTimeFormat;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SecondCommentAdapter extends ArrayAdapter<Comment> {
  private int page = 1;
  private String userId;
  private List<Comment> mNewsData;
  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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




  @Override
  public View getView(int position,
                      View convertView, ViewGroup parent) {
    Comment item = getItem(position);

    View view ;

    //列表设置滚动监听
    ListView listView = (ListView) parent;
    final ViewHolder vh;




    if (convertView == null) {
      view = LayoutInflater.from(getContext())
        .inflate(resourceId, parent, false);

      vh = new ViewHolder();
      vh.coComment = view.findViewById(R.id.co_comment);
      vh.coTime = view.findViewById(R.id.time);
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
    long timestamp=1;
    try {
      Date date = formatter.parse(item.getCreateTime());
      timestamp = date.getTime();

      System.out.println("Timestamp: " + timestamp);
    } catch (Exception e) {
      e.printStackTrace();
    }
    vh.coTime.setText(CalTimeFormat(timestamp));
    Glide.with(mContext).load("https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2023/08/30/ee2927bf-6b67-4040-957c-b26c5a5343ab.jpg")
      .into(vh.coHeadImage);
    vh.coBtn.setOnClickListener(new View.OnClickListener() {
      //回复按钮
      @Override
      public void onClick(View v) {


      }
    });
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
