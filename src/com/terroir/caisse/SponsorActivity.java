package com.terroir.caisse;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SponsorActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
	}
}
