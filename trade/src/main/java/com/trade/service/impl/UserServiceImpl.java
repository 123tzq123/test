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
import java.io.IOException;

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
            throw new GlobalException("账号已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordUtil.encrypt(dto.getPassword()));
        user.setRole("user");
        user.setStatus(1);
        //先插入数据库，此时id自动生成
        this.save(user);

        //设置默认昵称和默认头像
        String defaultAvatar = "https://idle-goods-image.oss-cn-beijing.aliyuncs.com/image/db60d339f30644c7be92be00826ed038.jpg";
        user.setNickname("闲置用户");
        user.setAvatar(defaultAvatar);
        //更新数据库
        this.updateById(user);
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
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        String token = jwtUtil.generateToken(user.getId());
        vo.setToken(token);
        return vo;
    }

    @Override
    public SysUser getUserInfo(Long userId) {
        SysUser user = this.getById(userId);
        if(user == null){
            throw new GlobalException("用户不存在");
        }
        //密码不返回前端
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateUserInfo(Long userId, String nickname, String avatar) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setNickname(nickname);
        user.setAvatar(avatar);
        this.updateById(user);
    }

    @Override
    public String updateAvatar(Long userId, MultipartFile file) throws IOException {
        String url = ossUtil.uploadFile(file);
        SysUser user = new SysUser();
        user.setId(userId);
        user.setAvatar(url);
        this.updateById(user);
        return url;
    }
}