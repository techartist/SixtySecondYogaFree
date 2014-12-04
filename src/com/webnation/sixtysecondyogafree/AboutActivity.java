package com.webnation.sixtysecondyogafree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Button doneBtn;
    	TextView abouttv;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        doneBtn = (Button)findViewById(R.id.done);
        doneBtn.setOnClickListener(this);
        abouttv = (TextView)findViewById(R.id.about);
        AndroidText at = new AndroidText("about", AboutActivity.this);
        abouttv.setText(Html.fromHtml(at.getAndroidText()));
    }

	public void onClick(View v) {
		finish();
		
	}
	

}
