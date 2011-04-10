package com.nhutniak.ohsnap365;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.widget.DatePicker;

/**
 * Contains logic for creating the email intent.
 * 
 * TODO: this class should become part of an interface so we can invoke a
 * different method in the future.
 */
public class EmailIntentComposer {

	private final static String USEDATE = "USEDATE ";
	private final static String SEPARATOR = "+";
	private final static String PREFIX = "snap";
	private final static String SUFFIX = "@ohsnap365.com";

	private static String composeEmailString(User user) {
		StringBuffer buff = new StringBuffer();
		buff.append(PREFIX);
		buff.append(SEPARATOR);
		buff.append(user.getLogin());
		buff.append(SEPARATOR);
		buff.append(user.getSecretWord());
		buff.append(SUFFIX);

		return buff.toString();
	}

	private static String composeDateString(DatePicker picker) {
		Date today = new Date(System.currentTimeMillis());
		Date pickerDate = new Date(picker.getYear()-1900, picker.getMonth(), picker.getDayOfMonth());
		
		if ((today.getYear() + 1900) == picker.getYear()
				&& today.getDate() == picker.getDayOfMonth()
				&& today.getMonth() == picker.getMonth()) 
		{
			return "";
		} else 
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			return USEDATE + format.format(pickerDate);
		}
	}

	public static Intent compose(User user, String caption, Uri imageUri, DatePicker date) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");

		String[] emails = new String[] { composeEmailString(user), };
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emails);

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, caption);
		
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, composeDateString( date ) );

		if (null != imageUri) {
			emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
		}

		return emailIntent;
	}
}
