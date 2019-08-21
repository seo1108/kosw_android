package kr.co.photointerior.kosw.service.fcm;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * 푸시 데이터 보유 클래스
 * Created by kugie
 */
public class Push implements Serializable {
    private final String TAG = Push.class.getSimpleName();
    private String title;
    private String pushType;
    private String message;
    private String clickAction;
    private boolean background;

    private Map<String, Object> dataMap = new HashMap<>();

    public Push(String title, String message){
        this.title = title;
        this.message = message;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
    public void addData(String key, Object value){
        dataMap.put(key, value);
    }
    public Object getData(String key){
        return dataMap.get(key);
    }
    public String getStringData(String key){
        return (String)getData(key);
    }
    public Long getLongData(String key){
        return (Long)getData(key);
    }
    public Integer getIntData(String key){
        return (Integer)getData(key);
    }
    public boolean has(String key){
        return getData(key) != null;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public String getClickAction() {
        return clickAction;
    }

    public void setClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public boolean isBackground() {
        return background;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    /**
     * JavaScript로 전달할 json string 반환.
     * @return 데이터 없을 경우 "{}"
     */
    public String toJsonString(){
        try {
            JSONObject json = new JSONObject();
            json.put("title", getTitle());
            json.put("message", getMessage());
            json.put("background", isBackground());
            json.put("data", dataMap);
            return json.toString();
        }catch(JSONException e){
            LogUtils.err(TAG, e);
        }
        return "{}";
    }

    public String string() {
        return "Push{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", background='" + background + '\'' +
                ", clickAction='" + clickAction + '\'' +
                ", dataMap=" + Arrays.toString(dataMap.values().toArray(new Object[dataMap.size()])) +
                '}';
    }
}
