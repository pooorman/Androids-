package com.example.ggnews.javabean;

import java.util.List;

public class Data {
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
// Getters and setters
}
