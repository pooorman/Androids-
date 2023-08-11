package com.example.ggnews.request;

public class RecordsRequest {
  private int current;
  private int size;
  private long userId;

  public int getCurrent() {
    return current;
  }

  public void setCurrent(int current) {
    this.current = current;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    String retValue;
    if(userId!=0){
      retValue = "?" + "&current=" + current
        + "&size=" + size + "&userId=" + userId;
    }
    else {
      retValue = "?" + "&current=" + current
        + "&size=" + size;
    }
    return retValue;
  }
}
