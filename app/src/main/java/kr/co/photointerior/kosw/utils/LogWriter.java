package kr.co.photointerior.kosw.utils;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.model.BeaconLog;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kugie on 2018. 04. 30.
 */

public class LogWriter {
    private String TAG = getClass().getSimpleName();
    private static LogWriter instance = new LogWriter();
    private String commonLog = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            "kosw" + File.separator + "kosw_log.txt";
    private String prefValue = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
            "kosw" + File.separator + "kosw_pref.txt";
    private SimpleDateFormat sf = new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss.SSS", Locale.KOREA );
    public static LogWriter instance(){
        return instance;
    }

    public synchronized void append(String value, String s){
        if(!Env.Bool.FILE_LOG_ENABLE.getValue()){
            return;
        }
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +"kosw");
        if( !dir.exists()){
            dir.mkdirs();
        }
        File logFile = new File(commonLog);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedWriter buf = null;
        try {
            buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(sf.format(new Date()) + "-" + takeStackPosition() + ":" + value);
            buf.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if( buf != null ){
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void send(BeaconLog log){
        if(!Env.Bool.SERVER_LOG_ENABLE.getValue()){
            return;
        }
        String sourcePosition = takeStackPosition();
        log.setMessage(sourcePosition + ":" + log.string());

        Map<String, Object> query = log.createQueryMap();

        Call<ResponseBase> call =
                new DefaultRestClient<App>(log.getContext()).getClient(App.class).sendServiceLog(query);
        call.enqueue(new Callback<ResponseBase>() {
            @Override
            public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                LogUtils.err(TAG, "service log response:" +response.raw().toString());
            }

            @Override
            public void onFailure(Call<ResponseBase> call, Throwable t) {
                LogUtils.err(TAG, "service log fail:" + Log.getStackTraceString(t));
            }
        });
    }

    public synchronized void append(SharedPreferences pref){
        if(!Env.Bool.FILE_LOG_ENABLE.getValue()){
            return;
        }
        File logFile = new File(prefValue);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedWriter buf = null;
        try {
            buf = new BufferedWriter(new FileWriter(logFile, true));
            Map<String, ?> map = pref.getAll();
            Iterator<String> keys = map.keySet().iterator();
            for( ; keys.hasNext(); ) {
                String k = keys.next();
                buf.append(sf.format(new Date()) + "- key=" + k + " : value=" + map.get(k));
                buf.newLine();
            }
            buf.append(sf.format(new Date()) + "- ========================================");
            buf.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if( buf != null ){
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String takeStackPosition(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String name = stackTraceElement.getMethodName();
        String className = stackTraceElement.getClassName();
        String lineNumber = stackTraceElement.getLineNumber() + "";
        return className.substring(className.lastIndexOf(".") + 1 ) + ":" + name +":[" + lineNumber + "]";
    }

    public synchronized void deleteCommonLogFile(){
        File logFile = new File(commonLog);
        if( logFile.exists()){
            logFile.delete();
        }
    }

    public synchronized void deletePrefLogFile(){
        File logFile = new File(prefValue);
        if( logFile.exists()){
            logFile.delete();
        }
    }
    private LogWriter(){

    }
}
