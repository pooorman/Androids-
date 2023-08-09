package com.example.ggnews;

public class RecordsRequest {
  private int current;
  private int size;
  private int userId;

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

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    String retValue;

    retValue = "?" + "&current=" + current
      + "&size=" + size + "&userId=" + userId;
//    if (current != -1) {
//      retValue += "&page=" + page;
//    }
    return retValue;
  }
}
