package kr.co.photointerior.kosw.conf;

import android.content.Context;

import kr.co.photointerior.kosw.pref.Pref;

/**
 *
 * Created by kugie
 */
public class AppInitializer {

    public static void init(Context context, AppCondition condittion){
        ConditionHolder.instance().init(condittion);
        Pref.instance().init(context);
    }

    private AppInitializer(){}
}
