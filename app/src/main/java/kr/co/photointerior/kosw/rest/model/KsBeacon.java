package kr.co.photointerior.kosw.rest.model;

import java.io.Serializable;

import kr.co.photointerior.kosw.utils.StringUtil;

/**
 * 수신한 비콘 데이터 보유 클래스
 */
public class KsBeacon implements Comparable<KsBeacon>, Serializable{
    private int beaconSeq;
    private String uuid;
    private String major;
    private String minor;
    private int rssi;
    private String buildingCode;
    private double altitude;

    public int getBeaconSeq() {
        return beaconSeq;
    }

    public KsBeacon setBeaconSeq(int beaconSeq) {
        this.beaconSeq = beaconSeq;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public KsBeacon setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getMajor() {
        return major;
    }
    public int getMajorToInt(){
        return StringUtil.isEmptyOrWhiteSpace(getMajor()) ? 0 : Float.valueOf(getMajor()).intValue();
    }
    public KsBeacon setMajor(String major) {
        this.major = major;
        return this;
    }

    public String getMinor() {
        return minor;
    }
    public int getMinorToInt(){
        return StringUtil.isEmptyOrWhiteSpace(getMinor()) ? 0 : Float.valueOf(getMinor()).intValue();
    }
    public KsBeacon setMinor(String minor) {
        this.minor = minor;
        return this;
    }

    public int getRssi() {
        return rssi;
    }

    public KsBeacon setRssi(int rssi) {
        this.rssi = rssi;
        return this;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public KsBeacon setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
        return this;
    }

    public double getAltitude() {
        return altitude;
    }

    public KsBeacon setAltitude(double altitude) {
        this.altitude = altitude;
        return this;
    }

    public String string() {
        final StringBuffer sb = new StringBuffer("KsBeacon{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", major=").append(major);
        sb.append(", minor=").append(minor);
        sb.append(", rssi=").append(rssi);
        sb.append(", buildingCode=").append(buildingCode);
        sb.append(", altitude=").append(altitude);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(KsBeacon s) {
        if (this.rssi < s.rssi) {
            return -1;
        } else if (this.rssi > s.getRssi()) {
            return 1;
        }
        return 0;
    }

    public KsBeacon deepCopy(){
        KsBeacon beacon = new KsBeacon();
        beacon.setAltitude(getAltitude());
        beacon.setBeaconSeq(getBeaconSeq());
        beacon.setBuildingCode(getBuildingCode());
        beacon.setMajor(getMajor());
        beacon.setMinor(getMinor());
        beacon.setUuid(getUuid());
        beacon.setRssi(getRssi());
        return beacon;
    }
}
