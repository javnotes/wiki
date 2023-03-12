package com.example.wiki.controller;

import com.example.wiki.req.EbookQueryReq;
import com.example.wiki.req.EbookSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.EbookQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luf
 * @date 2022/10/09 10:24
 **/
@RestController
@RequestMapping("/ebook")
public class EbookController {
    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp<PageResp<EbookQueryResp>> list(EbookQueryReq ebookQueryReq) {
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(ebookQueryReq);
        resp.setContent(list);
        return resp;
    }

    /**
     * 保存
     * @param req
     * @return
     */
    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp();
        ebookService.save(req);
        return resp;
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }

    @GetMapping("/all")
    public CommonResp<List<EbookQueryResp>> all(EbookQueryReq ebookQueryReq) {
        CommonResp<List<EbookQueryResp>> resp = new CommonResp<>();
        List<EbookQueryResp> list = ebookService.all(ebookQueryReq);
        resp.setContent(list);
        return resp;
    }


}
