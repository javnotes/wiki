package com.example.wiki.resp;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 统计数据返回对象，包含日期、阅读数、点赞数、阅读增长、点赞增长，用于首页显示
 */
public class StatisticResp {

    @JsonFormat(pattern = "MM-dd", timezone = "GMT+8")
    private Date date;

    // 阅读数
    private int viewCount;
    // 点赞数
    private int voteCount;
    // 阅读增长
    private int viewIncrease;
    // 点赞增长
    private int voteIncrease;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getViewIncrease() {
        return viewIncrease;
    }

    public void setViewIncrease(int viewIncrease) {
        this.viewIncrease = viewIncrease;
    }

    public int getVoteIncrease() {
        return voteIncrease;
    }

    public void setVoteIncrease(int voteIncrease) {
        this.voteIncrease = voteIncrease;
    }

    @Override
    public String toString() {
        return "StatisticResp{" +
                "date=" + date +
                ", viewCount=" + viewCount +
                ", voteCount=" + voteCount +
                ", viewIncrease=" + viewIncrease +
                ", voteIncrease=" + voteIncrease +
                '}';
    }
}
