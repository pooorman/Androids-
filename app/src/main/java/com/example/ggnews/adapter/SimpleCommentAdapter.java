//package com.example.ggnews.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.ggnews.R;
//import com.example.ggnews.javabean.Comment;
//
//import java.util.List;
//
//public class SimpleCommentAdapter extends RecyclerView.Adapter<SimpleCommentAdapter.ViewHolder> {
//
//  private List<Comment> mNewsData;
//  private Context mContext;
//  private int resourceId;
//
//  public SimpleCommentAdapter(Context context, int resourceId, List<Comment> data) {
//    this.mContext = context;
//    this.mNewsData = data;
//    this.resourceId = resourceId;
//
//
//  }
//
//  @NonNull
//  @Override
//  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////    View view = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
////    return new ViewHolder(view);
//  }
//
//  @Override
//  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//    Comment comment = mNewsData.get(position);
//    if (comment != null) {
////      holder.commentTextView.setText(comment.getUserName()+comment.getContent());
//    }
//  }
//
//  @Override
//  public int getItemCount() {
//    return mNewsData.size();
//  }
//
//  class ViewHolder extends RecyclerView.ViewHolder {
//    TextView commentTextView;
//
//    ViewHolder(View itemView) {
//      super(itemView);
//       commentTextView = itemView.findViewById(R.id.id_comment);
//    }
//  }
//}
