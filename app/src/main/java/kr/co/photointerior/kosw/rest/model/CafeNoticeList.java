package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class CafeNoticeList extends ResponseBase {
    @SerializedName("notice")
    private List<Notice> notice;

    public List<Notice> getNotice() {
        return notice;
    }

    public void setNotice(List<Notice> notice) {
        this.notice = notice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CafeNoticeList that = (CafeNoticeList) o;
        return Objects.equals(notice, that.notice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notice);
    }

    @Override
    public String toString() {
        return "CafeNoticeList{" +
                "notice=" + notice +
                '}';
    }
}
