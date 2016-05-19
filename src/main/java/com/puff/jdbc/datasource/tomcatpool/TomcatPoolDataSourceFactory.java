package com.puff.jdbc.datasource.tomcatpool;

import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.puff.framework.utils.StringUtil;
import com.puff.jdbc.core.DataBase;
import com.puff.jdbc.datasource.DataSourceFactory;
import com.puff.jdbc.dialect.Dialect;
import com.puff.jdbc.dialect.DialectFactory;

public class TomcatPoolDataSourceFactory implements DataSourceFactory {
	private Properties prop;
	private String url;
	private String username;
	private String password;
	private String driverClass = "com.mysql.jdbc.Driver";
	private boolean jmxEnabled = true;
	private boolean testWhileIdle = false;
	private boolean testOnBorrow = true;
	private String validationQuery = "SELECT 1";
	private boolean testOnReturn = false;
	private long validationInterval = 3000;

	private int timeBetweenEvictionRunsMillis = 30000;
	private int maxActive = 100;
	private int initialSize = 10;
	private int maxWait = 10000;
	private int removeAbandonedTimeout = 60;
	private int minEvictableIdleTimeMillis = 30000;
	private int minIdle = 10;
	private boolean logAbandoned = true;
	private boolean removeAbandoned = true;
	private String jdbcInterceptors = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

	private DataSource dataSource;

	@Override
	public boolean start() {
		PoolProperties p = new PoolProperties();
		p.setUrl(url);
		p.setDriverClassName(driverClass);
		p.setUsername(username);
		p.setPassword(password);
		p.setJmxEnabled(jmxEnabled);
		p.setTestWhileIdle(testWhileIdle);
		p.setTestOnBorrow(testOnBorrow);
		p.setValidationQuery(validationQuery);
		p.setTestOnReturn(testOnReturn);
		p.setValidationInterval(validationInterval);
		p.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		p.setMaxActive(maxActive);
		p.setInitialSize(initialSize);
		p.setMaxWait(maxWait);
		p.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		p.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		p.setMinIdle(minIdle);
		p.setLogAbandoned(logAbandoned);
		p.setRemoveAbandoned(removeAbandoned);
		p.setJdbcInterceptors(jdbcInterceptors);
		dataSource = new DataSource();
		dataSource.setPoolProperties(p);
		return true;
	}

	private Integer toInt(String str, int defVal) {
		if (StringUtil.empty(str)) {
			return defVal;
		}
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defVal;
		}
	}

	private Long toLong(String str, long defVal) {
		if (StringUtil.empty(str)) {
			return defVal;
		}
		try {
			return Long.valueOf(str);
		} catch (Exception e) {
			return defVal;
		}
	}

	private boolean toBoolean(String str, boolean defVal) {
		if (StringUtil.empty(str)) {
			return false;
		}
		try {
			return Boolean.valueOf(str);
		} catch (Exception e) {
			return defVal;
		}
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
		this.url = ps.getProperty("url");
		this.username = ps.getProperty("username");
		this.password = ps.getProperty("password");
		this.driverClass = ps.getProperty("driverClass");
		this.jmxEnabled = toBoolean(ps.getProperty("jmxEnabled"), jmxEnabled);
		this.testWhileIdle = toBoolean(ps.getProperty("testWhileIdle"), testWhileIdle);
		this.testOnBorrow = toBoolean(ps.getProperty("testOnBorrow"), testOnBorrow);
		this.validationQuery = ps.getProperty("validationQuery");
		this.testOnReturn = toBoolean(ps.getProperty("testOnReturn"), testOnReturn);
		this.validationInterval = toLong(ps.getProperty("validationInterval"), validationInterval);
		this.timeBetweenEvictionRunsMillis = toInt(ps.getProperty("timeBetweenEvictionRunsMillis"), timeBetweenEvictionRunsMillis);
		this.maxActive = toInt(ps.getProperty("maxActive"), maxActive);
		this.initialSize = toInt(ps.getProperty("initialSize"), initialSize);
		this.maxWait = toInt(ps.getProperty("maxWait"), maxWait);
		this.removeAbandonedTimeout = toInt(ps.getProperty("removeAbandonedTimeout"), removeAbandonedTimeout);
		this.minEvictableIdleTimeMillis = toInt(ps.getProperty("minEvictableIdleTimeMillis"), minEvictableIdleTimeMillis);
		this.minIdle = toInt(ps.getProperty("minIdle"), minIdle);
		this.logAbandoned = toBoolean(ps.getProperty("logAbandoned"), logAbandoned);
		this.removeAbandoned = toBoolean(ps.getProperty("removeAbandoned"), removeAbandoned);
		this.jdbcInterceptors = ps.getProperty("jdbcInterceptors");
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
