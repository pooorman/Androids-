package com.example.ggnews.response;


import com.example.ggnews.javabean.Comment;

import java.util.List;

public class CommentResponse {
  private List<Comment> records;
  private int total;
  private int size;
  private int current;

  public List<Comment> getRecords() {
    return records;
  }

  public int getTotal() {
    return total;
  }

  public int getSize() {
    return size;
  }

  public int getCurrent() {
    return current;
  }


}



