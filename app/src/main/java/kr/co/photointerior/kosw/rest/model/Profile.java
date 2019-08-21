package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 회원 프로필 정보
 */
public class Profile extends ResponseBase {
    @SerializedName("nickName")
    private String nickName;
    @SerializedName("userId")
    private String userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("compName")
    private String companyName;
    @SerializedName("deptName")
    private String departName;
    @SerializedName("deptSeq")
    private String departSeq;
    @SerializedName("regDate")
    private String registerData;
    @SerializedName("buildSeq")
    private String buildingSeq;
    @SerializedName("buildName")
    private String buildingName;
    @SerializedName("charSeq")
    private String characterSeq;
    @SerializedName("charCode")
    private String characterCode;
    @SerializedName("charFile")
    private String characterImageFile;
    @SerializedName("departs")
    private List<Depart> departs;
    private Depart depart;

    @SerializedName("chars")
    private List<Character> characters;

    private Character character;

    private boolean checkNick;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getDepartSeq() {
        return StringUtil.isEmptyOrWhiteSpace(departSeq) || "null".equalsIgnoreCase(departSeq)? "0" : departSeq;
    }

    public void setDepartSeq(String departSeq) {
        this.departSeq = departSeq;
    }

    public String getRegisterData() {
        return registerData;
    }

    public void setRegisterData(String registerData) {
        this.registerData = registerData;
    }

    public String getBuildingSeq() {
        return buildingSeq;
    }

    public void setBuildingSeq(String buildingSeq) {
        this.buildingSeq = buildingSeq;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getCharacterSeq() {
        return characterSeq;
    }

    public void setCharacterSeq(String characterSeq) {
        this.characterSeq = characterSeq;
    }

    public String getCharacterCode() {
        return characterCode;
    }

    public void setCharacterCode(String characterCode) {
        this.characterCode = characterCode;
    }

    public String getCharacterImageFile() {
        return characterImageFile;
    }

    public void setCharacterImageFile(String characterImageFile) {
        this.characterImageFile = characterImageFile;
    }

    public List<Depart> getDeparts() {
        return departs;
    }

    public void setDeparts(List<Depart> departs) {
        this.departs = departs;
    }

    public Depart getDepart() {
        return depart;
    }

    public void setDepart(Depart depart) {
        this.depart = depart;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public boolean isCheckNick() {
        return checkNick;
    }

    public void setCheckNick(boolean checkNick) {
        this.checkNick = checkNick;
    }

    public String string() {
        return super.string() + ", Profile{" +
                "nickName='" + nickName + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", departName='" + departName + '\'' +
                ", departSeq='" + departSeq + '\'' +
                ", registerData='" + registerData + '\'' +
                ", buildingSeq='" + buildingSeq + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", characterSeq='" + characterSeq + '\'' +
                ", characterCode='" + characterCode + '\'' +
                ", characterImageFile='" + characterImageFile + '\'' +
                ", departs=" + departs +
                ", characters=" +
                (characters != null ? Arrays.toString(characters.toArray(new Character[characters.size()])) : "null") +
                '\'' +
                '}';
    }
}
