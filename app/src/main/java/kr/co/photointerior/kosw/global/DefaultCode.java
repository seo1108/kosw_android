package kr.co.photointerior.kosw.global;

/**
 * Http response type definition enum.
 * Created by kugie on 2018. 4. 30.
 */
public enum DefaultCode {
    CUST_SEQ(20) ,
    ADMIN_SEQ(26) ,
    GPS_RANGE(2000) ,
    BEACON_SEQ(12) ;

    private int value;
    DefaultCode(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }

}
