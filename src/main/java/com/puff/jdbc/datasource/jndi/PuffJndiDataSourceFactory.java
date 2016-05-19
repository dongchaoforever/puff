package com.puff.jdbc.datasource.jndi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.puff.jdbc.core.DataBase;
import com.puff.jdbc.core.Record;
import com.puff.jdbc.datasource.DataSourceFactory;
import com.puff.jdbc.dialect.Dialect;
import com.puff.jdbc.dialect.DialectFactory;

public class PuffJndiDataSourceFactory implements DataSourceFactory {
	private Properties prop;

	@Override
	public DataBase getDataBase() {
		try {
			InitialContext initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource dataSource = (DataSource) envCtx.lookup(prop.getProperty("jndiName"));
			Dialect dialect = new DialectFactory().getDialect(prop.getProperty("dialect", "oracle"));
			DataBase dataBase = new DataBase();
			dataBase.setDataBaseName(prop.getProperty("dataBaseName"));
			Connection connection = dataSource.getConnection();
			PreparedStatement pst = null;
			ResultSet rs = null;
			List<Record> list;
			Record record;
			pst = connection.prepareStatement("select * from weixin_fans");
			rs = pst.executeQuery();
			list = new ArrayList<Record>();
			while (rs.next()) {
				ResultSetMetaData metaData = rs.getMetaData();
				int count = metaData.getColumnCount();
				record = new Record();
				for (int i = 1; i <= count; i++) {
					String columName = metaData.getColumnName(i).toLowerCase();
					record.set(columName, rs.getObject(columName));
				}
				list.add(record);
				dataBase.setDataSource(dataSource);
				dataBase.setDialect(dialect);
			}
			return dataBase;

		} catch (Exception e) {
			throw new RuntimeException("init jndi datasource fail", e);
		}
	}

	@Override
	public void init(Properties prop) {
		this.prop = prop;
	}

	@Override
	public boolean start() {
		return false;
	}

	@Override
	public boolean stop() {
		return false;
	}

}
