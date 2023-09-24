package com.example.ggnews.adapter;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ggnews.R;
import com.example.ggnews.activity.MainActivity;
import com.example.ggnews.activity.SelfActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaCursorAdapter extends RecyclerView.Adapter<ViewHolder> {

  private List<Uri> mNewsData;
  private Context mContext;
  private int resourceId;
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


  public MediaCursorAdapter(Context context, int resourceId, List<Uri> data) {
    this.mContext = context;
    this.mNewsData = data;
    this.resourceId = resourceId;
  }

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }







  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
    mLayoutInflater = LayoutInflater.from(mContext);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Uri uri = mNewsData.get(position);


    if (uri != null) {
      holder.ShowImage.setImageURI(uri);


      //点击选择
      holder.ShowImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
          dialog.setContentView(R.layout.dialog_image_zoom1);

          // 获取对话框中的ImageView和背景View
          ImageView zoomImage = dialog.findViewById(R.id.zoom_image1);
          View background = dialog.findViewById(R.id.background1);

          // 设置放大后的图片
          zoomImage.setImageURI(uri);

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

    }




  }

  @Override
  public int getItemCount() {
    return mNewsData.size();
  }


}
class ViewHolder extends RecyclerView.ViewHolder {
  ImageView ShowImage;

  public ViewHolder(@NonNull View itemView) {
    super(itemView);
    ShowImage = itemView.findViewById(R.id.id_image);
  }
}
