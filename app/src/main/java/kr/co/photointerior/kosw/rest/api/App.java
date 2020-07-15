package kr.co.photointerior.kosw.rest.api;

import java.util.Map;

import kr.co.photointerior.kosw.rest.model.AppVersion;
import kr.co.photointerior.kosw.rest.model.BeaconUuid;
import kr.co.photointerior.kosw.rest.model.Building;
import kr.co.photointerior.kosw.rest.model.Contents;
import kr.co.photointerior.kosw.rest.model.Logo;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * 앱 관련 Rest api interface.
 */
public interface App {
    @POST("api/app/codeRegister")
    @FormUrlEncoded
    Call<Logo> lookupCompanyCode(
            @Field("device") String device,
            @Field("code") String code
    );

    @POST("api/app/addrRegister")
    @FormUrlEncoded
    Call<Logo> lookupCompanyAddr(
            @Field("device") String device,
            @Field("addr") String code
    );

    /**
     * 건물 저장
     * @param query
     * @return
     */
    @POST("api/app/addBuilding")
    Call<Building> addBuilding(@QueryMap Map<String, Object> query);



    /**
     * 앱 업데이트 여부 조회
     * @param reqData
     * @return
     */
    @POST("api/app/version")
    Call<AppVersion> checkUpdate(
            @QueryMap Map<String, Object> reqData
    );

    /**
     * 푸시수신 여부 변경
     * @param reqData
     * @return
     */
    @POST("api/app/push")
    Call<ResponseBase> updatePushFlag(
            @QueryMap Map<String, Object> reqData
    );

    /**
     * 공지사항.이벤트 리스트 획득
     * @param query
     * @return
     */
    @POST("api/app/bbsList")
    Call<Contents> getBbsList(@QueryMap Map<String, Object> query);

    /**
     * FCM 토큰 저장
     * @param query
     * @return
     */
    @POST("api/app/fcmToken")
    Call<ResponseBase> sendFcmToken(@QueryMap Map<String, Object> query);

    /**
     * 올라간 층수 데이터 서버 전송
     * @param query
     * @return
     */
    @POST("api/user/floorGoUp2")
    Call<BeaconUuid> sendStairGoUpAmountToServer(@QueryMap Map<String, Object> query);

    /**
     * 올라간 층수 데이터 서버 전송
     * @param query
     * @return
     */
    @POST("api/user/floorUpa2")
    Call<BeaconUuid> sendStairGoUpAmountToServer2(@QueryMap Map<String, Object> query);

    /**
     * 사용자가 수신한 비콘이 현재 있는 건물의 비콘이 아닐 경우 정보를 바꾼다.
     * @param query
     * @return
     */
    @POST("api/app/changeBuilding")
    Call<BeaconUuid> changeBuilding(@QueryMap Map<String, Object> query);

    /**
     * 비콘 서비스 로그 서버 전송
     * @param query
     * @return
     */
    @POST("api/app/serviceLog")
    Call<ResponseBase> sendServiceLog(@QueryMap Map<String, Object> query);
}
