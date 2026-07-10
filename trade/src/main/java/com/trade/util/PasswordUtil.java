package com.trade.util;

import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

@Component
public class PasswordUtil {
    /**
     * 密码加密（生成随机盐值 + MD5）
     * @param rawPassword 原始明文密码
     * @return 加密字符串：md5值$盐值
     */
    public String encrypt(String rawPassword) {
        //生成6位随机盐值
        String salt = UUID.randomUUID().toString().substring(0, 6);
        String md5Str = md5(rawPassword + salt);
        return md5Str + "$" + salt;
    }

    /**
     * 校验密码
     * @param rawPassword 前端传来的明文密码
     * @param encryptedPassword 数据库中加密后的密码
     * @return 匹配返回true，反之false
     */
    public boolean matches(String rawPassword, String encryptedPassword) {
        String[] arr = encryptedPassword.split("\\$");
        if (arr.length != 2) {
            return false;
        }
        String salt = arr[1];
        return md5(rawPassword + salt).equals(arr[0]);
    }

    //核心MD5算法
    private String md5(String content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}