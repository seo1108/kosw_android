package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 활동 기록 데이터
 * "toady_ranking":1,"record_date":"20180623","record_amount":3,"stair_amount":24
 */
public class Record extends ResponseBase {
    @SerializedName("record_name")
    private String name;
    @SerializedName("record_amount")
    private String amount;
    @SerializedName("record_walk_amount")
    private String walkAmount;
    @SerializedName("record_date")
    private String date;
    @SerializedName("record_walk_date")
    private String walkDate;
    @SerializedName("week_name")
    private String weekName;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;
    @SerializedName("stair_amount")
    private String stairAmount;
    @SerializedName("toady_ranking")
    private String todayRanking;
    @SerializedName("act_amt")
    private String act_amt;
    @SerializedName("user_seq")
    private String user_seq;
    @SerializedName("rank")
    private String rank;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }
    public float getAmountToFloat(){
        return Float.parseFloat(StringUtil.isEmptyOrWhiteSpace(getAmount()) ? "0" : getAmount());
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWalkAmount() {
        return walkAmount;
    }

    public float getWalkAmountToFloat(){
        return Float.parseFloat(StringUtil.isEmptyOrWhiteSpace(getWalkAmount()) ? "0" : getWalkAmount());
    }

    public void setWalkAmount(String walkAmount) {
        this.walkAmount = walkAmount;
    }

    public String getDate() {
        return date;
    }

    /**
     * getDate()가 보유하는 날짜 형식은 "yyyyMMdd"이어야 함.
     * @param fmt 날짜형식
     * @return 날짜가 없을 경우 공백문자("")
     */
    public String getDate(String fmt){
        if(StringUtil.isEmptyOrWhiteSpace(getDate())){
            return "";
        }
        return DateUtil.formatDate("yyyyMMdd", getDate(), fmt);
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getWalkDate() {
        return walkDate;
    }

    public String getWalkDate(String fmt){
        if(StringUtil.isEmptyOrWhiteSpace(getDate())){
            return "";
        }
        return DateUtil.formatDate("yyyyMMdd", getWalkDate(), fmt);
    }

    public void setWalkDate(String walkDate) {
        this.walkDate = walkDate;
    }

    public String getWeekName() {
        return weekName;
    }

    public void setWeekName(String weekName) {
        this.weekName = weekName;
    }

    public String getStartDate() {
        return startDate;
    }
    public String getStartDate(String fmt){
        if(StringUtil.isEmptyOrWhiteSpace(getStartDate())){
            return "";
        }
        return DateUtil.formatDate("yyyyMMdd", getStartDate(), fmt);
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }
    public String getEndDate(String fmt){
        if(StringUtil.isEmptyOrWhiteSpace(getEndDate())){
            return "";
        }
        return DateUtil.formatDate("yyyyMMdd", getEndDate(), fmt);
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStairAmount() {
        return stairAmount;
    }
    public float getStairAmountToFloat(){
        return Float.parseFloat(StringUtil.isEmptyOrWhiteSpace(getStairAmount()) ? "0" : getStairAmount());
    }
    public void setStairAmount(String stairAmount) {
        this.stairAmount = stairAmount;
    }

    public String getTodayRanking() {
        return todayRanking;
    }
    public int getTodayRankingToInt(){
        return StringUtil.isEmptyOrWhiteSpace(getTodayRanking()) ? 0 : Float.valueOf(getTodayRanking()).intValue();
    }
    public void setTodayRanking(String todayRanking) {
        this.todayRanking = todayRanking;
    }

    public String getAct_amt() {
        return act_amt;
    }

    public void setAct_amt(String act_amt) {
        this.act_amt = act_amt;
    }

    public String getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(String user_seq) {
        this.user_seq = user_seq;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String string() {
        final StringBuffer sb = new StringBuffer("Record{");
        sb.append("name='").append(name).append('\'');
        sb.append(", amount='").append(amount).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", weekName='").append(weekName).append('\'');
        sb.append(", startDate='").append(startDate).append('\'');
        sb.append(", endDate='").append(endDate).append('\'');
        sb.append(", stairAmount='").append(stairAmount).append('\'');
        sb.append(", todayRanking='").append(todayRanking).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
