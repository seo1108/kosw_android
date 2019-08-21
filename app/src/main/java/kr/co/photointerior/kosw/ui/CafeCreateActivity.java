package kr.co.photointerior.kosw.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.CafeService;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.ui.row.RowAddCategory;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import kr.co.photointerior.kosw.widget.KoswButton;
import kr.co.photointerior.kosw.widget.KoswEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.Part;

public class CafeCreateActivity extends BaseActivity {
    private String TAG = "CafeCreate" ;
    private AppUser mAppUser;

    private KoswEditText et_name_length, et_input_name;
    private KoswEditText et_desc_length, et_desc;
    private CheckBox check_privacy_hide, check_privacy_open;
    private ImageView img_logo, add_cate_btn;
    private KoswButton btn_change, btn_make_cafe;
    private KoswEditText et_cate_title, et_cate_user_title;
    private LinearLayout category_linearlayout;

    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE=1112;

    private File tempFile;
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름

    private int mSelectItem;
    private int mCategoryIndex = 0;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe_create);
        changeStatusBarColor(getCompanyColor());
        DataHolder.instance().setAppUser(new AppUser());
        mAppUser = DataHolder.instance().getAppUser();
        findViews();
        attachEvents();
        setInitialData();
    }

    @Override
    protected void findViews() {
        et_input_name = findViewById(R.id.input_name);
        et_name_length = findViewById(R.id.input_name_length);

        et_name_length.setFocusable(false);
        et_name_length.setClickable(false);
        et_input_name.addTextChangedListener(mTextEditorWatcher);

        et_desc = findViewById(R.id.input_desc);
        et_desc_length = findViewById(R.id.input_desc_length);

        et_desc_length.setFocusable(false);
        et_desc_length.setClickable(false);
        et_desc.addTextChangedListener(mTextEditorWatcher1);


        check_privacy_hide = findViewById(R.id.check_privacy_hide);
        check_privacy_open = findViewById(R.id.check_privacy_open);

        check_privacy_hide.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : process the click event.
                if (check_privacy_hide.isChecked()) {
                    // TODO : CheckBox is checked.
                    check_privacy_hide.setChecked(true);
                    check_privacy_open.setChecked(false);
                } else {
                    // TODO : CheckBox is unchecked.
                    check_privacy_hide.setChecked(false);
                    check_privacy_open.setChecked(true);
                }
            }
        }) ;

        check_privacy_open.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : process the click event.
                if (check_privacy_open.isChecked()) {
                    // TODO : CheckBox is checked.
                    check_privacy_hide.setChecked(false);
                    check_privacy_open.setChecked(true);
                } else {
                    // TODO : CheckBox is unchecked.
                    check_privacy_hide.setChecked(true);
                    check_privacy_open.setChecked(false);
                }
            }
        }) ;

        btn_change = findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicDialog();
            }
        });

        img_logo = findViewById(R.id.img_logo);

        category_linearlayout = findViewById(R.id.category_linearlayout);

        add_cate_btn = findViewById(R.id.add_cate_btn);
        add_cate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RowAddCategory n_layout = new RowAddCategory(getApplicationContext());
                LinearLayout con = (LinearLayout)findViewById(R.id.category_linearlayout);
                //n_layout.setId(mCategoryIndex);
                con.addView(n_layout);

                KoswEditText et = (KoswEditText) con.findViewById(R.id.add_cate_title);
                et.setId(mCategoryIndex+2000);
                et.setFocusableInTouchMode(false);
                et.setFocusable(false);
                et.setFocusableInTouchMode(true);
                et.setFocusable(true);

                ImageView iv = (ImageView) con.findViewById(R.id.remove_cate_btn);
                iv.setId(mCategoryIndex+1000);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        n_layout.setVisibility(View.GONE);
                    }
                });

                mCategoryIndex++;
            }
        });

        et_cate_user_title = findViewById(R.id.txt_cate_user_title);
        et_cate_title = findViewById(R.id.txt_cate_title);

        btn_make_cafe = findViewById(R.id.btn_make_cafe);
        btn_make_cafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCafe();
            }
        });



    }

    @Override
    protected void attachEvents() {

    }

    @Override
    protected void setInitialData() {
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            et_name_length.setText(String.valueOf(s.length())+ "/50");
            if (s.length() > 50) {
                et_input_name.setText(String.valueOf(s));
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private final TextWatcher mTextEditorWatcher1 = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            et_desc_length.setText(String.valueOf(s.length())+ "/200");
            if (s.length() > 200) {
                et_desc.setText(String.valueOf(s));
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Multipart
    private void createCafe() {
        // 카페명
        String cafename = "";
        // 카페설명
        String cafedesc = "";
        // 공개/비공개
        String confirm = "";
        // 사용자추가 부서명
        String additions = "";
        // 관리자입력 부서명
        String category = "";
        // 로고 파일명
        String file = "";

        cafename = et_input_name.getText().toString();

        if (null == cafename || "".equals(cafename.trim())) {
            toast(R.string.warn_cafe_name);
            return;
        }

        if (null == cafedesc || "".equals(cafedesc.trim())) {
            toast(R.string.warn_cafe_desc);
            return;
        }


        cafedesc = et_desc.getText().toString();

        if (check_privacy_hide.isChecked()) {
            confirm = "Y";
        } else if (check_privacy_open.isChecked()) {
            confirm = "N";
        } else if (!check_privacy_hide.isChecked() && !check_privacy_open.isChecked()) {
            toast(R.string.warn_privacy_not_selected);
            return;
        }

        additions = et_cate_user_title.getText().toString();

        category = et_cate_title.getText().toString();


        for (int i = 0; i < mCategoryIndex; i++) {
            KoswEditText et = (KoswEditText)findViewById(i+2000);

            if (null != et) {
                if (et.isShown()) {
                    if (null != et.getText().toString() && !"".equals(et.getText().toString().trim())) {
                        category += "#@#" + et.getText().toString();
                    }
                }
            } else {
            }
        }



//        Log.d("111111", f.getAbsoluteFile().toString());

        showSpinner("");
        AppUserBase user = DataHolder.instance().getAppUserBase() ;
        Map<String, Object> query = KUtil.getDefaultQueryMap();
        query.put("user_seq",user.getUser_seq() );

        query.put("cafename", cafename);
        query.put("cafedesc", cafedesc);
        query.put("confirm", confirm);
        query.put("additions", additions);
        query.put("category", category);

        File f = null;
        MultipartBody.Part uploadFile = null;

        if (img_logo.isShown()) {
            f = SaveBitmapToFileCache(bitmap);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
            uploadFile = MultipartBody.Part.createFormData("file", f.getName(), requestFile);
            //query.put("file", uploadFile);
        }

        /*if (null != f) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), f);
            query.put("file", fileBody);
        }*/

        Call<ResponseBase> call =
                new DefaultRestClient<CafeService>(this)
                .getClient(CafeService.class).createCafe(query, uploadFile);
        call.enqueue(new Callback<ResponseBase>() {

            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, response.raw().toString());
                Log.d("111111111111", response.raw().toString());
                Log.d("111111111111", response.code() + "_____" + response.message());
                if(response.isSuccessful()){
                    ResponseBase base = response.body();
                    if(base.isSuccess()) {
                        // 카페 생성 성공시
                        toast(R.string.cafe_create_success);

                    }else{
                        toast("1 " + R.string.warn_fail_create_cafe);
                    }
                }else{
                    toast("2 " + R.string.warn_fail_create_cafe);
                }
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, t);
                Log.d("111111111111", t.toString());
                toast("3 " + R.string.warn_fail_create_cafe);
            }
        });

        closeSpinner();
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
        int permssionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);

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
        img_logo.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        img_logo.setVisibility(View.VISIBLE);
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
        img_logo.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        img_logo.setVisibility(View.VISIBLE);
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

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
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
}