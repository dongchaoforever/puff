package com.puff.web.fileupload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.puff.framework.utils.FileUtil;

public class FileUpload {

	private String contentType;
	private File file;
	private String fileName;
	private String parameterName;
	private String suffixName;
	private long size;

	public FileUpload(String contentType, File file, String fileName, String parameterName, String suffixName) {
		super();
		this.contentType = contentType;
		this.file = file;
		this.size = file.length();
		this.fileName = fileName;
		this.parameterName = parameterName;
		this.suffixName = suffixName;
	}

	public void save(File file) throws IOException {
		try {
			FileUtil.copy(new BufferedInputStream(new FileInputStream(this.file)), new BufferedOutputStream(new FileOutputStream(file)));
		} catch (IOException e) {
			throw e;
		} finally {
			close();
		}
	}

	public void save(String name) throws IOException {
		save(new File(name));
	}

	public <T> T save(FileSaver<T> saver) throws Exception {
		try {
			T result = saver.save(this);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	private void close() {
		if (file != null) {
			file.delete();
		}
	}

	public String getContentType() {
		return contentType;
	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return fileName;
	}

	public String getParameterName() {
		return parameterName;
	}

	public String getSuffixName() {
		return suffixName;
	}

	public long getSize() {
		return size;
	}

}