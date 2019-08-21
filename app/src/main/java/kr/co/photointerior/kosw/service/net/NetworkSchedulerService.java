package kr.co.photointerior.kosw.service.net;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.db.KsDbWorker;
import kr.co.photointerior.kosw.rest.DefaultRestClient;
import kr.co.photointerior.kosw.rest.api.App;
import kr.co.photointerior.kosw.rest.model.BeaconUuid;
import kr.co.photointerior.kosw.service.beacon.BeaconRagingInRegionService;
import kr.co.photointerior.kosw.utils.LogUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkSchedulerService extends JobService
        implements NetworkConnectivityReceiver.NetworkConnectivityListener{
    private String TAG = NetworkSchedulerService.class.getSimpleName();
    private NetworkConnectivityReceiver mConnectivityReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.err(TAG, "Service created");
        mConnectivityReceiver = new NetworkConnectivityReceiver(this);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob" + mConnectivityReceiver);
        registerReceiver(
                mConnectivityReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        );
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob");
        unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            List<Map<String, Object>> goupDataList = KsDbWorker.getGoUpFailData(getApplicationContext());
            //Toast.makeText(this, "connected size=" + goupDataList.size(), Toast.LENGTH_SHORT).show();
            for(int i = 0, k = goupDataList.size(); i < k; i++) {
                Map<String, Object> query = goupDataList.get(i);

                Iterator<String> keys = query.keySet().iterator();
                for( ; keys.hasNext(); ){
                    String ke = keys.next();
                    if(query.get(ke) == null){
                        query.put(ke, "");
                    }
                 }

                Call<BeaconUuid> call =
                        new DefaultRestClient<App>(getApplicationContext())
                                .getClient(App.class).sendStairGoUpAmountToServer(query);
                call.enqueue(new Callback<BeaconUuid>() {
                    @Override
                    public void onResponse(Call<BeaconUuid> call, Response<BeaconUuid> response) {
                        //LogUtils.err(TAG, "fail-data response raw:" + response.raw().toString());
                        if (response.isSuccessful()) {
                            //LogUtils.err(TAG, "fail-data response body:" + response.body().string());
                            BeaconUuid uuid = response.body();
                            if (uuid != null && uuid.isSuccess()) {

                                KsDbWorker.deleteGoUpFailData(getApplicationContext(), query);

                                BeaconRagingInRegionService.broadcastServerResult(uuid);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BeaconUuid> call, Throwable t) {
                        LogUtils.err(TAG, Log.getStackTraceString(t));

                    }
                });
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {

                }
            }
        }
    }
}
