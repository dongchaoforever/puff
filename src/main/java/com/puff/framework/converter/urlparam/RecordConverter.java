package com.puff.framework.converter.urlparam;

import com.puff.jdbc.core.Record;
import com.puff.web.mvc.PuffContext;

public class RecordConverter extends Converter<Record> {

	@Override
	Record convert(String s) {
		return PuffContext.getRecord();
	}

	@Override
	Record defaultVal() {
		return new Record();
	}

}
