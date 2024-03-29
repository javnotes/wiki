package com.example.wiki.req;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * SaveReq与表中实体不是完全一致，这里就多了一个content字段
 * 可以根据实际情况进行增加或减少字段
 */
public class DocSaveReq {
    private Long id;

    @NotEmpty(message = "电子书不能为空")
    private Long ebookId;

    @NotEmpty(message = "父文档不能为空")
    private Long parent;

    @NotEmpty(message = "名称不能为空")
    private String name;

    @NotEmpty(message = "排序不能为空")
    private Integer sort;

    private Integer viewCount;

    private Integer voteCount;

    @Override
    public String toString() {
        return "DocSaveReq{" +
                "id=" + id +
                ", ebookId=" + ebookId +
                ", parent=" + parent +
                ", name='" + name + '\'' +
                ", sort=" + sort +
                ", viewCount=" + viewCount +
                ", voteCount=" + voteCount +
                ", content='" + content + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NotNull(message = "内容不能为空")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEbookId() {
        return ebookId;
    }

    public void setEbookId(Long ebookId) {
        this.ebookId = ebookId;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

}