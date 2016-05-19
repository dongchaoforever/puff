package com.puff.jdbc.datasource.c3p0;

import java.beans.PropertyVetoException;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.puff.framework.utils.StringUtil;
import com.puff.jdbc.core.DataBase;
import com.puff.jdbc.datasource.DataSourceFactory;
import com.puff.jdbc.dialect.Dialect;
import com.puff.jdbc.dialect.DialectFactory;

public class C3P0DataSourceFactory implements DataSourceFactory {
	private Properties prop;
	private String url;
	private String username;
	private String password;
	private String driverClass = "com.mysql.jdbc.Driver";
	private int maxPoolSize = 100;
	private int minPoolSize = 10;
	private int initialPoolSize = 10;
	private int maxIdleTime = 20;
	private int acquireIncrement = 2;

	private ComboPooledDataSource dataSource;

	private void initC3p0Properties(String url, String username, String password, String driverClass, Integer maxPoolSize, Integer minPoolSize, Integer initialPoolSize,
			Integer maxIdleTime, Integer acquireIncrement) {
		this.url = url;
		this.username = username;
		this.password = password;
		this.driverClass = driverClass != null ? driverClass : this.driverClass;
		this.maxPoolSize = maxPoolSize != null ? maxPoolSize : this.maxPoolSize;
		this.minPoolSize = minPoolSize != null ? minPoolSize : this.minPoolSize;
		this.initialPoolSize = initialPoolSize != null ? initialPoolSize : this.initialPoolSize;
		this.maxIdleTime = maxIdleTime != null ? maxIdleTime : this.maxIdleTime;
		this.acquireIncrement = acquireIncrement != null ? acquireIncrement : this.acquireIncrement;
	}

	@Override
	public boolean start() {
		dataSource = new ComboPooledDataSource();
		dataSource.setJdbcUrl(url);
		dataSource.setUser(username);
		dataSource.setPassword(password);
		try {
			dataSource.setDriverClass(driverClass);
		} catch (PropertyVetoException e) {
			dataSource = null;
			System.err.println("C3P0 DataSource start error");
			throw new RuntimeException(e);
		}
		dataSource.setMaxPoolSize(maxPoolSize);
		dataSource.setMinPoolSize(minPoolSize);
		dataSource.setInitialPoolSize(initialPoolSize);
		dataSource.setMaxIdleTime(maxIdleTime);
		dataSource.setAcquireIncrement(acquireIncrement);
		return true;
	}

	private Integer toInt(String str) {
		if (StringUtil.empty(str)) {
			return null;
		}
		return Integer.parseInt(str);
	}

	@Override
	public boolean stop() {
		if (dataSource != null)
			dataSource.close();
		return true;
	}

	@Override
	public void init(Properties ps) {
		this.prop = ps;
		initC3p0Properties(ps.getProperty("url"), ps.getProperty("username"), ps.getProperty("password"), ps.getProperty("driverClass"), toInt(ps.getProperty("maxPoolSize")),
				toInt(ps.getProperty("minPoolSize")), toInt(ps.getProperty("initialPoolSize")), toInt(ps.getProperty("maxIdleTime")), toInt(ps.getProperty("acquireIncrement")));

	}

	@Override
	public DataBase getDataBase() {
		Dialect dialect = new DialectFactory().getDialect(driverClass);
		DataBase dataBase = new DataBase();
		dataBase.setDataBaseName(prop.getProperty("dataBaseName"));
		dataBase.setDataSource(dataSource);
		dataBase.setDbType(dialect.getDbType());
		dataBase.setDialect(dialect);
		dataBase.setUrl(url);
		dataBase.setUserName(username);
		dataBase.setPassword(password);
		dataBase.setShowSql(Boolean.valueOf(prop.getProperty("showSql", "true")));
		return dataBase;
	}

}
