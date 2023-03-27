package com.example.wiki.controller;

import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.StatisticResp;
import com.example.wiki.service.EbookSnapshotService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luf
 * @date 2023/03/26 23:14
 **/

@RestController
@RequestMapping("/ebook-snapshot")
public class EbookSnapshotController {

    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @GetMapping("/get-statistic")
    public CommonResp getStatistic() {
        CommonResp<List<StatisticResp>> resp = new CommonResp<>();
        List<StatisticResp> statisticRespList = ebookSnapshotService.getStatistic();
        resp.setContent(statisticRespList);
        return resp;
    }
}
