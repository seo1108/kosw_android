package kr.co.photointerior.kosw.rest.api;

import java.util.Map;

import kr.co.photointerior.kosw.rest.model.CafeDetail;
import kr.co.photointerior.kosw.rest.model.CafeMainList;
import kr.co.photointerior.kosw.rest.model.CafeMyAllList;
import kr.co.photointerior.kosw.rest.model.CafeNoticeList;
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
}
