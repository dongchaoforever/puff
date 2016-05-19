package com.puff.jdbc.core;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.puff.exception.ConverterExecption;
import com.puff.framework.annotation.PKType;
import com.puff.framework.converter.ConverterUtil;
import com.puff.framework.utils.IdWorker;
import com.puff.framework.utils.IdentityUtil;
import com.puff.framework.utils.StringUtil;

class PKGenerator {
	private final static IdWorker idWorker = IdentityUtil.idWorker();

	// 获取主键值
	public static Object getPKValue(Object obj, ColumnProperty cp, DataBase dataBase) throws Exception {
		PKType keyType = cp.getPkType();
		Type type = cp.getJavaType();
		Object key = cp.invokeGet(obj);
		if (key != null && StringUtil.notEmpty(key.toString())) {
			if (type == String.class) {
				return key;
			} else if (type == int.class || type == Integer.class || type == long.class || type == Long.class) {
				BigDecimal bd = new BigDecimal(key.toString());
				if (0 != bd.intValue()) {
					return key;
				}
			} else {
				throw new IllegalArgumentException("The primarykey java type only support String, int, Integer, long, Long ");
			}
		}
		switch (keyType) {
		case AUTO:
			if (DBType.ORACLE.equals(dataBase.getDbType())) {
				throw new IllegalArgumentException(" oracle cant not support auto increment ");
			}
			break;
		case SEQUENCE:
			key = getValue("select " + cp.getSeq_name() + ".nextval from dual", dataBase, type);
			break;

		case AUTO_SEQUENCE:
			if (DBType.ORACLE.equals(dataBase.getDbType())) {
				key = getValue("select " + cp.getSeq_name() + ".nextval from dual", dataBase, type);
			} else if (DBType.MYSQL.equals(dataBase.getDbType()) || DBType.POSTGRESQL.equals(dataBase.getDbType())) {
				cp.setPkType(PKType.AUTO);
			}
			break;
		case UUID:
			key = IdentityUtil.uuid32();
			break;
		case OBJECTID:
			key = IdentityUtil.getObjectId();
			break;
		case CUSTOM:
			if (null == key) {
				throw new IllegalArgumentException("Your set the @PrimaryKey is CUSTOM ...but the value is null, so you must manual set  ");
			}
			break;
		case IDWORKER:
			key = idWorker.nextStrId();
			break;
		default:
			key = IdentityUtil.getObjectId();
			break;
		}
		return key;
	}

	private static Object getValue(String sql, DataBase dataBase, Type type) throws SQLException, ConverterExecption {
		Object value = null;
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			conn = dataBase.getConnection();
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			if (rs.next()) {
				value = ConverterUtil.bigDecimal2Other(rs.getBigDecimal(1), type);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			dataBase.close(rs, pstm, conn);
		}
		return value;
	}
}
