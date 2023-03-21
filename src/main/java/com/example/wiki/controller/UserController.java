package com.example.wiki.controller;

import com.example.wiki.req.UserQueryReq;
import com.example.wiki.req.UserSaveReq;
import com.example.wiki.resp.CommonResp;
import com.example.wiki.resp.UserQueryResp;
import com.example.wiki.resp.PageResp;
import com.example.wiki.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author luf
 * @date 2022/10/09 10:24
 **/
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

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


}
