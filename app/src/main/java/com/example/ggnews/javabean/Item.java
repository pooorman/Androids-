package com.example.ggnews.javabean;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {
  private int likeNum;
  private List<String> imageUrlList;
  private String id;

  public Item() {
  }

  public int getLikeNum() {
    return likeNum;
  }

  public void setLikeNum(int likeNum) {
    this.likeNum = likeNum;
  }

  public String getImageUrlList() {
    if(imageUrlList.size()>0){
      return imageUrlList.get(0);
    }else{
      return null;
    }

  }

  public void setImageUrlList(List<String> imageUrlList) {
    this.imageUrlList = imageUrlList;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
