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

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class OSSUtil {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    /**
     * 上传文件到阿里云OSS
     * @param file 前端传过来的图片
     * @return 返回图片完整访问URL
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 创建OSS客户端实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 获取文件后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        // 使用UUID防止文件名重复
        String fileName = UUID.randomUUID().toString().replace("-","") + suffix;
        // 放到image文件夹，方便后期管理
        String objectName = "image/" + fileName;

        //上传文件
        ossClient.putObject(bucketName, objectName, file.getInputStream());
        //关闭客户端
        ossClient.shutdown();

        //返回外网访问地址
        return "https://"+bucketName+"."+endpoint+"/"+objectName;
    }
}