package com.example.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.wiki.req.UserLoginReq;
import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserResetPasswordReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.UserLoginResp;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.UserService;
import com.example.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author luf
 * @date 2022/10/09 10:24
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    @Resource
    private SnowFlake snowFlake;


    @Resource
    private RedisTemplate redisTemplate;

    /**
     * Spring 会将请求参数自动封装到 UserQueryReq 对象中，这个对象的属性名必须和请求参数名一致，否则无法自动封装，这里的 @Valid 注解是用来校验请求参数的
     */
    @GetMapping("/list")
    public CommonResp<PageResp<UserQueryResp>> list(@Valid UserQueryReq userQueryReq) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(userQueryReq);
        resp.setContent(list);
        return resp;
    }

    /**
     * 保存
     *
     * @param req
     * @return
     */
    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req) {
        CommonResp resp = new CommonResp();
        // 这里使用的是Spring自带的工具类,对密码进行MD5加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        userService.save(req);
        return resp;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

    @GetMapping("/all")
    public CommonResp<List<UserQueryResp>> all(UserQueryReq userQueryReq) {
        CommonResp<List<UserQueryResp>> resp = new CommonResp<>();
        List<UserQueryResp> list = userService.all(userQueryReq);
        resp.setContent(list);
        return resp;
    }

    /**
     * 重置密码
     *
     * @param req
     * @return
     */
    @PostMapping("/reset-password")
    public CommonResp restPassword(@Valid @RequestBody UserResetPasswordReq req) {
        CommonResp resp = new CommonResp();
        // 这里使用的是Spring自带的工具类,对密码进行MD5加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        // 这里使用的是Spring自带的工具类,对密码再次进行MD5加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        // 调用service层的登录方法，返回用户信息，没有异常就是登录成功
        UserLoginResp userLoginResp = userService.login(req);

        logger.info("用户{}登录成功", userLoginResp.getLoginName());
        // 生成token，类型为Long
        Long token = snowFlake.nextId();
        logger.info(token.toString());

        // 将String类型的token设置到返回结果中，随用户信息返回给前端
        userLoginResp.setToken(token.toString());
        // token作为key，用户信息作为value存入redis中
        // 将token和用户信息存入redis中，设置过期时间为一天，注意token类型为Long，需要转换为String
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userLoginResp), 3600 * 24, TimeUnit.SECONDS);

        resp.setContent(userLoginResp);
        return resp;
    }

    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp();
        // 从redis中删除token，直接删除，不需要判断是否存在，因为如果不存在，也不会报错，直接返回null
        redisTemplate.delete(token);
        logger.info("token:{}已删除", token);
        return resp;
    }

}