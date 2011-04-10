package com.nhutniak.ohsnap365;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Dialog responsible for modifying options for the application.
 */
public class OptionsDialog extends Dialog {

	private DatabaseActivity m_databaseActivity;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            the {@link Context}.
	 * @param activity
	 *            the {@link DatabaseActivity} controlling DAO interaction.
	 */
	public OptionsDialog(Context context, DatabaseActivity activity) {
		super(context);
		m_databaseActivity = activity;
		
		setContentView(R.layout.options_dialog);
		setTitle(R.string.titleOptions);
		
		loadUserInfo();
		
		getSaveButton().setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_databaseActivity.saveUserInfo(getLoginEditText().getText().toString(), getSecretWordEditText().getText().toString() );
				dismiss();
				
			}
		});
		getCancelButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadUserInfo();
				dismiss();
			}
		});
	}

	/**
	 * Resets the form to the saved user information as provided by the DAO.
	 */
	private void loadUserInfo() {
		User user = m_databaseActivity.getSavedUser();
		getLoginEditText().setText((null == user) ? "" : user.getLogin());
		getSecretWordEditText().setText((null == user) ? "" : user.getSecretWord());
	}

	private EditText getLoginEditText() {
		return (EditText) findViewById(R.id.login);
	}
	
	private EditText getSecretWordEditText() {
		return (EditText) findViewById(R.id.secretword);
	}

	private Button getCancelButton() {
		return (Button) findViewById(R.id.cancelOptions);
	}

	private Button getSaveButton() {
		return (Button) findViewById(R.id.saveOptions);
	}
}
