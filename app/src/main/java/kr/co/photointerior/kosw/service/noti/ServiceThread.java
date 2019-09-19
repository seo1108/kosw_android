package kr.co.photointerior.kosw.service.noti;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import kr.co.photointerior.kosw.R;
import kr.co.photointerior.kosw.conf.AppConst;
import kr.co.photointerior.kosw.pref.Pref;
import kr.co.photointerior.kosw.pref.PrefKey;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.UserService;
import kr.co.photointerior.kosw.rest.model.AppUser;
import kr.co.photointerior.kosw.rest.model.AppUserBase;
import kr.co.photointerior.kosw.rest.model.DataHolder;
import kr.co.photointerior.kosw.rest.model.MainData;
import kr.co.photointerior.kosw.rest.model.ResponseBase;
import kr.co.photointerior.kosw.service.stepcounter.StepCounterService;
import kr.co.photointerior.kosw.ui.MainActivity;
import kr.co.photointerior.kosw.utils.KUtil;
import kr.co.photointerior.kosw.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static java.text.DateFormat.getDateTimeInstance;
import static java.text.DateFormat.getTimeInstance;

public class ServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;
    private static Context mContext = null;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 1;
    private static String todayFloor = "0";
    private static String todayStep = "0";



    public ServiceThread(Context context){
        mContext = context;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        //반복적으로 수행할 작업을 한다.
        while(isRun){

            try{
                int mServiceCnt = 0;


                boolean isback = isAppOnForeground();

                Log.d("DDDDDDDDDDDDDDDDD", "isFore : " + isback);





                SharedPreferences pref = mContext.getSharedPreferences("background", MODE_PRIVATE);
                String background = pref.getString("background", "auto");

                // 앱이 백그라운드 실행중이고, 측정서비스가 미구동시 서비스 실행
                if (!isAppOnForeground()) {
                    if (!isMyServiceRunning(StepCounterService.class)) {
                  /*      Intent startintent = new Intent(mContext, StepCounterService.class);
                        mContext.stopService(startintent);
                        Thread.sleep(1000);
                        mContext.startService(startintent);*/
                        Intent startintent = new Intent(mContext, StepCounterService.class);
                        mContext.startService(startintent);
                    }

                    // 서비스 실행 후, 30분이 지나면, 서비스 재시작
                    if (mServiceCnt > 30) {
                        Intent startintent = new Intent(mContext, StepCounterService.class);
                        mContext.stopService(startintent);
                        Thread.sleep(5000);
                        mContext.startService(startintent);

                        mServiceCnt = 0;
                    }
                }

                //requestToServer();
                // 어제걸음수 전송된 데이터가 없다면 전송
                DateFormat d_dateFormat = new SimpleDateFormat("yyyyMMdd");
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                String today_date = d_dateFormat.format(cal.getTime());

                SharedPreferences prefr = mContext.getSharedPreferences("saveWalkInfo", MODE_PRIVATE);
                String lastSendDate = prefr.getString("lastSendDate", "");

                if (!today_date.equals(lastSendDate)) {
                    readYesterdayHistoryData();
                }

                mServiceCnt++;
            }catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    //Thread.sleep(30*60*1000); //30분에 한번씩 조회 및 자동측정 재시작
                    Thread.sleep(1*60*1000); //1분에 한번씩 조회 및 자동측정 재시작
                } catch (Exception ex) {    }
            }


        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void saveWalkStep(String date, int walk_count) {
        try {
            Map<String, Object> query = KUtil.getDefaultQueryMap();
            AppUserBase user = DataHolder.instance().getAppUserBase();

            if (null == user) return;

            query.put("user_seq", user.getUser_seq());
            query.put("walk_date", date);
            query.put("walk_count", walk_count);

            Call<ResponseBase> call =
                    new DefaultRestClient<UserService>(mContext)
                            .getClient(UserService.class)
                            .saveWalkStep(query);
            call.enqueue(new Callback<ResponseBase>() {
                @Override
                public void onResponse(Call<ResponseBase> call, Response<ResponseBase> response) {
                    Log.d("MAINFRAGMENT", "____________main data : " + response.raw().toString());
                    if (response.isSuccessful()) {
                        ResponseBase data = response.body();
                        if (data.isSuccess()) {
                            DateFormat dateFormat = getTimeInstance();
                            DateFormat d_dateFormat = new SimpleDateFormat("yyyyMMdd");
                            Calendar cal = Calendar.getInstance();
                            Date now = new Date();
                            cal.setTime(now);
                            String today_date = d_dateFormat.format(cal.getTime());

                            SharedPreferences prefr = mContext.getSharedPreferences("saveWalkInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefr.edit();
                            editor.putString("lastSendDate", today_date);
                            editor.commit();
                        } else {
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBase> call, Throwable t) {
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getStepCount() {
        try {
            FitnessOptions fitnessOptions =
                    FitnessOptions.builder()
                            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                            .build();
            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(mContext), fitnessOptions)) {

            } else {
                readHistoryData();

                // 어제걸음수 전송된 데이터가 없다면 전송
                DateFormat d_dateFormat = new SimpleDateFormat("yyyyMMdd");
                Calendar cal = Calendar.getInstance();
                Date now = new Date();
                cal.setTime(now);
                String today_date = d_dateFormat.format(cal.getTime());

                SharedPreferences prefr = mContext.getSharedPreferences("saveWalkInfo", MODE_PRIVATE);
                String lastSendDate = prefr.getString("lastSendDate", "");

                if (!today_date.equals(lastSendDate)) {
                    readYesterdayHistoryData();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Task<DataReadResponse> readHistoryData() {
        // Begin by creating the query.
        DataReadRequest readRequest = queryFitnessData();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                // For the sake of the sample, we'll print the data so we can see what we just
                                // added. In general, logging fitness information should be avoided for privacy
                                // reasons.
                                printData(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
    }

    /** Returns a {@link DataReadRequest} for all step count changes in the past week. */
    public static DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        //cal.add(Calendar.WEEK_OF_YEAR, -1);
        //long startTime = cal.getTimeInMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.getTimeInMillis();
        long startTime = calendar.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateTimeInstance();

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();



        // [END build_read_data_request]

        return readRequest;
    }

    public static void printData(DataReadResponse dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        } else {
            updateNotification();
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private static void dumpDataSet(DataSet dataSet) {
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                //Toast.makeText(mContext, dp.getValue(field).toString(), Toast.LENGTH_LONG).show();
                todayStep = dp.getValue(field).toString();
               // updateNotification();
            }
        }
    }







    private Task<DataReadResponse> readYesterdayHistoryData() {
        // Begin by creating the query.
        DataReadRequest readRequest = queryYesterdayFitnessData();

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                // For the sake of the sample, we'll print the data so we can see what we just
                                // added. In general, logging fitness information should be avoided for privacy
                                // reasons.
                                printYesterDayData(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
    }

    /** Returns a {@link DataReadRequest} for all step count changes in the past week. */
    public static DataReadRequest queryYesterdayFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.getTimeInMillis();
        long startTime = cal.getTimeInMillis();

        long endTime = startTime + (24*60*60*1000);

        java.text.DateFormat dateFormat = getDateTimeInstance();

        DataReadRequest readRequest =
                new DataReadRequest.Builder()
                        // The data request can specify multiple data types to return, effectively
                        // combining multiple data queries into one call.
                        // In this example, it's very unlikely that the request is for several hundred
                        // datapoints each consisting of a few steps and a timestamp.  The more likely
                        // scenario is wanting to see how many steps were walked per day, for 7 days.
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();



        // [END build_read_data_request]

        return readRequest;
    }

    public static void printYesterDayData(DataReadResponse dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpYesterdayDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpYesterdayDataSet(dataSet);
            }
        } else {

        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    private static void dumpYesterdayDataSet(DataSet dataSet) {
        Log.d("99999999999999", "[99] Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = getTimeInstance();
        DateFormat d_dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String s_date = d_dateFormat.format(cal.getTime());

        for (DataPoint dp : dataSet.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                // 어제자 걸음수 전송
                try {
                    saveWalkStep(s_date, Integer.parseInt(dp.getValue(field).toString()));
                } catch (Exception ex) {
                }

            }
        }
    }




    private static void updateNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, AppConst.NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentPendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle("오늘의 활동량")
                .setContentText(todayFloor + "층, " + todayStep + "걸음입니다.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setColorized(true)
                .setColor(Color.BLACK)
                .setContentIntent(contentPendingIntent);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(AppConst.NOTIFICATION_ID, builder.build());
    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = mContext.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

}
