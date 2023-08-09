package com.example.ggnews;

import java.util.List;

//public class Records {
//  private Integer id;
//  private Integer pUserId;
//  private Integer imageCode;
//  private String title;
//  private String content;
//  private String createTime;
//  private List<String> imageUrlList;
//  private Integer likeId;
//
//  public Integer getId() {
//    return id;
//  }
//
//  public void setId(Integer id) {
//    this.id = id;
//  }
//
//  public Integer getpUserId() {
//    return pUserId;
//  }
//
//  public void setpUserId(Integer pUserId) {
//    this.pUserId = pUserId;
//  }
//
//  public Integer getImageCode() {
//    return imageCode;
//  }
//
//  public void setImageCode(Integer imageCode) {
//    this.imageCode = imageCode;
//  }
//
//  public String getTitle() {
//    return title;
//  }
//
//  public void setTitle(String title) {
//    this.title = title;
//  }
//
//  public String getContent() {
//    return content;
//  }
//
//  public void setContent(String content) {
//    this.content = content;
//  }
//
//  public String getCreateTime() {
//    return createTime;
//  }
//
//  public void setCreateTime(String createTime) {
//    this.createTime = createTime;
//  }
//
//  public String getImageUrlList() {
//    //简化只展示一个Image
//    return imageUrlList.get(0);
//  }
//
//  public void setImageUrlList(List<String> imageUrlList) {
//    this.imageUrlList = imageUrlList;
//  }
//
//  public Integer getLikeId() {
//    return likeId;
//  }
//
//  public void setLikeId(Integer likeId) {
//    this.likeId = likeId;
//  }
//
//  public Integer getLikeNum() {
//    return likeNum;
//  }
//
//  public void setLikeNum(Integer likeNum) {
//    this.likeNum = likeNum;
//  }
//
//  public Boolean getHasLike() {
//    return hasLike;
//  }
//
//  public void setHasLike(Boolean hasLike) {
//    this.hasLike = hasLike;
//  }
//
//  public Integer getCollectId() {
//    return collectId;
//  }
//
//  public void setCollectId(Integer collectId) {
//    this.collectId = collectId;
//  }
//
//  public Integer getCollectNum() {
//    return collectNum;
//  }
//
//  public void setCollectNum(Integer collectNum) {
//    this.collectNum = collectNum;
//  }
//
//  public Boolean getHasCollect() {
//    return hasCollect;
//  }
//
//  public void setHasCollect(Boolean hasCollect) {
//    this.hasCollect = hasCollect;
//  }
//
//  public Boolean getHasFocus() {
//    return hasFocus;
//  }
//
//  public void setHasFocus(Boolean hasFocus) {
//    this.hasFocus = hasFocus;
//  }
//
//  public String getUsername() {
//    return username;
//  }
//
//  public void setUsername(String username) {
//    this.username = username;
//  }
//
//  private Integer likeNum;
//  private Boolean hasLike;
//  private Integer collectId;
//  private Integer collectNum;
//  private Boolean hasCollect;
//  private Boolean hasFocus;
//  private String username;
//
//  public Records() {
//  }
//}


public class Records {
  private String id;
  private String pUserId;
  private String imageCode;
  private String title;
  private String content;
  private String createTime;
  private List<String> imageUrlList;
  private String likeId;
  private int likeNum;
  private boolean hasLike;
  private String collectId;
  private int collectNum;
  private boolean hasCollect;
  private boolean hasFocus;
  private String username;

  // Add getter and setter methods for the above fields
  // ...

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getpUserId() {
    return pUserId;
  }

  public void setpUserId(String pUserId) {
    this.pUserId = pUserId;
  }

  public String getImageCode() {
    return imageCode;
  }

  public void setImageCode(String imageCode) {
    this.imageCode = imageCode;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getImageUrlList() {
    return imageUrlList.get(0);
  }

  public void setImageUrlList(List<String> imageUrlList) {
    this.imageUrlList = imageUrlList;
  }

  public String getLikeId() {
    return likeId;
  }

  public void setLikeId(String likeId) {
    this.likeId = likeId;
  }

  public int getLikeNum() {
    return likeNum;
  }

  public void setLikeNum(int likeNum) {
    this.likeNum = likeNum;
  }

  public boolean isHasLike() {
    return hasLike;
  }

  public void setHasLike(boolean hasLike) {
    this.hasLike = hasLike;
  }

  public String getCollectId() {
    return collectId;
  }

  public void setCollectId(String collectId) {
    this.collectId = collectId;
  }

  public int getCollectNum() {
    return collectNum;
  }

  public void setCollectNum(int collectNum) {
    this.collectNum = collectNum;
  }

  public boolean isHasCollect() {
    return hasCollect;
  }

  public void setHasCollect(boolean hasCollect) {
    this.hasCollect = hasCollect;
  }

  public boolean isHasFocus() {
    return hasFocus;
  }

  public void setHasFocus(boolean hasFocus) {
    this.hasFocus = hasFocus;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String toString() {
    return "Record{" +
      "id='" + id + '\'' +
      ", pUserId='" + pUserId + '\'' +
      ", imageCode='" + imageCode + '\'' +
      ", title='" + title + '\'' +
      ", content='" + content + '\'' +
      ", createTime='" + createTime + '\'' +
      ", imageUrlList=" + imageUrlList +
      ", likeId='" + likeId + '\'' +
      ", likeNum=" + likeNum +
      ", hasLike=" + hasLike +
      ", collectId='" + collectId + '\'' +
      ", collectNum=" + collectNum +
      ", hasCollect=" + hasCollect +
      ", hasFocus=" + hasFocus +
      ", username='" + username + '\'' +
      '}';
  }
}
