package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * 앱 버전 데이터 보유
 */
public class AppVersion extends ResponseBase {
    @SerializedName("curVer")
    private String currentVersion;
    @SerializedName("updated")
    private boolean isUpdated;
    @SerializedName("msg")
    private String message;
    @SerializedName("url")
    private String marketUrl;
    @SerializedName("kill")
    private boolean isForceFinish;

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMarketUrl() {
        return marketUrl;
    }

    public void setMarketUrl(String marketUrl) {
        this.marketUrl = marketUrl;
    }

    public boolean isForceFinish() {
        return isForceFinish;
    }

    public void setForceFinish(boolean forceFinish) {
        isForceFinish = forceFinish;
    }

    public String string() {
        return super.string() + "\nAppVersion{" +
                "currentVersion='" + currentVersion + '\'' +
                ", isUpdated=" + isUpdated +
                ", message='" + message + '\'' +
                ", marketUrl='" + marketUrl + '\'' +
                ", isForceFinish=" + isForceFinish +
                '}';
    }
}
