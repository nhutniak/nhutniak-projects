package com.nhutniak.ohsnap365;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OpenHelperManager.SqliteOpenHelperFactory;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.nhutniak.ohsnap365.persistence.DatabaseHelper;

/**
 * Responsible for providing the mechanisms to communicate with the data.
 */
public class DatabaseActivity {

	private final Context m_context;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            the {@link Context} to use for obtaining information for the
	 *            database.
	 */
	public DatabaseActivity(final Context context) {
		OpenHelperManager.setOpenHelperFactory(new SqliteOpenHelperFactory() {
			public OrmLiteSqliteOpenHelper getHelper(Context context) {
				return new DatabaseHelper(context);
			}
		});
		
		m_context = context;
	}

	/**
	 * Cleans up the database.
	 */
	public void release() {
		OpenHelperManager.release();
	}

	// Provides access to the DatabaseHelper class
	private DatabaseHelper getHelper() {
		return (DatabaseHelper) OpenHelperManager.getHelper(m_context);
	}

	/**
	 * Saves the user information to the storage device.
	 * 
	 * @param loginName
	 *            a {@link String}
	 * @param secretWord
	 *            a {@link String}
	 */
	public void saveUserInfo(final String loginName, final String secretWord) {
		try {
			Dao<User, String> userDao = getHelper().getUserDao();
			User user = new User(loginName, secretWord);

			// Delete all for now
			userDao.delete(userDao.queryForAll());

			userDao.create(user);
		} catch (SQLException e) {
			Log.e(getClass().getName(), "Could not load database", e);
		}
	}

	/**
	 * Retrieves the last saved user from storage.
	 * 
	 * @return a {@link User} or null if there is nothing stored.
	 */
	public User getSavedUser() {
		User user = null;
		try {
			Dao<User, String> simpleDao = getHelper().getUserDao();
			List<User> queryForAll = simpleDao.queryForAll();

			if (1 >= queryForAll.size()) {
				// Hardwire this to always return the first result for now...
				user = queryForAll.get(0);
			} else {
				// Only ever expect one user in the database.
			}
		} catch (SQLException e) {
			Log.e(getClass().getName(), "Database exception", e);
		}
		return user;
	}
}
