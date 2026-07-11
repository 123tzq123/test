package com.trade.exception;

import lombok.Data;

@Data
public class GlobalException extends RuntimeException{
    private Integer code;
    private String msg;

    public GlobalException(String msg){
        super(msg);
        this.code = 500;
        this.msg = msg;
    }

    public GlobalException(Integer code,String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}