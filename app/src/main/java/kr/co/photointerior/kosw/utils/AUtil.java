package kr.co.photointerior.kosw.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This is the utility class for Android. All methods of this class are static.
 * Created by kugie.
 * 2018. 04. 30.
 */
public class AUtil {
    /**
     * 실행중이 최상위 Activity 반환
     * @param context
     * @return
     */
    public static String getTopActivity(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        LogUtils.err("AUtil",
                "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName() +
                        ", Package Name :  " + componentInfo.getPackageName());
        return taskInfo.get(0).topActivity.getClassName();
    }
    /**
     * Returns the font size.
     * @param context
     * @param textSize
     * @return
     */
    public static int getFontWidth(Context context, int textSize) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return (int) ((((float) textSize) * ((float) dm.heightPixels)) / 1280.0f);
    }
    /**
     * Returns color from color resource id.
     * @param context
     * @param colorResId
     * @return
     */
    public static int getColor(Context context, int colorResId){
        return context.getResources().getColor(colorResId);
    }
    /**
     * Converts dpi to pixel.
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, float dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    /**
     * Returns the screen width in pixel.
     * @return
     */
    public static int displayWith(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * Returns the screen height in pixel.
     * @return
     */
    public static int displayHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static String getDeviceModel(){
        return Build.MODEL;
    }

    public static String getOsReleaseVersion(){
        return Build.VERSION.RELEASE;
    }

    public static String getManufacturor(){
        return Build.MANUFACTURER;
    }

    /**
     * Changes the status bar color.
     * @param activity
     * @param color
     */
    public static void darkenStatusBar(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(
                    darkenColor(ContextCompat.getColor(activity, color)));
        }

    }

    /**
     * Returns the darken color.
     * @param color
     * @return
     */
    private static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    /**
     * Returns the resource id dynamically from "/res" folders.
     * Usage : AUtil.loadResourceId(getBaseContext(), "drawable", "ic_launcher")
     *
     * @param context {@link Context}
     * @param resName Resource folder name. drawable or string or ...
     * @param resIdName Resource name with string ex:"ic_launcher"
     * @return
     */
    public static int loadResourceId(Context context, String resName, String resIdName){
        if( StringUtil.isEmptyOrWhiteSpace(resName) || StringUtil.isEmptyOrWhiteSpace(resIdName)){
            return -1;
        }
        //Log.e("resName=" + resName + ", resIdName=" + resIdName);
        return context.getResources().getIdentifier(resIdName, resName, context.getPackageName());
    }
    /**
     * Returns the app's version name.
     * @param context
     * @param targetPackage
     * @return if the package not installed, returns 'unknown'.
     */
    public static String getVersionName(Context context, String targetPackage){
        PackageInfo info = getPackageInfoByPkgName(context, targetPackage);
        return info == null ? "unknown" : info.versionName;
    }

    /**
     * Returns the app's version code.
     * @param context
     * @param targetPackage
     * @return if the package not installed, returns -1.
     */
    public static int getVersionCode(Context context, String targetPackage){
        PackageInfo info = getPackageInfoByPkgName(context, targetPackage);
        return info == null ? -1 : info.versionCode;
    }

    /**
     * Checks whether a app was installed.
     * @param context
     * @param targetPackage
     * @return true-installed
     */
    public static boolean isPackageExisted(Context context, String targetPackage) {
        PackageInfo info = getPackageInfoByPkgName(context, targetPackage);
        return info == null ? false : true;
    }

    /**
     * Finds {@link PackageInfo} by package name.
     * @param context
     * @param targetPackage
     * @return */
    public static PackageInfo getPackageInfoByPkgName(Context context, String targetPackage) {
        try {
            return context.getPackageManager().getPackageInfo(
                    targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {}
        return null;
    }

    /**
     * Tests WiFi network was connected.
     * @param context
     * @return true:connected
     */
    public static boolean isWifiConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi != null && wifi.isConnected();
    }
    /**
     * Tests mobile network was connected.
     * @param context
     * @return true:connected
     */
    public static boolean isMobileConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mobile != null && mobile.isConnected();
    }

    /**
     * Tests network was connected.
     * @param context
     * @return true:WiFi or Mobile network connected.
     */
    public static boolean isNetworkConnected(Context context){
        return isWifiConnected(context) || isMobileConnected(context);
    }

    /**
     * Gets application directory and returns it.
     * @param context
     * @return
     */
    public static String getApplicationDir(Context context){
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            android.util.Log.e(context.getPackageName(), "PackageInfo not found [" +s+ "]", e);
        }
        return s;
    }

    /**
     * Shows or hides soft keyboard.
     * @param context
     * @param et
     * @param isShow true:show
     */
    public static void toggleSoftKeyboard(Context context, EditText et, boolean isShow){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if( isShow){
            imm.showSoftInput(et, 0);
        }else{
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

    /**
     * Shows or hides soft keyboard.
     * @param context
     * @param view
     * @param isShow true:show
     */
    public static void toggleSoftKeyboard(Context context, View view, boolean isShow){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if( isShow){
            imm.showSoftInput(view, 0);
        }else{
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp
     * @param context
     * @return
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px
     * @param context
     * @return
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * Determines whether SD-card mounted or not.
     * @return
     */
    public static boolean isSDCardMounted() {
        String status = Environment.getExternalStorageState();
        if ( status.equals( Environment.MEDIA_MOUNTED ) ){
            return true;
        }
        return false;
    }

    /**
     * Formats local(KR) phone number.
     * @param pn
     * @return
     * 숫자일 경우 [4 <= 전화번호 길이 <=11]일 때만 형식화. 숫자가 아닌 문자가 포함되면 형식화 하지 않음.
     */
    public static String formatLocalPhoneNumber(String pn){
        if( pn == null ){
            return null;
        }
        if( !TextUtils.isDigitsOnly(pn) ){
            return pn;
        }
        int length = pn.length();
        if( length <= 4 ){
            return pn;
        }else if( length > 11 ){
            return pn;
        }else{
            String regEx = "";
            String rpEx = "";
            switch(length){
                case 5 :
                    regEx = "(\\d{1})(\\d{4})";
                    rpEx = "$1-$2";
                    break;
                case 6 :
                    regEx = "(\\d{2})(\\d{4})";
                    rpEx = "$1-$2";
                    break;
                case 7 :
                    regEx = "(\\d{3})(\\d{4})";
                    rpEx = "$1-$2";
                    break;
                case 8 :
                    regEx = "(\\d{4})(\\d{4})";
                    rpEx = "$1-$2";
                    break;
                case 9 :
                    if(pn.startsWith("02")){
                        regEx = "(\\d{2})(\\d{3})(\\d{4})";
                    }else{
                        regEx = "(\\d{1})(\\d{4})(\\d{4})";
                    }
                    rpEx = "$1-$2-$3";
                    break;
                case 10 :
                    if(pn.startsWith("02")){
                        regEx = "(\\d{2})(\\d{4})(\\d{4})";
                    }else{
                        regEx = "(\\d{2,3})(\\d{3,4})(\\d{4})";
                    }
                    rpEx = "$1-$2-$3";
                    break;
                case 11 :
                    regEx = "(\\d{2,3})(\\d{3,4})(\\d{4})";
                    rpEx = "$1-$2-$3";
                    break;
                case 12 :
                    break;
                case 13 :
                    break;
                case 14 :
                    break;
                case 15 :
                    break;
                case 16 :
                    break;
            }
            if(Pattern.matches(regEx, pn)){
                return pn.replaceAll(regEx, rpEx);
            }
        }
        return pn;
    }

    /**
     * Determines whether a Service is alive.
     * @param serviceClz
     * @param context
     * @return
     */
    public static boolean isServiceAlive(Class<?> serviceClz, Context context){
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClz.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether email is valid.
     * @param target
     * @return true-valid
     */
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Starts media scanning.
     * @param context
     * @param absPath
     */
    public static void startMediaScan(Context context, String absPath){
        MediaScannerConnection.scanFile(context,
                new String[] { new File(absPath).toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("ExternalStorage", "Scanned " + path + ":");
                        Log.d("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    public static final String f2871g = "display_name";

    /**
     * Returns
     * @param context
     * @param number
     * @return
     */
    public static String getDisplayName(Context context, String number) {
        String name = null;
        try {
            CursorLoader cl = new CursorLoader(context);
            cl.setUri(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            cl.setSelection("data1=?");
            cl.setSelectionArgs(new String[]{number});
            cl.setProjection(new String[]{"display_name"});
            //cl.setProjection(new String[]{C0919g.f2871g});
            Cursor cursor = cl.loadInBackground();
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex("display_name"));
                cursor.close();
            }
            if (!TextUtils.isEmpty(name)) {
                return name;
            }
            CursorLoader cl2 = new CursorLoader(context);
            cl2.setUri(Uri.parse("content://com.android.contacts/data/phones/filter/" + number));
            cl2.setProjection(new String[]{"display_name"});
            Cursor cursor2 = cl2.loadInBackground();
            if (cursor2 != null && cursor2.moveToFirst()) {
                name = cursor2.getString(cursor2.getColumnIndex("display_name"));
                cursor2.close();
            }
            if (TextUtils.isEmpty(name)) {
                return null;
            }
            return name;
        } catch (Exception ex) {
            Log.e(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Returns the formatted phone number.
     * @param phone
     * @return
     */
    public static String getKongGePhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() < 8) {
            return phone;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(phone.substring(0, 3)).append(" ");
        sb.append(phone.substring(3, 7)).append(" ");
        sb.append(phone.substring(7));
        Log.d("---> The input phone is :" + phone + " ,The output phone is:" + sb.toString());
        return phone.toString();
    }

    /**
     * Returns the formatted phone number.
     * @param phone
     * @return
     */
    public static String getZHXPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() < 8) {
            return phone;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(phone.substring(0, 3)).append("-");
        sb.append(phone.substring(3, 7)).append("-");
        sb.append(phone.substring(7));
        Log.d("---> The input phone is :" + phone + " ,The output phone is:" + sb.toString());
        return phone.toString();
    }

    /**
     * Returns the formatted phone number.
     * @param phone
     * @return
     */
    public static String getXHXPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() < 8) {
            return phone;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(phone.substring(0, 3)).append("_");
        sb.append(phone.substring(3, 7)).append("_");
        sb.append(phone.substring(7));
        Log.d("---> The input phone is :" + phone + " ,The output phone is:" + sb.toString());
        return phone.toString();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * Default constructor.
     */
    private AUtil(){}
}
