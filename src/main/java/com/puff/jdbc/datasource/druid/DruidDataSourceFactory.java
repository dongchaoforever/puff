package com.puff.jdbc.datasource.druid;

import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.puff.core.Puff;
import com.puff.jdbc.core.DataBase;
import com.puff.jdbc.datasource.DataSourceFactory;
import com.puff.jdbc.dialect.Dialect;
import com.puff.jdbc.dialect.DialectFactory;
import com.puff.web.mvc.own.DruidStatViewController;

/**
 * Druid.
 */
public class DruidDataSourceFactory implements DataSourceFactory {
	private Properties prop;
	private DruidDataSource dataSource;

	@Override
	public DataBase getDataBase() {
		Dialect dialect = new DialectFactory().getDialect(dataSource.getDriverClassName());
		DataBase dataBase = new DataBase();
		dataBase.setDataBaseName(prop.getProperty("dataBaseName"));
		dataBase.setDataSource(dataSource);
		dataBase.setDbType(dialect.getDbType());
		dataBase.setDialect(dialect);
		dataBase.setUrl(dataSource.getUrl());
		dataBase.setUserName(dataSource.getUsername());
		dataBase.setPassword(dataSource.getPassword());
		dataBase.setShowSql(Boolean.valueOf(prop.getProperty("showSql", "true")));
		return dataBase;
	}

	@Override
	public void init(Properties prop) {
		this.prop = prop;
		try {
			Puff.addSysMapping(DruidStatViewController.class, "index");
			Puff.addSysMapping(DruidStatViewController.class, "json");
			Puff.addSysMapping(DruidStatViewController.class, "res");
		} catch (Exception e) {
		}

	}

	@Override
	public boolean start() {
		try {
			dataSource = (DruidDataSource) com.alibaba.druid.pool.DruidDataSourceFactory.createDataSource(prop);
			dataSource.init();
		} catch (Exception e) {
			stop();
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean stop() {
		if (dataSource != null) {
			dataSource.close();
		}
		return true;
	}
}
