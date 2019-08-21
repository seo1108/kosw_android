package kr.co.photointerior.kosw.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A class manage SharedPreference.
 * Created by kugie on 2017. 11. 14..
 */
public class Pref {
    private static Pref instance = new Pref();
    private SharedPreferences preferences;
    private final String prefName = "BLE_PREF";

    /**
     * Returns instance of this class.
     * @return
     */
    public static Pref instance(){
        return instance;
    }

    /**
     * Initialize the shared preference.
     * @param context
     */
    public synchronized void init(Context context){
        if(preferences == null){
            preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        }
    }

    /**
     * Saves a string value.
     * @param key
     * @param value
     * @return
     */
    public synchronized boolean saveStringValue(PrefKey key, String value){
        Editor editor = preferences.edit();
        editor.putString(key.name(), value);
        return editor.commit();
    }

    /**
     * Gets the string value from SharedPreference and returns it.
     * @param key
     * @param defValue
     * @return
     */
    public synchronized String getStringValue(PrefKey key, String defValue){
        return preferences.getString(key.name(), defValue);
    }

    /**
     * Store int value to shared preference.
     * @param key
     * @param value
     * @return
     */
    public synchronized boolean saveIntValue(PrefKey key, int value){
        Editor editor = preferences.edit();
        editor.putInt(key.name(), value);
        return editor.commit();
    }

    /**
     * Returns int value.
     * @param key
     * @param def
     * @return
     */
    public synchronized int getIntValue(PrefKey key, int def){
        return preferences.getInt(key.name(), def);
    }

    /**
     * Stores long value.
     * @param key
     * @param value
     * @return
     */
    public synchronized boolean saveLongValue(PrefKey key, long value){
        Editor editor = preferences.edit();
        editor.putLong(key.name(), value);
        return editor.commit();
    }

    /**
     * Returns long value
     * @param key
     * @param def
     * @return
     */
    public synchronized long getLongValue(PrefKey key, long def){
        return preferences.getLong(key.name(), def);
    }

    /**
     * Stores boolean value.
     * @param key
     * @param value
     * @return
     */
    public synchronized boolean saveBooleanValue(PrefKey key, boolean value){
        Editor editor = preferences.edit();
        editor.putBoolean(key.name(), value);
        return editor.commit();
    }

    /**
     * Returns boolean value.
     * @param key
     * @param def
     * @return
     */
    public synchronized boolean getBooleanValue(PrefKey key, boolean def){
        return preferences.getBoolean(key.name(), def);
    }

    /**
     * Stores image to byte array.
     * @param key
     * @param values
     * @return
     */
    public synchronized boolean saveImageToByteArray(PrefKey key, byte[] values){
        ByteArrayOutputStream baos =null;
        Editor editor1 = preferences.edit();
        try {

            baos = new ByteArrayOutputStream();
            BitmapFactory.decodeByteArray(values, 0, values.length)
                    .compress(Bitmap.CompressFormat.JPEG, 30, baos);
            editor1.putString(key.name(), new String(Base64.encode(baos.toByteArray(), 8)));
            return editor1.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if( baos != null ){
                try {
                    baos.close();
                }catch(IOException e ){}
            }
        }
        return false;
    }

    /**
     * Returns drawable image form stored byte array.
     * @param key
     * @return
     */
    public synchronized Drawable getImageFromBytesArray(PrefKey key){
        Drawable drawable = null;
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(Base64.decode(preferences.getString(key.name(), "").getBytes(), 8));
            drawable = Drawable.createFromStream(bais, key.name());
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if( bais != null ){
                try {
                    bais.close();
                }catch( IOException e){}
            }
        }
        return drawable;
    }

    /**
     * remove all stored values.
     * @return
     */
    public boolean clear(){
        Editor editor = preferences.edit();
        return editor.clear().commit();
    }

    private Pref(){}
}
