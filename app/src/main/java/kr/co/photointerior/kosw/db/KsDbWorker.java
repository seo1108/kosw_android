package kr.co.photointerior.kosw.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.co.photointerior.kosw.global.Env;
import kr.co.photointerior.kosw.rest.model.Bbs;
import kr.co.photointerior.kosw.rest.model.BbsOpen;
import kr.co.photointerior.kosw.rest.model.BeaconUuid;
import kr.co.photointerior.kosw.utils.LogUtils;

/**
 * 데이터베이스 작업 클래스.
 */
public class KsDbWorker {
    private static String TAG = "KsDbWorker";

    /**
     * 전송실패 데이터 전체 선택 반환.
     * @param context
     * @return
     */
    public static List<Map<String, Object>> getGoUpFailData(Context context){
        List<Map<String, Object>> results = new ArrayList<>();
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            cur = sqlite.rawQuery("SELECT * FROM " + KsDb.Meta.TB_SEND_FAIL.name(), null);
            if(cur != null){
                while(cur.moveToNext()){
                    String[] cols = cur.getColumnNames();
                    //LogUtils.err(TAG, "column name=" + Arrays.toString(cols));
                    Map<String, Object> row = new HashMap<>();
                    for( String colName : cols ){
                        row.put(colName, cur.getString(cur.getColumnIndex(colName)));
                    }
                    results.add(row);
                }
            }
        }finally {
            if(cur != null){
                cur.close();
            }
            db.close();
        }
        /*for( Map<String, Object> row : results ) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> keys = row.keySet().iterator();
            for (; keys.hasNext(); ) {
                String ke = keys.next();
                sb.append(ke).append("=").append(row.get(ke)).append("\n");
            }
            LogUtils.err(TAG, sb.toString());
        }*/
        return results;
    }

    /**
     * 전송 완료된 실패 데이터 삭제
     * @param context
     * @param data
     */
    public static void deleteGoUpFailData(Context context, Map<String, Object> data){
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        try{
            sqlite = db.getWritableDB();
            sqlite.beginTransaction();
            sqlite.execSQL(
                    "DELETE FROM " + KsDb.Meta.TB_SEND_FAIL.name() + " " +
                    "WHERE seq=?",
                    new String[]{(String)data.get("seq")}
            );
            sqlite.setTransactionSuccessful();
        }finally{
            if(sqlite != null){
                sqlite.endTransaction();
            }
            db.close();
        }
    }

    /**
     * 계단 올라간 데이터 서버전송 실패했을 때 저장
     * @param context
     * @param data
     * @return
     */
    public static boolean insertFailData(Context context, Map<String, Object> data){
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        try {
            sqlite = db.getWritableDB();
            sqlite.beginTransaction();
            Iterator<String> keys = data.keySet().iterator();
            ContentValues values = new ContentValues();
            for(; keys.hasNext(); ){
                String k = keys.next();
                String value = data.get(k).toString();
                //LogUtils.err(TAG, "key=" + k + ", value=" + value);
                values.put(k, value);
            }
            sqlite.insert(KsDb.Meta.TB_SEND_FAIL.getName(), null, values);
            sqlite.setTransactionSuccessful();
            return true;
        }finally{
            if(sqlite != null){
                sqlite.endTransaction();
            }
            db.close();
        }

//        "	seq 			INTEGER PRIMARY KEY NOT NULL, " +
//        "	device			    VARCHAR, " +
//        "	token			    VARCHAR, " +
//        "	buildCode			VARCHAR, " +
//        "	beacon_uuid			VARCHAR, " +
//        "	major_value			VARCHAR, " +
//        "	minor_value			VARCHAR, " +
//        "	install_floor		VARCHAR, " +
//        "	beacon_seq			VARCHAR, " +
//        "	stair_seq			VARCHAR, " +
//        "	build_seq			VARCHAR, " +
//        "	floor_amount		VARCHAR, " +
//        "	stair_amount		VARCHAR, " +
//        "	cust_seq			VARCHAR, " +
//        "	build_code			VARCHAR, " +
//        "	godo			    VARCHAR, " +
//        "	goup_amt			INTEGER NOT NULL DEFAULT(1), " +
//        "	alti			    VARCHAR, " +
//        "	start_time			VARCHAR, " +
//        "	end_time			VARCHAR, " +
//        "	app_time			VARCHAR " +
    }
    /**
     * 특정 빌딩의 제일 낯은 계단 또는 높은 계단
     * @param context
     * @param uuid
     * @return
     */
    public static int[] getFloorMinMax(Context context, String uuid){
        int[] result = new int[2];
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            String query =
                    "SELECT MIN(install_floor) AS min_flr," +
                    "   MAX(install_floor) AS max_flr " +
                    " FROM " + KsDb.Meta.TB_UUID.getName() + " WHERE uuid=?";
            cur = sqlite.rawQuery(query, new String[]{uuid});
            if(cur != null){
                if(cur.moveToNext()){
                    result[0] = cur.getInt(cur.getColumnIndex("min_flr"));
                    result[1] = cur.getInt(cur.getColumnIndex("max_flr"));
                }
            }
        }finally{
            if(cur != null){
                cur.close();
            }
            db.close();
        }
        return result;
    }

    /**
     * 게시글 읽음 상태 추가
     * @param context
     * @param flag
     */
    public static void insertBbsReadStatus(Context context, BbsOpen flag){
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        try{
            sqlite = db.getWritableDB();
            sqlite.beginTransaction();
            ContentValues values = new ContentValues();

            values.put("bbs_seq", flag.getBbsSeq());
            values.put("is_read", flag.getReadFlag());
            sqlite.insert(KsDb.Meta.TB_BBS.getName(), null, values);
            LogUtils.err(TAG, flag.string());

//                "	seq 			    INTEGER PRIMARY KEY NOT NULL, " +	/*로컬 관리번호*/
//                "	bbs_seq 			VARCHAR PRIMARY KEY NOT NULL, " +	/*공지사항 고유번호 */
//                "	is_read			    VARCHAR NOT NULL DEFAULT('N') " + /*읽음 여부. N-읽지 않음*/
            sqlite.setTransactionSuccessful();
        }finally {
            if(sqlite != null){
                sqlite.endTransaction();
            }
            db.close();
        }
    }

    /**
     * 읽지 않은 게시글이 있는가 검사.
     * @param context
     * @return
     */
    public static boolean hasNotReadBbs(Context context){
        boolean result = false;
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            String query = "SELECT COUNT(bbs_seq) AS cnt FROM " + KsDb.Meta.TB_BBS_READ.getName() + " WHERE is_read=?";
            cur = sqlite.rawQuery(query, new String[]{"N"});
            int count = 0;
            if(cur != null){
                if(cur.moveToNext()){
                    count = cur.getInt(cur.getColumnIndex("cnt"));
                    result = count > 0;
                }
            }
            LogUtils.e(TAG, "read count=" + count);
        }finally{
            if(cur != null){
                cur.close();
            }
            db.close();
        }
        return result;
    }
    /**
     * 게시글 읽음 상태 처리
     * @param context
     * @param bbsSeq
     */
    public static void updateBbsReadStatus(Context context, String bbsSeq){
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        try{
            sqlite = db.getWritableDB();
            String query = "UPDATE " + KsDb.Meta.TB_BBS_READ.getName() + " ";
            query += "SET is_read='Y' WHERE bbs_seq=" + bbsSeq;
            LogUtils.err(TAG, "query=" + query + " bbsSeq=" + bbsSeq);
            sqlite.beginTransaction();
            sqlite.execSQL(query);
            //sqlite.execSQL(query, new String[]{bbsSeq});
            sqlite.setTransactionSuccessful();
        }finally {
            if(sqlite != null){
                sqlite.endTransaction();
            }
            db.close();
        }
    }

    public static void selectAllReadFlag(Context context){
        BbsOpen bbs = null;
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            String query = "SELECT * FROM " + KsDb.Meta.TB_BBS_READ.getName() + " ";
            cur = sqlite.rawQuery(query, null);
            if(cur != null){
                while (cur.moveToNext()){
                    int bs = cur.getInt(cur.getColumnIndex("bbs_seq"));
                    String flag = cur.getString(cur.getColumnIndex("is_read"));
                    LogUtils.err(TAG, "bbs_seq=" + bs + " flag=" + flag);
                }
            }
        }finally{
            if(cur != null){
                cur.close();
            }
            db.close();
        }
    }

    /**
     * 로컬에 관리하는 게시글 읽음 데이터 반환.
     * @param context
     * @param bbsSeq
     * @return
     */
    public static BbsOpen getLocalBbsReadFlags(Context context, String bbsSeq){
        BbsOpen bbs = null;
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            String query = "SELECT * FROM " + KsDb.Meta.TB_BBS.getName() + " WHERE bbs_seq=?";
            cur = sqlite.rawQuery(query, new String[]{bbsSeq});
            if(cur != null){
                if(cur.moveToNext()){
                    bbs = new BbsOpen();
                    bbs.setLocalSeq(cur.getInt(cur.getColumnIndex("seq")));
                    bbs.setBbsSeq(cur.getInt(cur.getColumnIndex("bbs_seq")));
                    bbs.setReadFlag(cur.getString(cur.getColumnIndex("is_read")));
                }
//                "	seq 			    INTEGER PRIMARY KEY NOT NULL, " +	/*로컬 관리번호*/
//                "	bbs_seq 			VARCHAR PRIMARY KEY NOT NULL, " +	/*공지사항 고유번호 */
//                "	is_read			    VARCHAR NOT NULL DEFAULT('N') " + /*읽음 여부. N-읽지 않음*/
            }
        }finally{
            if(cur != null){
                cur.close();
            }
            db.close();
        }
        return bbs;
    }

    /**
     * 공지 이벤트 읽음 상태값 등록
     * @param context
     * @param list
     */
    public static void replaceReadFlag(Context context, List<Bbs> list){
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        try{
            String query =  "INSERT OR REPLACE INTO " + KsDb.Meta.TB_BBS_READ.getName() +
                    " (bbs_seq, is_read) VALUES (?, (select is_read from tb_bbs_read where bbs_seq = ?))";
            sqlite = db.getWritableDB();
            sqlite.beginTransaction();
            String[] values = new String[2];
            for( Bbs b : list){
                values[0] = String.valueOf(b.getBbsSeq());
                values[1] = String.valueOf(b.getBbsSeq());
                sqlite.execSQL(query, values);
            }
            sqlite.setTransactionSuccessful();
        }finally {
            if(sqlite != null){
                sqlite.endTransaction();
            }
            db.close();
        }
        removeNoMatched(context, list);
    }

    private static void removeNoMatched(Context context, List<Bbs> list){
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            String in = "(";
            for( int i = 0, k = list.size(); i < k; i++ ){
                Bbs b = list.get(i);
                in += b.getBbsSeq();
                if( i < k-1 ){
                    in += ",";
                }
            }
            in += ")";
            String query =  "DELETE FROM " + KsDb.Meta.TB_BBS_READ.getName() + " WHERE bbs_seq NOT IN " + in;
            sqlite = db.getWritableDB();
            sqlite.beginTransaction();
            sqlite.execSQL(query);
            sqlite.setTransactionSuccessful();
        }finally {
            if(cur != null){
                cur.close();
            }
            if(sqlite != null){
                sqlite.endTransaction();
            }
            db.close();
        }
    }

    /**
     * 빌딩코드 기준 정보 선택 반환.
     * @param context
     * @param buildingCode
     * @return
     */
    public static List<BeaconUuid> getBeaconInfoOfCurrentBuildingCode(Context context, String buildingCode){
        List<BeaconUuid> list = new ArrayList<>();
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            String query =
                    "SELECT * FROM " + KsDb.Meta.TB_UUID.getName() +
                    " WHERE build_code=?  ORDER BY minor_value ASC ";
            cur = sqlite.rawQuery(query, new String[]{buildingCode});
            if(cur != null){
                while(cur.moveToNext()){
                    BeaconUuid beaconUuid = new BeaconUuid();
                    beaconUuid.setLocalSeq(cur.getInt(cur.getColumnIndex("seq")));
                    beaconUuid.setUuid(cur.getString(cur.getColumnIndex("uuid")));
                    beaconUuid.setMajorValue(cur.getString(cur.getColumnIndex("major_value")));
                    beaconUuid.setMinorValue(cur.getString(cur.getColumnIndex("minor_value")));
                    beaconUuid.setInstallFloor(cur.getString(cur.getColumnIndex("install_floor")));
                    beaconUuid.setBeaconSeq(cur.getString(cur.getColumnIndex("beacon_seq")));
                    beaconUuid.setOriginalBeaconSeq(beaconUuid.getBeaconSeq());
                    beaconUuid.setStairSeq(cur.getString(cur.getColumnIndex("stair_seq")));
                    beaconUuid.setBuildSeq(cur.getString(cur.getColumnIndex("build_seq")));
                    beaconUuid.setCustSeq(cur.getString(cur.getColumnIndex("cust_seq")));
                    beaconUuid.setFloorAmount(cur.getString(cur.getColumnIndex("floor_amount")));
                    beaconUuid.setStairAmount(cur.getString(cur.getColumnIndex("stair_amount")));
                    beaconUuid.setBuildCode(cur.getString(cur.getColumnIndex("build_code")));
                    beaconUuid.setAltitude(cur.getString(cur.getColumnIndex("altitude")));
                    list.add(beaconUuid);
                }
            }
        }finally {
            if(cur != null){
                cur.close();
            }
            db.close();
        }
        return list;
    }
    /**
     * 빌딩코드 기준 정보 선택 반환.
     * @param context
     * @param buildingCode
     * @return
     */
    public static BeaconUuid getBeaconByBuildingCode(Context context, String buildingCode){
        BeaconUuid beaconUuid = null;
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            String query =
                    "SELECT * FROM " + KsDb.Meta.TB_UUID.getName() +
                            " WHERE build_code=?  ORDER BY minor_value ASC ";
            cur = sqlite.rawQuery(query, new String[]{buildingCode});
            if(cur != null){
                if(cur.moveToNext()){
                    beaconUuid = new BeaconUuid();
                    beaconUuid.setLocalSeq(cur.getInt(cur.getColumnIndex("seq")));
                    beaconUuid.setUuid(cur.getString(cur.getColumnIndex("uuid")));
                    beaconUuid.setMajorValue(cur.getString(cur.getColumnIndex("major_value")));
                    beaconUuid.setMinorValue(cur.getString(cur.getColumnIndex("minor_value")));
                    beaconUuid.setInstallFloor(cur.getString(cur.getColumnIndex("install_floor")));
                    beaconUuid.setBeaconSeq(cur.getString(cur.getColumnIndex("beacon_seq")));
                    beaconUuid.setOriginalBeaconSeq(beaconUuid.getBeaconSeq());
                    beaconUuid.setStairSeq(cur.getString(cur.getColumnIndex("stair_seq")));
                    beaconUuid.setBuildSeq(cur.getString(cur.getColumnIndex("build_seq")));
                    beaconUuid.setCustSeq(cur.getString(cur.getColumnIndex("cust_seq")));
                    beaconUuid.setFloorAmount(cur.getString(cur.getColumnIndex("floor_amount")));
                    beaconUuid.setStairAmount(cur.getString(cur.getColumnIndex("stair_amount")));
                    beaconUuid.setBuildCode(cur.getString(cur.getColumnIndex("build_code")));
                    beaconUuid.setAltitude(cur.getString(cur.getColumnIndex("altitude")));
                }
//                "	seq 			INTEGER PRIMARY KEY NOT NULL, " +	/*fixed index*/
//                "	uuid 			VARCHAR, " +	/*비콘 UUID*/
//                "	major_value 		INTEGER, " +	/* major */
//                "	minor_value 		INTEGER, " +	/* minor */
//                "	install_floor 		INTEGER, " +	/* 설치층  */
//                "	beacon_seq 		INTEGER, " +	/* 비콘 관리번호 */
//                "	stair_seq 		INTEGER, " +	/* 계단 관리번호 */
//                "	build_seq 		INTEGER, " +	/* 빌딩 관리번호 */
//                "	cust_seq 		INTEGER " + /* 고객사 관리번호 */
            }
        }finally {
            if(cur != null){
                cur.close();
            }
            db.close();
        }
        return beaconUuid;
    }
    /**
     * UUID 기준 정보 선택 반환.
     * @param context
     * @param uuid
     * @return
     */
    public static BeaconUuid getBeaconByUuidByBuildingCode(Context context,
                                             String uuid, String major, String minor, String buildingCode){
        BeaconUuid beaconUuid = null;
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            String query =
                    "SELECT * FROM " + KsDb.Meta.TB_UUID.getName() +
                            " WHERE uuid=? AND major_value=? AND minor_value=? AND build_code=? ";
            cur = sqlite.rawQuery(query, new String[]{uuid, major, minor, buildingCode});
            if(cur != null){
                if(cur.moveToNext()){
                    beaconUuid = new BeaconUuid();
                    beaconUuid.setLocalSeq(cur.getInt(cur.getColumnIndex("seq")));
                    beaconUuid.setUuid(cur.getString(cur.getColumnIndex("uuid")));
                    beaconUuid.setMajorValue(cur.getString(cur.getColumnIndex("major_value")));
                    beaconUuid.setMinorValue(cur.getString(cur.getColumnIndex("minor_value")));
                    beaconUuid.setInstallFloor(cur.getString(cur.getColumnIndex("install_floor")));
                    beaconUuid.setBeaconSeq(cur.getString(cur.getColumnIndex("beacon_seq")));
                    beaconUuid.setOriginalBeaconSeq(beaconUuid.getBeaconSeq());
                    beaconUuid.setStairSeq(cur.getString(cur.getColumnIndex("stair_seq")));
                    beaconUuid.setBuildSeq(cur.getString(cur.getColumnIndex("build_seq")));
                    beaconUuid.setCustSeq(cur.getString(cur.getColumnIndex("cust_seq")));
                    beaconUuid.setFloorAmount(cur.getString(cur.getColumnIndex("floor_amount")));
                    beaconUuid.setStairAmount(cur.getString(cur.getColumnIndex("stair_amount")));
                    beaconUuid.setBuildCode(cur.getString(cur.getColumnIndex("build_code")));
                    beaconUuid.setAltitude(cur.getString(cur.getColumnIndex("altitude")));
                }
//                "	seq 			INTEGER PRIMARY KEY NOT NULL, " +	/*fixed index*/
//                "	uuid 			VARCHAR, " +	/*비콘 UUID*/
//                "	major_value 		INTEGER, " +	/* major */
//                "	minor_value 		INTEGER, " +	/* minor */
//                "	install_floor 		INTEGER, " +	/* 설치층  */
//                "	beacon_seq 		INTEGER, " +	/* 비콘 관리번호 */
//                "	stair_seq 		INTEGER, " +	/* 계단 관리번호 */
//                "	build_seq 		INTEGER, " +	/* 빌딩 관리번호 */
//                "	cust_seq 		INTEGER " + /* 고객사 관리번호 */
            }
        }finally {
            if(cur != null){
                cur.close();
            }
            db.close();
        }
        return beaconUuid;
    }
    /**
     * UUID 기준 정보 선택 반환.
     * @param context
     * @param uuid
     * @return
     */
    public static BeaconUuid getBeaconByUuid(Context context,
                                             String uuid, String major, String minor){
        BeaconUuid beaconUuid = null;
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            String query =
                    "SELECT * FROM " + KsDb.Meta.TB_UUID.getName() +
                            " WHERE uuid=? AND major_value=? AND minor_value=? ";
            cur = sqlite.rawQuery(query, new String[]{uuid, major, minor});
            if(cur != null){
                if(cur.moveToNext()){
                    beaconUuid = new BeaconUuid();
                    beaconUuid.setLocalSeq(cur.getInt(cur.getColumnIndex("seq")));
                    beaconUuid.setUuid(cur.getString(cur.getColumnIndex("uuid")));
                    beaconUuid.setMajorValue(cur.getString(cur.getColumnIndex("major_value")));
                    beaconUuid.setMinorValue(cur.getString(cur.getColumnIndex("minor_value")));
                    beaconUuid.setInstallFloor(cur.getString(cur.getColumnIndex("install_floor")));
                    beaconUuid.setBeaconSeq(cur.getString(cur.getColumnIndex("beacon_seq")));
                    beaconUuid.setOriginalBeaconSeq(beaconUuid.getBeaconSeq());
                    beaconUuid.setStairSeq(cur.getString(cur.getColumnIndex("stair_seq")));
                    beaconUuid.setBuildSeq(cur.getString(cur.getColumnIndex("build_seq")));
                    beaconUuid.setCustSeq(cur.getString(cur.getColumnIndex("cust_seq")));
                    beaconUuid.setFloorAmount(cur.getString(cur.getColumnIndex("floor_amount")));
                    beaconUuid.setStairAmount(cur.getString(cur.getColumnIndex("stair_amount")));
                    beaconUuid.setBuildCode(cur.getString(cur.getColumnIndex("build_code")));
                    beaconUuid.setAltitude(cur.getString(cur.getColumnIndex("altitude")));
                }
//                "	seq 			INTEGER PRIMARY KEY NOT NULL, " +	/*fixed index*/
//                "	uuid 			VARCHAR, " +	/*비콘 UUID*/
//                "	major_value 		INTEGER, " +	/* major */
//                "	minor_value 		INTEGER, " +	/* minor */
//                "	install_floor 		INTEGER, " +	/* 설치층  */
//                "	beacon_seq 		INTEGER, " +	/* 비콘 관리번호 */
//                "	stair_seq 		INTEGER, " +	/* 계단 관리번호 */
//                "	build_seq 		INTEGER, " +	/* 빌딩 관리번호 */
//                "	cust_seq 		INTEGER " + /* 고객사 관리번호 */
            }
        }finally {
            if(cur != null){
                cur.close();
            }
            db.close();
        }
        return beaconUuid;
    }
    /**
     * 특정 빌딩의 모든 비콘 정보 반환.
     * @param context
     * @param buildingCode
     * @param beacons
     */
    public static void getBeaconsByBuildingCode(Context context, String buildingCode, List<BeaconUuid> beacons){
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        Cursor cur = null;
        try{
            sqlite = db.getReadableDB();
            String query =
                    "SELECT * FROM " + KsDb.Meta.TB_UUID.getName() +
                            " WHERE build_code=? ORDER BY minor_value ASC ";
            cur = sqlite.rawQuery(query, new String[]{buildingCode});
            if(cur != null){
                while (cur.moveToNext()){
                    BeaconUuid beaconUuid = new BeaconUuid();
                    beaconUuid.setLocalSeq(cur.getInt(cur.getColumnIndex("seq")));
                    beaconUuid.setUuid(cur.getString(cur.getColumnIndex("uuid")));
                    beaconUuid.setMajorValue(cur.getString(cur.getColumnIndex("major_value")));
                    beaconUuid.setMinorValue(cur.getString(cur.getColumnIndex("minor_value")));
                    beaconUuid.setInstallFloor(cur.getString(cur.getColumnIndex("install_floor")));
                    beaconUuid.setBeaconSeq(cur.getString(cur.getColumnIndex("beacon_seq")));
                    beaconUuid.setOriginalBeaconSeq(beaconUuid.getBeaconSeq());
                    beaconUuid.setStairSeq(cur.getString(cur.getColumnIndex("stair_seq")));
                    beaconUuid.setBuildSeq(cur.getString(cur.getColumnIndex("build_seq")));
                    beaconUuid.setCustSeq(cur.getString(cur.getColumnIndex("cust_seq")));
                    beaconUuid.setFloorAmount(cur.getString(cur.getColumnIndex("floor_amount")));
                    beaconUuid.setStairAmount(cur.getString(cur.getColumnIndex("stair_amount")));
                    beaconUuid.setBuildCode(cur.getString(cur.getColumnIndex("build_code")));
                    beaconUuid.setAltitude(cur.getString(cur.getColumnIndex("altitude")));

                    beacons.add(beaconUuid);
                }
            }
        }finally {
            if(cur != null){
                cur.close();
            }
            db.close();
        }
    }
    /**
     * 회원이 등록한 빌딩의 UUID 전체를 DB에 교체.
     * @param context
     * @param list
     */
    public static void replaceUuid(Context context, List<BeaconUuid> list){
        deleteAllUuid(context);
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        try{
            sqlite = db.getWritableDB();
            sqlite.beginTransaction();
            ContentValues values = new ContentValues();
            for( BeaconUuid uuid : list ){
                values.put("uuid", uuid.getUuid().toUpperCase());
                values.put("major_value", uuid.getMajorValue());
                values.put("minor_value", uuid.getMinorValue());
                values.put("install_floor", uuid.getInstallFloor());
                values.put("beacon_seq", uuid.getBeaconSeq());
                values.put("stair_seq", uuid.getStairSeq());
                values.put("build_seq", uuid.getBuildSeq());
                values.put("cust_seq", uuid.getCustSeq());
                values.put("floor_amount", uuid.getFloorAmount());
                values.put("stair_amount", uuid.getStairAmount());
                values.put("build_code", uuid.getBuildCode());
                values.put("altitude", uuid.getAltitude());
                sqlite.insert(KsDb.Meta.TB_UUID.getName(), null, values);
                LogUtils.err(TAG, "replaced uuid=" + uuid.string());
                values.clear();
            }
            sqlite.setTransactionSuccessful();
        }finally {
            if(sqlite != null){
                sqlite.endTransaction();
            }
            db.close();
        }
    }

    private static void deleteAllUuid(Context context){
        KsDb db = new KsDb(context);
        SQLiteDatabase sqlite = null;
        try{
            sqlite = db.getWritableDB();
            sqlite.beginTransaction();
            sqlite.execSQL("DELETE FROM " + KsDb.Meta.TB_UUID.getName());
            sqlite.setTransactionSuccessful();
        }finally{
            if( sqlite != null ){
                sqlite.endTransaction();
            }
            db.close();
        }
    }



    private KsDbWorker(){}
}
