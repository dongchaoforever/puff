package com.puff.jdbc.datasource;

import com.puff.jdbc.core.DataBase;
import com.puff.plugin.Plugin;

public interface DataSourceFactory extends Plugin {

	public DataBase getDataBase();

}
