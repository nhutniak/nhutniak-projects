package com.nhutniak.ohsnap365;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class OhSnap365 extends Activity {
	EditText helloName;
	

	private final static String SEPARATOR = "+";
	private final static String PREFIX = "snap";
	private final static String SUFFIX = "@ohsnap365.com";
	
	private String composeEmailString()
	{
		StringBuffer buff = new StringBuffer();
		buff.append(PREFIX);
		buff.append(SEPARATOR);
		buff.append(getLoginEditText().getText().toString());
		buff.append(SEPARATOR);
		buff.append(getSecretWordEditText().getText().toString());
		buff.append(SUFFIX);
		
		return buff.toString();
	}
	
	private OnClickListener m_addListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			
			String[] emails = new String[] { composeEmailString(), };
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emails );
			
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getImageCaptionEditText().getText().toString() );

			Uri uri = getImageUri();
			if (null != uri) {
				emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
			}

			startActivity(Intent.createChooser(emailIntent, getString(R.string.appEmailTag) ));

			// displaySentMessage();
		}

		// private void displaySentMessage() {
		// Context context = getApplicationContext();
		// int duration = Toast.LENGTH_LONG;
		// helloName = (EditText) findViewById(R.id.helloName);
		// CharSequence text = "Hello " + helloName.getText() + "!";
		// Toast toast = Toast.makeText(context, text, duration);
		// toast.show();
		// }
	};

	private Uri getImageUri() {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();

		Uri uri = null;

		// if this is from the share menu
		if (Intent.ACTION_SEND.equals(action)) {
			if (extras.containsKey(Intent.EXTRA_STREAM)) {
				try {
					// Get resource path from intent callee
					uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
				} catch (Exception e) {
					Log.e(this.getClass().getName(), e.toString());
				}
			} else if (extras.containsKey(Intent.EXTRA_TEXT)) {

			}
		}
		return uri;
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button button = (Button) findViewById(R.id.launchEmailer);

		button.setOnClickListener(m_addListener);
		
		Uri uri = getImageUri();
		Log.println(Log.INFO, "URI:", Boolean.toString(null == uri) );
	}

	private EditText getLoginEditText() {
		return (EditText) findViewById(R.id.login);
	}

	private EditText getSecretWordEditText() {
		return (EditText) findViewById(R.id.secretword);
	}

	private EditText getImageCaptionEditText() {
		return (EditText) findViewById(R.id.caption);
	}
	
	
	@Override
	public void finish() {
		Button button = (Button) findViewById(R.id.launchEmailer);

		button.setOnClickListener(null);

		super.finish();
	}
}