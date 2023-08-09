package com.example.ggnews;


import java.util.List;

public class NewBaseResponse<T> {
  private int code;
  private String msg;
  private Data<T> data;

  public NewBaseResponse() {
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Data<T> getData() {
    return data;
  }

  public void setData(Data<T> data) {
    this.data = data;
  }

  public static class Data<T> {
    private List<Records> records;
    private int total;
    private int size;
    private int current;

    public List<Records> getRecords() {
      return records;
    }

    public void setRecords(List<Records> records) {
      this.records = records;
    }

    public int getTotal() {
      return total;
    }

    public void setTotal(int total) {
      this.total = total;
    }

    public int getSize() {
      return size;
    }

    public void setSize(int size) {
      this.size = size;
    }

    public int getCurrent() {
      return current;
    }

    public void setCurrent(int current) {
      this.current = current;
    }
  }

//  public static class Record {
//    private String id;
//    private String pUserId;
//    private String imageCode;
//    private String title;
//    private String content;
//    private String createTime;
//    private List<String> imageUrlList;
//    private String likeId;
//    private int likeNum;
//    private boolean hasLike;
//    private String collectId;
//    private int collectNum;
//    private boolean hasCollect;
//    private boolean hasFocus;
//    private String username;
//
//    // Add getter and setter methods for the above fields
//    // ...
//
//    @Override
//    public String toString() {
//      return "Record{" +
//        "id='" + id + '\'' +
//        ", pUserId='" + pUserId + '\'' +
//        ", imageCode='" + imageCode + '\'' +
//        ", title='" + title + '\'' +
//        ", content='" + content + '\'' +
//        ", createTime='" + createTime + '\'' +
//        ", imageUrlList=" + imageUrlList +
//        ", likeId='" + likeId + '\'' +
//        ", likeNum=" + likeNum +
//        ", hasLike=" + hasLike +
//        ", collectId='" + collectId + '\'' +
//        ", collectNum=" + collectNum +
//        ", hasCollect=" + hasCollect +
//        ", hasFocus=" + hasFocus +
//        ", username='" + username + '\'' +
//        '}';
//    }
//  }
}
