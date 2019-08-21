package kr.co.photointerior.kosw.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * 컨텐츠 데이터 보유 클래스
 */
public class Contents extends ResponseBase {
    @SerializedName("list")
    private List<Bbs> bbsList;

    /**
     * @return 공지사항 이벤트 리스트
     */
    public List<Bbs> getBbsList() {
        return bbsList;
    }
    public Bbs getBbs(int index){
        return getBbsList() != null ? getBbsList().get(index) : null;
    }
    public void setBbsList(List<Bbs> bbsList) {
        this.bbsList = bbsList;
    }

    public boolean hasBbsArticle(){
        return bbsList != null && bbsList.size() > 0;
    }

    public String string() {
        return super.string() + ", Contents{" +
                "bbsList=" + (bbsList==null ? "null" : Arrays.toString(bbsList.toArray(new Bbs[bbsList.size()]))) + '\'' +
                '}';
    }
}
