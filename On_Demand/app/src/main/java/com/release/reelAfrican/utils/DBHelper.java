package com.release.reelAfrican.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.release.reelAfrican.model.ContactModel1;
import com.release.reelAfrican.model.SubtitleModel;

import java.util.ArrayList;

/**
 * Created by Nikunj on 27-08-2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DOWNLOADMANAGER_ONDEMAND.db";
    public static final String TABLE_NAME = "DOWNLOADMANAGER_ONDEMAND";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MUVI_ID = "MUVI_ID";
    public static final String COLUMN_DOWNLOADID = "DOWNLOADID";
    public static final String COLUMN_DOWNLOAD_STATUS = "DOWNLOAD_STATUS";
    public static final String COLUMN_DOWNLOAD_USERNAME = "USERNAME";
    public static final String COLUMN_DOWNLOAD_UNIQUEID = "MUVI_UNIQUEID";
    public static final String COLUMN_POSTER = "Poster";
    public static final String COLUMN_MUVI_TOKEN = "Token";
    public static final String COLUMN_FILE_PATH = "Filepath";
    public static final String COLUMN_CONTENT_ID = "Contentid";
    public static final String COLUMN_GENERE = "Genere";
    public static final String COLUMN_MUVIID = "Muviid";
    public static final String COLUMN_DURATION = "Duration";
    public static final String COLUMN_DOWNLOAD_PROGRESS = "DOWNLOAD_PROGRESS";


    public static final String TABLE_NAME_SUBTITLE_LUIMERE = "SUBTITLE_ONDEMAND";
    public static final String COLUMN_UID = "UID";
    public static final String COLUMN_LANGUAGE = "LANGUAGE";
    public static final String COLUMN_PATH = "PATH";


    private static final String CREATE_SOL =
            "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                    COLUMN_MUVI_ID + " VARCHAR, " +

                    COLUMN_DOWNLOADID + " INTEGER, " +
                    COLUMN_DOWNLOAD_PROGRESS + " INTEGER, " +
                    COLUMN_DOWNLOAD_USERNAME + " VARCHAR, " +
                    COLUMN_DOWNLOAD_UNIQUEID + " VARCHAR, " +


                    COLUMN_POSTER + " VARCHAR, " +
                    COLUMN_MUVI_TOKEN + " VARCHAR, " +
                    COLUMN_FILE_PATH + " VARCHAR, " +
                    COLUMN_CONTENT_ID + " VARCHAR, " +
                    COLUMN_GENERE + " VARCHAR, " +
                    COLUMN_MUVIID + " VARCHAR, " +
                    COLUMN_DURATION + " VARCHAR, " +


                    COLUMN_DOWNLOAD_STATUS + " INTEGER)";

    private static final String CREATE_SOL_SUBTITLE_LUIMERE = "CREATE TABLE " + TABLE_NAME_SUBTITLE_LUIMERE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_UID + " VARCHAR, " +
            COLUMN_LANGUAGE + " VARCHAR, " +

            COLUMN_PATH + " VARCHAR)";

    private SQLiteDatabase database;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SOL);
        db.execSQL(CREATE_SOL_SUBTITLE_LUIMERE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertRecord(ContactModel1 contact) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MUVI_ID, contact.getMUVIID());
        contentValues.put(COLUMN_DOWNLOADID, contact.getDOWNLOADID());
        contentValues.put(COLUMN_DOWNLOAD_PROGRESS, contact.getProgress());
        contentValues.put(COLUMN_DOWNLOAD_USERNAME, contact.getUSERNAME());
        contentValues.put(COLUMN_DOWNLOAD_USERNAME, contact.getUSERNAME());
        contentValues.put(COLUMN_DOWNLOAD_UNIQUEID, contact.getUniqueId());
        contentValues.put(COLUMN_DOWNLOAD_STATUS, contact.getDSTATUS());


        contentValues.put(COLUMN_POSTER, contact.getPoster());
        contentValues.put(COLUMN_MUVI_TOKEN, contact.getToken());
        contentValues.put(COLUMN_FILE_PATH, contact.getPath());
        contentValues.put(COLUMN_CONTENT_ID, contact.getContentid());
        contentValues.put(COLUMN_GENERE, contact.getGenere());
        contentValues.put(COLUMN_MUVIID, contact.getMuviid());
        contentValues.put(COLUMN_DURATION, contact.getDuration());





        database.insert(TABLE_NAME, null, contentValues);
        database.close();
        return false;
    }

    public long insertRecordSubtittel(SubtitleModel contact) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_UID, contact.getUID());
        contentValues.put(COLUMN_LANGUAGE, contact.getLanguage());
        contentValues.put(COLUMN_PATH, contact.getPath());
        long status = database.insert(TABLE_NAME_SUBTITLE_LUIMERE, null, contentValues);
        database.close();
        return status;
    }
//    public void insertRecordAlternate(ContactModel contact) {
//        database = this.getReadableDatabase();
//        database.execSQL("INSERT INTO " + TABLE_NAME + "(" + COLUMN_FIRST_NAME + "," + COLUMN_LAST_NAME + ") VALUES('" + contact.getFirstName() + "','" + contact.getLastName() + "')");
//        database.close();
//    }

//    public String getCotacts(String id) {
//
//        //hp = new HashMap();
//        String resp = "";
//        database = this.getReadableDatabase();
//        Cursor res =  database.rawQuery( "select * from SOL where CUSTOMER_ID = '"+id+"'", null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            resp = "Name:-"+  res.getString(res.getColumnIndex(COLUMN_FIRM_NAME))
//                    +"\n"+"Email"+res.getString(res.getColumnIndex(COLUMN_LAST_NAME));
//            res.moveToNext();
//        }
//        return resp;
//    }

    public ContactModel1 getContact(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;


        cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID,
                COLUMN_MUVI_ID,
                        COLUMN_DOWNLOADID,
                COLUMN_DOWNLOAD_PROGRESS,COLUMN_DOWNLOAD_USERNAME,COLUMN_DOWNLOAD_UNIQUEID, COLUMN_DOWNLOAD_STATUS,

                        COLUMN_POSTER,COLUMN_MUVI_TOKEN,COLUMN_FILE_PATH,COLUMN_CONTENT_ID,COLUMN_GENERE,COLUMN_MUVIID,COLUMN_DURATION,



                }, COLUMN_DOWNLOAD_UNIQUEID + "=?",
                new String[]{id}, null, null, null, null);

        ContactModel1 contactModel=null;

        if (cursor != null && cursor.moveToFirst()) {


            contactModel = new ContactModel1();
            contactModel.setID(cursor.getString(0));
            contactModel.setMUVIID(cursor.getString(1));
            contactModel.setDOWNLOADID(cursor.getInt(2));
            contactModel.setProgress(cursor.getInt(3));
            contactModel.setUSERNAME(cursor.getString(4));
            contactModel.setUniqueId(cursor.getString(5));
            contactModel.setDSTATUS(cursor.getInt(6));


            contactModel.setPoster(cursor.getString(7));
            contactModel.setToken(cursor.getString(8));
            contactModel.setPath(cursor.getString(9));
            contactModel.setContentid(cursor.getString(10));
            contactModel.setGenere(cursor.getString(11));
            contactModel.setMuviid(cursor.getString(12));
            contactModel.setDuration(cursor.getString(13));

        }

        return contactModel;
        // return contact

    }



    public String getCotacts(String id) {

        //hp = new HashMap();
        String resp = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Sanji where URL = '"+id+"'", null );
        if (res != null && res.moveToFirst()) {

            while (res.isAfterLast() == false) {
                resp = "Name:-" + res.getString(res.getColumnIndex(COLUMN_MUVI_ID))
                        + "\n" + "Email" + res.getString(res.getColumnIndex(COLUMN_DOWNLOADID));
                res.moveToNext();
            }
        }
        return resp;
    }


   /* public ContactModel getLastrowid() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        String query = "SELECT ID from SOL order by ID DESC limit 1";

        cursor = db.rawQuery(query,null);

        ContactModel contactModel=null;

        if (cursor != null && cursor.moveToFirst()) {


            contactModel = new ContactModel();

            contactModel.setID(cursor.getString(0));



        }

        return contactModel;
        // return contact

    }

*/
//    public ContactModel getContacts(String id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = null;
//
//
//        cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_USERR_ID, COLUMN_CONTENTTYPE_ID,
//                        COLUMN_FILE_NAME, COLUMN_FILE_PATH, COLUMN_POSTER, COLUMN_GENERE, COLUMN_VIDEO_TITLE,
//                        COLUMN_VIDEO_DURATION,
//
//
////
////
//
//
//                }, COLUMN_USERR_ID + "=?",
//                new String[]{id}, null, null, null, null);
//
//        ContactModel contactModel=null;
//
//        if (cursor != null && cursor.moveToFirst()) {
//
//
//            contactModel = new ContactModel();
//
//            contactModel.setID(cursor.getString(0));
//            contactModel.setContenttypeid(cursor.getString(1));
//            contactModel.setFilename(cursor.getString(2));
//            contactModel.setFilepath(cursor.getString(3));
//            contactModel.setToken(cursor.getString(4));
//            contactModel.setPoster(cursor.getString(5));
//            contactModel.setGenere(cursor.getString(6));
//            contactModel.setVideotitle(cursor.getString(7));
//            contactModel.setVideoduration(cursor.getString(8));
//
//
//
//        }
//
//        return contactModel;
//        // return contact
//
//    }




//    public String[] getAllCountries()
//    {
//
//        database = this.getReadableDatabase();
//        Cursor cursor = this.database.query(TABLE_NAME, new String[] {COLUMN_REQUEST_ID}, null, null, null, null, null);
//
//        if(cursor.getCount() >0)
//        {
//            String[] str = new String[cursor.getCount()];
//            int i = 0;
//
//            while (cursor.moveToNext())
//            {
//                str[i] = cursor.getString(cursor.getColumnIndex(COLUMN_REQUEST_ID));
//
//                i++;
//            }
//            return str;
//        }
//        else
//        {
//            return new String[] {};
//        }
//    }




    public ArrayList<ContactModel1> getContactt(String id , int downloadstatus) {


        String query = "SELECT "+COLUMN_ID+","+COLUMN_MUVI_ID+","+COLUMN_DOWNLOADID+","+COLUMN_DOWNLOAD_PROGRESS+","+
                COLUMN_DOWNLOAD_USERNAME+","+COLUMN_DOWNLOAD_UNIQUEID+","+ COLUMN_DOWNLOAD_STATUS+","+

                COLUMN_POSTER+","+COLUMN_MUVI_TOKEN+","+COLUMN_FILE_PATH+","+COLUMN_CONTENT_ID+","+COLUMN_GENERE+","+COLUMN_MUVIID+","+COLUMN_DURATION+" FROM "+TABLE_NAME+" WHERE "+COLUMN_DOWNLOAD_USERNAME+" = '"+id+"' AND "+COLUMN_DOWNLOAD_STATUS+" = "+downloadstatus;
        SQLiteDatabase db = this.getReadableDatabase();
//
        Cursor cursor = db.rawQuery(query,null);
       /* Cursor cursor = null;

        cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID,
                        COLUMN_MUVI_ID,
                        COLUMN_DOWNLOADID,
                        COLUMN_DOWNLOAD_PROGRESS,COLUMN_DOWNLOAD_USERNAME,COLUMN_DOWNLOAD_UNIQUEID, COLUMN_DOWNLOAD_STATUS,

                        COLUMN_POSTER,COLUMN_MUVI_TOKEN,COLUMN_FILE_PATH,COLUMN_CONTENT_ID,COLUMN_GENERE,COLUMN_MUVIID,COLUMN_DURATION,



                }, COLUMN_DOWNLOAD_USERNAME + "=?",
                new String[]{id}, null, null, null, null);
*/
        ContactModel1 contactModel=null;

        ArrayList<ContactModel1> contacts = new ArrayList<ContactModel1>();

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                contactModel = new ContactModel1();
                contactModel.setID(cursor.getString(0));
                contactModel.setMUVIID(cursor.getString(1));
                contactModel.setDOWNLOADID(cursor.getInt(2));
                contactModel.setProgress(cursor.getInt(3));
                contactModel.setUSERNAME(cursor.getString(4));
                contactModel.setUniqueId(cursor.getString(5));
                contactModel.setDSTATUS(cursor.getInt(6));


                contactModel.setPoster(cursor.getString(7));
                contactModel.setToken(cursor.getString(8));
                contactModel.setPath(cursor.getString(9));
                contactModel.setContentid(cursor.getString(10));
                contactModel.setGenere(cursor.getString(11));
                contactModel.setMuviid(cursor.getString(12));
                contactModel.setDuration(cursor.getString(13));




                contacts.add(contactModel);
            }
        }
        cursor.close();
//        database.close();

        return contacts;
    }



    public ArrayList<ContactModel1> getDownloadcontent(String id) {


//        String query = "SELECT "+COLUMN_ID+","+COLUMN_MUVI_ID+","+COLUMN_DOWNLOADID+","+COLUMN_DOWNLOAD_PROGRESS+","+
//                COLUMN_DOWNLOAD_USERNAME+","+COLUMN_DOWNLOAD_UNIQUEID+","+ COLUMN_DOWNLOAD_STATUS+","+
//
//                COLUMN_POSTER+","+COLUMN_MUVI_TOKEN+","+COLUMN_FILE_PATH+","+COLUMN_CONTENT_ID+","+COLUMN_GENERE+","+COLUMN_MUVIID+","+COLUMN_DURATION+" FROM "+TABLE_NAME+" WHERE "+COLUMN_DOWNLOAD_USERNAME+" = '"+id+"' AND "+COLUMN_DOWNLOAD_STATUS+" = "+downloadstatus;
        SQLiteDatabase db = this.getReadableDatabase();
//
        //Cursor cursor = db.rawQuery(query,null);
       Cursor cursor = null;

        cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID,
                        COLUMN_MUVI_ID,
                        COLUMN_DOWNLOADID,
                        COLUMN_DOWNLOAD_PROGRESS,COLUMN_DOWNLOAD_USERNAME,COLUMN_DOWNLOAD_UNIQUEID, COLUMN_DOWNLOAD_STATUS,

                        COLUMN_POSTER,COLUMN_MUVI_TOKEN,COLUMN_FILE_PATH,COLUMN_CONTENT_ID,COLUMN_GENERE,COLUMN_MUVIID,COLUMN_DURATION,



                }, COLUMN_DOWNLOAD_USERNAME + "=?",
                new String[]{id}, null, null, null, null);

        ContactModel1 contactModel=null;

        ArrayList<ContactModel1> contacts = new ArrayList<ContactModel1>();

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                contactModel = new ContactModel1();
                contactModel.setID(cursor.getString(0));
                contactModel.setMUVIID(cursor.getString(1));
                contactModel.setDOWNLOADID(cursor.getInt(2));
                contactModel.setProgress(cursor.getInt(3));
                contactModel.setUSERNAME(cursor.getString(4));
                contactModel.setUniqueId(cursor.getString(5));
                contactModel.setDSTATUS(cursor.getInt(6));


                contactModel.setPoster(cursor.getString(7));
                contactModel.setToken(cursor.getString(8));
                contactModel.setPath(cursor.getString(9));
                contactModel.setContentid(cursor.getString(10));
                contactModel.setGenere(cursor.getString(11));
                contactModel.setMuviid(cursor.getString(12));
                contactModel.setDuration(cursor.getString(13));




                contacts.add(contactModel);
            }
        }
        cursor.close();
//        database.close();

        return contacts;
    }




    public ArrayList<ContactModel1> getAllRecords() {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<ContactModel1> contacts = new ArrayList<ContactModel1>();
        ContactModel1 contactModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                contactModel = new ContactModel1();

                contactModel.setID(cursor.getString(0));
                contactModel.setMUVIID(cursor.getString(1));
                contactModel.setDOWNLOADID(cursor.getInt(2));
                contactModel.setProgress(cursor.getInt(3));
                contactModel.setDSTATUS(cursor.getInt(4));





                contacts.add(contactModel);
            }
        }
        cursor.close();
        database.close();

        return contacts;
    }

//    public ArrayList<ContactModel> getAllRecordsAlternate() {
//        database = this.getReadableDatabase();
//        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
//
//        ArrayList<ContactModel> contacts = new ArrayList<ContactModel>();
//        ContactModel contactModel;
//        if (cursor.getCount() > 0) {
//            for (int i = 0; i < cursor.getCount(); i++) {
//                cursor.moveToNext();
//
//                contactModel = new ContactModel();
//                contactModel.setID(cursor.getString(0));
//                contactModel.setRequestid(cursor.getString(1));
//                contactModel.setFirstName(cursor.getString(2));
//                contactModel.setLastName(cursor.getString(3));
//
//                contacts.add(contactModel);
//            }
//        }
//        cursor.close();
//        database.close();
//
//        return contacts;
//    }


    public void updateRecord(ContactModel1 contact) {
        database = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ID, contact.getID());
        contentValues.put(COLUMN_MUVI_ID, contact.getMUVIID());
        contentValues.put(COLUMN_DOWNLOADID, contact.getDOWNLOADID());
        contentValues.put(COLUMN_DOWNLOAD_PROGRESS, contact.getProgress());
        contentValues.put(COLUMN_DOWNLOAD_USERNAME, contact.getUSERNAME());
        contentValues.put(COLUMN_DOWNLOAD_USERNAME, contact.getUSERNAME());
        contentValues.put(COLUMN_DOWNLOAD_UNIQUEID, contact.getUniqueId());
        contentValues.put(COLUMN_DOWNLOAD_STATUS, contact.getDSTATUS());
        contentValues.put(COLUMN_POSTER, contact.getPoster());
        contentValues.put(COLUMN_MUVI_TOKEN, contact.getToken());
        contentValues.put(COLUMN_FILE_PATH, contact.getPath());
        contentValues.put(COLUMN_CONTENT_ID, contact.getContentid());
        contentValues.put(COLUMN_GENERE, contact.getGenere());
        contentValues.put(COLUMN_MUVIID, contact.getMuviid());
        contentValues.put(COLUMN_DURATION, contact.getDuration());




        database.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{contact.getID()});
        database.close();
    }

//    public void updateRecordAlternate(ContactModel contact) {
//        database = this.getReadableDatabase();
//        database.execSQL("update " + TABLE_NAME + " set " + COLUMN_FIRST_NAME + " = '" + contact.getFirstName() + "', " + COLUMN_LAST_NAME + " = '" + contact.getLastName() + "' where " + COLUMN_ID + " = '" + contact.getID() + "'");
//        database.close();
//    }

    public void deleteAllRecords() {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME, null, null);
        database.close();
    }

    public void deleteAllRecordsAlternate() {
        database = this.getReadableDatabase();
        database.execSQL("delete from " + TABLE_NAME);
        database.close();
    }

    public void deleteRecord(ContactModel1 contact) {
        database = this.getReadableDatabase();
        database.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{contact.getID()});
        database.close();
    }

   /* public void deleteRecordAlternate(ContactModel contact) {
        database = this.getReadableDatabase();
        database.execSQL("delete from " + TABLE_NAME + " where " + COLUMN_ID + " = '" + contact.getID() + "'");
        database.close();
    }*/

    public ArrayList<String> getAllTableName()
    {
        database = this.getReadableDatabase();
        ArrayList<String> allTableNames=new ArrayList<String>();
        Cursor cursor=database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null);
        if(cursor.getCount()>0)
        {
            for(int i=0;i<cursor.getCount();i++)
            {
                cursor.moveToNext();
                allTableNames.add(cursor.getString(cursor.getColumnIndex("name")));
            }
        }
        cursor.close();
        database.close();
        return allTableNames;
    }

}
