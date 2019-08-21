package kr.co.photointerior.kosw.global;

/**
 * 기간 구분 enum
 */
public enum PeriodType {
    DAILY("D"),
    WEEKLY("W"),
    MONTHLY("M"),
    YEARLY("Y"),
    ALL("A");
    private String value;
    PeriodType(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }
}
