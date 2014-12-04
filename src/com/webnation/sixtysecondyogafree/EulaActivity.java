package com.webnation.sixtysecondyogafree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EulaActivity extends Activity implements OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Button doneBtn;
    	TextView eulatv;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eula);
        doneBtn = (Button)findViewById(R.id.done);
        doneBtn.setOnClickListener(this);
        eulatv = (TextView)findViewById(R.id.eula);
        AndroidText at = new AndroidText("eula", EulaActivity.this);
        eulatv.setText(Html.fromHtml(at.getAndroidText()));
    }

	public void onClick(View v) {
		finish();
		
	}

}

