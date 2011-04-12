package com.nhutniak.ohsnap365;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;

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

	private static String composeDateString(Calendar calendar) {
		Date today = new Date(System.currentTimeMillis());
		Date pickerDate = new Date(calendar.get(Calendar.YEAR) - 1900, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

		if (today.getYear() == pickerDate.getYear()
				&& today.getDate() == pickerDate.getDate()
				&& today.getMonth() == pickerDate.getMonth()) 
		{
			return "";
		} else 
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			return USEDATE + format.format(pickerDate);
		}
	}

	public static Intent compose(User user, String caption, Uri imageUri, Calendar calendar) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");

		String[] emails = new String[] { composeEmailString(user), };
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emails);

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, caption);

		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, composeDateString( calendar ) );

		if (null != imageUri) {
			emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
		}

		return emailIntent;
	}
}
