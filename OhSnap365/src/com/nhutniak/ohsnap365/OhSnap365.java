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
	
	private DatabaseActivity m_databaseActivity;
	
	public OhSnap365() {
		m_databaseActivity = new DatabaseActivity( this );
	}

	/**
	 * Action to perform when send button is clicked.
	 */
	private OnClickListener m_addListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			m_databaseActivity.saveUserInfo(getLoginEditText().getText().toString(), getSecretWordEditText().getText().toString() );
			
			startActivity(Intent.createChooser(EmailIntentComposer.compose(
											  	m_databaseActivity.getSavedUser(),
											  	getImageCaptionEditText().getText().toString(),
											  	getImageUri()), 
											   getString(R.string.appEmailTag)));

			finish();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getSendPictureButton().setOnClickListener(m_addListener);
		
		// Do something if we have no image.
		// Uri uri = getImageUri();
	
		User user = m_databaseActivity.getSavedUser();
		getLoginEditText().setText((null == user) ? "" : user.getLogin());
		getSecretWordEditText().setText((null == user) ? "" : user.getSecretWord());
	}

	@Override
	public void finish() {
		getSendPictureButton().setOnClickListener(null);
		m_databaseActivity.release();
		super.finish();
	}

	/**
	 * Attempts to find the image that is associated with the launch of the
	 * application.
	 * 
	 * @return a {@link Uri} or null if no image was provided.
	 */
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
			}
		}
		return uri;
	}

	private EditText getImageCaptionEditText() {
		return (EditText) findViewById(R.id.caption);
	}

	private EditText getLoginEditText() {
		return (EditText) findViewById(R.id.login);
	}

	private EditText getSecretWordEditText() {
		return (EditText) findViewById(R.id.secretword);
	}

	private Button getSendPictureButton() {
		return (Button) findViewById(R.id.launchEmailer);
	}
}