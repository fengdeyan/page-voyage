package me.yan.common.enums;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    // 成功
    SUCCESS(200, "操作成功"),
    USERNAME_EMPTY(400, "用户名不能为空"),   // <-- 新增
    PASSWORD_EMPTY(400, "密码不能为空"),
    // 客户端错误
    PARAM_ERROR(400, "参数非法"),
    LOGIN_ERROR(401, "用户名或密码错误"),
    NO_PERMISSION(403, "权限不足"),
    // 业务错误
    USER_NOT_EXIST(1001, "用户不存在"),
    // 系统异常
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试");

    private final Integer code;
    private final String msg;

    private ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}