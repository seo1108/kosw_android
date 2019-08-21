package kr.co.photointerior.kosw.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 계단왕 데이터베이스 클래스.
 */
public class KsDb {
    private Context context;
    private KsSqlHelper dbHelper;
    private SQLiteDatabase database;
    public static final int DB_VERSION = 3;

    /**
     * Database name and table name enum.
     */
    public enum Meta{
        /** DB name */
        DB_NAME("kosw.sqlite", "100"),
        /** beacon uuid data */
        TB_UUID("tb_uuid", "101"),
        /** 공지사항.이벤트  */
        TB_BBS("tb_bbs", "102"),
        /** 공지사항.이벤트 읽음 플래그  */
        TB_BBS_READ("tb_bbs_read", "103"),
        /** 서버전송 실패 데이터 */
        TB_SEND_FAIL("tb_send_fail", "104");

        private String name;
        private String code;
        Meta(String name, String code){
            this.name = name;
            this.code =code;
        }
        public String getName(){
            return this.name;
        }
        public String getCode(){
            return this.code;
        }
    }

    public KsDb(Context context){
        this.context = context;
        this.dbHelper = new KsSqlHelper(this.context);
    }

    public SQLiteDatabase getReadableDB(){
        database = dbHelper.getReadableDatabase();
        return database;
    }

    public SQLiteDatabase getWritableDB(){
        database = dbHelper.getWritableDatabase();
        return database;
    }

    public void open(){
        dbHelper = new KsSqlHelper(context);
    }

    public void close(){
        if( database != null ){
            database.close();
        }
        database = null;
        if( dbHelper != null ){
            dbHelper.close();
        }
        dbHelper = null;
    }

    public void close(SQLiteDatabase db){
        if( db != null ){
            db.close();
        }
    }

    public void close(SQLiteDatabase db, Cursor cur){
        if( cur != null ){
            cur.close();
        }
        close(db);
    }

    private class KsSqlHelper extends SQLiteOpenHelper {

        private String uuidQuery =
                "CREATE TABLE "+Meta.TB_UUID.getName()+" ( "+
                        "	seq 			INTEGER PRIMARY KEY NOT NULL, " +	/*fixed index*/
                        "	uuid 			VARCHAR, " +	/*비콘 UUID*/
                        "	major_value 		INTEGER, " +	/* major */
                        "	minor_value 		INTEGER, " +	/* minor */
                        "	install_floor 		INTEGER, " +	/* 설치층  */
                        "	beacon_seq 		INTEGER, " +	/* 비콘 관리번호 */
                        "	stair_seq 		INTEGER, " +	/* 계단 관리번호 */
                        "	build_seq 		INTEGER, " +	/* 빌딩 관리번호 */
                        "	cust_seq 		INTEGER, " + /* 고객사 관리번호 */
                        "	floor_amount 	INTEGER, " + /* 빌딩의 층수 */
                        "	stair_amount 	INTEGER, " + /* 빌딩의 1개 증의 개단수 */
                        "	build_code 		VARCHAR, " + /* 빌딩코드 */
                        "	altitude 		VARCHAR " + /* 층의 고도 */
                        ")";

        private String bbsQuery =
                "CREATE TABLE " + Meta.TB_BBS.getName() + " ( "+
                        "	seq 			    INTEGER PRIMARY KEY NOT NULL, " +	/*로컬 관리번호*/
                        "	bbs_seq 			INTEGER NOT NULL, " +	/*공지사항 고유번호 */
                        "	is_read			    VARCHAR NOT NULL DEFAULT('N') " + /*읽음 여부. N-읽지 않음*/
                        ")";


        private String bbsReadQuery =
                "CREATE TABLE " + Meta.TB_BBS_READ.getName() + " ( "+
                        "	bbs_seq 			INTEGER PRIMARY KEY NOT NULL, " +	/*공지사항 고유번호 */
                        "	is_read			    VARCHAR NOT NULL DEFAULT('N') " + /*읽음 여부. N-읽지 않음*/
                        ")";

        private String goUpDataQuery =
                "CREATE TABLE " + Meta.TB_SEND_FAIL.getName() + " ( "+
                "	seq 			INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "	device			    VARCHAR, " +
                "	token			    VARCHAR, " +
                "	buildCode			VARCHAR, " +
                "	beacon_uuid			VARCHAR, " +
                "	major_value			VARCHAR, " +
                "	minor_value			VARCHAR, " +
                "	install_floor		VARCHAR, " +
                "	beacon_seq			VARCHAR, " +
                "	stair_seq			VARCHAR, " +
                "	build_seq			VARCHAR, " +
                "	floor_amount		VARCHAR, " +
                "	stair_amount		VARCHAR, " +
                "	cust_seq			VARCHAR, " +
                "	build_code			VARCHAR, " +
                "	godo			    VARCHAR, " +
                "	goup_amt			INTEGER NOT NULL DEFAULT(1), " +
                "	alti			    VARCHAR, " +
                "	start_time			VARCHAR, " +
                "	end_time			VARCHAR, " +
                "	app_time			VARCHAR " +
                ")";

        public KsSqlHelper(Context context) {
            super(context, Meta.DB_NAME.getName(), null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(uuidQuery);
            db.execSQL(bbsQuery);
            db.execSQL(bbsReadQuery);
            db.execSQL(goUpDataQuery);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
		    /*if (!db.isReadOnly()) {
		        db.execSQL("PRAGMA foreign_keys=ON;");// Enable foreign key constraints
		    }*/
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL(bbsReadQuery);
            db.execSQL(goUpDataQuery);
        }

    }
}
