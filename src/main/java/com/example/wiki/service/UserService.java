package com.example.wiki.service;

import com.example.wiki.domain.User;
import com.example.wiki.domain.UserExample;
import com.example.wiki.exception.BusinessException;
import com.example.wiki.exception.BusinessExceptionCode;
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
     * 保存：新增 or 更新,用户名不能重复
     *
     * @param req
     */
    public void save(UserSaveReq req) {
        // 将 req 对象复制到 User 对象中
        User user = CopyUtil.copy(req, User.class);
        // 判断是新增还是更新,如果 id 为空，则是新增
        if (ObjectUtils.isEmpty(req.getId())) {
            User userDB = selectByLoginName(req.getLoginName());
            // 判断用户名是否存在
            if (ObjectUtils.isEmpty(userDB)) {
                // 新增
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            } else {
                //用户名已存在
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }
        } else {
            // 更新,用户名不可更新，所以设置为空，防止被更新
            user.setLoginName(null);
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);

    }

    /**
     * 根据登录名查询用户，登录名是唯一的，所以只能查到一个用户
     *
     * @param loginName
     * @return
     */
    public User selectByLoginName(String loginName) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginNameEqualTo(loginName);
        // 查询，返回一个列表，即使只有一个元素，也是一个列表
        List<User> userList = userMapper.selectByExample(userExample);
        if (userList.size() > 0) {
            return userList.get(0);
        } else {
            return null;
        }
    }
}
