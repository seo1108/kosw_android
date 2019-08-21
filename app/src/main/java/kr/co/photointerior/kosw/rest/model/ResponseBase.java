package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import kr.co.photointerior.kosw.global.ResponseType;

/**
 * 서버응답 기본데이터 보유 클래스입니다.
 * Created by kugie on 2018. 4. 30.
 */

public class ResponseBase implements Serializable {
    private static final long serialVersionUID = -7162804077415538099L;
    /** 응답코드 {@link ResponseType} 참조 */
    @SerializedName("resCode")
    private String responseCode;
    /** 응답메세지 */
    @SerializedName("resMsg")
    private String responseMessage;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * 서버요청 작업이 성공했는가 여부를 판단.
     * @return true 성공.
     */
    public boolean isSuccess(){
        return "0000".equals(getResponseCode());
    }

    public String string() {
        return "ResponseBase{" +
                "responseCode='" + responseCode + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                '}';
    }
}
