package com.webnation.sixtysecondyogafree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class AndroidText extends Application {
	
    private String fileText = "";
    private String fileName = "";
    private Context context = null;
    
    AndroidText(String filename, Context c) { 
    	this.fileName = filename;
    	this.context = c;
    	
    }
    
    
	public String getAndroidText() { 
	   	 String fileContent = "";
	   	 int i;
	   	 int resourceID; 
	   	 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	       // try opening the myfilename.txt
	        try {
	       	 resourceID = context.getResources().getIdentifier(fileName, "raw", "com.webnation.sixtysecondyogafree");
	       	 Log.d("We're Here", "resourceID =" + resourceID);
	       	 InputStream inputStream = context.getResources().openRawResource(resourceID);
	   	  
	   	     if (inputStream.available() > 0) { 
	   		    i = inputStream.read();
	   		  while (i != -1)
	   		      {
	   		       byteArrayOutputStream.write(i);
	   		       i = inputStream.read();
	   		      }
	   		      inputStream.close();
	   		     fileContent = byteArrayOutputStream.toString();
	   	  }
	   		  
	     } 
	     catch (IOException e) { 
	   	  e.printStackTrace();
	   	  Log.d("IO Exception", e.toString());
	   	  
	     } 
	     catch (Exception e) { 
	       	 e.printStackTrace();
	       	 Log.d("Exception", e.toString());
	     }
	        
	     return fileContent;
		}

}
