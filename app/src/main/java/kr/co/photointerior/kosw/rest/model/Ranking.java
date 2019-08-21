package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 개인 그룹 랭킹 데이터
 */
public class Ranking extends Record {
    /** 나의기록인가 여부. Y or N*/
    @SerializedName("is_me")
    private String isMeFlag;
    @SerializedName("ranking")
    private String ranking;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("cust_name")
    private String customerName;
    @SerializedName("dept_name")
    private String departName;
    public String getIsMeFlag() {
        return isMeFlag;
    }
    public boolean isMe(){
        return "Y".equalsIgnoreCase(getIsMeFlag());
    }
    public void setIsMeFlag(String isMeFlag) {
        this.isMeFlag = isMeFlag;
    }

    public String getRanking() {
        return ranking;
    }
    public int getRankingToInt(){
        return Float.valueOf(StringUtil.isEmptyOrWhiteSpace(getRanking()) ? "0" : getRanking()).intValue();
    }
    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustomerName() {
        return StringUtil.isEmptyOrWhiteSpace(customerName) ? "" : customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDepartName() {
        return StringUtil.isEmptyOrWhiteSpace(departName) ? "" : departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.string() + ", Ranking{");
        sb.append("isMeFlag='").append(isMeFlag).append('\'');
        sb.append(", ranking='").append(ranking).append('\'');
        sb.append(", nickname='").append(nickname).append('\'');
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", customerName='").append(customerName).append('\'');
        sb.append(", departName='").append(departName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
