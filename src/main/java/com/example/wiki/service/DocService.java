package com.example.wiki.service;

import com.example.wiki.domain.Content;
import com.example.wiki.domain.Doc;
import com.example.wiki.domain.DocExample;
import com.example.wiki.exception.BusinessException;
import com.example.wiki.exception.BusinessExceptionCode;
import com.example.wiki.mapper.ContentMapper;
import com.example.wiki.mapper.DocMapper;
import com.example.wiki.mapper.DocMapperCust;
import com.example.wiki.req.DocQueryReq;
import com.example.wiki.req.DocSaveReq;
import com.example.wiki.resp.DocQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.RedisUtil;
import com.example.wiki.util.RequestContext;
import com.example.wiki.util.SnowFlake;
import com.example.wiki.websocket.WebSocketServer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author luf
 * @date 2022/12/08 22:33
 **/
@Service
public class DocService {

    private static final Logger logger = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    ContentMapper contentMapper;

    @Resource
    DocMapperCust docMapperCust;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private WsService wsService;

    public PageResp<DocQueryResp> list(DocQueryReq docQueryReq) {

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        PageHelper.startPage(docQueryReq.getPage(), docQueryReq.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);
        PageInfo<Doc> pageInfo = new PageInfo<>(docList);


        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }


    /**
     * ebookId为null，则查不到数据，可防止查出数据表中所有数据
     *
     * @param ebookId
     * @return
     */
    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);
        // 列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);
        return list;
    }

    /**
     * 保存文档：新增 or 更新
     * @param req
     */
    @Transactional
    public void save(DocSaveReq req) {
        // 只复制Doc实体的属性
        Doc doc = CopyUtil.copy(req, Doc.class);
        // 只复制content实体的属性，id、content，注意doc.id=content.id
        Content content = CopyUtil.copy(req, Content.class);

        // 新增文档
        if (ObjectUtils.isEmpty(req.getId())) {
            doc.setId(snowFlake.nextId());
            // 新增文档时，阅读数和点赞数都为0
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);
        } else {
            // 更新文档
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0) {
                // 说明content表中本来没有数据，需要新增
                contentMapper.insert(content);
            }
        }
    }

    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> idsStr) {
        DocExample docExample = new DocExample();
        //删除条件
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(idsStr);
        docMapper.deleteByExample(docExample);
    }

    /**
     * 根据id查询文档内容Content(Long id,String content)
     */
    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);
        // 文档阅读数+1
        docMapperCust.increaseViewCount(id);

        // 判断content是否为空，凡是返回值为对象的方法，都要判断是否为空，避免空指针异常
        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }
    }

    /**
     * 文档点赞，点赞数+1，使用redis实现点赞数的缓存，防止用户短时间内重复点赞
     * 点赞后，使用websocket向前端推送消息
     */
    public void vote(Long id) {

        //远程IP+文档id作为redis的key，防止用户短时间内重复点赞，可设置24小时内不可重复点赞
        //扩展：会员ID(如果有)可以作为redis的key，防止会员短时间内重复点赞，可设置24小时内不可重复点赞
        String ip = RequestContext.getRemoteAddr();

        if (redisUtil.validateRepeat("DOC_VOTE" + id + "_" + ip, 3)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT_ERROR);
        }

        // 组装消息，使用websocket向前端推送消息
        Doc docDB = docMapper.selectByPrimaryKey(id);
        // 获取日志id,MDC.get("LOG_ID")，在LogAspect中设置,用于区分日志,方便排查问题,get方法是线程安全的
        String logId = MDC.get("LOG_ID");
        wsService.sendInfo("【" + docDB.getName() + "】被点赞", logId);
    }

    public void updateEbookInfo() {
        docMapperCust.updateEbookInfo();
    }
}
