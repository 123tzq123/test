package com.trade.exception;

import lombok.Data;

@Data
public class GlobalException extends RuntimeException{
    private Integer code;
    public GlobalException(Integer code,String msg){
        super(msg);
        this.code = code;
    }
}