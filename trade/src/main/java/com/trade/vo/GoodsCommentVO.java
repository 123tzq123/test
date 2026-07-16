package com.trade.vo;
import com.trade.domain.SysUser;
import lombok.Data;
import java.util.List;

@Data
public class GoodsCommentVO {
    private Long id;
    private Long goodsId;
    private Integer score;
    private String content;
    private String imgList; //数据库原始字符串
    private List<String> imgListStr; //拆分后的图片数组，给到前端
    private String createTime;
    private SysUser buyerInfo;
}