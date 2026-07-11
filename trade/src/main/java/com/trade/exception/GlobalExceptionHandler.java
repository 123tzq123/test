package com.trade.exception;

import com.trade.vo.ResultVO;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //捕获自定义业务异常
    @ExceptionHandler(GlobalException.class)
    public ResultVO<?> handleGlobalException(GlobalException e) {
        return ResultVO.fail(e.getCode(), e.getMsg());
    }

    //@Valid 参数校验异常
    @ExceptionHandler(BindException.class)
    public ResultVO<?> handleBindException(BindException e) {
        String message = e.getFieldError().getDefaultMessage();
        return ResultVO.fail(500, message);
    }

    //系统未知异常
    @ExceptionHandler(Exception.class)
    public ResultVO<?> handleException(Exception e) {
        //打印异常堆栈，后端查看报错信息
        e.printStackTrace();
        return ResultVO.fail(500, "服务器内部异常，请稍后重试");
    }
}