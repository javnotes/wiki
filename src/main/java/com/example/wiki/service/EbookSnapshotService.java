package com.example.wiki.service;

import com.example.wiki.mapper.EbookSnapshotMapperCust;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * 电子书快照服务，定时生成电子书快照，用于首页统计数据
 * @author luf
 * @date 2023/03/26 22:50
 **/

@Service
public class EbookSnapshotService {

    @Resource
    private EbookSnapshotMapperCust ebookSnapshotMapperCust;

    public void genSnapshot() {
        ebookSnapshotMapperCust.genSnapshot();
    }
}
