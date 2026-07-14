package com.trade.exception;

import com.trade.vo.ResultVO;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //捕获我们自己抛出的自定义业务异常 GlobalException
    @ExceptionHandler(GlobalException.class)
    public ResultVO<?> handleGlobalException(GlobalException e) {
        return ResultVO.fail(e.getCode(), e.getMsg());
    }

    //处理@RequestBody JSON参数校验异常（MethodArgumentNotValidException）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<?> handleValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError.getDefaultMessage();
        return ResultVO.fail(400, message);
    }

    //处理普通表单提交校验异常（BindException）
    @ExceptionHandler(BindException.class)
    public ResultVO<?> handleBindException(BindException e) {
        FieldError fieldError = e.getFieldError();
        String message = fieldError.getDefaultMessage();
        return ResultVO.fail(400, message);
    }

    //兜底捕获所有未知异常
    @ExceptionHandler(Exception.class)
    public ResultVO<?> handleException(Exception e) {
        //控制台打印异常堆栈，方便开发调试
        e.printStackTrace();
        return ResultVO.fail(500, "服务器内部异常，请稍后重试");
    }
}