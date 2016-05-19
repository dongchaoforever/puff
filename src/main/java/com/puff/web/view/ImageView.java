package com.puff.web.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.puff.exception.ExceptionUtil;
import com.puff.exception.ViewException;

public class ImageView extends View {

	static enum ImageType {
		JPG("image/jpeg"), BMP("image/bmp"), PNG("image/png"), GIF("image/gif"), ICO("image/x-icon");

		private String contentType;

		private ImageType(String contentType) {
			this.contentType = contentType;
		}

		public String getContentType() {
			return contentType;
		}
	}

	/**
	 * 
	 */

	private InputStream in;

	private ImageType imageType = ImageType.JPG;

	public ImageView(InputStream in) {
		this.in = in;
	}

	public ImageView(ImageType imageType, InputStream in) {
		this(in);
		this.imageType = imageType;
	}

	public ImageView(File file) {
		try {
			this.in = new FileInputStream(file);
			String fileName = file.getName().toLowerCase();
			if (fileName.endsWith(".gif")) {
				this.imageType = ImageType.GIF;
			}
			if (fileName.endsWith(".png")) {
				this.imageType = ImageType.PNG;
			}
			if (fileName.endsWith(".bmp")) {
				this.imageType = ImageType.BMP;
			}
			if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
				this.imageType = ImageType.JPG;
			}
			if (fileName.endsWith(".ico")) {
				this.imageType = ImageType.ICO;
			}
		} catch (FileNotFoundException e) {
			ExceptionUtil.throwRuntime(e);
		}
	}

	public ImageView(String fileName) {
		this(new File(fileName));
	}

	@Override
	public void view() {
		if (in == null) {
			return;
		}
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		OutputStream output = null;
		try {
			output = response.getOutputStream();
			response.setContentType(imageType.getContentType());
			bis = new BufferedInputStream(in);
			bos = new BufferedOutputStream(output);
			byte data[] = new byte[1024 * 16];
			int size = bis.read(data);
			while (size != -1) {
				bos.write(data, 0, size);
				size = bis.read(data);
			}
			bos.flush();
		} catch (Exception e) {
			throw new ViewException(e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
