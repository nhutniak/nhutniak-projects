package com.nhutniak.ohsnap365;

import android.content.Intent;
import android.net.Uri;

/**
 * Contains logic for creating the email intent.
 * 
 * TODO: this class should become part of an interface so we can invoke a
 * different method in the future.
 */
public class EmailIntentComposer {

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

	public static Intent compose(User user, String caption, Uri imageUri) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");

		String[] emails = new String[] { composeEmailString(user), };
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emails);

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, caption);

		if (null != imageUri) {
			emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
		}

		return emailIntent;
	}
}
