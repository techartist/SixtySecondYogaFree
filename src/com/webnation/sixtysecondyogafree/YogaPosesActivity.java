package com.webnation.sixtysecondyogafree;

import java.io.IOException;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Menu;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.SQLException;

public class YogaPosesActivity extends SherlockFragmentActivity implements NavigationFragment.OnChangePictureSelectedListener, ImageFragment.OnPauseListener{

	//the heart of this application is the database
	//database helper keeps track of everything
	public DataBaseHelper myDbHelper;
	
	//menu items
	private Menu menu = null;
	MenuItem miAdvance = null;
    MenuItem miDifficulty = null;
    
    //keys for shared preferences
	private static final String KEY_EULA_ACCEPTED = "splash shown";
	private static final String KEY_OPENING_SHOWN = "opening shown";
	private static final String KEY_DIFFICULTY = "difficulty";
	private static final String KEY_AUTO_OR_MANUAL = "advance";
	private static final String KEY_POSE_COUNT = "count";
	private static final String KEY_POSE_POSITION = "position";

    //Difficulty enums
	private final int DIFFICULTY_BASIC = 0;
	private final int DIFFICULTY_ADVANCED = 1;
	private final int DIFFICULTY_ADVANCED_AND_BASIC = 2;
	
	//advance automatic or manual enums
	private final int ADVANCE_MANUAL = 0;
	private final int ADVANCE_AUTOMATIC = 1;
	
	//start off basic, manual
	private int Difficulty = DIFFICULTY_BASIC;
	private int Automatic = ADVANCE_MANUAL;
	
	//Count is tied to a database key, keeps track 
	//of the poses, order of the poses, etc
	private int Count = 1;

	//Pose Object keeps all of the data about a pose
	private Poses nextPose = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("YogaPosesActivity", "onCreate()");
		
		//if user has previous settings, get them from shared prefs. 
        getSharedPrefs();
		
		setContentView(R.layout.main);
		myDbHelper = new DataBaseHelper(YogaPosesActivity.this);
		
		//if not being recreated after being killed
		if (savedInstanceState == null) { 
			CopyDatabase(myDbHelper);
			populatePoses();
		}
		else { 
			Count = savedInstanceState.getInt("Count", 1);
		}

        //get shared preferences to see if Eula and Opening Messages were displayed
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(YogaPosesActivity.this);
        if(!prefs.getBoolean(KEY_EULA_ACCEPTED, false)) {
            showEula();
            // Determine if EULA was accepted this time
            prefs.edit().putBoolean(KEY_EULA_ACCEPTED, true).commit();
        }
        if(!prefs.getBoolean(KEY_OPENING_SHOWN, false)) {
        	displayOpeningMsg();
            // Determine if Opening Message was displayed
            prefs.edit().putBoolean(KEY_OPENING_SHOWN, true).commit();
        }
        
        
        //keep screen on while app is active
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Keep screen in portrait mode. 
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
      //remove title from action bar
        ActionBar actionBar = getSupportActionBar(); 
        actionBar.setDisplayShowTitleEnabled(false); 
       
        
        //reads data into the Pose Object
        GetPoseByCount();
        //sets up pose in the ImageFragment
        changePose(); 
        
        //sets Automatic poses
        if (Automatic == ADVANCE_AUTOMATIC) { 
	    	  NavigationFragment navFrag = (NavigationFragment) getSupportFragmentManager()
		    		.findFragmentById(R.id.button_navigation);
		        if (navFrag != null && navFrag.isInLayout()) {
		        	navFrag.setbAutoPlayIsOn(true);
		        }
	      }
        
	}
	
	/*
	 * communicate if timer is to be paused, coming from ImageFragement
	 * Message route:  ImageFragment -> YogaPosesActivity -> NavigationFragment
	 */
	public void onPauseSelected() { 
	    NavigationFragment navFrag = (NavigationFragment) getSupportFragmentManager()
	    		.findFragmentById(R.id.button_navigation);
	        if (navFrag != null && navFrag.isInLayout()) {
	        	Log.d("We're Here", "onPauseSelected() OnPauseTicking=" + navFrag.getIsClockTicking());
	        	if (navFrag.getIsClockTicking()) { 
	                 navFrag.Pause();
	        	}
	        }
	}
	
	/* 
	 * @see com.webnation.yogaposesactivity.NavigationFragment.OnChangePictureSelectedListener
	 * Messages coming from Navigation Fragment
	 * Message route:  NavigationFragment -> YogaPosesActivity -> ImageFragment
	 *                                                         -> NavigationFragment
	 *                                                         -> DetailFragment
	 */
	public void onForwardItemSelected() {
        //add one to the count
		incrementCount();
		//read information into Pose Object
		GetPoseByCount();
		//set up next pose
		changePose();
	  }
	
	/* 
	 * @see com.webnation.yogaposesactivity.NavigationFragment.OnChangePictureSelectedListener
	 * Coming from Navigation Fragment
	 * Message route:  NavigationFragment -> YogaPosesActivity -> ImageFragment
	 *                                                         -> NavigationFragment
	 *                                                         -> DetailFragment
	 */
	public void onBackItemSelected() {
		//remove one from count
		decrementCount();
		//read information into Pose Object
		GetPoseByCount();
		//set up next pose
		changePose();
	  }
	
	/*
	 * Does all the heavy lifting of communication with the fragments
	 */
	private void changePose() { 
		//set up image and text to the right pose.  Passes in Count and PoseData
		ImageFragment fragment = (ImageFragment) getSupportFragmentManager()
	            .findFragmentById(R.id.imageview_text);
	        if (fragment != null && fragment.isInLayout()) {
	          fragment.SetImageViewByPose(Count,nextPose);
	        }
	    //set up back button as enabled, communicate if pose is to be repeated
	    NavigationFragment navFrag = (NavigationFragment) getSupportFragmentManager()
	    		.findFragmentById(R.id.button_navigation);
	        if (navFrag != null && navFrag.isInLayout()) {
	          navFrag.SetBackButton(Count);
	          navFrag.setIsRepeatedPose(nextPose.getRepeat());
	        }
	    //if detail fragment exists, set up the right text
		DetailFragment detailFrag = (DetailFragment) getSupportFragmentManager()
		            .findFragmentById(R.id.yogatext);
		        if (detailFrag != null && detailFrag.isInLayout()) {
		        	detailFrag.SetYogaTextByPose(Count,nextPose);
		        }
		       
	}

	
	
	/* the count is the key value that lives in the application
	 * We navigate through poses using the count.  
	 */
	public void incrementCount() { 
		int numRows = myDbHelper.getNumberOfRows(Difficulty);
		Log.d("Count", "Count=" + Count);
		if (Count >= numRows) { 
			//add more poses, in random order to the table. 
			myDbHelper.PopulatePoseOrderTable(Difficulty);
			//Count = 1;
		}
		//else { 
		   Count++;
		//}
	}
	
	public void decrementCount() { 
		if (Count != 1) { 
			Count--;
		}
		
	}
	
	/*
	 * displays an opening dialog first time 
	 * user opens the app
	 */
	public  void displayOpeningMsg() {
		final Dialog openingDialog = new Dialog(this);
		openingDialog.setContentView(R.layout.opening);
		openingDialog.setTitle("Welcome to 60 second Yoga");

        //set up edits
        final TextView tvOpeningMsg = (TextView) openingDialog.findViewById(R.id.opening);
        AndroidText at = new AndroidText("opening", YogaPosesActivity.this);
        tvOpeningMsg.setText(Html.fromHtml(at.getAndroidText()));
        Button btnOK = (Button) openingDialog.findViewById(R.id.btnOK);
       	btnOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        	  openingDialog.cancel();
            }
        });
        openingDialog.show();
		
	}
	
    //shows terms and conditions
    private void showEula() {
    	Intent i = new Intent(this, EulaActivity.class);
		this.startActivity(i);
		
	}
    
    /*
     * Gets pose information which will be
     * passed around to the various fragments
     * Info passed in Poses object
     */
    public void GetPoseByCount() {
    	Log.d("We're Here", "GetPoseByCount");
    	
    	try { 
    		//Pose Data will be passed around to the various fragments
    		nextPose = myDbHelper.getPoseByCount(Count);
    		myDbHelper.close();
    	    Log.d("Pose Data", "Title=" + nextPose.getTitle());
    	    
    	} 
    	catch (SQLException sqle) { 
    		Log.d("Database Error", sqle.toString());
    		
    	}
    	catch (Exception e) {
  		   Log.d("Exception",e.toString());
  	   }
  
    }
    
    //grabs the database from the assets directory 
    //and reads it into the /data/data/<package/database directory 
	public void CopyDatabase(DataBaseHelper dbh) { 
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
        try { 
        	//check to see if there's a database and if not creates one
        	dbh.createDataBase();
        	dbh.close();
 	    } 
        catch (IOException ioe) {
 		   Log.d("Database Error","Unable to create database");
 	   }
        catch (Exception e) {
 		   Log.d("Exception",e.toString());
 	   }
 
    }//end of CopyDatabase
	
	/* 
	 * this is how we keep track of where we are in the pose order
	 * used for navigating back and forward through poses. 
	 * There is a table which holds a selection of poses
	 * based on count and difficulty level
	 */
	public void populatePoses() {
		myDbHelper.createTable();
		myDbHelper.deleteFromPoseOrder();
		myDbHelper.PopulatePoseOrderTable(Difficulty);
	}
	
	/* called when user selects a skip or difficulty level from the menu
	 * 1. gets new list of poses and reads it into PoseOrder Table
	 * based on Difficulty Level.  
	 * 2. Resets Count
	 * 3. Gets next pose and reads it into Poses Object.
	 * 4. Updates pose in all the Fragments
	 */
	public void changeDifficultyLevelOrSkip() { 
		populatePoses();
		Count = 1;
		GetPoseByCount();
	    changePose();
	}
	
	/*
	 * as the name implies
	 * saves preferences for automatic and difficulty
	 */
	public void PopulateSharedPrefs() { 
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(YogaPosesActivity.this);
		boolean Success = settings.edit().putInt(KEY_DIFFICULTY, Difficulty).commit();
	    Success =  settings.edit().putInt(KEY_AUTO_OR_MANUAL,Automatic).commit();
		
	}
	
	/*
	 * as the name implies
	 * gets preferences for automatic and difficulty
	 */
	public void getSharedPrefs() { 
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(YogaPosesActivity.this);
	      Difficulty = prefs.getInt(KEY_DIFFICULTY,0);
	      Automatic = prefs.getInt(KEY_AUTO_OR_MANUAL,0);
	   
	      
	}
	
	/*save the value of Count if activity gets destroyed
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle icicle) {
		  super.onSaveInstanceState(icicle);
		  icicle.putInt("Count", Count);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    new MenuInflater(this).inflate(R.menu.main, menu);
	    this.menu = menu;
	    miAdvance = menu.findItem(R.id.menu_automatic);
	    miDifficulty = menu.findItem(R.id.menu_settings);
	    updateMenuTitles();

	    return(super.onCreateOptionsMenu(menu));
	}
	
	/*
	 * as the name implies
	 * updates titles to match settings of Difficulty and Advance settings
	 */
	private void updateMenuTitles() { 
		switch (Difficulty) { 
    	case DIFFICULTY_BASIC:
    		miDifficulty.setTitle("Basic");
    		break;
    	case DIFFICULTY_ADVANCED:
    		miDifficulty.setTitle("Advanced");
    		break;
    	case DIFFICULTY_ADVANCED_AND_BASIC:
    		miDifficulty.setTitle("All");
    		break;
    	default:
    		miDifficulty.setTitle("Difficulty");
    		break;
    
        }
		
		switch (Automatic) { 
    	case ADVANCE_MANUAL:
    		miAdvance.setTitle("Manual");
    		break;
    	case ADVANCE_AUTOMATIC:
    		miAdvance.setTitle("Auto");
    		break;
    	default:
    		miAdvance.setTitle("Progress");
    		break;
    
        }
		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected(android.view.MenuItem)
	 * 
	 * user communicates with app through action bar and menu items
	 * 
	 * Whenever a user selects a new setting (basic, advanced, automatic or manual, 
	 * the counting database table is wiped clean and repopulated with new entries based on 
	 * current user selections.  
	 * 
	 * The action bar is also updated to reflect current selections
	 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    { 
    	//whenever user clicks on menu item, stop and clear clock
    	NavigationFragment navFrag = (NavigationFragment) getSupportFragmentManager()
	    		.findFragmentById(R.id.button_navigation);
	        if (navFrag != null && navFrag.isInLayout()) {
	          navFrag.CancelTicker();
	        }
    
    	//check selected menu item
    	switch (item.getItemId()) { 
    	//set basic difficulty
    	case R.id.basic: 
    		Difficulty = DIFFICULTY_BASIC;
    		Toast.makeText(YogaPosesActivity.this, "Difficulty is Basic", Toast.LENGTH_SHORT).show();
    		changeDifficultyLevelOrSkip();
    		updateMenuTitles();
    		return true;
    	//set advanced difficulty
    	case R.id.advanced: 
    		Toast.makeText(YogaPosesActivity.this, "Difficulty is Advanced", Toast.LENGTH_SHORT).show();
    		Difficulty = DIFFICULTY_ADVANCED;
    		changeDifficultyLevelOrSkip();
    		updateMenuTitles();
    		return true;
    	//set all difficulty
    	case R.id.allPoses:
    		Toast.makeText(YogaPosesActivity.this, "All Poses Will Be Displayed", Toast.LENGTH_SHORT).show();
    		Difficulty = DIFFICULTY_ADVANCED_AND_BASIC;
    		changeDifficultyLevelOrSkip();
    		updateMenuTitles();
    		return true;
    	//set automatic advance
    	case R.id.automatic:
    		Toast.makeText(YogaPosesActivity.this, "Poses will be shown automatically", Toast.LENGTH_SHORT).show();
    		Automatic = ADVANCE_AUTOMATIC;
    		navFrag.setbAutoPlayIsOn(true);
    		updateMenuTitles();
    		return true;
        //set manual advance
    	case R.id.manual:
    		Toast.makeText(YogaPosesActivity.this, "You Need to Manually Advance Poses", Toast.LENGTH_SHORT).show();
    		Automatic = ADVANCE_MANUAL;
    		navFrag.setbAutoPlayIsOn(false);
    		updateMenuTitles();
    		return true;
    	case R.id.noAds:
    		Intent intent = new Intent(Intent.ACTION_VIEW);
    		intent.setData(Uri.parse("market://details?id=com.webnation.sixtysecondyoga"));
    		startActivity(intent);
    		return true;
    	//show about screen
    	case R.id.about:
    		Intent i = new Intent(this, AboutActivity.class);
    		this.startActivity(i);
    		return true;
    	//quit program
    	case R.id.quit:
    		finish();
    		return true;
    	//skip pose, sets in database
    	case R.id.skip:
    		final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);

            // set the message to display
            alertbox.setMessage("This yoga pose will be permanently skipped from now on!");
            alertbox.setCancelable(true);
            alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { 
                	boolean success = myDbHelper.setSkipByCount(Count);
                	changeDifficultyLevelOrSkip();
            		dialog.dismiss();
            		Toast.makeText(YogaPosesActivity.this, "The pose is permanently skipped", Toast.LENGTH_SHORT).show();
            		
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            // show it
            alertbox.show();
            return true;
    		
    		
    	default: 
    	   return super.onOptionsItemSelected(item);
    	}
    }
     
   //gets and sets count
  	public void setCount(int count) { 
  		Count = count;
  	}
  	
  	public int getCount() { 
  		return Count;
  	}
	//////////////////////////////////////////////
  	
  	
  	//OnStart, Stop, Resume, Pause Methods, get and set Shared Prefs
  	//Log in Debug so we can see what is called and when.  
    /////////////////
    @Override
    public void onRestart() {
      super.onRestart();
      getSharedPrefs();
      Log.d(getClass().getSimpleName(), "onRestart()");
    }

    @Override
    public void onStart() {
      super.onStart();
      getSharedPrefs();
      Log.d(getClass().getSimpleName(), "onStart()");
    }

    @Override
    public void onResume() {
      super.onResume();
      getSharedPrefs();
    }

    @Override
    public void onPause() {
      Log.d(getClass().getSimpleName(), "onPause()");
      super.onPause();
      PopulateSharedPrefs();
    }

    @Override
    public void onStop() {
      Log.d(getClass().getSimpleName(), "onStop()");
      super.onStop();
      PopulateSharedPrefs();
    }

    @Override
    public void onDestroy() {
      Log.d(getClass().getSimpleName(), "onDestroy()");
      super.onDestroy();
    }
	



}



