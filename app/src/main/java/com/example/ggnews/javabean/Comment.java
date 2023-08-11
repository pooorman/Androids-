package com.example.ggnews.javabean;

import com.google.gson.annotations.SerializedName;

public class Comment {
  private String id;
  @SerializedName("appKey")
  private String appKey;
  private String pUserId;
  private String userName;
  private String shareId;
  private String parentCommentId;
  private String parentCommentUserId;
  private String replyCommentId;
  private String replyCommentUserId;
  private int commentLevel;
  private String content;
  private int status;
  private int praiseNum;
  private int topStatus;
  private String createTime;

  public String getId() {
    return id;
  }

  public String getAppKey() {
    return appKey;
  }

  public String getPUserId() {
    return pUserId;
  }

  public String getUserName() {
    return userName;
  }

  public String getShareId() {
    return shareId;
  }

  public String getParentCommentId() {
    return parentCommentId;
  }

  public String getParentCommentUserId() {
    return parentCommentUserId;
  }

  public String getReplyCommentId() {
    return replyCommentId;
  }

  public String getReplyCommentUserId() {
    return replyCommentUserId;
  }

  public int getCommentLevel() {
    return commentLevel;
  }

  public String getContent() {
    return content;
  }

  public int getStatus() {
    return status;
  }

  public int getPraiseNum() {
    return praiseNum;
  }

  public int getTopStatus() {
    return topStatus;
  }

  public String getCreateTime() {
    return createTime;
  }
}
