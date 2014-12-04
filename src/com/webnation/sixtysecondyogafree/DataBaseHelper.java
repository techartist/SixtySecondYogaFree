package com.webnation.sixtysecondyogafree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.webnation.sixtysecondyogafree/databases/";
    private static String DB_NAME = "yogaposes"; 
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_GRAPHIC = "graphic";
	public static final String COLUMN_TEXT = "text";
	public static final String COLUMN_DIFFICULTY = "difficulty";
	public static final String COLUMN_SKIP = "skip";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_YOGANAME = "yoga_name";
	public static final String COLUMN_REPEAT = "repeat";
	public static final String TABLE_POSES = "yogaposes";
	public static final String TABLE_POSE_ORDER = "poseorder";
	public static final String COLUMN_POSE_ID = "pose_id";
	
	private static final int SKIP = 1;
    public SQLiteDatabase myDataBase; 
	protected int numOfRows = 0;
	private String[] allColumns = { DataBaseHelper.COLUMN_ID,
			DataBaseHelper.COLUMN_GRAPHIC,DataBaseHelper.COLUMN_TEXT, 
			DataBaseHelper.COLUMN_TITLE,DataBaseHelper.COLUMN_DIFFICULTY,
			DataBaseHelper.COLUMN_SKIP, DataBaseHelper.COLUMN_YOGANAME, 
			DataBaseHelper.COLUMN_REPEAT};
	private String[] allColumnsPoseOrder = { DataBaseHelper.COLUMN_ID,
			DataBaseHelper.COLUMN_POSE_ID  };
	private String CreateTablePoseOrder = "CREATE TABLE poseorder (_id INTEGER PRIMARY KEY,pose_id NUMERIC)";
	private String InsertInformation = "insert into poseorder select null, _id from yogaposes order by Random()";
	private String DeletePoses = "delete from poseorder";
	private String CountPoses = "select Count(*) from poseorder";
	private String GetPose = "select a.* from yogaposes a inner join poseorder b on b.pose_id = a._id";
	
 
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
    
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DB_NAME);
    }

 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
    	
 
    	if(dbExist){
    		//do nothing - database already exists
    		Log.d("Database Check", "Database Exists");

    	}else{
    		Log.d("Database Check", "Database Doesn't Exist");
    		this.close();
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
        	this.close();
        	try {
    			copyDataBase();
    			Log.d("Database Check", "Database Copied");
 
    		} catch (IOException e) {
        		Log.d("I/O Error", e.toString());

        		throw new Error("Error copying database");
        	}
        	catch (Exception e) { 
        		Log.d("Exception in createDatabase", e.toString());
        		
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
    	Log.d("We're Here", "DatabaseHelper.checkDataBase()");
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		Log.d("We're Here", "Database doesn't exist");
    		Log.d("Database Error", e.toString());
 
    	}
    	catch (Exception e) { 
    		Log.d("Exception in checkDatabase", e.toString());
    		
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring bytestream.
     * */
    private void copyDataBase() throws IOException{
    	Log.d("We're Here", "Copy Database");
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
    	Log.d("We're Here", "SQLite db opened)");
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
    	Log.d("We're Here", "Output Stream Obtained)");
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
    	Log.d("We're Here", "Copy Database Successful)");
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close(); 	
    }
    
    /*formats the basic SQL Statement*/
    public String formatSqlStatement(int Difficulty) { 
    
    	String SQLStatement = "";
		SQLStatement += " Where " + COLUMN_SKIP + "= 0";
		
		switch (Difficulty) { 
		  case 0: 
			SQLStatement +=  " and difficulty=" + String.valueOf(Difficulty);
		    break;
		  case 1: 
			SQLStatement +=  " and difficulty=" + String.valueOf(Difficulty);
			break;
		  default: 
			break;
		}
    	
		
		return SQLStatement;
    	
    }
    public void createTable() { 
    	String sql = "CREATE TABLE IF NOT EXISTS poseorder (_id INTEGER PRIMARY KEY,pose_id NUMERIC)";
    	try {
			Log.d("Database Call", sql);
			this.openDataBase();
			myDataBase.execSQL(sql);
			this.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.d("SQL Exception", e.toString());
		}
    	catch (Exception e) {
			Log.d("Exception", "PopulatePoseOrderTable " + e.toString());
		}
    	
    }
   
    
    
    /*
     * Sets up pose order table to allow
     * people to set order of Poses per Session
     */
    public void PopulatePoseOrderTable(int Difficulty) { 
    	try {
    		
			String sql = "insert into poseorder select null, _id from yogaposes";
			sql += " " + formatSqlStatement(Difficulty);
			sql += " order by Random()";
			Log.d("Database Call", sql);
			this.openDataBase();
			myDataBase.execSQL(sql);
			this.close();
		} // TODO Auto-generated catch block
			catch (SQLException e) {
			Log.d("SQL Exception", "PopulatePoseOrderTable " + e.toString());
		}
    	    catch (Exception e) {
			Log.d("Exception", "PopulatePoseOrderTable " + e.toString());
		}
    	
    }
    
    /*clear out the database to so we can get 
     * a fresh set of poses
     */
    public void deleteFromPoseOrder() { 
    	try {
			this.openDataBase();
			myDataBase.execSQL(DeletePoses);
			this.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.d("SQL Exception", e.toString());
		}
    	
    }
     
    
    /* gets random Pose from database based on 
	 * skip, difficulty level
	 */
	public Poses getPoseByCount(int Count) { 
		Poses newPose = null;
		String SQLStatement = GetPose + " where b._id =" + String.valueOf(Count);
		Log.d("Database call", SQLStatement);
		try { 
			    this.openDataBase();
				Cursor cursor = myDataBase.rawQuery(SQLStatement, null);
				Log.d("We're Here", "Successfull Call to DB");
				cursor.moveToFirst();
				newPose = cursorToPose(cursor);
				cursor.close();
				this.close();
						
		}
		catch (SQLException sqle) {
				Log.d("Database Error", "Error getting PoseID");
				Log.d("SQL EXception", sqle.toString());
		}
		catch (Exception e) { 
			Log.d("EXception", e.toString());
		}
		return newPose;
       
		
	}
	
	

	
	/* gets random Pose from database based on 
	 * skip, difficulty level
	 */
	public Poses getRandomPose(int Difficulty) { 
		Poses newPose = null;
		String SQLStatement = "Select * from " + TABLE_POSES;
		SQLStatement += " " + formatSqlStatement(Difficulty);
		SQLStatement += " ORDER BY RANDOM() LIMIT 1";
		Log.d("Database call", SQLStatement);
		try { 
			    this.openDataBase();
				Cursor cursor = myDataBase.rawQuery(SQLStatement, null);
				Log.d("We're Here", "Successfull Call to DB");
				cursor.moveToFirst();
				newPose = cursorToPose(cursor);
				cursor.close();
				this.close();
						
		}
		catch (SQLException sqle) {
				Log.d("Database Error", "Error getting PoseID");
				Log.d("SQL EXception", sqle.toString());
		}
		catch (Exception e) { 
			Log.d("EXception", e.toString());
		}
		return newPose;
        
		
	}
	
	
	/*sledgehammer way to figure out where we are in the count.
	 */
	public int getNumberOfRows(int Difficulty) { 
		
		int numberOfRows = 0;
		String SQLStatement = "Select * from " + TABLE_POSES;
		SQLStatement += " " + formatSqlStatement(Difficulty);
		Log.d("Database call", SQLStatement);
		try { 
			    this.openDataBase();
				Cursor cursor = myDataBase.rawQuery(SQLStatement, null);
				Log.d("We're Here", "Successfull Call to DB");
				numberOfRows = cursor.getCount();
				cursor.close();
				this.close();
						
		}
		catch (SQLException sqle) {
				Log.d("Database Error", "Error getting PoseID");
				Log.d("SQL EXception", sqle.toString());
		}
		catch (Exception e) { 
			Log.d("EXception", e.toString());
		}
		this.close();
		return numberOfRows;	
	}
	
	/* allows a pose to be skipped */
	public boolean setSkip(int skipPoseId) { 
		try{ 
			Log.d("We're here", "set Skip");
			this.openDataBase();
			ContentValues args = new ContentValues();
			args.put(COLUMN_SKIP,SKIP);
			String SQLStatement = "update " + TABLE_POSES;
			SQLStatement += " set " + COLUMN_SKIP + "=1 Where ";
			SQLStatement += COLUMN_ID + "=" + String.valueOf(skipPoseId);
			Log.d("Database call", SQLStatement);
			int rowcount = myDataBase.update(DataBaseHelper.TABLE_POSES, args, COLUMN_ID + "=" +  String.valueOf(skipPoseId),null);
			Log.d("Rows Updated", String.valueOf(rowcount));
			this.close();
			return true;
		}
		catch (SQLException sqle) {
			Log.d("Database Error", "Error setting skip");
			
			return false;
		}
		catch (Exception e) {
			Log.d("Exception", e.toString());
			return false;
		}
		
		
	}
	
	/* allows a pose to be skipped by passing in the current count*/
	public boolean setSkipByCount(int Count) { 
		Poses pose = getPoseByCount(Count); 
		int PoseID = (int)pose.getId();
		return setSkip(PoseID);
		
	}

	
	/* undoes all the skips in the database. 
	 * Cannot be undone.
	 */
	public boolean undoAllSkip() { 
		try{ 
			this.openDataBase();
			ContentValues args = new ContentValues();
			args.put(COLUMN_SKIP,SKIP);
			myDataBase.update(this.TABLE_POSES, args, null,null);
			this.close();
			return true;
		}
		catch (SQLException sqle) {
			Log.d("Database Error", "Error setting skip");
			
			return false;
		}
		catch (Exception e) {
			Log.d("Exception", e.toString());
			return false;
		}
		
		
	}
	
	
	
    /**
     * grabs the information about a pose from the database
     * @param id - poseID in database
     * @return Poses object
     * @throws SQLException
     */
	public Poses getPose(int id) {
		Poses newPose = null;
		this.openDataBase();
    	Log.d("We're Here", "getPose");
		try { 
				Cursor cursor = myDataBase.query(DataBaseHelper.TABLE_POSES,
				allColumns, DataBaseHelper.COLUMN_ID + " = " + id, null,
				null, null, null);
	
		cursor.moveToFirst();
		Log.d("Database contents", "Graphic string: " + cursor.getString(1));
		Log.d("Database contents", "YogaName: " + cursor.getString(6));
		newPose = new Poses(cursor.getLong(0),
				cursor.getString(1),
				cursor.getString(2),
				cursor.getString(3),
				cursor.getInt(4),
				cursor.getInt(5),
				cursor.getString(6),
				cursor.getInt(7));
		cursor.close();
		}
		catch (SQLException sqle) {
			Log.d("Database Error", "Error getting Pose from database");
			Log.d("SQL Exception", sqle.toString());
		}
		catch (Exception e) {
			Log.d("Exception", e.toString());
		}
		this.close();
		return newPose;
	}
	
	/*grabs info from a cursor object and 
	 instantiates the Pose object with data */
	public Poses cursorToPose(Cursor cursor) {

		Poses pose = null;
		try { 
			Log.d("Database contents", "Graphic string: " + cursor.getString(1));
			Log.d("Database contents", "Yoga Name: " + cursor.getString(6));
			pose = new Poses(cursor.getLong(0),
					cursor.getString(1),
					cursor.getString(2),
					cursor.getString(3),
					cursor.getInt(4),
					cursor.getInt(5),
					cursor.getString(6),
					cursor.getInt(7));
			
		}
		catch (SQLException sqle) { 
			Log.d("Database Error in cursorToPose", sqle.toString());
		}
		catch (Exception e) {
			Log.d("Exception", e.toString());
		}
		return pose;
	}
	
	/**
	 * Inserts a pose into the database
	 * @param pose, fully instantiated pose with values.
	 * @return true if successful, false otherwise
	 * also, the pose will have its id set to the one in the 
	 * database after this call. 
	 */
	public boolean createPose(Poses pose) {
		if (!pose.getGraphic().equals("") && !pose.getText().equals("") && !pose.getTitle().equals("")) { 
			this.openDataBase();
			ContentValues values = new ContentValues();
			values.put(DataBaseHelper.COLUMN_GRAPHIC, pose.getGraphic());
			values.put(DataBaseHelper.COLUMN_TEXT, pose.getText());
			values.put(DataBaseHelper.COLUMN_TITLE, pose.getTitle());
			values.put(DataBaseHelper.COLUMN_DIFFICULTY, pose.getDifficulty());
			values.put(DataBaseHelper.COLUMN_SKIP, pose.getSkip());
			try { 
				long insertId = myDataBase.insert(DataBaseHelper.TABLE_POSES, null,values);
				pose.setId(insertId);
				this.close();
				return true;
			}
			catch (SQLException sqle) {
				Log.d("Database Error", "Error inserting Pose into database");
				Log.d("SQL Exception", sqle.toString());
				return false;
			}
			catch (Exception e) {
				Log.d("Exception", e.toString());
				return false;
			}
		}
		else { 
			Log.d("Insert Pose Error", "Pose does not have all information");
			return false;
			
		}	
		
	}
 
    public void openDataBase() {
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
        try { 
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } 
        catch (SQLException sqle){ 
        	Log.d("Database Error", "Could not Open DB)");
        	throw sqle;
        }
 
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
    /*for debug*/
	public int getNumberOfRows() {
		this.openDataBase();
		int icount = 0;
		try { 
			Cursor mcursor = myDataBase.rawQuery("Select Count(*) from " + TABLE_POSES, null);
		    mcursor.moveToFirst();
		    icount = mcursor.getInt(0);
		    mcursor.close();
		}
		catch (SQLException sqle) {
			Log.d("Database Error", "Error getting number of rows");
			Log.d("SQL EXception", sqle.toString());
		}
		
		this.close();
        return icount;
	} 
	
	/*as the name implies for debug*/
	public int getNumberOfColumns() {
		this.openDataBase();
		int iColumnCount = 0;
		try { 
			Cursor mcursor = myDataBase.rawQuery("Select * from " + TABLE_POSES, null);
		    iColumnCount = mcursor.getColumnCount();
		    mcursor.close();
		}
		catch (SQLException sqle) {
			Log.d("Database Error", "Error getting number of columns");
			Log.d("SQL EXception", sqle.toString());
		}
		
		this.close();
        return iColumnCount;
	}
	
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
	
	public Cursor execTestSQL(int Difficulty) { 
		String SQLStatement = "Select * from " + TABLE_POSES;
		SQLStatement += " Where " + COLUMN_SKIP + "= 0";
		switch (Difficulty) { 
		  case 0: 
			SQLStatement +=  " and difficulty=" + String.valueOf(Difficulty);
		    break;
		  case 1: 
			SQLStatement +=  " and difficulty=" + String.valueOf(Difficulty);
			break;
		  default: 
			break;
		}
		
		//SQLStatement +=  " and difficulty=0";
		SQLStatement += " ORDER BY RANDOM() LIMIT 1";
		try { 
			//Cursor cursor = myDataBase.rawQuery(SQLStatement, null);
			this.openDataBase();
			Cursor cursor = myDataBase.query(DataBaseHelper.TABLE_POSES,
					allColumns, DataBaseHelper.COLUMN_ID + " = 2", null,
					null, null, null);
			return cursor;
		}
		catch (SQLException sqle) {
			Log.d("Database Error", "Error getting PoseID");
			Log.d("SQL EXception", sqle.toString());
			return null;
	    }
	    catch (Exception e) { 
		   Log.d("EXception", e.toString());
		   return null;
	    }
		
	}
 
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.
 
}