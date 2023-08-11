package com.example.ggnews.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.ggnews.R;

import java.io.File;
import java.util.ArrayList;

public class MediaCursorAdapter extends CursorAdapter {

  private Context mContext;
  private String userID;
  private String url;
  private String ImageCode;

  public String getImageCode() {
    return ImageCode;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setImageCode(String imageCode) {
    ImageCode = imageCode;
  }

  public ArrayList<File> getArrayList() {
    return arrayList;
  }

  public void setArrayList(ArrayList<File> arrayList) {
    this.arrayList = arrayList;
  }

  private ArrayList<File> arrayList=null;
  private ImageView showHeadImage;
  private LayoutInflater mLayoutInflater;
  private static final int NORMAL_LENGTH = 20;
  private static final String TAG = MediaCursorAdapter.class.getSimpleName();

  public MediaCursorAdapter(Context context) {
    super(context, null, 0);
    mContext = context;

    mLayoutInflater = LayoutInflater.from(mContext);
  }



  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  @Override
  public void bindView(View convertView,
                       Context context, Cursor cursor) {



    int position = cursor.getPosition();
    View view ;
    final ViewHolder vh;

    if (convertView == null) {
      view =mLayoutInflater.inflate(R.layout.image_item, (ViewGroup) cursor, false);

      vh = new ViewHolder();
      vh.ShowImage = view.findViewById(R.id.id_image);

      view.setTag(vh);
    } else {
      view = convertView;
      vh = (ViewHolder) view.getTag();
    }


    vh.ShowImage = view.findViewById(R.id.id_image);
    //获取当前图片的image
    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
      String data = cursor.getString(columnIndex);
      Uri dataUri = Uri.parse(data);
      String imageUrl = dataUri.toString();


      Glide.with(mContext).load(imageUrl)
        .into(vh.ShowImage);


      //点击选择
      vh.ShowImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //头部图片更改
          Glide.with(mContext).load(vh.ShowImage.getResources())
            .into(showHeadImage);

          //存入文件数组
          arrayList.add(new File(vh.ShowImage.getResources().toString()));

          //蓝点和取消

        }
      });


  }

  @Override
  public View newView(Context context,
                      Cursor cursor, ViewGroup viewGroup) {
    View itemView = mLayoutInflater.inflate(R.layout.image_item,
      viewGroup, false);

    if (itemView != null) {
      ViewHolder vh = new ViewHolder();
//      vh.ShowImage = itemView.findViewById(R.id.ce_image);
      itemView.setTag(vh);

      return itemView;
    }

    return null;
  }

  public class ViewHolder {
    ImageView ShowImage;
  }
}
