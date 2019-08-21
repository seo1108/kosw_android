package kr.co.photointerior.kosw.rest.api;

import java.util.Map;

import kr.co.photointerior.kosw.rest.model.ActivityRecord;
import kr.co.photointerior.kosw.rest.model.Admin;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Bbs;
import kr.co.photointerior.kosw.rest.model.CityRank;
import kr.co.photointerior.kosw.rest.model.CityRankList;
import kr.co.photointerior.kosw.rest.model.GGRList;
import kr.co.photointerior.kosw.rest.model.MainData;
import kr.co.photointerior.kosw.rest.model.MapBuildingUser;
import kr.co.photointerior.kosw.rest.model.Profile;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * 회원 관련 Rest api interface.
 */
public interface UserService {
    /**
     * 회원가입
     * @param reqData
     * @return
     */
    @POST("api/user/signup")
    Call<AppUserBase> signUp(@QueryMap Map<String, Object> reqData);


    //addAdmin
    /**
     * Admin
     * @param reqData
     * @return
     */
    @POST("api/user/addAdmin")
    Call<Admin> addAdmin(@QueryMap Map<String, Object> reqData);

    /**
     * cafe
     * @param reqData
     * @return
     */
    @POST("api/user/selectCafe")
    Call<Admin> selectCafe(@QueryMap Map<String, Object> reqData);

    /**
     * signedcafe
     * @param reqData
     * @return
     */
    @POST("api/user/selectSignCafe")
    Call<MapBuildingUser> selectSignCafe(@QueryMap Map<String, Object> reqData);


    /**
     * 로그인 시도
     * @param reqData
     * @return
     */
    @POST("api/user/login")
    Call<AppUserBase> tryLogin(@QueryMap Map<String, Object> reqData);

    /**
     * 카카오로그인 시도
     * @param reqData
     * @return
     */
    @POST("api/user/loginByKakao")
    Call<AppUserBase> tryKakaoLogin(@QueryMap Map<String, Object> reqData);

    /**
     * 로그인 시도
     * @param reqData
     * @return
     */
    @POST("api/user/loginByToken")
    Call<AppUserBase> tryLoginByTokenAndId(@QueryMap Map<String, Object> reqData);


    @POST("api/user/loginByTokenBuild")
    Call<AppUserBase> tryLoginByTokenAndIdBuild(@QueryMap Map<String, Object> reqData);


    /**
     * 캐릭터 변경을 위한 기본 캐릭터 정보 획득
     * @param reqData
     * @return
     */
    @POST("api/user/characters")
    Call<AppUserBase> getDefaultCharacter(@QueryMap Map<String, Object> reqData);

    /**
     * 닉네임 중복여부 검사.
     * @param reqData
     * @return
     */
    @POST("api/user/checkNick")
    Call<ResponseBase> checkNickName(@QueryMap Map<String, Object> reqData);

    /**
     * 카페 가입
     * @param reqData
     * @return
     */
    @POST("api/user/updateCustDepart")
    Call<ResponseBase> updateCustDepart(@QueryMap Map<String, Object> reqData);

    @POST("api/user/deleteCustDepart")
    Call<ResponseBase> deleteCustDepart(@QueryMap Map<String, Object> reqData);

    /**
     * 닉네임과 캐릭터 정보 서버 저장
     * @param queryMap
     * @return
     */
    @POST("api/user/nickAndCharSave")
    Call<ResponseBase> saveNickAndCharacter(@QueryMap Map<String, Object> queryMap);

    /**
     * 패스워드 찾기
     * @param queryMap
     * @return
     */
    @POST("api/user/findPwd")
    Call<ResponseBase> findOutPassword(@QueryMap Map<String, Object> queryMap);

    /**
     * 패스워드 찾기
     * @param queryMap
     * @return
     */
    @POST("api/user/changePwd")
    Call<ResponseBase> changePassword(@QueryMap Map<String, Object> queryMap);

    /**
     * 이용자 정보 획득
     * @param queryMap device, token
     * @return
     */
    @POST("api/user/profile")
    Call<Profile> getUserInfo(@QueryMap Map<String, Object> queryMap);

    /**
     * 이용자 정보 획득
     * @param queryMap device, id, token
     * @return
     */
    @POST("api/user/withdrawal")
    Call<ResponseBase> withdrawal(@QueryMap Map<String, Object> queryMap);

    /**
     * 메인화면 구성 데이터 획득
     * @param queryMap
     * @return
     */
    @POST("api/user/main")
    Call<MainData> getMainData(@QueryMap Map<String, Object> queryMap);

    /**
     * 사용자의 계단 활동 기록 획득
     * @param queryMap
     * @return
     */
    @POST("api/user/activityRecord")
    Call<ActivityRecord> getActivityRecords(@QueryMap Map<String, Object> queryMap);

    /**
     * 개인랭킹
     * @param queryMap
     * @return
     */
    @POST("api/user/rankingPrivate2")
    Call<ActivityRecord> getRankingPrivate(@QueryMap Map<String, Object> queryMap);

    /**
     * 그룹랭킹
     * @param queryMap
     * @return
     */
    @POST("api/user/rankingGroup")
    Call<ActivityRecord> getRankingGroup(@QueryMap Map<String, Object> queryMap);

    /**
     * 주간.월간 분석 데이터 획득
     * @param queryMap
     * @return
     */
    @POST("api/user/analysis")
    Call<ActivityRecord> getAnalysisData(@QueryMap Map<String, Object> queryMap);

    /**
     * 오늘의 랭킹정보 획득
     * @param queryMap
     * @return
     */
    @POST("api/user/rankinNotices")
    Call<Bbs> todayRanks(@QueryMap Map<String, Object> queryMap);


    /**
     * 현재 층
     * @param queryMap
     * @return
     */

    @POST("api/user/curFloorLog")
    Call<ResponseBase> sendBeaconLog(@QueryMap Map<String, Object> query);


    /**
     * 커뮤니티 글등록
     * @param queryMap
     * @return
     */
    @POST("api/user/addRankNotice")
    Call<ResponseBase> addRankNotice(@QueryMap Map<String, Object> queryMap);

    @POST("api/user/modRankNotice")
    Call<ResponseBase> modRankNotice(@QueryMap Map<String, Object> queryMap);

    @POST("api/user/delRankNotice")
    Call<ResponseBase> delRankNotice(@QueryMap Map<String, Object> queryMap);


    /**
     * 명예의 전당
     * @param reqData
     * @return
     */
    @POST("api/user/getCityRank")
    Call<CityRankList> getCityRank(@QueryMap Map<String, Object> reqData);

    /**
     * 걸음수 저장
     * @param queryMap
     * @return
     */
    @POST("api/user/saveWalkStep")
    Call<ResponseBase> saveWalkStep(@QueryMap Map<String, Object> queryMap);

}
