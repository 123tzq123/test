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
    //自定义错误码+消息
    public static <T> ResultVO<T> fail(Integer code, String msg) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return resultVO;
    }

    //默认500错误码
    public static <T> ResultVO<T> fail(String msg) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(500);
        resultVO.setMsg(msg);
        return resultVO;
    }
}