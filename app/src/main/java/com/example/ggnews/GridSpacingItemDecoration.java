package com.example.ggnews;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
  private int spacing;
  private int borderSize;
  private Paint borderPaint;

  public GridSpacingItemDecoration(int spacing, int borderSize, int borderColor) {
    this.spacing = spacing;
    this.borderSize = borderSize;
    borderPaint = new Paint();
    borderPaint.setColor(borderColor);
    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(borderSize);
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    outRect.left = spacing;
    outRect.right = spacing;
    outRect.bottom = spacing;
    outRect.top = spacing;
    // Add top spacing only for the first row to avoid double spacing between rows
//    调整大小
//    if (parent.getChildAdapterPosition(view) < 3) {
//      outRect.top = spacing;
//    } else {
//      outRect.top = 0;
//  }

  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      View child = parent.getChildAt(i);
      int position = parent.getChildAdapterPosition(child);

      // Draw border for each item
      c.drawRect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom(), borderPaint);
    }
  }
}
