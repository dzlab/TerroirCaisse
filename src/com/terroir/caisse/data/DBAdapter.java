package com.terroir.caisse.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBAdapter {

	DatabaseHelper	DBHelper;
	Context			context;
	SQLiteDatabase	db;
	
	public static String TAG = DBAdapter.class.getSimpleName();
	
	public DBAdapter(Context context){
		this.context = context;
		DBHelper = new DatabaseHelper(context);
	}	
	
	public DBAdapter open(){
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		db.close();
	}
	
	public void drop() {
		db.execSQL("DROP TABLE IF EXISTS producers");
	}
	
	public void Truncate(){
		db.execSQL("DELETE FROM producers");
	}
	
	public long insert(Producer producer){
		ContentValues values = new ContentValues();
		values.put("raison_social", producer.raison_social);
		values.put("sous_type", producer.sous_type);
		values.put("address", producer.address);
		values.put("code_postal", producer.code_postal);
		values.put("ville", producer.ville);
		values.put("mail", producer.mail);
		values.put("telephone", producer.telephone);
		values.put("latitude", String.valueOf(producer.latitude));
		values.put("longitude", String.valueOf(producer.longitude));
		
		return db.insert("producers", null, values);		
	}
	
	
	public boolean supprimerProduit(long id){
		return db.delete("producers", "_id="+id, null)>0;
	}
	
	public List<Producer> query(){
		List<Producer> producers = new ArrayList<Producer>();
		Cursor cursor = db.query("producers", new String[]{"_id","raison_social","sous_type","address","code_postal","ville","mail","telephone","latitude","longitude"}, null, null, null, null, null);
		//cursor.moveToFirst();
		while(cursor.isAfterLast() == false) {
			Producer p = new Producer();
			try {				
				p.raison_social = cursor.getString(cursor.getColumnIndex("raison_social"));				
				p.sous_type = cursor.getString(cursor.getColumnIndex("sous_type"));				
				p.address = cursor.getString(cursor.getColumnIndex("address"));				
				p.code_postal = cursor.getString(cursor.getColumnIndex("code_postal"));				
				p.ville = cursor.getString(cursor.getColumnIndex("ville"));			
				p.mail = cursor.getString(cursor.getColumnIndex("mail"));			
				p.telephone = cursor.getString(cursor.getColumnIndex("telephone"));
				p.latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("latitude")));
				p.longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex("longitude")));
				
			}finally {
				producers.add(p);		
				cursor.moveToNext();
			}			
		}
		return producers;
	}

	public Map<String, Integer> categories() {
		Map<String, Integer> map = new HashMap<String, Integer>(); 
		Cursor cur = db.query("producers", new String[]{"sous_type"}, null, null, "sous_type", null, null);
		cur.moveToFirst();
        int index = cur.getColumnIndex("sous_type");
        while (cur.isAfterLast() == false) {
        	String category = cur.getString(index);
        	map.put(category, 0);
        	Log.i(TAG, "category: "+category);        	
        	cur.moveToNext();
        }  
        cur.close(); 
        return map;
	}
	
	public Cursor query(String category) {
		return db.query("producers", new String[]{"raison_social"}, "sous_type = '" + category + "'", null, "raison_social", null, null);
	}
	
	public int count(String key, String value) {
		Log.i(TAG, key+"="+value);
		//int count = -1;
		Cursor cursor = db.query("producers", new String[]{key}, key + " like '%" + value+"%'", null, null, null, null);
		return cursor.getCount();
/*		
		try {
			String SQL_STATEMENT = "SELECT COUNT(*) AS count FROM producers WHERE "+key+"=?";
			Log.i(TAG, SQL_STATEMENT);
			cursor = db.rawQuery(SQL_STATEMENT, new String[] { value });			
			if(cursor.moveToFirst()) {
				int index = cursor.getColumnIndex(key);
				count = cursor.getInt(index);
			}			 
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor!=null)
				cursor.close();
		}		     		
		return count;
*/
	}
}
