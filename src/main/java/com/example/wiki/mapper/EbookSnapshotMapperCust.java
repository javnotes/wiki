package com.example.wiki.mapper;


import com.example.wiki.resp.StatisticResp;

import java.util.List;

public interface EbookSnapshotMapperCust {

    void genSnapshot();

    List<StatisticResp> getStatistic();

}