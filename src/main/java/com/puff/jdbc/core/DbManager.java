package com.puff.jdbc.core;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.puff.framework.utils.StringUtil;
import com.puff.log.Log;
import com.puff.log.LogFactory;

/**
 * DbManager
 */
public final class DbManager {
	private static final Log log = LogFactory.get(DbManager.class);
	private static final Map<String, DataBase> dataBaseMap = new HashMap<String, DataBase>();
	public static final String PUFF_DEFAULT_DATABASE = "__PUFF_DEFAULT_DATABASE";

	private DbManager() {
	}

	public static Map<String, DataBase> getDataBases() {
		return dataBaseMap;
	}

	public static void addDataBase(DataBase dataBase) {
		String dbName = StringUtil.empty(dataBase.getDataBaseName(), PUFF_DEFAULT_DATABASE);
		if (dataBaseMap.containsKey(dbName)) {
			String err = "★★★★★ the dbName:" + dbName + " is already exits ,please check your db.xml ";
			log.error(err);
			throw new IllegalArgumentException(err);
		}
		dataBaseMap.put(dbName, dataBase);
	}

	public static final DataBase getDataBase() {
		return getDataBase(PUFF_DEFAULT_DATABASE);
	}

	public static final DataBase getDataBase(String name) {
		DataBase dataBase = dataBaseMap.get(name);
		if (dataBase == null) {
			String err = "★★★★★ can not get database use the name:" + name + ",pleade check your db.xml";
			log.error(err);
			throw new RuntimeException(err);
		}
		return dataBase;
	}

	public static Connection getConnection() {
		return getDataBase().getConnection();
	}

	public static Connection getConnection(String name) {
		return getDataBase(name).getConnection();
	}

}
