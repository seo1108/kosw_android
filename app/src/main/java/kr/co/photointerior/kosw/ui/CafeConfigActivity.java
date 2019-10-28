package kr.co.photointerior.kosw.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.Cafe;
import kr.co.photointerior.kosw.rest.model.CafeDetail;
import kr.co.photointerior.kosw.rest.model.CafeSubCategory;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.ui.dialog.DialogCommon;
import kr.co.photointerior.kosw.utils.AbstractAcceptor;
import kr.co.photointerior.kosw.utils.Acceptor;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import kr.co.photointerior.kosw.widget.KoswTextView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class CafeConfigActivity extends BaseActivity {
    private String TAG = LogUtils.makeLogTag(CafeConfigActivity.class);
    private Cafe mCafe;
    private List<CafeSubCategory> mCate;
    private Dialog mDialog;
    private ImageView btn_back;
    private KoswTextView txt_cafekey, txt_privacy;
    private TextView txt_category, txt_notice, txt_member;
    private KoswEditText txt_cafename, txt_cafedesc, et_logo, et_logo_desc;
    private KoswButton btn_invite, btn_edit_cafe, btn_change_logo, btn_delete_logo;
    private CheckBox check_privacy_hide, check_privacy_open;
    private RelativeLayout rl_category, rl_notice, rl_member;
    private ImageView iv_logo;

    private String mCafeseq, mConfirm;



    private int mSelectItem;
    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE=1112;

    private File tempFile;
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름

    private Bitmap bitmap;


    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_config);

        mActivity = this;

        mCafeseq = getIntent().getStringExtra("cafeseq");

        getCafeDetail();

        findViews();
        attachEvents();
    }

    @Override
    protected void findViews() {
        txt_cafename = findViewById(R.id.txt_cafename);
        txt_cafedesc = findViewById(R.id.txt_cafedesc);

        txt_cafekey = findViewById(R.id.txt_cafekey);
        txt_cafekey.setTypeface(txt_cafekey.getTypeface(), Typeface.BOLD);

        btn_invite = findViewById(R.id.btn_invite);

        txt_privacy = findViewById(R.id.txt_privacy);
        txt_privacy.setTypeface(txt_privacy.getTypeface(), Typeface.BOLD);

        txt_category = findViewById(R.id.txt_category);
        txt_category.setTypeface(txt_category.getTypeface(), Typeface.BOLD);

        txt_notice = findViewById(R.id.txt_notice);
        txt_notice.setTypeface(txt_notice.getTypeface(), Typeface.BOLD);

        txt_member = findViewById(R.id.txt_member);
        txt_member.setTypeface(txt_member.getTypeface(), Typeface.BOLD);

        check_privacy_hide = findViewById(R.id.check_privacy_hide);
        check_privacy_open = findViewById(R.id.check_privacy_open);

        rl_category = findViewById(R.id.rl_category);
        rl_notice = findViewById(R.id.rl_notice);
        rl_member = findViewById(R.id.rl_member);

        btn_edit_cafe = findViewById(R.id.btn_edit_cafe);
        btn_back = findViewById(R.id.btn_back);

        iv_logo = findViewById(R.id.iv_logo);

        btn_change_logo = findViewById(R.id.btn_change_logo);
        btn_delete_logo = findViewById(R.id.btn_delete_logo);

        et_logo = findViewById(R.id.et_logo);
        et_logo_desc = findViewById(R.id.et_logo_desc);

        et_logo.setFocusable(false);
        et_logo.setClickable(false);

        et_logo_desc.setFocusable(false);
        et_logo_desc.setClickable(false);
    }

    @Override
    protected void attachEvents() {
        btn_invite.setOnClickListener(v->{
            shareCafe();
        });

        check_privacy_hide.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(txt_cafename.getWindowToken(), 0);
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(txt_cafedesc.getWindowToken(), 0);

                // TODO : process the click event.
                if (check_privacy_hide.isChecked()) {
                    // TODO : CheckBox is checked.
                    check_privacy_hide.setChecked(true);
                    check_privacy_open.setChecked(false);

                    //updateConfirm("Y");
                    mConfirm = "Y";
                } else {
                    // TODO : CheckBox is unchecked.
                    check_privacy_hide.setChecked(true);
                    check_privacy_open.setChecked(false);
                }
            }
        }) ;

        check_privacy_open.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(txt_cafename.getWindowToken(), 0);
                ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(txt_cafedesc.getWindowToken(), 0);

                // TODO : process the click event.
                if (check_privacy_open.isChecked()) {
                    // TODO : CheckBox is checked.
                    check_privacy_hide.setChecked(false);
                    check_privacy_open.setChecked(true);

                    //updateConfirm("N");
                    mConfirm = "N";
                } else {
                    // TODO : CheckBox is unchecked.
                    check_privacy_hide.setChecked(false);
                    check_privacy_open.setChecked(true);
                }
            }
        });

        rl_category.setOnClickListener(v->{
            Bundle bu = new Bundle();
            bu.putSerializable("cafeseq", mCafe.getCafeseq());
            callActivity(CafeCategoryActivity.class, bu,false);
        });

        rl_notice.setOnClickListener(v->{
            Bundle bu = new Bundle();
            bu.putSerializable("cafeseq", mCafe.getCafeseq());
            bu.putSerializable("type", "MODIFY");
            callActivity(CafeNoticeActivity.class, bu,false);
        });

        rl_member.setOnClickListener(v->{
            Bundle bu = new Bundle();
            bu.putSerializable("cafeseq", mCafe.getCafeseq());
            bu.putSerializable("adminseq", mCafe.getAdminseq());
            callActivity(CafeMemberActivity.class, bu,false);
        });

        btn_edit_cafe.setOnClickListener(v->{
            showConfirmPopup();
        });

        btn_back.setOnClickListener(v->{
            Intent intent = new Intent() ;
            setResult(RESULT_OK, intent);
            finish();
        });

        btn_change_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicDialog();
            }
        });

        btn_delete_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLogo();

            }
        });
    }

    @Override
    protected void setInitialData() {
        txt_cafename.setText(mCafe.getCafename());
        txt_cafedesc.setText(mCafe.getCafedesc());
        if (null != mCafe.getCafekey()) {
            txt_cafekey.setText(getResources().getString(R.string.txt_cafekey) + " : " + mCafe.getCafekey());
        } else {
            txt_cafekey.setText("");
        }

        mConfirm = mCafe.getConfirm();
        if ("Y".equals(mCafe.getConfirm())) {
            check_privacy_hide.setChecked(true);
            check_privacy_open.setChecked(false);
        } else {
            check_privacy_hide.setChecked(false);
            check_privacy_open.setChecked(true);
        }

        if (null != mCafe.getLogo() && !"".equals(mCafe.getLogo())) {
            Picasso.with(mActivity)
                    .load(mCafe.getLogo())
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .into(iv_logo);
        }
    }

    private void getCafeDetail() {
        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();

        try {
            query.put("user_seq", user.getUser_seq());
        } catch (Exception ex) {
            SharedPreferences prefr = getSharedPreferences("userInfo", MODE_PRIVATE);
            query.put("user_seq", prefr.getInt("user_seq", -1));
        }

        if (null != mCafeseq && !"".equals(mCafeseq)) {
            query.put("cafeseq", mCafeseq);
        }

        Call<CafeDetail> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).detail(query);

        call.enqueue(new Callback<CafeDetail>() {
            @Override
            public void onResponse(Call<CafeDetail> call, Response<CafeDetail> response) {
                closeSpinner();
                LogUtils.err(TAG, response.toString());
                if (response.isSuccessful()) {
                    CafeDetail cafedetail = response.body();
                    //LogUtils.err(TAG, "profile=" + profile.string());
                    if (cafedetail.isSuccess()) {
                        mCafe = cafedetail.getCafe();
                        mCafeseq = mCafe.getCafeseq();
                        mCate = mCafe.getCategory();

                        setInitialData();
                    } else {
                    }
                }

                if (!"0000".equals(response.body().getResponseCode())) {
                    toast(response.body().getResponseMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CafeDetail> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    private void shareCafe() {
        String subject = "계단왕 '" + mCafe.getCafename() + "' 카페로 초대합니다.";

        String text = "계단왕 '" + mCafe.getCafename() + "' 카페로 초대합니다.\n\n저희 계단왕 '" + mCafe.getCafename() + "' 카페에 가입해 주세요.\n"
                + "계단왕 앱 메뉴의 카페 페이지에서 아래의 키값으로 바로 가입할 수 있습니다. (비공개 카페는 키값을 통해서만 카페 가입이 가능합니다.)\n\n"
                + "카페키값 : " + mCafe.getCafekey() + "\n\n"
                + "계단왕은 그룹 멤버들과 함께 운동하며 이야기를 나누는 공간입니다. 아이폰, 안드로이드에서 무료로 다운받아 사용해 보세요.\n\n"
                + Env.Url.URL_SHARE.url();

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooser = Intent.createChooser(intent, "공유할 앱을 선택하세요.");
        startActivity(chooser);
    }

    private void updateConfirm(String confirm) {
        showSpinner("");

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("confirm", confirm);
        query.put("cafeseq", mCafeseq);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).updateConfirm(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        if ("Y".equals(confirm)) {
                            toast(R.string.cafe_close);
                        } else {
                            toast(R.string.cafe_open);
                        }
                    }else{
                        toast(R.string.warn_cafe_member_fail_kick);
                    }
                }else{
                    toast(R.string.warn_cafe_member_fail_kick);
                }

                closeSpinner();
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    private void modifyCafeInfo() {
        showSpinner("");

        String cafename = txt_cafename.getText().toString();
        String cafedesc = txt_cafedesc.getText().toString();

        if ("".equals(cafename)) {
            toast(R.string.warn_cafe_name);
            return;
        }

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafename", cafename);
        query.put("cafedesc", cafedesc);
        query.put("confirm", mConfirm);
        query.put("cafeseq", mCafeseq);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).modifyCafeInfo(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        toast(R.string.cafe_info_edit_success);
                    }else{
                        toast(R.string.warn_cafe_fail_info_modify);
                    }
                }else{
                    toast(R.string.warn_cafe_fail_info_modify);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    @Multipart
    private void updateLogo() {
        showSpinner("");

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mCafeseq);

        File f = null;
        MultipartBody.Part uploadFile = null;

        f = SaveBitmapToFileCache(bitmap);

        double bytes = f.length();
        double kilobytes = (bytes / 1024);

        if (kilobytes > 50) {
            closeSpinner();
            toast(R.string.warn_logo_upload_size);
        } else {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
            uploadFile = MultipartBody.Part.createFormData("file", f.getName(), requestFile);

            Call<ResponseBase> call =
                    new DefaultRestClient<CafeService>(this)
                            .getClient(CafeService.class).updateLogo(query, uploadFile);
            call.enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    closeSpinner();
                    LogUtils.err(TAG, response.raw().toString());
                    if (response.isSuccessful()) {
                        ResponseBase base = response.body();
                        if (base.isSuccess()) {
                            // 로고 업로드 성공시
                            toast(R.string.txt_logo_update);

                        } else {
                            toast(R.string.warn_logo_upload_fail);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                    LogUtils.err(TAG, t);
                    toast(R.string.warn_logo_upload_fail);
                }
            });

            closeSpinner();
        }
    }

    private void deleteLogo() {
        showSpinner("");

        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );
        query.put("cafeseq", mCafeseq);

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                        .getClient(CafeService.class).deleteLogo(query);

        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                closeSpinner();
                LogUtils.err(TAG, response.raw().toString());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if (base.isSuccess()) {
                        // 로고 삭제 성공시
                        toast(R.string.txt_logo_delete);

                        Picasso.with(mActivity)
                                .load(R.drawable.ic_logo)
                                .placeholder(R.drawable.ic_logo)
                                .error(R.drawable.ic_logo)
                                .into(iv_logo);
                    } else {
                        toast(R.string.warn_logo_delete_fail);
                    }

                }else{
                    toast(response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                closeSpinner();
                LogUtils.err(TAG, t);
                toast(R.string.warn_server_not_smooth);
            }
        });
    }

    private void showConfirmPopup(){
        if(!isFinishing()){
            if(mDialog != null){
                mDialog.dismiss();
            }
            Acceptor acceptor = new AbstractAcceptor() {
                @Override
                public void accept() {
                    modifyCafeInfo();

                }
            };
            String msg = getString(R.string.txt_confirm_message);
            mDialog =
                    new DialogCommon(this,
                            acceptor,
                            getString(R.string.txt_warn),
                            msg,
                            new String[]{getString(R.string.txt_cancel), null, getString(R.string.txt_confirm)});
            mDialog.setCancelable(false);
            mDialog.show();
        }
    }

    private void showPicDialog() {
        mSelectItem = -1;
        final CharSequence[] oItems = {"사진 촬영하기", "앨범에서 사진찾기"};

        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

        oDialog.setTitle("사진 선택")
                .setSingleChoiceItems(oItems, -1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        mSelectItem = which;
                    }
                })
                .setNeutralButton("선택", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (mSelectItem == 0) {
                            // 사진촬영하기
                            //selectPhoto();
                            checkCameraPermission();
                        } else if (mSelectItem == 1) {
                            // 앨범에서 사진찾기
                            selectGallery();
                        }
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        int permssionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permssionCheck!= PackageManager.PERMISSION_GRANTED) {

            //Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this,"로고 등록을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_CODE);
                Toast.makeText(this,"로고 등록을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
        } else {
            takePhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();
                    takePhoto();
                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    private void takePhoto() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this, "kr.co.photointerior.kosw.fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, CAMERA_CODE);
        }
    }

    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/" + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();
        return storageDir;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode)
            {
                case GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;
                case CAMERA_CODE:
                    getPictureForPhoto(); //카메라에서 가져오기
                    break; default:
                break;
            }
        }
    }

    private void sendPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        iv_logo.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        iv_logo.setVisibility(View.VISIBLE);


        updateLogo();
    }

    private void getPictureForPhoto() {
        bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;
        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        iv_logo.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        iv_logo.setVisibility(View.VISIBLE);

        updateLogo();
    }



    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }

        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }


    private File SaveBitmapToFileCache(Bitmap bitmap) {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";
        String filename = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/" + mImageCaptureName;

        File fileCacheItem = new File(filename);
        OutputStream out = null;

        try
        {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return fileCacheItem;
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent() ;
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onStart() {
        measureStart();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        measureStop();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        measureStop();
        super.onPause();
    }


    @Override
    protected void onResume() {
        measureStart();
        super.onResume();
    }
}
