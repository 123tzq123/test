package com.trade.controller;

import com.trade.constant.AttributeConst;
import com.trade.domain.SysUser;
import com.trade.dto.UserLoginDTO;
import com.trade.service.UserService;
import com.trade.vo.ResultVO;
import com.trade.vo.UserVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public ResultVO<Void> register(@Validated @RequestBody UserLoginDTO dto){
        userService.register(dto);
        return ResultVO.success(null);
    }
    @PostMapping("/login")
    public ResultVO<UserVO> login(@Validated @RequestBody UserLoginDTO dto){
        UserVO vo = userService.login(dto);
        return ResultVO.success(vo);
    }
    @PostMapping("/updateAvatar")
    public ResultVO<String> updateAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        String url = userService.updateAvatar(userId,file);
        return ResultVO.success(url);
    }
    //获取当前登录用户信息
    @GetMapping("/getInfo")
    public ResultVO<SysUser> getUserInfo(HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        SysUser user = userService.getUserInfo(userId);
        return ResultVO.success(user);
    }

    //更新昵称和头像
    @PutMapping("/updateInfo")
    public ResultVO<String> updateInfo(@RequestBody Map<String,String> map, HttpServletRequest request){
        Long userId = (Long) request.getAttribute(AttributeConst.LOGIN_USER_ID);
        String nickname = map.get("nickname");
        String avatar = map.get("avatar");
        userService.updateUserInfo(userId, nickname, avatar);
        return ResultVO.success("修改成功");
    }
}