package com.nhutniak.ohsnap365;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {
	@DatabaseField(id = true)
	private String login;
	@DatabaseField
	private String secretWord;

	public User() {

	}

	public User(String login, String secretWord) {
		this.login = login;
		this.secretWord = secretWord;
	}

	public String getLogin() {
		return login;
	}

	public String getSecretWord() {
		return secretWord;
	}
}
