package com.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trade.domain.SysUser;
import com.trade.dto.UserLoginDTO;
import com.trade.exception.GlobalException;
import com.trade.mapper.SysUserMapper;
import com.trade.service.UserService;
import com.trade.util.JwtUtil;
import com.trade.util.OSSUtil;
import com.trade.util.PasswordUtil;
import com.trade.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;

@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private OSSUtil ossUtil;

    //替换原来的 BCryptPasswordEncoder
    @Resource
    private PasswordUtil passwordUtil;

    @Override
    public void register(UserLoginDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser one = this.getOne(wrapper);
        if (one != null) {
            throw new GlobalException(500, "账号已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        //使用我们自己的加密方法
        user.setPassword(passwordUtil.encrypt(dto.getPassword()));
        user.setRole("user");
        user.setStatus(1);
        this.save(user);
    }

    @Override
    public UserVO login(UserLoginDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser user = this.getOne(wrapper);
        if (user == null) {
            throw new GlobalException(500, "用户名错误");
        }
        //替换密码匹配逻辑
        if (!passwordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new GlobalException(500, "密码错误");
        }
        if (user.getStatus() == 0) {
            throw new GlobalException(500, "账号被禁用");
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        String token = jwtUtil.generateToken(user.getId());
        vo.setToken(token);
        return vo;
    }

    @Override
    public String updateAvatar(Long userId, MultipartFile file) {
        try {
            String url = ossUtil.uploadFile(file);
            SysUser user = this.getById(userId);
            user.setAvatar(url);
            this.updateById(user);
            return url;
        } catch (Exception e) {
            throw new GlobalException(500, "头像上传失败");
        }
    }
}