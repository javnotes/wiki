package com.example.wiki.service;

import com.example.wiki.domain.Ebook;
import com.example.wiki.domain.EbookExample;
import com.example.wiki.mapper.EbookMapper;
import com.example.wiki.req.EbookQueryReq;
import com.example.wiki.req.EbookSaveReq;
import com.example.wiki.resp.EbookQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luf
 * @date 2022/12/08 22:33
 **/
@Service
public class EbookService {

    private static final Logger logger = LoggerFactory.getLogger(EbookService.class);
    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookQueryResp> list(EbookQueryReq ebookQueryReq) {

        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(ebookQueryReq.getName())) {
            criteria.andNameLike("%" + ebookQueryReq.getName() + "%");
        }

        PageHelper.startPage(ebookQueryReq.getPage(), ebookQueryReq.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        logger.info("总页数：{}", +pageInfo.getPages());
        logger.info("总行数：{}", +pageInfo.getTotal());

        //List<EbookResp> respList = new ArrayList<>();
        //for (Ebook ebook : ebookList) {
        //    //EbookResp ebookResp = new EbookResp();
        //    //BeanUtils.copyProperties(ebook, ebookResp);
        //    对象复制
        //    EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
        //
        //    respList.add(ebookResp);
        //}

        // 列表复制
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public List<EbookQueryResp> all(EbookQueryReq ebookQueryReq) {
        EbookExample ebookExample = new EbookExample();
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);
        List<EbookQueryResp> list = CopyUtil.copyList(ebookList, EbookQueryResp.class);
        return list;
    }

    /**
     * 保存：新增 or 更新
     *
     * @param req
     */
    public void save(EbookSaveReq req) {
        Ebook ebook = CopyUtil.copy(req, Ebook.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            ebookMapper.insert(ebook);
        } else {
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }
}
