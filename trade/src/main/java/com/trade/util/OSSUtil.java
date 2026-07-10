//package com.trade.util;
//
//import com.aliyun.oss.OSS;
//import com.trade.config.OSSConfig;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//import javax.annotation.Resource;
//import java.util.UUID;
//
//@Component
//public class OSSUtil {
//    @Resource
//    private OSS ossClient;
//    @Resource
//    private OSSConfig ossConfig;
//
//    public String uploadFile(MultipartFile file) throws Exception{
//        String originalName = file.getOriginalFilename();
//        String suffix = originalName.substring(originalName.lastIndexOf("."));
//        String fileName = UUID.randomUUID().toString().replace("-","")+suffix;
//        ossClient.putObject(ossConfig.getBucketName(),fileName,file.getInputStream());
//        String url = "https://"+ossConfig.getBucketName()+"."+ossConfig.getEndpoint()+"/"+fileName;
//        return url;
//    }
//}

package com.trade.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Component
public class OSSUtil {
    //本地保存图片，项目根目录下upload文件夹
    public String uploadFile(MultipartFile file) throws Exception {
        String projectPath = System.getProperty("user.dir");
        File uploadDir = new File(projectPath + File.separator + "upload");
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
        File destFile = new File(uploadDir, fileName);
        file.transferTo(destFile);
        //返回本地访问路径，正式项目部署时替换成你的服务器公网地址
        return "/upload/" + fileName;
    }
}