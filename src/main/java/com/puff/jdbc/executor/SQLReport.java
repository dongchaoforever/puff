package com.puff.jdbc.executor;

import java.text.MessageFormat;
import java.util.List;

import com.puff.log.Log;
import com.puff.log.LogFactory;

public class SQLReport {
	private static final Log log = LogFactory.get(SQLReport.class);

	public static final String SQL_QUERY_ELAPSED = "Execute SQL query ''{0}'' elapsed {1} millis";
	public static final String SQL_COMMAND_ELAPSED = "Execute SQL command ''{0}'' elapsed {1} millis";
	public static final String SQL_EXECUTE_FAILED = "Execute SQL failed: ''{0}'' {1}";
	public static final String SQL_PROCEDURE_ELAPSED = "{0} success, elapsed {1} millis";
	public static final String SQL_PROCEDURE_FAILED = "{0} failed:  {1}";
	public static final String SQL_PARAMETER = "Parameter {0}";

	public static void report(String key, Object[] arguments) {
		log.info(MessageFormat.format(key, arguments));
	}

	public static void parameter(String key, List<Object> param) {
		log.info(MessageFormat.format(key, handleParameter(param)));
	}

	public static void callProcedureFail(String name, Exception e, List<Object> param) {
		log.error(MessageFormat.format(SQL_PROCEDURE_FAILED, new Object[] { name, e.getMessage() }));
		if (param != null && param.size() > 0) {
			parameter(SQL_PARAMETER, param);
		}
	}

	public static void executeFail(String sql, Exception e, List<Object> param) {
		log.error(MessageFormat.format(SQL_EXECUTE_FAILED, new Object[] { sql, e.getMessage() }));
		if (param != null && param.size() > 0) {
			parameter(SQL_PARAMETER, param);
		}
	}

	public static Object handleParameter(List<Object> param) {
		StringBuilder sb = new StringBuilder().append("{");
		for (int i = 0, len = param.size(); i < len; i++) {
			sb.append(param.get(i));
			if (i < len - 1) {
				sb.append(",");
			}
		}
		return sb.append("}");
	}

}
