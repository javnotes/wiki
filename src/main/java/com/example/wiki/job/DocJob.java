package com.example.wiki.job;

import com.example.wiki.service.DocService;
import com.example.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author luf
 * @date 2023/03/25 20:19
 **/

@Component
public class DocJob {
    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Resource
    private DocService docService;

    @Resource
    private SnowFlake snowFlake;


    /**
     * 每30秒更新电子书信息
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void updateEbookInfo() {
        // 增加日志流水号
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("更新电子书信息开始");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("更新电子书信息结束，耗时：{} 毫秒", System.currentTimeMillis() - start);
    }
}
