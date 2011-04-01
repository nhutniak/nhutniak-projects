package com.nhutniak.ohsnap365;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
		
		loadClickableOhSnapInfo();
		getSendPictureButton().setOnClickListener(m_addListener);

		// Load the image or display "no image" information.
		final Uri uri = getImageUri();
		if( null != uri )
		{
			loadImage(uri);
		}
		else
		{
			getPreviewImageTag().setText( getString(R.string.noImageTag) );
		}
	
		User user = m_databaseActivity.getSavedUser();
		getLoginEditText().setText((null == user) ? "" : user.getLogin());
		getSecretWordEditText().setText((null == user) ? "" : user.getSecretWord());
	}

	@Override
	public void finish() {
		getSendPictureButton().setOnClickListener(null);
		m_databaseActivity.release();
		getPreviewImage().setImageURI(null);
		super.finish();
	}

	/**
	 * Inserts a message string with a clickable OhSnap365 hypertext link.
	 */
	private void loadClickableOhSnapInfo() {
		getVisitOhSnap365Tag().setText(Html.fromHtml(getString(R.string.gotoOhSnap365)));
		getVisitOhSnap365Tag().setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * Attempts to load the image provided by the resource.
	 * @param uri
	 */
	private void loadImage(final Uri uri) {
		Runnable loadImage = new Runnable() {
			@Override
			public void run() {
				String imageTag;
				try {
					imageTag = setPreviewImage(uri);
				} catch (OutOfMemoryError e) {
					// We seem to encounter memory issues if the app is
					// launched twice one after the other due to image
					// size. So we will attempt to clear some memory
					// in case this occurs.
					System.gc();
	
					try {
						imageTag = setPreviewImage(uri);
					} catch (OutOfMemoryError e2) {
						// Could not display the image due to memory
						// constraints
						getPreviewImage().setImageURI(null);
						imageTag = getString(R.string.outOfMemoryImage);
					}
				}
				
				getPreviewImageTag().setText(imageTag);
				getPreviewImage().setVisibility(View.VISIBLE);
			}
	
	        // TODO: this is ugly.  Fix at a later time.
			private String setPreviewImage(final Uri uri)
					throws OutOfMemoryError {
				getPreviewImage().setImageURI(uri);
				return getString(R.string.previewTag);
			}
		};
		
		getPreviewImage().postDelayed(loadImage, 100);
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

	private ImageView getPreviewImage() {
		return (ImageView) findViewById(R.id.picview);
	}

	private TextView getPreviewImageTag() {
		return ((TextView) findViewById(R.id.topTag));
	}

	private TextView getVisitOhSnap365Tag() {
		return ((TextView) findViewById(R.id.visitOhSnap));
	}
	
	private EditText getSecretWordEditText() {
		return (EditText) findViewById(R.id.secretword);
	}

	private Button getSendPictureButton() {
		return (Button) findViewById(R.id.launchEmailer);
	}
}