package com.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.SysUser;
import com.trade.dto.UserLoginDTO;
import com.trade.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends IService<SysUser> {
    //注册
    void register(UserLoginDTO dto);
    //登录返回UserVO+token
    UserVO login(UserLoginDTO dto);
    //获取当前登录用户信息
    SysUser getUserInfo(Long userId);
    //更新昵称和头像
    void updateUserInfo(Long userId, String nickname, String avatar);
    //修改头像
    String updateAvatar(Long userId, MultipartFile file) throws IOException;
}