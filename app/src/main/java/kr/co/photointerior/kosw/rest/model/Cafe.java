package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * 카페 기본 정보
 */
public class Cafe extends ResponseBase {
    @SerializedName("total")
    private String total;
    @SerializedName("cafedesc")
    private String cafedesc;
    @SerializedName("additions")
    private String additions;
    @SerializedName("cafename")
    private String cafename;
    @SerializedName("cafekey")
    private String cafekey;
    @SerializedName("logo")
    private String logo;
    @SerializedName("opendate")
    private String opendate;
    @SerializedName("category")
    private List<CafeSubCategory> category;
    @SerializedName("cafeseq")
    private String cafeseq;
    @SerializedName("confirm")
    private String confirm;
    @SerializedName("isjoin")
    private String isjoin;
    @SerializedName("admin")
    private String admin;
    @SerializedName("adminseq")
    private String adminseq;
    @SerializedName("myadditions")
    private String myadditions;
    @SerializedName("cateseq")
    private String cateseq;
    @SerializedName("joindate")
    private String joindate;


    private boolean selected;
    /**
     * 카페타입 : A -> 내가 개설한 카페, J -> 참여중인 카페
     */
    private String cafetype;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCafedesc() {
        return cafedesc;
    }

    public void setCafedesc(String cafedesc) {
        this.cafedesc = cafedesc;
    }

    public String getAdditions() {
        return additions;
    }

    public void setAdditions(String additions) {
        this.additions = additions;
    }

    public String getCafename() {
        return cafename;
    }

    public void setCafename(String cafename) {
        this.cafename = cafename;
    }

    public String getCafekey() {
        return cafekey;
    }

    public void setCafekey(String cafekey) {
        this.cafekey = cafekey;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getOpendate() {
        return opendate;
    }

    public void setOpendate(String opendate) {
        this.opendate = opendate;
    }

    public List<CafeSubCategory> getCategory() {
        return category;
    }

    public void setCategory(List<CafeSubCategory> category) {
        this.category = category;
    }

    public String getCafeseq() {
        return cafeseq;
    }

    public void setCafeseq(String cafeseq) {
        this.cafeseq = cafeseq;
    }

    public String getCafetype() {
        return cafetype;
    }

    public void setCafetype(String cafetype) {
        this.cafetype = cafetype;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getIsjoin() {
        return isjoin;
    }

    public void setIsjoin(String isjoin) {
        this.isjoin = isjoin;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getAdminseq() {
        return adminseq;
    }

    public void setAdminseq(String adminseq) {
        this.adminseq = adminseq;
    }

    public String getMyadditions() {
        return myadditions;
    }

    public void setMyadditions(String myadditions) {
        this.myadditions = myadditions;
    }

    public String getCateseq() {
        return cateseq;
    }

    public void setCateseq(String cateseq) {
        this.cateseq = cateseq;
    }

    public String getJoindate() {
        return joindate;
    }

    public void setJoindate(String joindate) {
        this.joindate = joindate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cafe cafe = (Cafe) o;
        return selected == cafe.selected &&
                Objects.equals(total, cafe.total) &&
                Objects.equals(cafedesc, cafe.cafedesc) &&
                Objects.equals(additions, cafe.additions) &&
                Objects.equals(cafename, cafe.cafename) &&
                Objects.equals(cafekey, cafe.cafekey) &&
                Objects.equals(logo, cafe.logo) &&
                Objects.equals(opendate, cafe.opendate) &&
                Objects.equals(category, cafe.category) &&
                Objects.equals(cafeseq, cafe.cafeseq) &&
                Objects.equals(confirm, cafe.confirm) &&
                Objects.equals(isjoin, cafe.isjoin) &&
                Objects.equals(admin, cafe.admin) &&
                Objects.equals(adminseq, cafe.adminseq) &&
                Objects.equals(myadditions, cafe.myadditions) &&
                Objects.equals(cateseq, cafe.cateseq) &&
                Objects.equals(joindate, cafe.joindate) &&
                Objects.equals(cafetype, cafe.cafetype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, cafedesc, additions, cafename, cafekey, logo, opendate, category, cafeseq, confirm, isjoin, admin, adminseq, myadditions, cateseq, joindate, selected, cafetype);
    }

    @Override
    public String toString() {
        return "Cafe{" +
                "total='" + total + '\'' +
                ", cafedesc='" + cafedesc + '\'' +
                ", additions='" + additions + '\'' +
                ", cafename='" + cafename + '\'' +
                ", cafekey='" + cafekey + '\'' +
                ", logo='" + logo + '\'' +
                ", opendate='" + opendate + '\'' +
                ", category=" + category +
                ", cafeseq='" + cafeseq + '\'' +
                ", confirm='" + confirm + '\'' +
                ", isjoin='" + isjoin + '\'' +
                ", admin='" + admin + '\'' +
                ", adminseq='" + adminseq + '\'' +
                ", myadditions='" + myadditions + '\'' +
                ", cateseq='" + cateseq + '\'' +
                ", joindate='" + joindate + '\'' +
                ", selected=" + selected +
                ", cafetype='" + cafetype + '\'' +
                '}';
    }
}

