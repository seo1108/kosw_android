package kr.co.photointerior.kosw.rest.model;

import org.w3c.dom.DOMImplementation;

import java.io.Serializable;

/**
 * Created by kimminjib on 2018. 11. 9..
 */

public class Place implements Serializable  {

    public  Double lat ;
    public  Double lng ;
    public  String name ;
    public  String[] types ;

    public  String place_id ;
    public  String vicinity ;

    public  String country ;
    public  String countrycd ;
    public  String city ;

    public  boolean checkDB;
    public  boolean check ;

    public Place(Double lat, Double lng, String name, String[] types, String place_id, String vicinity, boolean check) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.types = types;
        this.place_id = place_id;
        this.vicinity = vicinity;
        this.check = check ;
    }


    public Place() {
        this.lat = 0.0;
        this.lng = 0.0;
        this.name = "";
        this.types = new String[]{""};
        this.place_id = "";
        this.vicinity = "";
        this.check = false ;
        this.checkDB = false ;
        this.country = "대한민국" ;
        this.country = "KR" ;
        this.city = "" ;
    }

}
