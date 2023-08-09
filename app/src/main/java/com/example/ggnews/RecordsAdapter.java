package com.example.ggnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecordsAdapter extends ArrayAdapter<Records> {
  private List<Records> mNewsData;
  private Context mContext;
  private int resourceId;
  public RecordsAdapter(Context context,
                     int resourceId, List<Records> data) {
    super(context, resourceId, data);
    this.mContext = context;
    this.mNewsData = data;
    this.resourceId = resourceId;
  }

  @Override
  public View getView(int position,
                      View convertView, ViewGroup parent) {
    Records records = getItem(position);
    View view ;

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


      view.setTag(vh);
    } else {
      view = convertView;
      vh = (RecordsAdapter.ViewHolder) view.getTag();
    }

    vh.tvTitle.setText(records.getTitle());
    vh.tvCollectCount.setText(records.getCollectNum());
    vh.tvLikesCount.setText(records.getLikeNum());
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
//    ImageView btnLike;
//    ImageView btnComment;
//    ImageView btnCollection;

    TextView tvLikesCount;
    TextView  tvCollectCount;
    TextView tvTitle;
  }

}
