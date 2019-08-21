package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import kr.co.photointerior.kosw.utils.DateUtil;

/**
 * 공지사항 . 이벤트, 오늘의 랭킹
 */
public class Bbs extends ResponseBase implements Serializable {
    @SerializedName("bbs_seq")
    private String bbsSeq;
    @SerializedName("admin_name")
    private String adminName;
    @SerializedName("bbs_type")
    private String bbsType;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("expire_date")
    private String expireDate;
    @SerializedName("bbs_reg_time")
    private String registerTime;
    /** 오늘의 랭킹 알림 데이터 */
    @SerializedName("rankNotices")
    private List<TodayRank> todayRankList;
    private String newerFlag = "N";

    public String getBbsSeq() {
        return bbsSeq;
    }

    public void setBbsSeq(String bbsSeq) {
        this.bbsSeq = bbsSeq;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     * 게시물 타입
     * @return N-공지, E-이벤트
     */
    public String getBbsType() {
        return bbsType;
    }
    public boolean isNotice(){
        return "N".equals(getBbsType());
    }
    public void setBbsType(String bbsType) {
        this.bbsType = bbsType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 유효기간
     * @return yyyyMMdd
     */
    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    /**
     * 등록일시
     * @return yyyyMMddHHmmss
     */
    public String getRegisterTime() {
        return registerTime;
    }
    public String getRegisterTime(String fmt){
        return DateUtil.formatDate("yyyyMMddHHmmss", getRegisterTime(), fmt);
    }
    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    /**
     * @return N-게시글 읽지 않음.
     */
    public String getNewerFlag() {
        return newerFlag;
    }
    public boolean isNewer(){
        return "Y".equalsIgnoreCase(getNewerFlag());
    }
    public void setNewerFlag(String newerFlag) {
        this.newerFlag = newerFlag;
    }

    public List<TodayRank> getTodayRankList() {
        return todayRankList;
    }

    public void setTodayRankList(List<TodayRank> todayRankList) {
        this.todayRankList = todayRankList;
    }

    @Override
    public String toString() {
        return "Bbs{" +
                "bbsSeq='" + bbsSeq + '\'' +
                ", adminName='" + adminName + '\'' +
                ", bbsType='" + bbsType + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", registerTime='" + registerTime + '\'' +
                '}';
    }
}
