package com.webnation.sixtysecondyogafree;


import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;
import android.util.Log;

public class DetailActivity extends SherlockFragmentActivity {

	public static final String EXTRA_COUNT = "Count";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	// Get the recipe index
    	int Count = getIntent().getIntExtra(EXTRA_COUNT, 1);
    	
    	// Show the new fragment (full screen)
    	DetailFragment details = DetailFragment.newInstance(Count);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
    	
    }

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("DetailActivity", "onPause");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("DetailActivity", "onStop");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("DetailActivity", "onDestroy");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("DetailActivity", "onResume");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("DetailActivity", "onStart");
	}
    
}