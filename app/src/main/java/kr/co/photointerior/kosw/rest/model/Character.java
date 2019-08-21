package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * 사용자 캐릭터 정보
 */
public class Character extends ResponseBase {
    @SerializedName("seq")
    private String charSeq;
    @SerializedName("name")
    private String charName;
    @SerializedName("code")
    private String charCode;
    @SerializedName("imageFile")
    private String charImageFile;
    /** 신체타입 -2, -1, 0, 1, 2 */
    @SerializedName("bodyType")
    private String charBodyType;
    /** 저지타입 default, gold, green, red */
    @SerializedName("jerseyType")
    private String chaJerseyType;
    /** 성별 M,W */
    @SerializedName("gender")
    private String charGender;
    /** 앱 메인 계단 포함 이미지 'Y', 계단 미포함 이미지 'N' */
    @SerializedName("stairYn")
    private String charStairYn;
    /** 캐릭터 선택상태 */
    private boolean selected;

    public String getCharSeq() {
        return charSeq;
    }

    public void setCharSeq(String charSeq) {
        this.charSeq = charSeq;
    }

    public String getCharName() {
        return charName;
    }

    public void setCharName(String charName) {
        this.charName = charName;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public String getCharImageFile() {
        return charImageFile;
    }

    public void setCharImageFile(String charImageFile) {
        this.charImageFile = charImageFile;
    }

    public String getCharBodyType() {
        return charBodyType;
    }

    public void setCharBodyType(String charBodyType) {
        this.charBodyType = charBodyType;
    }

    public String getChaJerseyType() {
        return chaJerseyType;
    }

    public void setChaJerseyType(String chaJerseyType) {
        this.chaJerseyType = chaJerseyType;
    }

    public String getCharGender() {
        return charGender;
    }

    public void setCharGender(String charGender) {
        this.charGender = charGender;
    }

    public String getCharStairYn() {
        return charStairYn;
    }

    public void setCharStairYn(String charStairYn) {
        this.charStairYn = charStairYn;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String string() {
        return super.string() +", Character{" +
                "charSeq='" + charSeq + '\'' +
                ", charName='" + charName + '\'' +
                ", charCode='" + charCode + '\'' +
                ", charImageFile='" + charImageFile + '\'' +
                ", charBodyType='" + charBodyType + '\'' +
                ", chaJerseyType='" + chaJerseyType + '\'' +
                ", charGender='" + charGender + '\'' +
                ", charStairYn='" + charStairYn + '\'' +
                ", selected='" + selected + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return "Character{" +
                "charSeq='" + charSeq + '\'' +
                ", charName='" + charName + '\'' +
                ", charCode='" + charCode + '\'' +
                ", charImageFile='" + charImageFile + '\'' +
                ", charBodyType='" + charBodyType + '\'' +
                ", chaJerseyType='" + chaJerseyType + '\'' +
                ", charGender='" + charGender + '\'' +
                ", charStairYn='" + charStairYn + '\'' +
                ", selected=" + selected +
                '}';
    }
}
