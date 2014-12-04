package com.webnation.sixtysecondyogafree;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/*
 * This class inflates and governs the navigation 
 * fragment containing the back, forward, start, stop and pause
 * buttons.  Inflates /res/layout/navigation.xml
 */
public class NavigationFragment extends SherlockFragment {
    
	//CountDownTimerWithPause.java
	private CountDownTimerWithPause cdt = null;
	
	//flag on whether timer is paused
	private boolean btnIsPaused = false;
	
	//flag to tell if Timer is Actively going
	private boolean bClockIsTicking = false;
	
	//is Automatic advance of poses set to on or off
	private boolean bAutoPlayIsOn = false;
	
	//flag to tell if pose is to be repeated
	private int bIsRepeatedPose = 0;
	
	//flag used to tell if poses was already  repeated
	private boolean bPoseAlreadyRepeated = false;
	
	//drawn on top of the buttons
	private Drawable stopBtn = null;
	private Drawable pauseBtn = null;
	private Drawable playBtn = null;
	
	//displays the timer
	private TextView tvClock;
	
	//naviagtion buttons
	private Button btnStart;
	private Button btnPause;
	private Button btnForward;
	private Button btnBack;
	
	//sounds a gong inbetween poses
    private MediaPlayer bell;
    
    //The count keeps the place holder for where we are in the call list
    private int Count = 1;
    
    //object that contains all information about the pose (Poses.java)
    private Poses newPose = null;
    
    //interface to the YogaPosesActivity activity class
    private OnChangePictureSelectedListener listener;
    
    private Vibrator myVib;
	
	@Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
      View nav =inflater.inflate(R.layout.navigation, container, false);
      //set up all the textViews and Buttons
      tvClock = (TextView)nav.findViewById(R.id.ticker);
      btnStart = (Button)nav.findViewById(R.id.start);
      btnPause = (Button)nav.findViewById(R.id.pause);
      btnForward = (Button)nav.findViewById(R.id.next);
      btnBack = (Button)nav.findViewById(R.id.back);
      btnPause.setEnabled(false);
      btnBack.setEnabled(false);
      stopBtn = getResources().getDrawable(R.drawable.ic_action_playback_stop);
      pauseBtn = getResources().getDrawable(R.drawable.ic_action_playback_pause);
      playBtn = getResources().getDrawable(R.drawable.ic_action_playback_play);
      btnPause.setText("Pause ");
      

      return(nav);
    }
	
	/*
	 * Way to communicate with the activity
	 * http://www.vogella.com/articles/AndroidFragments/article.html
	 * YogaPosesActivity must implement this interface
	 * 
	 * messages pass this way
	 * NavigationFragment --> YogaPosesActivity --> ImageFragment
	 */
	public interface OnChangePictureSelectedListener {
	      public void onBackItemSelected();
	
	      public void onForwardItemSelected();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onAttach(android.app.Activity)
	 */
	@Override
    public void onAttach(Activity activity) {
      super.onAttach(activity);
      if (activity instanceof OnChangePictureSelectedListener) {
        listener = (OnChangePictureSelectedListener) activity;
      } else {
        throw new ClassCastException(activity.toString()
            + " must implemenet NavigationFragment.OnChangePictureSelectedListener");
      }
    }
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 * Sets up all the onClick events for this fragment
	 */
	@Override
	  public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    try {
	    	
			int resourceID = getResources().getIdentifier("bell01", "raw", "com.webnation.sixtysecondyogafree");
			
			bell = MediaPlayer.create(getActivity(), resourceID);
			myVib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		} catch (Exception e) {
			Log.d("We're Here", e.toString());
		}

	    setRetainInstance(true);
	    setHasOptionsMenu(true);
	    
	    /*
	     * Sets up countdown timer and all its behavior
	     */
	    cdt = new CountDownTimerWithPause(60000,150,true) { 
        	@Override
        	public void onTick(long millisUntiFinished) { 
        		//will only display whole numbers in display
        		tvClock.setText("Remaining: " + millisUntiFinished/1000 + " sec");
        		
        	}
        	
        	//at the end of the interval, perform these tasks.
        	@Override
        	public void onFinish() {
        		
        		tvClock.setText("");
        		bell.start();
        		myVib.vibrate(1500);
        		if (bAutoPlayIsOn) { 
        			if ((bIsRepeatedPose>0) && (!bPoseAlreadyRepeated)) { 
        					tvClock.setText("Repeat pose on other side");
        					bPoseAlreadyRepeated = true;
        			}
        				
        			else if ((bIsRepeatedPose>0) && (bPoseAlreadyRepeated)) { 
        				   bPoseAlreadyRepeated = false;
        				   listener.onForwardItemSelected();
             			   tvClock.setText("Get into next pose...");
        			}
        			
        			else { 
        			  bPoseAlreadyRepeated = false;
        			  listener.onForwardItemSelected();
        			  tvClock.setText("Get into next pose...");
        			}
        			//want to sleep 15 seconds between poses so 
        			//user can get into new pose. 
        			//automatically reset clock if 
        			//poses are set to automatic
        			new Thread() {
        		        public void run() {
        		                try {
        		                	Thread.sleep(15000);
        		                    getActivity().runOnUiThread(new Runnable() {

        		                        @Override
        		                        public void run() {
        		                        	//start over
                                            cdt.ResetClocktoMaxTime(60000);
                                            bell.start();
                                            myVib.vibrate(1500);
                                            cdt.resume();
        		                        }
        		                    });
        		                } catch (InterruptedException e) {
        		                    e.printStackTrace();
        		                    Log.d("Timer", e.toString());
        		                }
        		                catch (Exception e) {
        	        				Log.d("Timer", e.toString());
        	        			}
        		         }
        		        
        		    }.start();
        		    bell.start();
        		    myVib.vibrate(1500);
        		    
        		}
        		//not automatic, end timer, reset buttons
        		else { 
        		  tvClock.setText("");
            	  btnStart.setText("Start ");
        		  bClockIsTicking = false;
    			  btnStart.setCompoundDrawablesWithIntrinsicBounds(null, playBtn, null, null);
        		}
        		
        	}
        };  //end of countdown timer
        
        /******* OnClickListeners *********************/
        btnStart.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		tvClock.setText("Remaining: 60 sec");
        		StartTicker();        	 
            }
        });
        
        btnPause.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        	   Pause();
            }
        });
        
        btnForward.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        	    GoForward();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        	    GoBack();
            }
        });
        /******* end of OnClick listeners *********************/
	  }
	  //end of onActivityCreated
	  //////////////////////////////////
	
	
	/* 
	 * can't go backwards if the count is 1 
	 * so disable Back Button 
	 */
	public void SetBackButton(int Count) { 
		this.Count = Count;
		if (Count==1) { 
			btnBack.setEnabled(false);
		}
		else { 
			btnBack.setEnabled(true);
		}
		
	}
	
	/* 
	 * as the name implies
	 * can be called by the activity
	 */
	public void Pause() { 
		if (!btnIsPaused) { 
		  cdt.pause();    
          btnIsPaused = true;
          btnPause.setText("Resume");
		}
		else { 
		  cdt.resume();
		  btnIsPaused = false;
		  btnPause.setText("Pause ");
		}
	}
	
	
	/* 
	 * Starts the ticking of the timer
	 * puts start and play buttons in 
	 * proper configuration
	 * could be called 
	 * from DetailActivity or YogaPosesActivity
	 * sends in isAutoMatic - if Clock is Automatic or Manual
	 */
	public void StartTicker() { 	   
		 cdt.ResetClocktoMaxTime(60000);
		 if (!bClockIsTicking) { 
			 btnPause.setEnabled(true);
           cdt.create();
           bClockIsTicking = true;
           btnStart.setText(" Stop ");
           btnStart.setCompoundDrawablesWithIntrinsicBounds(null, stopBtn, null, null);
           
		 }
		 else { 
			 CancelTicker();		
		 }
	}
	
	/* 
	 * as the name implies
	 * puts the play and pause
	 * button in proper configuration
	 */
	public void CancelTicker() { 
		cdt.cancel();
		tvClock.setText("");
		btnPause.setEnabled(false);
		bClockIsTicking = false;
		btnStart.setText("Start ");
		btnStart.setCompoundDrawablesWithIntrinsicBounds(null, playBtn, null, null);
		btnPause.setText("Pause ");
	}
	
	/*
	 * Advances the pose forward
	 */
	public void GoForward() { 
		  //send data to Activity
		  listener.onForwardItemSelected();
		  CancelTicker();
		  
	}
	  
	/*
	 * Advances the pose back one pose
	 */
	public void GoBack() { 
		  //send data to Activity
		  listener.onBackItemSelected();
		  CancelTicker();
		  
	}

	/*
	 * getters and setters to be called from the activity
	 */
	public boolean isbAutoPlayIsOn() {
		return bAutoPlayIsOn;
	}

	public void setbAutoPlayIsOn(boolean bAutoPlayIsOn) {
		this.bAutoPlayIsOn = bAutoPlayIsOn;
	}
	
	public void setIsRepeatedPose(int bIsRepeatedPose) { 
		this.bIsRepeatedPose = bIsRepeatedPose;
	}
	
    public int getIsRepeatedPose() { 
    	return bIsRepeatedPose;
    }
    
    public boolean getIsClockTicking() { 
    	return bClockIsTicking;
    }



}
