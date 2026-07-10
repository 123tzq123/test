package com.trade.vo;
import lombok.Data;

@Data
public class ResultVO<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> ResultVO<T> success(T data) {
        ResultVO<T> vo = new ResultVO<>();
        vo.setCode(200);
        vo.setMsg("成功");
        vo.setData(data);
        return vo;
    }
    public static <T> ResultVO<T> fail(String msg){
        ResultVO<T> vo = new ResultVO<>();
        vo.setCode(500);
        vo.setMsg(msg);
        return vo;
    }
}