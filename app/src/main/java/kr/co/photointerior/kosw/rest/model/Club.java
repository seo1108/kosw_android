package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import kr.co.photointerior.kosw.utils.DateUtil;
import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * Club
 */
public class Club extends ResponseBase {

    @SerializedName("club_seq")
    private String club_seq;
    @SerializedName("user_seq")
    private String user_seq;
    @SerializedName("regi_date")
    private String regi_date;
    @SerializedName("club_kind")
    private String club_kind;
    @SerializedName("stair_amt")
    private String stair_amt;

    public String getClub_seq() {
        return club_seq;
    }

    public void setClub_seq(String club_seq) {
        this.club_seq = club_seq;
    }

    public String getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(String user_seq) {
        this.user_seq = user_seq;
    }

    public String getRegi_date() {
        return regi_date;
    }

    public String getRegi_date(String fmt){
        if(StringUtil.isEmptyOrWhiteSpace(getRegi_date())){
            return "";
        }
        return DateUtil.formatDate("yyyyMMdd", getRegi_date(), fmt);
    }

    public void setRegi_date(String regi_date) {
        this.regi_date = regi_date;
    }

    public String getClub_kind() {
        return club_kind;
    }

    public void setClub_kind(String club_kind) {
        this.club_kind = club_kind;
    }

    public String getStair_amt() {
        return stair_amt;
    }

    public float getStair_amtToFloat(){
        return Float.parseFloat(StringUtil.isEmptyOrWhiteSpace( getStair_amt()) ? "0" :  getStair_amt());
    }

    public void setStair_amt(String stair_amt) {
        this.stair_amt = stair_amt;
    }


    public Club(){}


    @Override
    public String toString() {
        return "club{" +
                "club_seq='" + club_seq + '\'' +
                ", user_seq='" + user_seq + '\'' +
                '}';
    }

    public static  String getClubName(String club_kind) {

        String clubName = "" ;

        if (club_kind == "00") {
            clubName = "" ;
        } else if (club_kind.equals("01")) {
            clubName = "100클럽" ;
        } else if (club_kind.equals("02")) {
            clubName = "1000클럽" ;
        } else if (club_kind.equals("03")) {
            clubName = "10000클럽" ;
        } else if (club_kind.equals("04")) {
            clubName = "100000클럽" ;
        } else if (club_kind.equals("05")) {
            clubName = "1000000클럽" ;
        }

        return clubName ;
    }

}
