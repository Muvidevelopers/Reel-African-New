package com.release.reelAfrican.activity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.release.reelAfrican.R;
import com.release.reelAfrican.adapter.MyDownloadAdapter;
import com.release.reelAfrican.model.ContactModel1;
import com.release.reelAfrican.utils.DBHelper;
import com.release.reelAfrican.utils.ProgressBarHandler;
import com.release.reelAfrican.utils.Util;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


public class MyDownloads extends AppCompatActivity {



    //public static ProgressDialog  pDialog;
    Context context;
    ListView list;
    TextView noDataTextView;
    RelativeLayout nodata;
    /*ArrayList<Detail> mylist;
    ArrayList<Developer> jj;*/
    MyDownloadAdapter adapter;
    List<String[]> allElements;
   // CSVReader readers = null;
    String[] nextLine=null;
    SharedPreferences pref;
    String emailIdStr = "";
    ProgressDialog progressDialog;
    DBHelper dbHelper;

    ArrayList<ContactModel1> databaseupdate;
    DownloadManager downloadManager;
    public boolean downloading;
   // MydownloadModel mydownloadModel;
    static String path,filename,_filename,token,title,poster,genre,duration,rdate,movieid,user,uniqid;
    ArrayList<ContactModel1> download;
    ProgressBarHandler pDialog;
    ArrayList<String> SubTitleName = new ArrayList<>();
    ArrayList<String> SubTitlePath = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydownload);
        dbHelper=new DBHelper(MyDownloads.this);


        Toolbar mActionBarToolbar= (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mActionBarToolbar.setTitle(Util.getTextofLanguage(MyDownloads.this,Util.MY_DOWNLOAD,Util.DEFAULT_MY_DOWNLOAD));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();

                finish();
            }
        });

        list= (ListView)findViewById(R.id.listView);
        nodata= (RelativeLayout) findViewById(R.id.noData);
        noDataTextView= (TextView) findViewById(R.id.noDataTextView);
        pref = getSharedPreferences(Util.LOGIN_PREF, 0);
        if (pref!=null){
            emailIdStr= pref.getString("PREFS_LOGIN_EMAIL_ID_KEY", null);


        }else {
            emailIdStr = "";


        }


//        databaseupdate=dbHelper.getContactt(emailIdStr,2);
//        if (databaseupdate.size()>0) {
//
//
//            Toast.makeText(getApplicationContext(),"Downloading",Toast.LENGTH_LONG).show();
//            int i = 0;
//
//            for (i = 0; i < databaseupdate.size(); i++) {
//
//                checkDownLoadStatusFromDownloadManager1(databaseupdate.get(i));
//
//
//            }
//        }else {
//
//
//        }


        download=dbHelper.getContactt(emailIdStr,1);
        //download=dbHelper.getDownloadcontent(emailIdStr);
        if(download.size()>0) {
            adapter = new MyDownloadAdapter(MyDownloads.this, android.R.layout.simple_dropdown_item_1line, download);
            list.setAdapter(adapter);
        }else {
            nodata.setVisibility(View.VISIBLE);
            noDataTextView.setText(Util.getTextofLanguage(MyDownloads.this,Util.NO_CONTENT,Util.DEFAULT_NO_CONTENT));
        }



       /* ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.listitem,mylist);
        list.setAdapter(adapter);*/
       list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                SubTitleName.clear();
                SubTitlePath.clear();

//                try {
//
//
//
//                    String path1 = Environment.getExternalStorageDirectory() + "/Android/data/"+getApplicationContext().getPackageName().trim()+"/WITHDRM/" + download.get(position).getMUVIID().trim() + "-1." + "mlv";
//                    File file = new File(path1);
//                    if (file != null && file.exists()) {
//
//                        file.delete();
//
//                    }
//
//                }catch (Exception e){
//
//
//
//                }

                pDialog = new ProgressBarHandler(MyDownloads.this);
                pDialog.show();

                new Thread(new Runnable(){
                    public void run(){

                        SQLiteDatabase DB = MyDownloads.this.openOrCreateDatabase("DOWNLOADMANAGER_ONDEMAND.db", MODE_PRIVATE, null);
                        Cursor cursor = DB.rawQuery("SELECT LANGUAGE,PATH FROM SUBTITLE_ONDEMAND WHERE UID = '"+download.get(position).getUniqueId()+"'", null);
                        int count = cursor.getCount();

                        if (count > 0) {
                            if (cursor.moveToFirst()) {
                                do {
                                    SubTitleName.add(cursor.getString(0).trim());
                                    SubTitlePath.add(cursor.getString(1).trim());


                                    Log.v("BIBHU3","SubTitleName============"+cursor.getString(0).trim());
                                    Log.v("BIBHU3","SubTitlePath============"+cursor.getString(1).trim());

                                } while (cursor.moveToNext());
                            }
                        }


                        final String pathh=download.get(position).getPath();
                        final String titles=download.get(position).getMUVIID();
                        final String gen=download.get(position).getGenere();
                        final String tok=download.get(position).getToken();
                        final String contentid=download.get(position).getContentid();
                        final String muviid=download.get(position).getMuviid();
                        String post=download.get(position).getPoster();
                        final String vidduration=download.get(position).getDuration();
                        final String filename=pathh.substring(pathh.lastIndexOf("/") + 1);


                        try {


                            sleep(1200);


                            runOnUiThread(new Runnable() {
                                //
                                @Override
                                public void run() {

                                    Intent in=new Intent(MyDownloads.this,MarlinBroadbandExample.class);
                                    Log.v("SUBHA", "PATH" + pathh);


                                    in.putExtra("SubTitleName", SubTitleName);
                                    in.putExtra("SubTitlePath", SubTitlePath);

                                    in.putExtra("FILE", pathh);
                                    in.putExtra("Title", titles);
                                    //in.putExtra("GENRE", gen);
                                    in.putExtra("TOK", tok);

                                    in.putExtra("contid", contentid);
                                    in.putExtra("gen", gen);
                                    in.putExtra("muvid", muviid);
                                    in.putExtra("vid", vidduration);
                                    in.putExtra("FNAME", filename);
                                    //
                                    startActivity(in);



                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {


                                            if (pDialog != null && pDialog.isShowing()) {
                                                pDialog.hide();
                                                pDialog = null;
                                            }


                                        }
                                    });


                                }
                            });


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();


            }
        });



    }

    public void visible(){

        if(download.size()>0) {
            adapter = new MyDownloadAdapter(MyDownloads.this, android.R.layout.simple_dropdown_item_1line, download);
            list.setAdapter(adapter);

        }else {

            nodata.setVisibility(View.VISIBLE);
            noDataTextView.setText(Util.getTextofLanguage(MyDownloads.this,Util.NO_CONTENT,Util.DEFAULT_NO_CONTENT));
        }


    }

    public void checkDownLoadStatusFromDownloadManager1(final ContactModel1 model) {


        if (model.getDOWNLOADID() != 0) {


            new Thread(new Runnable() {

                @Override
                public void run() {

                    downloading = true;
                    //  Util.downloadprogress=0;
                    int bytes_downloaded = 0;
                    int bytes_total = 0;
                    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    while (downloading) {


                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(model.getDOWNLOADID()); //filter by id which you have receieved when reqesting download from download manager
                        Cursor cursor = downloadManager.query(q);


                        if (cursor != null && cursor.getCount() > 0) {
                            if (cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                int status = cursor.getInt(columnIndex);
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {

                                    model.setDSTATUS(1);
                                    dbHelper.updateRecord(model);

                                } else if (status == DownloadManager.STATUS_FAILED) {
                                    // 1. process for download fail.
                                    model.setDSTATUS(0);

                                } else if ((status == DownloadManager.STATUS_PAUSED) ||
                                        (status == DownloadManager.STATUS_RUNNING)) {
                                    model.setDSTATUS(2);

                                } else if (status == DownloadManager.STATUS_PENDING) {
                                    //Not handling now
                                }
                                int sizeIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                                int downloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                                long size = cursor.getInt(sizeIndex);
                                long downloaded = cursor.getInt(downloadedIndex);
                                double progress = 0.0;
                                if (size != -1) progress = downloaded * 100.0 / size;
                                // At this point you have the progress as a percentage.
                                model.setProgress((int) progress);
                                //Util.downloadprogress=(int) progress;

                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                    //downloading = false;
//                                download_layout.setVisibility(View.GONE);
                                    //writefilepath();
                                    //String path=Environment.getExternalStorageDirectory() + "/WITHDRM/"+fname;

//                                    String path1 = Environment.getExternalStorageDirectory() + "/Android/data/"+getApplicationContext().getPackageName().trim()+"/WITHDRM/" + Util.dataModel.getVideoTitle().trim() + "-1." + "mlv";
//                                    File file = new File(path1);
//                                    if (file != null && file.exists()) {
//
//                                        file.delete();
//
//                                    }

                                }


                            }
                        } else {
                            // model.setDSTATUS(3);
                        }


//


                        // Log.d(Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor));
                        cursor.close();
                    }


                }
            }).start();


        }

    }
}
