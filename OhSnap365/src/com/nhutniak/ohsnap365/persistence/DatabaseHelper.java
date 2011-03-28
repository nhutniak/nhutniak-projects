package com.nhutniak.ohsnap365.persistence;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import com.nhutniak.ohsnap365.User;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "ohSnap365.db";

	private static final int DATABASE_VERSION = 1;

	private Dao<User, String> userDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, User.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		Log.i(DatabaseHelper.class.getName(), "onUpgrade");
		try {
			TableUtils.dropTable(connectionSource, User.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop database", e);
			throw new RuntimeException(e);
		}
	}

	public Dao<User, String> getUserDao() throws SQLException {
		if (null == userDao) {
			userDao = getDao(User.class);
		}
		return userDao;
	}

	@Override
	public void close() {
		super.close();
		userDao = null;
	}
}
