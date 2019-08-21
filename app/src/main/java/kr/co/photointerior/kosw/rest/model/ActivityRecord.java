package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 활동기록, 랭킹 데이터 보유 클래스.
 */
public class ActivityRecord extends ResponseBase {
    /** 활동기록 일자 yyyyMMdd */
    @SerializedName("record_date")
    private String recordDate;
    /** 전체 활동기록 */
    @SerializedName("total_record")
    private Record totalRecord;
    /** 일평균 활동기록 */
    @SerializedName("daily_average")
    private Record dailyAverage;
    /** 최대 활동기록 */
    @SerializedName("max_record")
    private Record maxAmount;
    /** 일별 전체 활동기록 */
    @SerializedName("daily_records")
    private List<Record> dailyRecords;
    /** 개인별 랭킹 */
    @SerializedName("ranking_private")
    private List<Ranking> rankingPrivateList;

    /** 그룹별 랭킹 */
    @SerializedName("ranking_group")
    private List<Ranking> rankingGroupList;

    /** 개인 활동 분석 */
    @SerializedName("analysis_data")
    private List<Record> analysisRecords;
    @SerializedName("analysis_ranking")
    private String analysisRanking;
    @SerializedName("analysis_total")
    private String analysisTotal;

    /** 개인 랭킹 */
    @SerializedName("todayRank")
    private Record todayRank;
    @SerializedName("totalRank")
    private Record totalRank;

    /* 클럽달성 내역 */
    @SerializedName("clubList")
    private List<Club> clubList ;

    public List<Club> getClubList() {
        return clubList;
    }

    public void setClubList(List<Club> clubList) {
        this.clubList = clubList;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public Record getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Record totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Record getDailyAverage() {
        return dailyAverage;
    }

    public void setDailyAverage(Record dailyAverage) {
        this.dailyAverage = dailyAverage;
    }

    public Record getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Record maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<Record> getDailyRecords() {
        return dailyRecords;
    }

    public void setDailyRecords(List<Record> dailyRecords) {
        this.dailyRecords = dailyRecords;
    }

    public List<Ranking> getRankingPrivateList() {
        return rankingPrivateList;
    }

    public void setRankingPrivateList(List<Ranking> rankingPrivateList) {
        this.rankingPrivateList = rankingPrivateList;
    }

    /**
     * @return 그룹 랭킹
     */
    public List<Ranking> getRankingGroupList() {
        return rankingGroupList;
    }

    public void setRankingGroupList(List<Ranking> rankingGroupList) {
        this.rankingGroupList = rankingGroupList;
    }

    /**
     * 분석
     * @return
     */
    public List<Record> getAnalysisRecords() {
        return analysisRecords;
    }

    public void setAnalysisRecords(List<Record> analysisRecords) {
        this.analysisRecords = analysisRecords;
    }

    /**
     * 분석 랭킹
     * @return
     */
    public String getAnalysisRanking() {
        return analysisRanking;
    }
    public int getAnalysisRankingToInt(){
        return StringUtil.isEmptyOrWhiteSpace(getAnalysisRanking()) ? 0 :
                Float.valueOf(getAnalysisRanking()).intValue();
    }
    public void setAnalysisRanking(String analysisRanking) {
        this.analysisRanking = analysisRanking;
    }

    /**
     * 분석기간 총 오른 계단 수
     * @return
     */
    public String getAnalysisTotal() {
        return analysisTotal;
    }
    public int getAnalysisTotalToInt(){
        return StringUtil.isEmptyOrWhiteSpace(getAnalysisTotal()) ? 0 : Float.valueOf(getAnalysisTotal()).intValue();
    }
    public void setAnalysisTotal(String analysisTotal) {
        this.analysisTotal = analysisTotal;
    }

    public Record getTodayRank() {
        return todayRank;
    }

    public void setTodayRank(Record todayRank) {
        this.todayRank = todayRank;
    }

    public Record getTotalRank() {
        return totalRank;
    }

    public void setTotalRank(Record totalRank) {
        this.totalRank = totalRank;
    }
}
