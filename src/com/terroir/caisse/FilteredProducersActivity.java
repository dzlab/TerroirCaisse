package com.terroir.caisse;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.terroir.caisse.adapter.HomeAdapter;
import com.terroir.caisse.data.Producer;

public class FilteredProducersActivity extends Activity {
	
	protected HomeAdapter adapter;
	protected ListView list;
	protected Context context;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        context = this;
        list = (ListView) FilteredProducersActivity.this.findViewById(R.id.lstProducers);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView l, View v, int position, long id) {
				Producer producer = (Producer) adapter.getItem(position);    			   
				Intent wake = new Intent(FilteredProducersActivity.this, CardActivity.class);				
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
				Intent wake = new Intent(FilteredProducersActivity.this, HomeMapActivity.class);				
				startActivity(wake);
			}
		});
        ImageView infoButton = (ImageView) findViewById(R.id.btnHomeInfo);
		infoButton.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {	
				LayoutInflater factory = FilteredProducersActivity.this.getLayoutInflater();
                final View alertDialogView = factory.inflate(R.layout.dialog_bdrt, null);
                AlertDialog.Builder adb = new AlertDialog.Builder(FilteredProducersActivity.this); 
                adb.setView(alertDialogView);
                adb.setTitle("Information fournis par la BDRT"); 
                ImageView logo = (ImageView) alertDialogView.findViewById(R.id.logo_bdrt);
                logo.setImageResource(R.drawable.bdrt_logotype_noir);	                
                adb.show();
			}
		});
        // get the category for filtering from the initial activity
        try {
        	Intent intent = getIntent();
        	String root = intent.getStringExtra("sous_type");
        	if(root != null && !root.equals("")) {
        		List<Producer> producers = new ArrayList<Producer>();
        		for(String category: HomeActivity.categories.keySet()) {
        			if(category!=null && category.toUpperCase().contains(root)) {
        				for(Producer p:HomeActivity.categories.get(category)) {
        					producers.add(p);
        				}
        			}
        		}
            	adapter = new HomeAdapter(context, producers);
            	list.setAdapter(adapter);	
        	}
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
}
