package com.trade.controller;

import com.trade.util.OSSUtil;
import com.trade.vo.ResultVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private OSSUtil ossUtil;

    @PostMapping("/upload")
    public ResultVO<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            if (!(".jpg".equalsIgnoreCase(suffix) || ".png".equalsIgnoreCase(suffix))) {
                return ResultVO.fail(404,"仅支持jpg、png格式图片");
            }
            if(file.getSize() > 2 * 1024 * 1024){
                return ResultVO.fail(404,"图片大小不能超过2MB");
            }
            String url = ossUtil.uploadFile(file);
            return ResultVO.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail(404,"图片上传失败，请检查OSS配置");
        }
    }
}