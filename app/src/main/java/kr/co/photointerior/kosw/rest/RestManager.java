package kr.co.photointerior.kosw.rest;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kr.co.photointerior.kosw.global.DefaultCode;
import kr.co.photointerior.kosw.rest.model.Place;

import static android.os.Build.ID;

/**
 * Created by kimminjib on 2018. 11. 9..
 */

public class RestManager  {

    private Context context ;

    public interface CallbackPlaces {
        public void result(ArrayList<Place> r) ;
    }

    public static RestManager restmanager = new RestManager()  ;

    private RestManager() {

    }

    private void setContext(Context ctx) {

        this.context = ctx ;
    }

    public static RestManager getInstance() {
        return restmanager ;
    }

    public CallbackPlaces mCallbackPlaces ;

    // 구글 place 데이터 가져오기
    public  void getPlaceList(CallbackPlaces callback , Double lat ,Double lng) {

        this.mCallbackPlaces = callback ;

        PlaceTask placeTask = new PlaceTask() ;
        HashMap<String,String> param = new HashMap<>() ;
        param.put( "urlPath","https://maps.googleapis.com/maps/api/place/nearbysearch/json") ;
        param.put( "lat" , String.valueOf(lat) ) ;
        param.put( "lng", String.valueOf(lng) ) ;
        param.put( "name" , "") ;
        param.put( "radius" , String.valueOf(DefaultCode.GPS_RANGE.getValue())) ;
        param.put( "key" , "AIzaSyBkR1H_VybNvbKqehC8oRXzspYv97xK_hY" ) ;

        placeTask.execute(param) ;
    }

    /*
    String urlPath = hashMaps[0].get("urlPath") ;
    String confmKey = hashMaps[0].get("confmKey") ;
    String currentPage = hashMaps[0].get("currentPage") ;
    String countPerPage = hashMaps[0].get("countPerPage") ;
    String keyword = hashMaps[0].get("keyword") ;
     */
    // 구글 place 데이터 가져오기
    public  void getPlaceList(Context ctx, CallbackPlaces callback ,String input) {

        this.context = ctx ;
        this.mCallbackPlaces = callback ;

        AddrTask placeTask = new AddrTask() ;

        HashMap<String,String> param = new HashMap<>() ;
        param.put( "urlPath","http://www.juso.go.kr/addrlink/addrLinkApi.do") ;
        param.put( "confmKey" , "U01TX0FVVEgyMDE4MTEyODE1MTk0OTEwODMzNTg=") ;
        param.put( "currentPage" , "1" ) ;
        param.put( "countPerPage" , "100" ) ;

        param.put( "keyword" , input ) ;

        placeTask.execute(param) ;
    }


    // 구글 place 데이터 가져오기
    public  void getPlaceList(CallbackPlaces callback , Double lat ,Double lng,String input) {

        this.mCallbackPlaces = callback ;

        PlaceTask placeTask = new PlaceTask() ;
        HashMap<String,String> param = new HashMap<>() ;
        param.put( "urlPath","https://maps.googleapis.com/maps/api/place/nearbysearch/json") ;
        param.put( "lat" , String.valueOf(lat) ) ;
        param.put( "lng", String.valueOf(lng) ) ;
        param.put( "name" , input) ;
        param.put( "radius" , String.valueOf(20000)) ;
        param.put( "key" , "AIzaSyBkR1H_VybNvbKqehC8oRXzspYv97xK_hY" ) ;

        placeTask.execute(param) ;
    }


    public class PlaceTask extends AsyncTask<HashMap<String,String>,Integer,String> {

        private String str, receiveMsg;

        @Override
        protected String doInBackground(HashMap<String, String>[] hashMaps) {
            URL url = null;
            String urlPath = hashMaps[0].get("urlPath") ;
            String lat = hashMaps[0].get("lat") ;
            String lng = hashMaps[0].get("lng") ;
            String key = hashMaps[0].get("key") ;
            String search = hashMaps[0].get("name") ;
            String radius = hashMaps[0].get("radius") ;

            try {
                url = new URL(String.format("%s?language=ko&location=%s,%s&radius=%s&key=%s&name=%s",urlPath,lat,lng, radius , key,search));

                Log.v("kmj",url.toString()) ;
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    Log.v("kmj : ", receiveMsg);

                    reader.close();
                } else {
                    Log.v("kmj", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ArrayList<Place> places = new ArrayList<>() ;

            try {
                JSONArray jarray = new JSONObject(s).getJSONArray("results");
                for (int i = 0 ; i < jarray.length() ; i++) {
                    JSONObject p = jarray.getJSONObject(i) ;
                    JSONObject g = p.getJSONObject("geometry").getJSONObject("location") ;
                    String lat = g.getString("lat") ;
                    String lng = g.getString("lng") ;
                    String name = p.getString("name") ;
                    String place_id = p.getString("place_id") ;
                    JSONArray ts = p.getJSONArray("types") ;
                    String types[] = new String[ts.length()]  ;
                    for (int t = 0 ; t <  ts.length() ; t++ ) {
                        types[t] = ts.getString(t) ;
                    }
                    String vicinity = p.getString("vicinity") ;

                    Place place = new Place(Double.valueOf(lat) , Double.valueOf(lng) , name , types, place_id,vicinity, false) ;
                    places.add(place) ;

                 }

                 mCallbackPlaces.result(places);

            } catch (JSONException e) {
                e.printStackTrace();
                mCallbackPlaces.result(places);
            }

        }

    }

    public class AddrTask extends AsyncTask<HashMap<String,String>,Integer,String> {

        private String str, receiveMsg;

        @Override
        protected String doInBackground(HashMap<String, String>[] hashMaps) {
            URL url = null;
            String urlPath = hashMaps[0].get("urlPath") ;
            String confmKey = hashMaps[0].get("confmKey") ;
            String currentPage = hashMaps[0].get("currentPage") ;
            String countPerPage = hashMaps[0].get("countPerPage") ;
            String keyword = hashMaps[0].get("keyword") ;

            try {
                url = new URL(String.format("%s?confmKey=%s&currentPage=%s&countPerPage=%s&keyword=%s&resultType=json",urlPath,confmKey,currentPage,countPerPage,keyword ));

                Log.v("kmj",url.toString()) ;
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    Log.v("kmj : ", receiveMsg);

                    reader.close();
                } else {
                    Log.v("kmj", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ArrayList<Place> places = new ArrayList<>() ;

            try {
                JSONArray jarray = new JSONObject(s).getJSONObject("results").getJSONArray("juso");
                for (int i = 0 ; i < jarray.length() ; i++) {
                    JSONObject g = jarray.getJSONObject(i) ;
                    String lat = "0";
                    String lng = "0" ;
                    String name = g.getString("bdNm") ;
                    String place_id = g.getString("bdMgtSn") ;
                    String types[] = new String[1]  ;
                    types[0] = "address" ;
                    String vicinity = g.getString("roadAddrPart1") ;
                    /*
                    Geocoder geoCoder = new Geocoder(context);
                    List<Address> list = null;
                    try {
                        list = geoCoder.getFromLocationName(vicinity,1) ;
                        if (list != null) {
                            if (list.size() == 0) {
                            } else {
                               Address addr =  list.get(0) ;
                                lat = String.valueOf(addr.getLatitude()) ;
                                lng = String.valueOf(addr.getLongitude()) ;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    */
                    Place place = new Place(Double.valueOf(lat) , Double.valueOf(lng) , name , types, place_id,vicinity, false) ;
                    places.add(place) ;

                }

                mCallbackPlaces.result(places);

            } catch (JSONException e) {
                e.printStackTrace();
                mCallbackPlaces.result(places);
            }

        }

    }

}
