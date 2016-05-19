package com.puff.jdbc.core;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.puff.core.Puff;
import com.puff.framework.parse.XNode;
import com.puff.framework.parse.XPathParser;
import com.puff.framework.utils.IOUtil;
import com.puff.framework.utils.PathUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.jdbc.datasource.DataSourceFactory;
import com.puff.log.Log;
import com.puff.log.LogFactory;

public class DB {

	private static final Log log = LogFactory.get();

	public static void start() throws Exception {
		InputStream inputStream = PathUtil.fromClassPath("/db.xml");
		if (inputStream == null) {
			return;
		}
		XPathParser parser = new XPathParser(inputStream);
		XNode evalNode = parser.evalNode("/Puff");
		List<XNode> evalNodes = evalNode.evalNodes("dataBase");
		if (evalNodes != null && evalNodes.size() > 0) {
			log.info("start init database ... ");
			for (XNode parnet : evalNodes) {
				Properties prop = new Properties();
				String dataBaseName = parnet.getStringAttribute("name");
				String className = parnet.getStringAttribute("class");
				if (StringUtil.empty(className)) {
					throw new IllegalArgumentException(
							" please set the property 'class' in dataSource element at ds.xml, and the class must implements com.puff.jdbc.datasource.DataSourceFactory ");
				}
				Class<?> clazz = Class.forName(className);
				if (!DataSourceFactory.class.isAssignableFrom(clazz)) {
					throw new IllegalArgumentException("The class: '" + className + "' must implements com.puff.jdbc.datasource.DataSourceFactory ");
				}
				for (XNode child : parnet.getChildren()) {
					String name = child.getStringAttribute("name");
					String value = child.getStringAttribute("value");
					if (StringUtil.hasEmpty(name, value))
						continue;
					prop.put(name, value);
				}
				prop.put("dataBaseName", StringUtil.empty(dataBaseName, DbManager.PUFF_DEFAULT_DATABASE));
				DataSourceFactory factory = (DataSourceFactory) clazz.newInstance();
				factory.init(prop);
				factory.start();
				DataBase dataBase = factory.getDataBase();
				Connection conn = null;
				try {
					conn = dataBase.getConnection();
					DatabaseMetaData dbmd = conn.getMetaData();
					dataBase.setDatabaseProductVersion(dbmd.getDatabaseProductVersion());
					dataBase.setDriverVersion(dbmd.getDriverVersion());
				} catch (SQLException e) {
					throw e;
				} finally {
					dataBase.close(conn);
				}
				Puff.addPlugin(factory);
				log.info("dataBase start:" + dataBase);
				log.info("dataBase info:" + dataBase.version());
				DbManager.addDataBase(dataBase);
			}
		}
		IOUtil.close(inputStream);
	}

}
