package com.example.wiki.service;

import com.example.wiki.mapper.EbookSnapshotMapperCust;
import com.example.wiki.resp.StatisticResp;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 * 电子书快照服务，定时生成电子书快照，用于首页统计数据
 * @author luf
 * @date 2023/03/26 22:50
 **/

@Service
public class EbookSnapshotService {

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    /**
     * 生成快照，执行定时任务
     */
    public void genSnapshot() {
        ebookSnapshotMapperCust.genSnapshot();
    }

    /**
     * 获取首页数值数据：总阅读数、总点赞数、今日阅读数、今日点赞数、今日预计阅读数、今日预计阅读增长
     */
    public List<StatisticResp> getStatistic() {
        return ebookSnapshotMapperCust.getStatistic();
    }

    public List<StatisticResp> get30Statistic() {
        return ebookSnapshotMapperCust.get30Statistic();
    }
}
