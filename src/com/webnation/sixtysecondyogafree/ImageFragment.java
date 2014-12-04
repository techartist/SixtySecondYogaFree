package com.webnation.sixtysecondyogafree;


import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/* this fragment displays the image for each pose */
public class ImageFragment extends SherlockFragment {
	private static int iYogaPoseID = 0;
	private ImageView ivPose;
	private final int DIFFICULTY_BASIC = 0;
	private final int DIFFICULTY_ADVANCED = 1;
	private final int DIFFICULTY_ADVANCED_AND_BASIC = 2;
	private int Difficulty = DIFFICULTY_BASIC;
	private static int Count = 1;
	private int PoseID = 0;
	private TextView tvYogaName;
	private TextView tvYogaText;
	private TextView tvTitle;
	
	private boolean mIsTablet = false;
	private Poses newPose = null;
	private OnPauseListener listener; 
	
	@Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
	  Log.d("ImageFragment", "onCreateView");
      View image =inflater.inflate(R.layout.image_view, container, false);
      ivPose = (ImageView)image.findViewById(R.id.yogapose);
      tvTitle = (TextView)image.findViewById(R.id.title);
      tvYogaName = (TextView)image.findViewById(R.id.yoganame);
      tvYogaText = (TextView)image.findViewById(R.id.yogatext);


      return(image);
    }
	

	/*
	 * Factory method for instantiating fragment
	 */
	public static ImageFragment newInstance(int count) {
		// Create a new fragment instance
		ImageFragment image = new ImageFragment();
		// Set the recipe index
		Bundle args = new Bundle();
	    args.putInt("Count", Count);
	    image.setArguments(args);
	    Count = count;
        return image;
	}
	
	/*
	 * Way to communicate with the activity
	 * http://www.vogella.com/articles/AndroidFragments/article.html
	 * YogaPosesActivity must implement this interface
	 * Sends message to pause timer to the nav fragment via activity
	 * 
	 * messages pass this way
	 * ImageFragment --> YogaPosesActivity --> NavigationFragment
	 */
	public interface OnPauseListener {
	      public void onPauseSelected();
	
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onAttach(android.app.Activity)
	 */
	@Override
    public void onAttach(Activity activity) {
      super.onAttach(activity);
      if (activity instanceof OnPauseListener) {
        listener = (OnPauseListener) activity;
      } else {
        throw new ClassCastException(activity.toString()
            + " must implemenet ImageFragment.OnPauseListener");
      }
    }
	
	@Override
	  public void onActivityCreated(Bundle savedInstanceState) {
		setRetainInstance(true);
		boolean dbExists = false;
		Log.d("ImageFragment", "onActivityCreated");
	    
		// This is a tablet if this view exists and is visible
		View poseDetails = getActivity().findViewById(R.id.yoga_text);
		mIsTablet = poseDetails != null && poseDetails.getVisibility() == View.VISIBLE;
		
		//Only put this listener if this is a phone.  Table has Detail Fragment
		if (!mIsTablet) { 
			/*
			 * When user touches the image, the text of how to do the 
			 * pose is revealed.  When the user touches the text
			 * the image is revealed. Also pauses/unpauses the clock if clock is ticking
			 */
			ivPose.setOnClickListener(new View.OnClickListener() { 
	        	   public void onClick(View v) {     		  
	        		   tvYogaText.setVisibility(View.VISIBLE);
 	        		  ivPose.setVisibility(View.INVISIBLE);
 	        		  listener.onPauseSelected();
	        	   }  
			  });
	           //basically sets visibility on and off.  Text Lives the 
			   //entire time in the activity
	          tvYogaText.setOnClickListener(new View.OnClickListener() { 
	        	       public void onClick(View v) {
	        	         	tvYogaText.setVisibility(View.INVISIBLE);
	        	         	ivPose.setVisibility(View.VISIBLE);
	        	         	listener.onPauseSelected();
	        	       }
	        	        	
	          });     
	       	
		}
		    
	    super.onActivityCreated(savedInstanceState);
	    
	}
	

	/*
	 * Getters and setters
	 * The count var pretty much drives the 
	 * entire application.
	 */
	public int getCount() {
		return Count;
	}
	
	public void setCount(int count) {
		Count = count;
	}
	
    /*
     * Get Image based on Poses Object passed in 
     * from YogaPosesActivity
     */
    public void SetImageViewByPose(int count, Poses newPose) {
    	this.newPose = newPose;  
    	Count = count;
    	Log.d("Pose Data", "Title=" + newPose.getTitle());
	    PoseID = (int)newPose.getId(); 

	    tvTitle.setText(newPose.getTitle());
	    //if tvYogaText view around, this is not a tablet
	    if (tvYogaText != null) 
	    	tvYogaText.setText(newPose.getText());
	    tvYogaName.setText(newPose.getYogaName());
    	
    	try { 
    	    
    	    int resourceID = getResources().getIdentifier(newPose.getGraphic(), "drawable", "com.webnation.sixtysecondyogafree");
    	    ivPose.setImageResource(resourceID);
    	    ivPose.setVisibility(View.VISIBLE);
    	    tvYogaText.setVisibility(View.INVISIBLE);
    	} 
    	catch (Exception e) {
  		   Log.d("Exception",e.toString());
  	   }
  
    }

    /* Mostly used for debugging so we can see when everything is called */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("ImageFragment", "onDestroy()");
	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d("ImageFragment", "onPause()");
	}



	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("ImageFragment", "onResume()");
	}



	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d("ImageFragment", "onStart()");
	}



	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("ImageFragment", "onStop()");
	}

    

}
