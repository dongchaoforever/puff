package com.puff.jdbc.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.puff.jdbc.dialect.Dialect;
import com.puff.jdbc.dialect.OracleDialect;

public class DataBase {

	private String dataBaseName;

	private DBType dbType;

	private String url;

	private String userName;

	private String password;

	private boolean showSql;

	private String databaseProductVersion;

	private String driverVersion;

	private DataSource dataSource;

	private int transactionLevel = Connection.TRANSACTION_READ_COMMITTED;

	private Dialect dialect;

	private static final ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

	public DataBase() {
		this.dialect = new OracleDialect();
	}

	public DataBase(DataSource dataSource, Dialect dialect) {
		this.dataSource = dataSource;
		this.dialect = dialect;
	}

	public String getDataBaseName() {
		return dataBaseName;
	}

	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}

	public DBType getDbType() {
		return dbType;
	}

	public void setDbType(DBType dbType) {
		this.dbType = dbType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public String getDatabaseProductVersion() {
		return databaseProductVersion;
	}

	public void setDatabaseProductVersion(String databaseProductVersion) {
		this.databaseProductVersion = databaseProductVersion;
	}

	public String getDriverVersion() {
		return driverVersion;
	}

	public void setDriverVersion(String driverVersion) {
		this.driverVersion = driverVersion;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int getTransactionLevel() {
		return transactionLevel;
	}

	public void setTransactionLevel(int transactionLevel) {
		this.transactionLevel = transactionLevel;
	}

	public Dialect getDialect() {
		return dialect;
	}

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	public static ThreadLocal<Connection> getThreadLocal() {
		return threadLocal;
	}

	public static boolean inTransaction() {
		return getThreadLocalConnection() != null;
	}

	public static final Connection getThreadLocalConnection() {
		return threadLocal.get();
	}

	public static final void setThreadLocalConnection(Connection connection) {
		threadLocal.set(connection);
	}

	public static final void removeThreadLocalConnection() {
		threadLocal.remove();
	}

	public static final void beginTransaction() throws SQLException {
		Connection conn = getThreadLocalConnection();
		if (conn != null) {
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	public static final void commitTransaction() throws SQLException {
		Connection conn = getThreadLocalConnection();
		if (conn != null) {
			try {
				conn.commit();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				conn.close();
			}
		}
	}

	public static final void rollbackTransaction() throws SQLException {
		Connection conn = getThreadLocalConnection();
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				throw new SQLException(e);
			} finally {
				conn.close();
			}
		}
	}

	public Connection getConnection() {
		try {
			Connection conn = threadLocal.get();
			if (null == conn) {
				conn = dataSource.getConnection();
			}
			return conn;
		} catch (Exception e) {
			throw new RuntimeException("Can not get connection from dataBase,dataBase:" + dataBaseName, e);
		}
	}

	/**
	 * Close ResultSet、Statement、Connection ThreadLocal support declare
	 * transaction.
	 */
	public final void close(ResultSet rs, Statement st, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
			}
		}

		if (threadLocal.get() == null) {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public final void close(Statement st, Connection conn) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
			}
		}
		if (threadLocal.get() == null) {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public final void close(Connection conn) {
		if (threadLocal.get() == null)
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
	}

	public String toString() {
		return " [dataBaseName=" + dataBaseName + ", dbType=" + dbType + ", url=" + url + ", userName=" + userName + ", password=**********, showSql=" + showSql + ", dialect="
				+ dialect.getClass().getSimpleName() + ", transactionLevel=" + transactionLevel + " ]";
	}

	public String version() {
		return " [databaseProductVersion=" + databaseProductVersion + ", driverVersion=" + driverVersion + "]";
	}

}
