package kr.co.photointerior.kosw.rest.model;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * 데이터 보유 컨테이너 클래스.
 */
public class DataHolder {
    private static DataHolder instance = new DataHolder();
    private AppUser appUser;
    private AppUserBase appUserBase;
    private Profile profile;

    private int bgColors[]  ;

    public void setAppUser(AppUser user){
        this.appUser = user;
    }

    public AppUser getAppUser(){
        return this.appUser;
    }

    public void clearAppUser(){
        this.appUser = null;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void clearProfile(){
        this.profile = null;
    }

    public AppUserBase getAppUserBase() {
        return appUserBase;
    }

    public void setAppUserBase(AppUserBase appUserBase) {
        this.appUserBase = appUserBase;
    }

    public static DataHolder instance(){
        return instance;
    }

    public int[] getBgColors() {
        return bgColors;
    }

    public void setBgColors(int[] bgColors) {
        this.bgColors = bgColors;
    }

    private DataHolder(){}
}
