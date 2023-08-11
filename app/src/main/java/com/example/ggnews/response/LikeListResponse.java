package com.example.ggnews.response;

import com.example.ggnews.javabean.Data;
import com.example.ggnews.javabean.Records;

import java.util.List;

public class LikeListResponse {
  private int code;
  private String msg;
  private Data data;

  // Getters and setters


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

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

}



