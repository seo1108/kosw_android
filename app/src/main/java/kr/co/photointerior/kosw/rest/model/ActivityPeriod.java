package kr.co.photointerior.kosw.rest.model;

import kr.co.photointerior.kosw.global.PeriodType;
import kr.co.photointerior.kosw.utils.DateUtil;

/**
 * 주별/월별 통계 기간 데이터 보유
 */
public class ActivityPeriod {
	//public enum PeriodType{ WEEKLY, MONTHLY }
    private PeriodType type;
	/** 기준 일자 */
    private String baseDate;
    /** 기준일자가 속한 주.월의 시작일자 */
    private String startDate;
    /** 지군일자가 속한 주.월의 종료일자 */
    private String endDate;
    /** 주간일 경우 x주차의 (x), 월간의 경우 x월의 (x) */
    private String stepValue;
	private String visibleString;

	public PeriodType getType() {
		return type;
	}
	public void setType(PeriodType type) {
		this.type = type;
	}
	public String getBaseDate() {
		return baseDate;
	}
	public void setBaseDate(String baseDate) {
		this.baseDate = baseDate;
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getStepValue() {
		return stepValue;
	}
	public void setStepValue(String stepValue) {
		this.stepValue = stepValue;
	}

	/**
	 * Weekly, monthly 타입에 따라 날짜 표현을 반환.
	 * @return 주별 (xxxx년 xx월 x주차), 월별 (xxxx년 xx월)
	 */
	public String getVisibleString(){
		/*if( PeriodType.WEEKLY.equals(type)){
			return DateUtil.formatDate("yyyyMMdd", getBaseDate(), "yyyy년 MM월 " + getStepValue() + "주차");
		}
		return  DateUtil.formatDate("yyyyMMdd", getBaseDate(), "yyyy년 MM월");*/
		return this.visibleString;
	}

	public ActivityPeriod setVisibleString(String txt){
		this.visibleString = txt;
		return this;
	}

	public void pickupVisibleString(){
		if( PeriodType.WEEKLY == type ){
			this.visibleString = DateUtil.formatDate("yyyyMMdd", getBaseDate(), "yyyy년 MM월 " + getStepValue() + "주차");
		}else {
			this.visibleString = DateUtil.formatDate("yyyyMMdd", getBaseDate(), "yyyy년 MM월");
		}
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("ActivityPeriod{");
		sb.append("type=").append(type);
		sb.append(", baseDate='").append(baseDate).append('\'');
		sb.append(", startDate='").append(startDate).append('\'');
		sb.append(", endDate='").append(endDate).append('\'');
		sb.append(", stepValue='").append(stepValue).append('\'');
		sb.append(", visibleString='").append(getVisibleString()).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
