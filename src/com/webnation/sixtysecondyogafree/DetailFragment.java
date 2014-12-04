package com.webnation.sixtysecondyogafree;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;

public class DetailFragment extends SherlockFragment {
	
	private TextView tvYogaText;
	public DataBaseHelper myDbHelper;
	int PoseID = 0;
	private int Count = 1;
	Poses nextPose = null;
	
	public static DetailFragment newInstance(int count) {
		// Create a new fragment instance
		DetailFragment detail = new DetailFragment();
		// Set the recipe index
		detail.setCount(count);
        return detail;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myDbHelper = new DataBaseHelper(getActivity());
		Log.d("DetailFragment", "onCreateView");// TODO Auto-generated method stub

		View yogatext =inflater.inflate(R.layout.detail, container, false);
		tvYogaText = (TextView)yogatext.findViewById(R.id.yoga_text);
		Log.d("Calling Activity",getActivity().toString());
		return yogatext;
	}
	
	public int getCount() {
		return Count;
	}
	
	public void setCount(int count) {
		Count = count;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		Log.d("DetailFragment", "onActivityCreated");
		
	}
	
	//Get the Yoga Text.
    public void SetYogaTextByPose(int Count, Poses nextPose) {
    	this.nextPose = nextPose;
    	Log.d("We're Here", "SetYogaTextByCount");
    	   
        PoseID = (int)nextPose.getId(); 
    	tvYogaText.setText(nextPose.getText());
    	
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
