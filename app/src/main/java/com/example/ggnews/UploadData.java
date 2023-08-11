package com.example.ggnews;

import java.util.List;

public class UploadData {
    private String imageCode;
    private List<String> imageUrlList;

  public UploadData() {
  }

  public String getImageCode() {
    return imageCode;
  }

  public void setImageCode(String imageCode) {
    this.imageCode = imageCode;
  }

  public List<String> getImageUrlList() {
    return imageUrlList;
  }

  public void setImageUrlList(List<String> imageUrlList) {
    this.imageUrlList = imageUrlList;
  }

  // Add getters and setters for the fields
}
