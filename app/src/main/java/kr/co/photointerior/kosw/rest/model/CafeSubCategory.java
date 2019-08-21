package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 카페 서브 카테고리 정보
 */
public class CafeSubCategory extends ResponseBase implements Serializable {
    @SerializedName("cateseq")
    private String cateseq;
    @SerializedName("name")
    private String name;

    public CafeSubCategory() {}

    public String getCateseq() {
        return cateseq;
    }

    public void setCateseq(String cateseq) {
        this.cateseq = cateseq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String string() {
        final StringBuffer sb = new StringBuffer(super.string() + ",\nCafeSubCategory{");
        sb.append("cateseq='").append(cateseq).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
