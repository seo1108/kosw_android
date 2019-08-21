package kr.co.photointerior.kosw.rest.model;

import android.os.Build;
import android.util.Log;

import java.util.Locale;
import java.util.Map;

import kr.co.photointerior.kosw.utils.KUtil;

/**
 * 계단와 앱 사용자
 */
public class AppUser extends AppUserBase {
    /** 호속 회사 */
    private Company company;
    /** 활동 빌팅 */
    private Building building;
    /** 회원이 소속된 부서 */
    private Depart depart;
    /** 기본 캐릭터 */
    private Character character;

    private String departCode;
    private String departName;
    public AppUser(){}
    public AppUser(String id, String pwd){
        super(id, pwd);
    }

    public String getDepartCode() {
        return departCode;
    }

    public void setDepartCode(String departCode) {
        this.departCode = departCode;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Depart getDepart() {
        return depart;
    }

    public void setDepart(Depart depart) {
        this.depart = depart;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Map<String, Object> createSignUpQueryMap(){
        Map<String, Object> map = KUtil.getDefaultQueryMap();
        map.put("id", getUserId());
        map.put("pwd", getUserPwd());
        map.put("name", getUserName());
        String nick =  getUserId().split("@", -1)[0];
        map.put("nickname", getUserName());
        map.put("buildCode", (getBuilding() == null ? "" : getBuilding().getBuildingCode()));
        map.put("departCode", (getDepart() == null ? "" : getDepart().getDepartSeq()));
        // add 2019.1.1
        map.put("deviceModel", Build.MODEL) ;
        map.put("locale",Locale.getDefault().getCountry()) ;

        return map;
    }

    public Map<String, Object> createCharacterQueryMap(){
        Map<String, Object> map = KUtil.getDefaultQueryMap();
        map.put("charSeq", character.getCharSeq());
        map.put("nick", getNickName());
        return map;
    }

    public String string() {
        return super.string() + "\nAppUser{" +
                "company='" + (company != null ? company.string() : "null") + '\'' +
                ", building='" + (building != null ? building.string() : "null") + '\'' +
                ", depart='" + (depart != null ? depart.string() : "null") + '\'' +
                ", character='" + (character != null ? character.string() : "null") + '\'' +
                '}';
    }
}
