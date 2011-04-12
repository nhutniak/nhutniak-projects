package com.nhutniak.ohsnap365;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

	// Identifier for the date picker dialog
	private static final int DIALOG_DATE_PICKER = 2;
	
	// Max image size to display to avoid memory issues
	private static final int IMAGE_MAX_SIZE = 400;
	
	private DatabaseActivity m_databaseActivity;
	
	private Calendar m_calendar;
	
	public OhSnap365() {
		m_databaseActivity = new DatabaseActivity( this );
		m_calendar = Calendar.getInstance();
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
											  	m_calendar), 
											   getString(R.string.appEmailTag)));

			finish();
		}
	};
	
	/**
	 * Action to perform when the change date button is clicked.
	 */
	private OnClickListener m_changeDateListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(DIALOG_DATE_PICKER);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		loadClickableOhSnapInfo();
		getSendPictureButton().setOnClickListener(m_addListener);
		getChangeDateButton().setOnClickListener(m_changeDateListener);
		setDisplayDate();
		
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
		case DIALOG_DATE_PICKER:
			dialog = new DatePickerDialog(this, m_dateSetListener,
					m_calendar.get(Calendar.YEAR),
					m_calendar.get(Calendar.MONTH),
					m_calendar.get(Calendar.DAY_OF_MONTH));
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
	
	private DatePickerDialog.OnDateSetListener m_dateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			m_calendar.set(year, monthOfYear, dayOfMonth);
			setDisplayDate();
		}
	};
	
	@Override
	public void finish() {
		getChangeDateButton().setOnClickListener(null);
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
				getPreviewImage().setImageBitmap( decodeFile( uri ) );
				getPreviewImageTag().setText(getString(R.string.previewTag));
				getPreviewImage().setVisibility(View.VISIBLE);
			}
		};
		
		getPreviewImage().postDelayed(loadImage, 100);
	}
	
	/**
	 * Decodes the URI provided and provides a scaled bitmap to the {@link #IMAGE_MAX_SIZE}.
	 * 
	 * @see http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966
	 * 
	 * @param uri
	 * 		a  {@link Uri}.
	 * @return a {@link Bitmap} or null if an error occurs.
	 */
	private Bitmap decodeFile( Uri uri )
	{
		Bitmap b = null;
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			
			InputStream fis = getApplicationContext().getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(fis, null, o);
	        fis.close();

	        int scale = 1;
	        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
	            scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
	        }

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize = scale;
	        fis = getApplicationContext().getContentResolver().openInputStream(uri);
	        b = BitmapFactory.decodeStream(fis, null, o2);
	        fis.close();
		} catch( FileNotFoundException e ) {
			Log.e("decodeFile", "Could not find file: " + e);
		} catch (IOException e) {
			Log.e("decodeFile", "IOException occured: " + e);
		}
		
		return b;
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
	
	private void setDisplayDate() {
		getChangeDateButton().setText( String.valueOf(m_calendar.get(Calendar.MONTH) + 1) + " / "
										+ String.valueOf(m_calendar.get(Calendar.DAY_OF_MONTH)) + " / " 
										+ String.valueOf(m_calendar.get(Calendar.YEAR)));
	}

	private Button getChangeDateButton() {
		return (Button) findViewById(R.id.changeDate);
	}
}