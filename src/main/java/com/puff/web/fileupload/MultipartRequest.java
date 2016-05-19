package com.puff.web.fileupload;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.puff.core.Puff;
import com.puff.exception.ExceptionUtil;
import com.puff.framework.utils.IOUtil;
import com.puff.framework.utils.IdentityUtil;
import com.puff.framework.utils.StringUtil;
import com.puff.web.mvc.PuffContext;

@SuppressWarnings("rawtypes")
public class MultipartRequest {

	private HashMap<String, FileUpload> files;
	private Map<String, String[]> paramMap;

	public Enumeration getParameterNames() {
		final Iterator<Entry<String, String[]>> iterator = paramMap.entrySet().iterator();
		return new Enumeration() {
			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public String nextElement() {
				return iterator.next().getKey();
			}
		};
	}

	public String getParameter(String name) {
		String[] arr = paramMap.get(name);
		if (arr != null && arr.length > 0) {
			return arr[0];
		}
		return null;
	}

	public Map<String, String[]> getParameterMap() {
		return paramMap;
	}

	public String[] getParameterValues(String name) {
		return paramMap.get(name);
	}

	private MultipartRequest(HashMap<String, FileUpload> files, Map<String, String[]> paramMap) {
		this.files = files;
		this.paramMap = paramMap;
	}

	public FileUpload getFile(String name) {
		return files.get(name);
	}

	public List<FileUpload> getFiles() {
		return new ArrayList<FileUpload>(files.values());
	}

	public static MultipartRequest getMultipartRequest(long maxUploadSize) throws UploadOverFlowException, IOException {
		if (maxUploadSize < 0) {
			throw new IllegalArgumentException("The param maxUploadSize must gt 0");
		}
		HttpServletRequest request = PuffContext.getRequest();
		String contentType = request.getContentType();
		int contentLength = request.getContentLength();
		if (contentType == null || !contentType.startsWith("multipart/form-data")) {
			return null;
		}
		int start = contentType.indexOf("boundary=");
		int boundaryLen = new String("boundary=").length();
		String boundary = contentType.substring(start + boundaryLen);
		boundary = "--" + boundary;
		boundaryLen = bytesLen(boundary);
		byte buffer[] = new byte[contentLength];
		int once = 0;
		int total = 0;
		DataInputStream in = null;
		HashMap<String, FileUpload> files = new HashMap<String, FileUpload>();
		HashMap<String, String[]> params = new HashMap<String, String[]>();
		try {
			in = new DataInputStream(request.getInputStream());
			while ((total < contentLength) && (once >= 0)) {
				once = in.read(buffer, total, contentLength);
				total += once;
			}
			int pos1 = 0;
			int pos0 = byteIndexOf(buffer, boundary, 0);
			do {
				pos0 += boundaryLen;
				pos1 = byteIndexOf(buffer, boundary, pos0);
				if (pos1 == -1) {
					break;
				}
				pos0 += 2;
				byte[] subBytes = subBytes(buffer, pos0, pos1 - 2);
				boolean result = parseFile(subBytes, files, maxUploadSize);
				if (!result) {
					parseForm(subBytes, params);
				}
				pos0 = pos1;
			} while (true);
		} catch (UploadOverFlowException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtil.close(in);
		}
		return new MultipartRequest(files, params);
	}

	public static MultipartRequest getMultipartRequest() throws IOException, UploadOverFlowException {
		return getMultipartRequest(Puff.getDefMaxUploadSize());
	}

	private static void parseForm(byte[] buffer, HashMap<String, String[]> params) {
		String[] tokens = { "name=\"", "\"\r\n\r\n" };
		int[] position = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			position[i] = byteIndexOf(buffer, tokens[i], 0);
		}
		String paramName = subBytesString(buffer, position[0] + bytesLen(tokens[0]), position[1]);
		byte[] bytes = subBytes(buffer, position[1] + bytesLen(tokens[1]), buffer.length);
		String value = new String(bytes);
		String[] newArr = new String[] { value };
		if (params.containsKey(paramName)) {
			String[] old = params.get(paramName);
			newArr = new String[old.length + 1];
			newArr[0] = value;
			System.arraycopy(old, 0, newArr, 1, old.length);
		}
		params.put(paramName, newArr);
	}

	private static boolean parseFile(byte[] buffer, HashMap<String, FileUpload> files, long maxUploadSize) throws UploadOverFlowException {
		String[] tokens = { "name=\"", "\"; filename=\"", "\"\r\n", "Content-Type: ", "\r\n\r\n" };
		int[] position = new int[tokens.length];
		for (int i = 0; i < tokens.length; i++) {
			position[i] = byteIndexOf(buffer, tokens[i], 0);
		}
		if (position[1] > 0 && position[1] < position[2]) {
			String paramName = subBytesString(buffer, position[0] + bytesLen(tokens[0]), position[1]);
			String fileName = subBytesString(buffer, position[1] + bytesLen(tokens[1]), position[2]);
			if (StringUtil.empty(fileName)) {
				return true;
			}
			fileName = new File(fileName).getName();
			String contentType = subBytesString(buffer, position[3] + bytesLen(tokens[3]), position[4]);
			byte[] bytes = subBytes(buffer, position[4] + bytesLen(tokens[4]), buffer.length);
			if (bytes.length > maxUploadSize) {
				throw new UploadOverFlowException(paramName, fileName, bytes.length, maxUploadSize);
			}
			BufferedOutputStream bos = null;
			FileOutputStream fos = null;
			String suffixName = "";
			File file = null;
			try {
				int i = fileName.lastIndexOf(".");
				if (i != -1) {
					suffixName = fileName.substring(i + 1);
				}
				file = File.createTempFile(IdentityUtil.uuid32(), "." + suffixName);
				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos);
				bos.write(bytes);
			} catch (Exception e) {
				ExceptionUtil.throwRuntime(e);
			} finally {
				IOUtil.close(fos);
				IOUtil.close(bos);
			}
			files.put(paramName, new FileUpload(contentType, file, fileName, paramName, suffixName));
			return true;
		} else {
			return false;
		}
	}

	public static int byteIndexOf(byte[] source, String search, int start) {
		return byteIndexOf(source, search.getBytes(), start);
	}

	private static int byteIndexOf(byte[] source, byte[] search, int start) {
		int i;
		if (search.length == 0) {
			return 0;
		}
		int max = source.length - search.length;
		if (max < 0) {
			return -1;
		}
		if (start > max) {
			return -1;
		}
		if (start < 0) {
			start = 0;
		}
		searchForFirst: for (i = start; i <= max; i++) {
			if (source[i] == search[0]) {
				int k = 1;
				while (k < search.length) {
					if (source[k + i] != search[k])
						continue searchForFirst;
					k++;
				}
				return i;
			}
		}
		return -1;
	}

	private static byte[] subBytes(byte[] source, int from, int end) {
		byte[] result = new byte[end - from];
		System.arraycopy(source, from, result, 0, end - from);
		return result;
	}

	private static String subBytesString(byte[] source, int from, int end) {
		try {
			return new String(subBytes(source, from, end), Puff.getEncoding());
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	private static int bytesLen(String s) {
		return s.getBytes().length;
	}

}
