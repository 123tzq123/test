package com.trade.vo;

import lombok.Data;

@Data
public class ChatSessionVO {
    // 对方用户ID
    private Long otherId;
    // 对方头像
    private String otherAvatar;
    // 对方昵称
    private String otherName;
    // 最后一条消息内容
    private String lastMsg;
    // 最后消息时间
    private String lastMsgTime;
    // 当前会话未读消息数量
    private Long unreadNum;
    // 关联商品ID
    private Long goodsId;
}