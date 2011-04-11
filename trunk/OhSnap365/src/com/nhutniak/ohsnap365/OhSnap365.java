package com.nhutniak.ohsnap365;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OhSnap365 extends Activity {
	
	// Identifier for the custom options dialog
	private static final int DIALOG_OPTIONS_ID = 1;
	
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
			startActivity(Intent.createChooser(EmailIntentComposer.compose(
											  	m_databaseActivity.getSavedUser(),
											  	getImageCaptionEditText().getText().toString(),
											  	getImageUri(), 
											  	getDatePicker()), 
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
		if( null == user )
		{
			Runnable launchOptions = new Runnable() {
				
				@Override
				public void run() {
					showDialog(DIALOG_OPTIONS_ID);
					Toast.makeText(getApplicationContext(), R.string.noUserMessage, Toast.LENGTH_SHORT).show();
				}
			};
			
			launchOptions.run();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menuOptions:
	    	showDialog(DIALOG_OPTIONS_ID);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch(id) {
		case DIALOG_OPTIONS_ID:
			dialog = new OptionsDialog(this, m_databaseActivity);
			break;
		default:
			dialog = null;
		}
		return dialog;
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

	private ImageView getPreviewImage() {
		return (ImageView) findViewById(R.id.picview);
	}

	private TextView getPreviewImageTag() {
		return ((TextView) findViewById(R.id.topTag));
	}

	private TextView getVisitOhSnap365Tag() {
		return ((TextView) findViewById(R.id.visitOhSnap));
	}
	
	private Button getSendPictureButton() {
		return (Button) findViewById(R.id.launchEmailer);
	}
	
	private DatePicker getDatePicker() {
		return (DatePicker) findViewById(R.id.date);
	}
}