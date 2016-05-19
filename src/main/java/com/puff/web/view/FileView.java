package com.puff.web.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.puff.exception.ViewException;
import com.puff.framework.utils.IOUtil;

public class FileView extends View {

	private String fileName;
	private File file;
	private InputStream in;

	public FileView(File file) {
		this.file = file;
		this.fileName = file.getName();
		try {
			this.in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public FileView(String fileName) {
		this(new File(fileName));
	}

	public FileView(String fileName, InputStream in) {
		this.fileName = fileName;
		this.in = in;
	}

	/**
	 * 
	 */

	@Override
	public void view() {
		if (file == null && in == null) {
			return;
		}
		try {
			fileName = URLEncoder.encode(fileName, encoding);
			String userAgent = request.getHeader("user-agent");
			// 如果没有UA，则默认使用IE的方式进行编码
			String rtn = "filename=\"" + fileName + "\"";
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				// IE浏览器，只能采用URLEncoder编码
				if (userAgent.indexOf("msie") != -1) {
					rtn = "filename=\"" + fileName + "\"";
				}
				// Opera浏览器只能采用filename*
				else if (userAgent.indexOf("opera") != -1) {
					rtn = "filename*=UTF-8''" + fileName;
				}
				// Safari浏览器，只能采用ISO编码的中文输出
				else if (userAgent.indexOf("safari") != -1) {
					rtn = "filename=\"" + new String(fileName.getBytes(encoding), "ISO8859-1") + "\"";
				}
				// FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出       
				else if (userAgent.indexOf("mozilla") != -1) {
					rtn = "filename=\"" + new String(fileName.getBytes(encoding), "ISO8859-1") + "\"";
				}
			}
			response.addHeader("Content-Disposition", "attachment;" + rtn);
		} catch (UnsupportedEncodingException e) {
		}
		if (file != null) {
			response.addHeader("Content-Length", "" + file.length());
		}
		response.setContentType("application/octet-stream");
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(in);
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[1024 * 16];
			for (int n = -1; (n = bis.read(buffer)) != -1;) {
				bos.write(buffer, 0, n);
			}
			bos.flush();
		} catch (Exception e) {
			throw new ViewException(e);
		} finally {
			IOUtil.close(bis);
			IOUtil.close(bos);
		}
	}

}
