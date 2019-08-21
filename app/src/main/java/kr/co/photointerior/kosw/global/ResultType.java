package kr.co.photointerior.kosw.global;

import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * Http response type definition enum.
 * Created by kugie on 2018. 4. 30.
 */
public enum ResultType {
    BUILDING_SELECT(10000) ,
    NOTICE_INPUT(20000) ;

    private int value;
    ResultType(int value){
        this.value = value;
    }
    public int getValue(){
        return value;
    }

}
