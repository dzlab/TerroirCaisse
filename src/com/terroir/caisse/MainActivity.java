package com.terroir.caisse;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity implements OnTabChangeListener {

	protected TabHost tabHost;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);		
		
		Resources ressources = getResources(); 
		tabHost = getTabHost(); 
		
		// Android tab
		Intent intentHome = new Intent().setClass(this, HomeActivity.class);
		TabSpec tabSpecHome = tabHost
			.newTabSpec("Home")
			.setIndicator("", ressources.getDrawable(R.drawable.icon_home_config))
			.setContent(intentHome);

		// Category tab
		Intent intentCategory = new Intent().setClass(this, CategoryActivity.class);
		TabSpec tabSpecCategory = tabHost
			.newTabSpec("Category")
			.setIndicator("", ressources.getDrawable(R.drawable.icon_category_config))
			.setContent(intentCategory);
		
		// Favorite tab
		Intent intentFavoris = new Intent().setClass(this, FavorisActivity.class);
		TabSpec tabSpecFavoris = tabHost
			.newTabSpec("Favoris")			
			.setIndicator("", ressources.getDrawable(R.drawable.icon_favoris_config))
			.setContent(intentFavoris);
				
		// add all tabs 
		tabHost.addTab(tabSpecHome);		
		tabHost.addTab(tabSpecCategory);
		tabHost.addTab(tabSpecFavoris);
		
		//set Windows tab as default (zero based)
		tabHost.setCurrentTab(0);
		setSelectedTabColor();
	}

	private void setSelectedTabColor() {
		//int color_selected = Color.parseColor("#b95e41");
		int color_selected = getResources().getColor(R.color.tab_selected);
		//int color_unselected=Color.parseColor("#aaa97b");
		int color_unselected=getResources().getColor(R.color.tab_unselected);
        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) {  
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(color_unselected);              
        }  
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(color_selected);   
        
        tabHost.setOnTabChangedListener(this);
    }
	
	@Override  
    public void onTabChanged(String tabId) {  
        setSelectedTabColor(); 
    } 
}