package kr.co.photointerior.kosw.global;

import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * Http response type definition enum.
 * Created by kugie on 2018. 4. 30.
 */
public enum ResponseType {
    SUCCESS("0000", "success"),
    DUPLICATED_DATA("1001", "duplicated the data."),
    INVALID_PARAMETER("1002", "invalid parameter."),
    DATA_NOT_EXIST("1003", "data not exitst."),
    PAGE_NOT_FOUND("1004", "data not found."),
    DATA_TYPE_MISMATCH("1005", "data type mismatch."),
    WORK_FAIL("1006", "server work fail."),
    NOT_AUTHENTICATED("1007", "email authentication not proceed."),
    SERVER_ERROR("2000", "internal server error or database error."),
    HAS_NO_LOGIN_INFO("3000", "login information not found."),
    EXTRA("9999", "extra conditions.");
    private String resCode;
    private String msg;
    ResponseType(String code, String msg){
        this.resCode = code;
        this.msg = msg;
    }
    public String code() {
        return resCode;
    }

    public String msg() {
        return this.msg;
    }

    public boolean isMatchCode(String code){
        return StringUtil.isEquals(this.resCode, code);
    }
}
