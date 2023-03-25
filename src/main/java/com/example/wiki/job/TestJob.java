//package com.example.wiki.job;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * @author luf
// * @date 2023/03/25 11:36
// **/
//
//@Component
//public class TestJob {
//
//    private static final Logger LOG = LoggerFactory.getLogger(TestJob.class);
//
//
//    /**
//     * 固定时间间隔执行，单位毫秒
//     * fixedRate：上一次开始执行时间点之后1秒再执行，不管上一次执行了多久，只要上一次执行完毕后1秒再执行。
//     */
//    //@Scheduled(fixedRate = 2000)
//    //public void simple() throws InterruptedException {
//    //    SimpleDateFormat format = new SimpleDateFormat("mm:ss");
//    //    String dateString = format.format(System.currentTimeMillis());
//    //    Thread.sleep(3000);
//    //    LOG.info("每隔3秒执行一次：{}", dateString);
//    //}
//    //
//
//    //自定义时间间隔执行，单位毫秒，不管上一次执行了多久，只要上一次执行完毕后，间隔时间到了就执行。错过就错过，空闲就空闲。
//    @Scheduled(cron = "0/3 * * * * ?")
//    public void cron() throws InterruptedException {
//        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
//        String dateString = format.format(System.currentTimeMillis());
//        Thread.sleep(5000);
//        LOG.info("每隔5秒执行一次：{}", dateString);
//    }
//}
