package com.trade.exception;

import com.trade.vo.ResultVO;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //捕获自定义异常
    @ExceptionHandler(GlobalException.class)
    public ResultVO<?> handleGlobalException(GlobalException e){
        return ResultVO.fail(e.getMessage());
    }
    //参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVO<?> validException(MethodArgumentNotValidException ex){
        FieldError error = ex.getBindingResult().getFieldError();
        return ResultVO.fail(error.getDefaultMessage());
    }
    //通用异常
    @ExceptionHandler(Exception.class)
    public ResultVO<?> commonException(Exception e){
        e.printStackTrace();
        return ResultVO.fail("服务器内部异常");
    }
}