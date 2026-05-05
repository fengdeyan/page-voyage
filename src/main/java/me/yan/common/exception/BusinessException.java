package me.yan.common.exception;


import lombok.Getter;
import me.yan.common.enums.ResultCodeEnum;

/**
 * 全局业务异常
 * @Author: 冯德衍
 */
@Getter
public class BusinessException extends RuntimeException{

    /**
     * 状态码
     */
    private final Integer code;

    public BusinessException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMsg());
        this.code = resultCodeEnum.getCode();
    }

    public BusinessException(Integer code, String message){
        super(message);
        this.code = code;
    }
}
