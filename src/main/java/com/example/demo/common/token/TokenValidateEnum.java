package com.example.demo.common.token;

/**
 * Token校验枚举值
 *
 * @author : ssk
 * @date : 2020/3/4
 */
public enum TokenValidateEnum {

    NO_VALID("110", "签名不合法"),
    EXPIRE("111", "token已过期"),
    OTHER_ERROR("112", "用户token验证出现未知问题");

    /**
     * 枚举值
     */
    private String value;

    /**
     * 枚举值描述
     */
    private String desc;

    TokenValidateEnum(final String value, final String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

}