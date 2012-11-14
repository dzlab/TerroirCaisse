package com.terroir.caisse.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper{

	Context			context;
	public static final String DATABASE_NAME   = "TerroirCaisse"; 
	public static final String TABLE_PRODUCERS = "producers";
	public static final String TABLE_BOOKMARKS = "bookmarks";
	
	public DatabaseHelper(Context context) {
		//super(context, "producers", null, 1);
		super(context, DATABASE_NAME, null, 1);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table producers (_id integer primary key autoincrement, "
				+ "raison_social text, sous_type text, " 
				+ "address text, code_postal text,"
				+ "ville text, mail text,"
				+ "telephone text, latitude text,"
				+ "longitude text"
				+ ");");			
		// Create book-mark table
		db.execSQL("create table "+TABLE_BOOKMARKS+" (_id integer primary key autoincrement, "
				+ "raison_social text, sous_type text, " 
				+ "address text, code_postal text,"
				+ "ville text, mail text,"
				+ "telephone text, latitude text,"
				+ "longitude text"
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Toast.makeText(context, "Mise à jour de la Base de données version "+oldVersion+" vers "+newVersion, Toast.LENGTH_SHORT).show();
		db.execSQL("DROP TABLE IF EXISTS producers");	
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_BOOKMARKS);
		onCreate(db);
	}
	
}
