package kr.co.photointerior.kosw.rest.model;

import android.content.Context;

import java.util.Map;

import kr.co.photointerior.kosw.utils.KUtil;

public class BeaconLog extends ResponseBase {
    private String userId;
    private String buildingCode;
    private String message;
    private Context context;
    private String stackData;
    public BeaconLog(Context context){
        this.context = context;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public Context getContext(){
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getStackData() {
        return stackData;
    }

    public void setStackData(String stackData) {
        this.stackData = stackData;
    }

    public String string() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("userId='").append(userId).append('\'');
        sb.append(", buildingCode='").append(buildingCode).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Map<String, Object> createQueryMap(){
        Map<String, Object> map = KUtil.getDefaultQueryMap();
        map.put("user_id", getUserId());
        map.put("build_code", getBuildingCode());
        map.put("log_msg", getMessage());
        return map;
    }
}
