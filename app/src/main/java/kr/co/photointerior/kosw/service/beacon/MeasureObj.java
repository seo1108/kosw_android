package kr.co.photointerior.kosw.service.beacon;


/**
 * Created by kimminjib on 2018. 11. 6..
 */

public class MeasureObj {

    public  int pos ;

    public  Double altitude ;
    public  Double orientation ;
    public  Double step ;
    public  Double x;
    public  Double y;
    public  Double z;

    public  Double altitudeGap ;
    public  Double orientationGap ;
    public  Double stepGap ;
    public  Double xGap;
    public  Double yGap;
    public  Double zGap;

    public long timestamp ;
    public long timestampGap ;

    public MeasureObj() {
        this.altitude = 0.0 ;
        this.orientation = 0.0 ;
        this.step = 0.0 ;
        this.x = 0.0 ;
        this.y = 0.0 ;
        this.z = 0.0 ;

        this.altitudeGap = 0.0 ;
        this.orientationGap = 0.0 ;
        this.stepGap = 0.0 ;
        this.xGap = 0.0 ;
        this.yGap = 0.0 ;
        this.zGap = 0.0 ;

        this.timestamp = System.currentTimeMillis()/1000;
        this.timestampGap = 0 ;
    }
}
