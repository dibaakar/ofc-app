package com.ofcampus.databasehelper;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.Util.JobDataReturnFor;
import com.ofcampus.model.JobDetails;

public class JOBListTable {

	public static String TABLENAME= "joblist";
	
	public static String POSTID="postId";
	public static String SUBJECT="subject";
	public static String CONTENT="content";
	public static String POSTEDON="postedOn";
	public static String POSTUSERID="id";
	public static String POSTUSERNAME="name";
	public static String POSTUSERIMAGE="image";
	public static String ISSYNCDATA="issyncdata";
	
	
	 private static OfCampusDBHelper dbHelper = null;
	 private static JOBListTable mInstance;
	 private SQLiteDatabase sampleDB;
	 
	public static synchronized JOBListTable getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new JOBListTable(context);
		}
		return mInstance;
	}

	public JOBListTable(Context context) {
		if (dbHelper == null) {
			dbHelper = ((OfCampusApplication) context.getApplicationContext()).DB_HELPER;
		}
	}
	
	

	
	public void inserJobData(ArrayList<JobDetails> Jobs, int size) { 

		try {
			sampleDB = dbHelper.getDB();
			sampleDB.beginTransaction();
			String sql = "Insert or Replace into " + TABLENAME + " (" + POSTID
					+ "," + SUBJECT + "," + CONTENT + "," + POSTEDON + ","
					+ POSTUSERID + "," + POSTUSERNAME + "," + POSTUSERIMAGE
					+ "," + ISSYNCDATA + ") values(?,?,?,?,?,?,?,?)";
			SQLiteStatement insert = sampleDB.compileStatement(sql);

			if (Jobs.size() > size) {
				for (int i = 0; i < size; i++) {
					JobDetails mJob = Jobs.get(i);
					insert.clearBindings();
					insert.bindLong(1, Integer.parseInt(mJob.getPostid()));
					insert.bindString(2, mJob.getSubject());
					insert.bindString(3, mJob.getContent());
					insert.bindString(4, mJob.getPostedon());
					insert.bindString(5, mJob.getId());
					insert.bindString(6, mJob.getName());
					insert.bindString(7, mJob.getImage());
					insert.bindString(8, mJob.getISSyncData());
					insert.execute();
				}
				sampleDB.setTransactionSuccessful();
			}else {
				for (int i = 0; i < Jobs.size(); i++) {
					JobDetails mJob = Jobs.get(i);
					insert.clearBindings();
					insert.bindLong(1, Integer.parseInt(mJob.getPostid()));
					insert.bindString(2, mJob.getSubject());
					insert.bindString(3, mJob.getContent());
					insert.bindString(4, mJob.getPostedon());
					insert.bindString(5, mJob.getId());
					insert.bindString(6, mJob.getName());
					insert.bindString(7, mJob.getImage());
					insert.bindString(8, mJob.getISSyncData());
					insert.execute();
				}
				sampleDB.setTransactionSuccessful();
			}
			Log.e("TAG", "Done");

		} catch (Exception e) {
			Log.e("XML:", e.toString());
		} finally {
			sampleDB.endTransaction();
		}
	}
	
	
	public void inserJobData(ArrayList<JobDetails> Jobs) {

		try {
			sampleDB = dbHelper.getDB();
			sampleDB.beginTransaction();
			String sql = "Insert or Replace into " + TABLENAME + " (" + POSTID
					+ "," + SUBJECT + "," + CONTENT + "," + POSTEDON + ","
					+ POSTUSERID + "," + POSTUSERNAME + "," + POSTUSERIMAGE
					+ "," + ISSYNCDATA + ") values(?,?,?,?,?,?,?,?)";
			SQLiteStatement insert = sampleDB.compileStatement(sql);

			for (int i = 0; i < Jobs.size(); i++) {
				JobDetails mJob = Jobs.get(i);
				insert.clearBindings();
				insert.bindLong(1, Integer.parseInt(mJob.getPostid()));
				insert.bindString(2, mJob.getSubject());
				insert.bindString(3, mJob.getContent());
				insert.bindString(4, mJob.getPostedon());
				insert.bindString(5, mJob.getId());
				insert.bindString(6, mJob.getName());
				insert.bindString(7, mJob.getImage());
				insert.bindString(8, mJob.getISSyncData());
				insert.execute();
			}
			sampleDB.setTransactionSuccessful();
			Log.e("TAG", "Done");

		} catch (Exception e) {
			Log.e("XML:", e.toString());
		} finally {
			sampleDB.endTransaction();
		}
	}
	
	public ArrayList<JobDetails> fatchJobData(JobDataReturnFor mJobDataReturnFor) {
		ArrayList<JobDetails> jobs = null;
		String sql = "";
		if (mJobDataReturnFor==JobDataReturnFor.syncdata) {
			sql = "select * from joblist where issyncdata like 1 order by postid desc";
		}else{
			sql = "select * from joblist where issyncdata not like 1 order by postid desc";
		}
		Cursor mCursor=null;
		try {
			mCursor = dbHelper.getDB().rawQuery(sql, null);
			if (mCursor != null && mCursor.getCount() >= 1) {
				jobs = GetJobData(mCursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		curcorClose(mCursor);
		return jobs;
	}
	
	
	
	private ArrayList<JobDetails> GetJobData(Cursor mCursor){
		ArrayList<JobDetails> jobs=new ArrayList<JobDetails>();
		if (mCursor.moveToFirst()) {
			do {
				JobDetails mDetails = new JobDetails();
				mDetails.setPostid(""+mCursor.getInt(mCursor.getColumnIndex(POSTID)));
				mDetails.setSubject(mCursor.getString(mCursor.getColumnIndex(SUBJECT)));
				mDetails.setContent(mCursor.getString(mCursor.getColumnIndex(CONTENT)));
				mDetails.setPostedon(mCursor.getString(mCursor.getColumnIndex(POSTEDON)));
				mDetails.setId(mCursor.getString(mCursor.getColumnIndex(POSTID)));
				mDetails.setName(mCursor.getString(mCursor.getColumnIndex(POSTUSERNAME)));
				mDetails.setImage(mCursor.getString(mCursor.getColumnIndex(POSTUSERIMAGE)));
				mDetails.setISSyncData(mCursor.getString(mCursor.getColumnIndex(ISSYNCDATA)));
				jobs.add(mDetails);
			} while (mCursor.moveToNext());
		}
		return jobs;
	}
	
	public boolean deleteoutDatedPost(int count) {
		boolean success = false;
		String sql = "";
		try {
			sql = "delete from joblist where (postId < (((select min(postId) from joblist) +'"+count+"')))";
			success = dbHelper.getDB().rawQuery(sql, null).moveToFirst();
			sql = "update joblist set issyncdata='0' where issyncdata like 1";
			success = dbHelper.getDB().rawQuery(sql, null).moveToFirst();
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}
	
	
	private void curcorClose(Cursor cursor){
		try {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/****Query List*****/
//	delete from joblist where postId < (((select min(postId) from joblist) +2))
//	select * from joblist where issyncdata like 1
//	select * from joblist where issyncdata not like 1
	
}
