package com.terroir.caisse;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.terroir.caisse.adapter.HomeAdapter;
import com.terroir.caisse.data.DBAdapter;
import com.terroir.caisse.data.Producer;


public class FavorisActivity extends Activity {
	
	protected List<Producer> producers;
	
	
	protected HomeAdapter adapter;
	protected ListView list;
	protected Context context;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;
        
        DBAdapter db = new DBAdapter(context);
        try{
        	db.open();
            producers = db.bookmarked();	
        }catch(Exception e) {
        	e.printStackTrace();
        }finally {
        	db.close();	
        }             
        
        list = (ListView) FavorisActivity.this.findViewById(R.id.lstProducers);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView l, View v, int position, long id) {
				Producer producer = (Producer) adapter.getItem(position);    			   
				Intent wake = new Intent(FavorisActivity.this, CardActivity.class);				
			    wake.putExtra("raison_social", producer.raison_social);
			    wake.putExtra("address", producer.address);
			    wake.putExtra("code_postal", producer.code_postal);
			    wake.putExtra("distance", String.valueOf(producer.distance));
			    wake.putExtra("latitude", producer.latitude);
			    wake.putExtra("longitude", producer.longitude);
			    wake.putExtra("mail", producer.mail);
			    wake.putExtra("sous_type", producer.sous_type);
			    wake.putExtra("telephone", producer.telephone);
			    wake.putExtra("ville", producer.ville);
			    startActivity(wake);			    			   
			}
		});
        ImageButton btnMap = (ImageButton) findViewById(R.id.btnHomeMap);
        btnMap.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent wake = new Intent(FavorisActivity.this, HomeMapActivity.class);				
				startActivity(wake);
			}
		});
        // get the category for filtering from the initial activity
        adapter = new HomeAdapter(context, producers);
    	list.setAdapter(adapter);
    }
}