package com.example.wiki.req;

/**
 * Spring 会将请求参数自动封装到 EbookQueryReq 对象中
 * @author luf
 * @date 2023/02/28 20:36
 **/
public class EbookQueryReq extends PageReq {

    private Long id;
    private String name;

    private Long category2Id;

    @Override
    public String toString() {
        return "EbookQueryReq{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category2Id=" + category2Id +
                "} " + super.toString();
    }

    public Long getCategory2Id() {
        return category2Id;
    }

    public void setCategory2Id(Long category2Id) {
        this.category2Id = category2Id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
