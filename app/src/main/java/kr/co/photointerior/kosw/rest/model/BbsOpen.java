package kr.co.photointerior.kosw.rest.model;

import java.io.Serializable;

/**
 * 게시글 읽었는가 여부 판단.
 */
public class BbsOpen implements Serializable{
    private int localSeq;
    private int bbsSeq;
    private String readFlag = "N";

    public int getLocalSeq() {
        return localSeq;
    }

    public void setLocalSeq(int localSeq) {
        this.localSeq = localSeq;
    }

    public int getBbsSeq() {
        return bbsSeq;
    }

    public void setBbsSeq(int bbsSeq) {
        this.bbsSeq = bbsSeq;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public String string() {
        final StringBuffer sb = new StringBuffer("BbsOpen{");
        sb.append("localSeq=").append(localSeq);
        sb.append(", bbsSeq=").append(bbsSeq);
        sb.append(", readFlag='").append(readFlag).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
