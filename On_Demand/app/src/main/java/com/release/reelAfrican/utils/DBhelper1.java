package com.release.reelAfrican.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.release.reelAfrican.model.SubtitleModel;

import java.util.ArrayList;

/**
 * Created by Nikunj on 27-08-2015.
 */
public class DBhelper1 extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SUBTITLE_MONETISER.db";
    public static final String TABLE_NAME = "SUBTITLE_MONETISER";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_UID = "UID";
    public static final String COLUMN_LANGUAGE = "LANGUAGE";
    public static final String COLUMN_PATH = "PATH";







    private static final String CREATE_SOL = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_UID + " VARCHAR, " +
            COLUMN_LANGUAGE + " VARCHAR, " +

                    COLUMN_PATH + " VARCHAR)";

    private SQLiteDatabase database;

    public DBhelper1(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SOL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertRecord(SubtitleModel contact) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_UID, contact.getUID());
        contentValues.put(COLUMN_LANGUAGE, contact.getLanguage());
        contentValues.put(COLUMN_PATH, contact.getPath());
        database.insert(TABLE_NAME, null, contentValues);
        database.close();
        return false;
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

    public SubtitleModel getContact(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;


        cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID,COLUMN_UID,
                        COLUMN_PATH,




                }, COLUMN_UID + "=?",
                new String[]{id}, null, null, null, null);

        SubtitleModel contactModel=null;

        if (cursor != null && cursor.moveToFirst()) {


            contactModel = new SubtitleModel();
            contactModel.setID(cursor.getString(0));
            contactModel.setUID(cursor.getString(1));
            contactModel.setPath(cursor.getString(2));


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
                resp = "Name:-" + res.getString(res.getColumnIndex(COLUMN_UID))
                        + "\n" + "Email" + res.getString(res.getColumnIndex(COLUMN_PATH));
                res.moveToNext();
            }
        }
        return resp;
    }




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




    public ArrayList<SubtitleModel> getPath(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;





        cursor = db.query(TABLE_NAME, new String[]{COLUMN_PATH,




                }, COLUMN_UID + "=?",
                new String[]{id}, null, null, null, null);

        SubtitleModel contactModel=null;

        ArrayList<SubtitleModel> contacts = new ArrayList<SubtitleModel>();

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                contactModel = new SubtitleModel();
//                contactModel.setID(cursor.getString(0));
//                contactModel.setUID(cursor.getString(1));
//                contactModel.setLanguage(cursor.getString(2));
                contactModel.setPath(cursor.getString(0));




                contacts.add(contactModel);
            }
        }
        cursor.close();
//        database.close();

        return contacts;
    }



    public ArrayList<SubtitleModel> getLanguage(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;





        cursor = db.query(TABLE_NAME, new String[]{COLUMN_LANGUAGE,




                }, COLUMN_UID + "=?",
                new String[]{id}, null, null, null, null);

        SubtitleModel contactModel=null;

        ArrayList<SubtitleModel> contacts = new ArrayList<SubtitleModel>();

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                contactModel = new SubtitleModel();
//                contactModel.setID(cursor.getString(0));
//                contactModel.setUID(cursor.getString(1));
//                contactModel.setLanguage(cursor.getString(2));
                contactModel.setLanguage(cursor.getString(0));




                contacts.add(contactModel);
            }
        }
        cursor.close();
//        database.close();

        return contacts;
    }





    public ArrayList<SubtitleModel> getAllRecords() {
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        ArrayList<SubtitleModel> contacts = new ArrayList<SubtitleModel>();
        SubtitleModel contactModel;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();

                contactModel = new SubtitleModel();

                contactModel.setID(cursor.getString(0));
                contactModel.setUID(cursor.getString(1));
                contactModel.setLanguage(cursor.getString(2));
                contactModel.setPath(cursor.getString(3));






                contacts.add(contactModel);
            }
        }
        cursor.close();
        database.close();

        return contacts;
    }



}
