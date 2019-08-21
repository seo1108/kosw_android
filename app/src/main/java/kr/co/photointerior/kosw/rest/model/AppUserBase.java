package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * 계단와 앱 사용자 기본정보
 */
public class AppUserBase extends ResponseBase {

    @SerializedName("cust_seq")
    private int cust_seq;

    @SerializedName("cust_name")
    private String cust_name;

    @SerializedName("userId")
    private String userId;
    @SerializedName("userPwd")
    private String userPwd;
    @SerializedName("userName")
    private String userName;
    @SerializedName("nickName")
    private String nickName;

    /** 닉네임 수정시 중복검사 했는가 여부 */
    private boolean nickLookupFinish;
    @SerializedName("charCode")
    private String characterCode;
    @SerializedName("build_code")
    private String buildingCode;

    @SerializedName("build_seq")
    private String build_seq;

    @SerializedName("cust_build_seq")
    private String cust_build_seq;


    @SerializedName("build_name")
    private String build_name;
    @SerializedName("build_addr")
    private String build_addr;

    @SerializedName("place_id")
    private String place_id;

    @SerializedName("build_lat")
    private Double build_lat;
    @SerializedName("build_lng")
    private Double build_lng;


    @SerializedName("latitude")
    private Double latitude;
    @SerializedName("longitude")
    private Double longitude;


    @SerializedName("build_stair_amt")
    private int build_stair_amt;

    @SerializedName("build_floor_amt")
    private int build_floor_amt;

    @SerializedName("user_seq")
    private int user_seq;

    @SerializedName("dept_seq")
    private int dept_seq;

    @SerializedName("dept_name")
    private String dept_name;

    @SerializedName("admin_seq")
    private int admin_seq;

    @SerializedName("token")
    private String userToken;

    @SerializedName("approval_flag")
    private String approval_flag;

    @SerializedName("country")
    private String country;

    @SerializedName("city")
    private String city;

    @SerializedName("isbuild")
    private String isbuild;

    @SerializedName("total_amt")
    private int total_amt;

    @SerializedName("loginType")
    private String loginType;

    @SerializedName("openid")
    private String openid;

    /**
     * 푸시메세지 수신 플래그. pushFlag->push_flag로 변경 2018.06.15.
     */
    @SerializedName("push_flag")
    private String pushFlag;
    /** 회사로고 이미지 파일명 */
    @SerializedName("compLogo")
    private String companyLogo;
    /** 회사칼라 HEX 값 */
    @SerializedName("compColor")
    private String companyColor;
    /** 캐릭터 리스트 */
    @SerializedName("characters")
    private List<Character> characters;
    /** 회원이 오늘 활동한 정보 */
    @SerializedName("today_activity")
    private BeaconUuid todayActivity;
    /** 회원이 등록한 건물에 설치된 전체 비콘 UUID */
    @SerializedName("uuids")
    private List<BeaconUuid> beaconUuidList;
    @SerializedName("mainCharImageFile")
    private String mainCharFilename;
    @SerializedName("subCharImageFile")
    private String subCharFilenane;

    @SerializedName("stair_seq")
    private int stair_seq;
    @SerializedName("beacon_seq")
    private int beacon_seq;


    @SerializedName("bbsSeqs")
    private List<String> bbsSequences;

    public AppUserBase(){}
    public AppUserBase(String userId, String userPwd){
        this.userId = userId;
        this.userPwd = userPwd;
    }

    public AppUserBase(String userId, String pwd, String userName, String nick){
        this(userId, pwd);
        this.userName = userName;
        this.nickName = nick;
    }

    public int getBuild_stair_amt() {
        return build_stair_amt;
    }

    public void setBuild_stair_amt(int build_stair_amt) {
        this.build_stair_amt = build_stair_amt;
    }

    public int getBuild_floor_amt() {
        return build_floor_amt;
    }

    public void setBuild_floor_amt(int build_floor_amt) {
        this.build_floor_amt = build_floor_amt;
    }

    public String getCust_build_seq() {
        return cust_build_seq;
    }

    public void setCust_build_seq(String cust_build_seq) {
        this.cust_build_seq = cust_build_seq;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getApproval_flag() {
        return approval_flag;
    }

    public void setApproval_flag(String approval_flag) {
        this.approval_flag = approval_flag;
    }

    public int getAdmin_seq() {
        return admin_seq;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public void setAdmin_seq(int admin_seq) {
        this.admin_seq = admin_seq;
    }

    public int getStair_seq() {
        return stair_seq;
    }

    public void setStair_seq(int stair_seq) {
        this.stair_seq = stair_seq;
    }

    public int getBeacon_seq() {
        return beacon_seq;
    }

    public void setBeacon_seq(int beacon_seq) {
        this.beacon_seq = beacon_seq;
    }

    public String getBuild_seq() {
        return build_seq;
    }

    public void setBuild_seq(String build_seq) {
        this.build_seq = build_seq;
    }

    public int getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(int user_seq) {
        this.user_seq = user_seq;
    }

    public int getDept_seq() {
        return dept_seq;
    }

    public void setDept_seq(int dept_seq) {
        this.dept_seq = dept_seq;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isNickLookupFinish() {
        return nickLookupFinish;
    }

    public void setNickLookupFinish(boolean nickLookupFinish) {
        this.nickLookupFinish = nickLookupFinish;
    }

    public String getCharacterCode() {
        return characterCode;
    }

    public void setCharacterCode(String characterCode) {
        this.characterCode = characterCode;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getPushFlag() {
        return pushFlag;
    }

    public void setPushFlag(String pushFlag) {
        this.pushFlag = pushFlag;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getCompanyColor() {
        return companyColor;
    }

    public void setCompanyColor(String companyColor) {
        this.companyColor = companyColor;
    }

    public int getCust_seq() {
        return cust_seq;
    }

    public void setCust_seq(int cust_seq) {
        this.cust_seq = cust_seq;
    }

    public Double getBuild_lat() {
        return build_lat;
    }

    public void setBuild_lat(Double build_lat) {
        this.build_lat = build_lat;
    }

    public Double getBuild_lng() {
        return build_lng;
    }

    public void setBuild_lng(Double build_lng) {
        this.build_lng = build_lng;
    }

    public String getBuild_name() {
        return build_name;
    }

    public void setBuild_name(String build_name) {
        this.build_name = build_name;
    }

    public String getBuild_addr() {
        return build_addr;
    }

    public void setBuild_addr(String build_addr) {
        this.build_addr = build_addr;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsbuild() {
        return isbuild;
    }

    public void setIsbuild(String isbuild) {
        this.isbuild = isbuild;
    }

    /**
     * @return 회원이 활동 지역으로 추가한 건물에 설치된 전체 비콘 정보
     */
    public List<BeaconUuid> getBeaconUuidList() {
        return beaconUuidList;
    }

    public void setBeaconUuidList(List<BeaconUuid> beaconUuidList) {
        this.beaconUuidList = beaconUuidList;
    }

    /**
     * @return 회원이 오늘 활동한 정보
     */
    public BeaconUuid getTodayActivity() {
        return todayActivity;
    }

    public void setTodayActivity(BeaconUuid todayActivity) {
        this.todayActivity = todayActivity;
    }

    public String getMainCharFilename() {
        return mainCharFilename;
    }

    public void setMainCharFilename(String mainCharFilename) {
        this.mainCharFilename = mainCharFilename;
    }

    public String getSubCharFilenane() {
        return subCharFilenane;
    }

    public void setSubCharFilenane(String subCharFilenane) {
        this.subCharFilenane = subCharFilenane;
    }

    public int getTotal_amt() {
        return total_amt;
    }

    public void setTotal_amt(int total_amt) {
        this.total_amt = total_amt;
    }


    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }


    public List<String> getBbsSequences() {
        return bbsSequences;
    }

    public void setBbsSequences(List<String> bbsSequences) {
        this.bbsSequences = bbsSequences;
    }

    public String string() {
        return super.string() + "\nAppUserBase{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                ", nickName='" + nickName + '\'' +
                ", nickLookupFinish='" + nickLookupFinish + '\'' +
                ", characterCode='" + characterCode + '\'' +
                ", buildingCode='" + buildingCode + '\'' +
                ", userToken='" + userToken + '\'' +
                ", pushFlag='" + pushFlag + '\'' +
                ", companyLogo='" + companyLogo + '\'' +
                ", companyColor='" + companyColor + '\'' +
                ", mainCharFilename='" + mainCharFilename + '\'' +
                ", subCharFilenane='" + subCharFilenane + '\'' +
                ", loginType='" + loginType + '\'' +
                ", openid='" + openid + '\'' +
                ", characters='" + (characters != null ? Arrays.toString(characters.toArray(new Character[characters.size()])): "null") + '\'' +
                '}';
    }
}
