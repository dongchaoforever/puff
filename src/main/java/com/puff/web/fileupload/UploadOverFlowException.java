package com.puff.web.fileupload;

public class UploadOverFlowException extends Exception {

	private static final long serialVersionUID = 5180542907855842627L;

	public final String paramName;

	public final String fileName;

	public final long fileSize;

	public UploadOverFlowException(String paramName, String fileName, long fileSize, long maxUploadSize) {
		super(new StringBuilder("The maxUploadSize is ").append(maxUploadSize).append(" but'").append(paramName).append("' uoload file'").append(fileName).append("' size is ")
				.append(fileSize).toString());
		this.paramName = paramName;
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

}
