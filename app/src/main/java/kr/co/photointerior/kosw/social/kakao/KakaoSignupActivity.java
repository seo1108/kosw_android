package kr.co.photointerior.kosw.social.kakao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.List;

import kr.co.photointerior.kosw.ui.BaseUserActivity;
import kr.co.photointerior.kosw.ui.MainActivity;

public class KakaoSignupActivity extends BaseUserActivity {
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    /**
     * 자동가입앱인 경우는 가입안된 유저가 나오는 것은 에러 상황.
     */
    protected final void showSignup() {
        Logger.d("not registered user");
//        redirectLoginActivity();
        String message = "not registered user.\nYou should signup at UserManagememt menu.";
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
/*    protected void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                if (errorResult.getErrorCode() == Integer.parseInt(ErrorCode.CLIENT_ERROR_CODE.toString())) {
                    Toast.makeText(KakaoSignupActivity.this, "Unstable network connection. \\nPlease try again later.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile.toString());


//                UserInfo userInfo = GlobalApplication.getGlobalApplicationInstance().getUserInfo();
//                userInfo.setId(String.valueOf(userProfile.getId()));
//                userInfo.setName(userProfile.getNickname());
//                userInfo.setProfileImagePath(userProfile.getThumbnailImagePath());
//
//                String userKey = PreferenceUtils.getString(AppCode.DataKey.USER_KEY, null, KakaoSignupActivity.this);
//                // 사용자 키값이 없으면 회원가입
//                // 있으면 회원정보 조회
//                if (userKey == null) {
////                    requestJoinMember(AppCode.LoginType.KAKAOTALK, String.valueOf(userProfile.getId()),
////                            userProfile.getNickname(), null);
//                } else {
//
//                }
                finish();
            }

            @Override
            public void onNotSignedUp() {
                showSignup();
            }
        });
    }*/

    private void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onSuccess(MeV2Response response) {
                /*Logger.d("user id : " + response.getId());
                Logger.d("email: " + response.getKakaoAccount().getEmail());
                Logger.d("profile image: " + response.getKakaoAccount()
                        .getProfileImagePath());
                redirectMainActivity();*/
                finish();
            }

            /*@Override
            public void onNotSignedUp() {
                showSignup();
            }*/
        });
    }

    private void redirectMainActivity() {
        startActivity(new Intent(this, MainActivity.class));

        finish();
    }
}
