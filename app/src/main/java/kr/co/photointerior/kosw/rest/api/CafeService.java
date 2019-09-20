package kr.co.photointerior.kosw.rest.api;

import java.util.Map;

import kr.co.photointerior.kosw.rest.model.CafeBbsList;
import kr.co.photointerior.kosw.rest.model.CafeDetail;
import kr.co.photointerior.kosw.rest.model.CafeMainList;
import kr.co.photointerior.kosw.rest.model.CafeMemberList;
import kr.co.photointerior.kosw.rest.model.CafeMyAllList;
import kr.co.photointerior.kosw.rest.model.CafeNoticeList;
import kr.co.photointerior.kosw.rest.model.CafeRankingList;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public interface CafeService {

    /**
     * 내가 가입한 카페 리스트 (메인화면)
     * @param queryMap
     * @return
     */
    @POST("api/cafe/selectMyCafeList")
    Call<CafeMainList> selectMyCafeList(@QueryMap Map<String, Object> queryMap);

    /**
     * 내가 개설/가입한 카페 리스트
     * @param queryMap
     * @return
     */
    @POST("api/cafe/selectMyCafeMainList")
    Call<CafeMyAllList> selectMyCafeMainList(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 개설하기
     * @param queryMap
     * @return
     */
    @Multipart
    @POST("api/cafe/createCafe")
    Call<ResponseBase> createCafe(@QueryMap Map<String, Object> queryMap, @Part MultipartBody.Part image);
    //Call<ResponseBase> createCafe(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 개설하기 (파일전송 없음)
     * @param queryMap
     * @return
     */
    @POST("api/cafe/createCafe")
    Call<ResponseBase> createCafeNologo(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 검색
     * @param queryMap
     * @return
     */
    @POST("api/cafe/openCafeList")
    Call<CafeMainList> openCafeList(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 상세
     * @param queryMap
     * @return
     */
    @POST("api/cafe/detail")
    Call<CafeDetail> detail(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 공지리스트
     * @param queryMap
     * @return
     */
    @POST("api/cafe/notice")
    Call<CafeNoticeList> notice(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 가입하기
     * @param queryMap
     * @return
     */
    @POST("api/cafe/join")
    Call<ResponseBase> join(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 개인랭킹
     * @param queryMap
     * @return
     */
    @POST("api/cafe/rankingIndividual")
    Call<CafeRankingList> rankingIndividual(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 분류랭킹
     * @param queryMap
     * @return
     */
    @POST("api/cafe/rankingByCategory")
    Call<CafeRankingList> rankingByCategory(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 분류내 개인랭킹
     * @param queryMap
     * @return
     */
    @POST("api/cafe/rankingIndividualByCategory")
    Call<CafeRankingList> rankingIndividualByCategory(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 게시판 리스트
     * @param queryMap
     * @return
     */
    @POST("api/cafe/bbs")
    Call<CafeBbsList> bbs(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 게시글 작성
     * @param queryMap
     * @return
     */
    @POST("api/cafe/writeBbs")
    Call<ResponseBase> writeBbs(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 게시글 수정
     * @param queryMap
     * @return
     */
    @POST("api/cafe/modifyBbs")
    Call<ResponseBase> modifyBbs(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 게시글 삭제
     * @param queryMap
     * @return
     */
    @POST("api/cafe/deleteBbs")
    Call<ResponseBase> deleteBbs(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 게시글 답글 작성
     * @param queryMap
     * @return
     */
    @POST("api/cafe/writeComment")
    Call<ResponseBase> writeComment(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 멤버 리스트
     * @param queryMap
     * @return
     */
    @POST("api/cafe/searchUser")
    Call<CafeMemberList> searchUser(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 멤버 강퇴
     * @param queryMap
     * @return
     */
    @POST("api/cafe/kickUser")
    Call<ResponseBase> kickUser(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 공개/비공개 설정
     * @param queryMap
     * @return
     */
    @POST("api/cafe/updateConfirm")
    Call<ResponseBase> updateConfirm(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 공지사항 작성
     * @param queryMap
     * @return
     */
    @POST("api/cafe/writeNotice")
    Call<ResponseBase> writeNotice(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 공지사항 수정
     * @param queryMap
     * @return
     */
    @POST("api/cafe/modifyNotice")
    Call<ResponseBase> modifyNotice(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 정보 수정
     * @param queryMap
     * @return
     */
    @POST("api/cafe/modifyCafeInfo")
    Call<ResponseBase> modifyCafeInfo(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 분류 정보 수정
     * @param queryMap
     * @return
     */
    @POST("api/cafe/modifyCategory")
    Call<ResponseBase> modifyCategory(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 나의 분류 정보 수정
     * @param queryMap
     * @return
     */
    @POST("api/cafe/updateMyInfo")
    Call<ResponseBase> updateMyInfo(@QueryMap Map<String, Object> queryMap);

    /**
     * 카페 공지사항 삭제
     * @param queryMap
     * @return
     */
    @POST("api/cafe/deleteNotice")
    Call<ResponseBase> deleteNotice(@QueryMap Map<String, Object> queryMap);

}
