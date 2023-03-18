package com.example.wiki.service;

import com.example.wiki.domain.User;
import com.example.wiki.domain.UserExample;
import com.example.wiki.mapper.UserMapper;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.util.CopyUtil;
import com.example.wiki.util.SnowFlake;
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
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq userQueryReq) {

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(userQueryReq.getLoginName())) {
            criteria.andLoginNameEqualTo(userQueryReq.getLoginName());
        }

        PageHelper.startPage(userQueryReq.getPage(), userQueryReq.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        // 列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    public List<UserQueryResp> all(UserQueryReq userQueryReq) {
        UserExample userExample = new UserExample();
        List<User> userList = userMapper.selectByExample(userExample);
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);
        return list;
    }

    /**
     * 保存：新增 or 更新
     *
     * @param req
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);

        if (ObjectUtils.isEmpty(req.getId())) {
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        } else {
            userMapper.updateByPrimaryKey(user);
        }
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);

    }
}
