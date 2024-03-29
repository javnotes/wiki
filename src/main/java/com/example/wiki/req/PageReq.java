package com.example.wiki.req;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * @author luf
 * @date 2023/03/10 15:56
 **/
public class PageReq {

    @NotNull(message = "【页码】不能为空")
    @Max(value = 500, message = "【页码】不能超过500")
    private Integer page;

    @NotNull(message = "【每页条数】不能为空")
    @Max(value = 1000, message = "【每页条数】不能超过1000")
    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PageReq{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}
