package com.trade.controller;

import com.trade.dto.UserLoginDTO;
import com.trade.service.UserService;
import com.trade.vo.ResultVO;
import com.trade.vo.UserVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    public ResultVO<String> updateAvatar(MultipartFile file, HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        String url = userService.updateAvatar(userId,file);
        return ResultVO.success(url);
    }
}