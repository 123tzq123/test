package com.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.trade.domain.SysUser;
import com.trade.dto.UserLoginDTO;
import com.trade.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IService<SysUser> {
    //注册
    void register(UserLoginDTO dto);
    //登录返回UserVO+token
    UserVO login(UserLoginDTO dto);
    //修改头像
    String updateAvatar(Long userId, MultipartFile file);
}