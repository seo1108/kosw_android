package kr.co.photointerior.kosw.rest.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.AppVersion;
import kr.co.photointerior.kosw.rest.model.Beacon;
import kr.co.photointerior.kosw.rest.model.Building;
import kr.co.photointerior.kosw.rest.model.BuildingStair;
import kr.co.photointerior.kosw.rest.model.Buildings;
import kr.co.photointerior.kosw.rest.model.Company;
import kr.co.photointerior.kosw.rest.model.Companys;
import kr.co.photointerior.kosw.rest.model.Departs;
import kr.co.photointerior.kosw.rest.model.GGRList;
import kr.co.photointerior.kosw.rest.model.Logo;
import kr.co.photointerior.kosw.rest.model.MapBuildingUser;
import kr.co.photointerior.kosw.rest.model.Place;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * 회사 관련 Rest api interface.
 */
public interface CustomerService {
    /**
     * 앱 업데이트 여부 조회
     * @param reqData
     * @return
     */
    @POST("api/customer/company")
    Call<Company> getCompanyInfo(
            @QueryMap Map<String, Object> reqData
    );

    /**
     * 빌딩 신규등록을 위한 조회
     * @param reqData
     * @return
     */
    @POST("api/customer/lookupBuildingCode")
    Call<Building> lookupBuildingCode(
            @QueryMap Map<String, Object> reqData
    );

    //getBuilding
    /**
     * 500미터 이내 최고 이용한 빌딩
     * @param reqData
     * @return
     */
    @POST("api/customer/selectRecentBuilding")
    Call<Building> selectRecentBuilding(
            @QueryMap Map<String, Object> reqData
    );

    /**
     * 활동하는 빌딩 정보 추가
     * @param reqData
     * @return
     */
    @POST("api/customer/addBuilding")
    Call<Building> addBuilding(
            @QueryMap Map<String, Object> reqData
    );

    //getBuilding
    /**
     * 활동하는 빌딩 정보 추가
     * @param reqData
     * @return
     */
    @POST("api/customer/getBuilding")
    Call<Building> getBuilding(
            @QueryMap Map<String, Object> reqData
    );

    /**
     * 활동하는 빌딩 정보 추가
     * @param reqData
     * @return
     */
    @POST("api/customer/getBuildingStair")
    Call<BuildingStair> getBuildingStair(
            @QueryMap Map<String, Object> reqData
    );


    /**
     *  Beacon
     * @param reqData
     * @return
     */
    @POST("api/customer/getBeacon")
    Call<Beacon> getBeacon(
            @QueryMap Map<String, Object> reqData
    );


    /**
     * 회사 정보 추가
     * @param reqData
     * @return
     */
    @POST("api/customer/addCompany")
    Call<Company> addCompany(
            @QueryMap Map<String, Object> reqData
    );

    /**
     * 회사 전체 정보
     * @param reqData
     * @return
     */
    @POST("api/customer/selectAllCompany")
    Call<Companys> selectAllCompany(
            @QueryMap Map<String, Object> reqData
    );


    /**
     * 회사 전체 정보
     * @param reqData
     * @return
     */
    @POST("api/customer/selectDepart")
    Call<Departs> selectDepart(
            @QueryMap Map<String, Object> reqData
    );


    /**
     * 맵
     * @param reqData
     * @return
     */
    @POST("api/customer/getBuildingAndUser")
    Call<MapBuildingUser> getBuildingAndUser(
            @QueryMap Map<String, Object> reqData
    );


    /**
     *  빌딩정보
     * @param reqData
     * @return
     */
    @POST("api/customer/lookupBuildings")
    Call<Buildings> getBuildings(@QueryMap Map<String, Object> reqData);


    /**
     * 회원이 등록한 빌딩정보
     * @param reqData
     * @return
     */
    @POST("api/customer/lookupRegisteredBuildings")
    Call<Company> getCompanyBuildings(@QueryMap Map<String, Object> reqData);

    /**
     * 회원이 등록한 빌딩정보
     * @param reqData
     * @return
     */
    @POST("api/customer/switchBuilding")
    Call<AppUserBase> switchBaseBuilding(@QueryMap Map<String, Object> reqData);


    /**
     * 건물카페별 일간주간레드닷획득히스토리
     * @param reqData
     * @return
     */
    @POST("api/customer/getGGRHistory")
    Call<GGRList> getGGRHistory(@QueryMap Map<String, Object> reqData);

}
