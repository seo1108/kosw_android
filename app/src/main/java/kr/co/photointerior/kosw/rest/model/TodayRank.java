package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 오늘의 랭킹 정보
 */
public class TodayRank extends ResponseBase {
    @SerializedName("msg")
    private String content;
    @SerializedName("insert_date")
    private String datetime;
    @SerializedName("rnoti_seq")
    private String rnoti_seq;
    @SerializedName("user_seq")
    private String user_seq;
    @SerializedName("cust_seq")
    private String cust_seq;
    @SerializedName("build_seq")
    private String build_seq;
    @SerializedName("noti_type")
    private String noti_type;
    @SerializedName("mnoti_seq")
    private String mnoti_seq;

    @SerializedName("nickname")
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    //rnoti_seq, user_seq, cust_seq , build_seq, msg, noti_type, insert_date, mnoti_seq

    public String getRnoti_seq() {
        return rnoti_seq;
    }

    public void setRnoti_seq(String rnoti_seq) {
        this.rnoti_seq = rnoti_seq;
    }

    public String getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(String user_seq) {
        this.user_seq = user_seq;
    }

    public String getCust_seq() {
        return cust_seq;
    }

    public void setCust_seq(String cust_seq) {
        this.cust_seq = cust_seq;
    }

    public String getBuild_seq() {
        return build_seq;
    }

    public void setBuild_seq(String build_seq) {
        this.build_seq = build_seq;
    }

    public String getNoti_type() {
        return noti_type;
    }

    public void setNoti_type(String noti_type) {
        this.noti_type = noti_type;
    }

    public String getMnoti_seq() {
        return mnoti_seq;
    }

    public void setMnoti_seq(String mnoti_seq) {
        this.mnoti_seq = mnoti_seq;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDatetime() {
        return datetime;
    }
    public String getDatetime(String fmt){
        return StringUtil.isEmptyOrWhiteSpace(getDatetime()) ? "" :
                DateUtil.formatDate("yyyyMMddHHmmss", getDatetime(), fmt);
    }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String string() {
        final StringBuffer sb = new StringBuffer(super.string()).append(", TodayRank{");
        sb.append("content='").append(content).append('\'');
        sb.append(", datetime='").append(datetime).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
