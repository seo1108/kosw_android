package kr.co.photointerior.kosw.rest.model;

/**
 * 스피너 wor를 구성하는 데이터
 */
public class SpinnerRow {
    private String basis;
    private String name;
    public SpinnerRow(){}
    public SpinnerRow(String basis, String name){
        this.basis = basis;
        this.name = name;
    }
    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String string() {
        final StringBuffer sb = new StringBuffer("SpinnerRow{");
        sb.append("basis='").append(basis).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
