package com.puff.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.puff.exception.ExceptionUtil;

abstract public class FileUtil {

	public static void delete(File dir) {
		dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					delete(f);
				} else {
					f.delete();
				}
				return false;
			}
		});
		dir.delete();
	}

	public static void delete(String delpath) {
		File file = new File(delpath);
		if (!file.isDirectory()) {
			file.delete();
		} else {
			String[] filelist = file.list();
			for (int i = 0, len = filelist.length; i < len; i++) {
				File delfile = new File(delpath + File.separator + filelist[i]);
				if (!delfile.isDirectory()) {
					delfile.delete();
				} else {
					delete(delpath + File.separator + filelist[i]);
				}
			}
			file.delete();
		}
	}

	public static long copy(File src, File dest, long position, long count) throws IOException {
		FileInputStream in = new FileInputStream(src);
		FileOutputStream out = new FileOutputStream(dest);
		FileChannel inChannel = in.getChannel();
		FileChannel outChannel = out.getChannel();
		try {
			return inChannel.transferTo(position, count, outChannel);
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	public static final int BUFFER_SIZE = 4096;

	// ---------------------------------------------------------------------
	// Copy methods for java.io.File
	// ---------------------------------------------------------------------

	/**
	 * Copy the contents of the given input File to the given output File.
	 * 
	 * @param in
	 *            the file to copy from
	 * @param out
	 *            the file to copy to
	 * @return the number of bytes copied
	 * @throws java.io.IOException
	 *             in case of I/O errors
	 */
	public static int copy(File in, File out) throws IOException {
		return copy(new BufferedInputStream(new FileInputStream(in)), new BufferedOutputStream(new FileOutputStream(out)));
	}

	/**
	 * Copy the contents of the given byte array to the given output File.
	 * 
	 * @param in
	 *            the byte array to copy from
	 * @param out
	 *            the file to copy to
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public static void copy(byte[] in, File out) throws IOException {
		ByteArrayInputStream inStream = new ByteArrayInputStream(in);
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(out));
		copy(inStream, outStream);
	}

	/**
	 * Copy the contents of the given input File into a new byte array.
	 * 
	 * @param in
	 *            the file to copy from
	 * @return the new byte array that has been copied to
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public static byte[] copyToByteArray(File in) throws IOException {
		return copyToByteArray(new BufferedInputStream(new FileInputStream(in)));
	}

	// ---------------------------------------------------------------------
	// Copy methods for java.io.InputStream / java.io.OutputStream
	// ---------------------------------------------------------------------

	/**
	 * Copy the contents of the given InputStream to the given OutputStream.
	 * Closes both streams when done.
	 * 
	 * @param in
	 *            the stream to copy from
	 * @param out
	 *            the stream to copy to
	 * @return the number of bytes copied
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public static int copy(InputStream in, OutputStream out) throws IOException {
		try {
			int byteCount = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Copy the contents of the given byte array to the given OutputStream.
	 * Closes the stream when done.
	 * 
	 * @param in
	 *            the byte array to copy from
	 * @param out
	 *            the OutputStream to copy to
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public static void copy(byte[] in, OutputStream out) throws IOException {
		try {
			out.write(in);
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Copy the contents of the given InputStream into a new byte array. Closes
	 * the stream when done.
	 * 
	 * @param in
	 *            the stream to copy from
	 * @return the new byte array that has been copied to
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public static byte[] copyToByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
		copy(in, out);
		return out.toByteArray();
	}

	// ---------------------------------------------------------------------
	// Copy methods for java.io.Reader / java.io.Writer
	// ---------------------------------------------------------------------

	/**
	 * Copy the contents of the given Reader to the given Writer. Closes both
	 * when done.
	 * 
	 * @param in
	 *            the Reader to copy from
	 * @param out
	 *            the Writer to copy to
	 * @return the number of characters copied
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public static int copy(Reader in, Writer out) throws IOException {
		try {
			int byteCount = 0;
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Copy the contents of the given String to the given output Writer. Closes
	 * the write when done.
	 * 
	 * @param in
	 *            the String to copy from
	 * @param out
	 *            the Writer to copy to
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public static void copy(String in, Writer out) throws IOException {
		try {
			out.write(in);
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Copy the contents of the given Reader into a String. Closes the reader
	 * when done.
	 * 
	 * @param in
	 *            the reader to copy from
	 * @return the String that has been copied to
	 * @throws IOException
	 *             in case of I/O errors
	 */
	public static String copyToString(Reader in) throws IOException {
		StringWriter out = new StringWriter();
		copy(in, out);
		return out.toString();
	}

	public static List<String> readLines(Reader input) throws IOException {
		BufferedReader reader = new BufferedReader(input);
		List<String> list = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			list.add(line);
			line = reader.readLine();
		}
		return list;
	}

	public static String readFile(File file) throws IOException {
		Reader in = new FileReader(file);
		StringWriter out = new StringWriter();
		copy(in, out);
		return out.toString();
	}

	public static String readFile(File file, String encoding) throws IOException {
		InputStream inputStream = new FileInputStream(file);
		return toString(encoding, inputStream);
	}

	public static String toString(InputStream inputStream) throws UnsupportedEncodingException, IOException {
		Reader reader = new InputStreamReader(inputStream);
		StringWriter writer = new StringWriter();
		copy(reader, writer);
		return writer.toString();
	}

	public static String toString(String encoding, InputStream inputStream) {
		StringWriter writer = null;
		try {
			Reader reader = new InputStreamReader(inputStream, encoding);
			writer = new StringWriter();
			copy(reader, writer);
		} catch (Exception e) {
			ExceptionUtil.throwRuntime(e);
		}
		return writer.toString();
	}

	public static void saveFile(File file, String content) {
		saveFile(file, content, null, false);
	}

	public static void saveFile(File file, String content, boolean append) {
		saveFile(file, content, null, append);
	}

	public static void saveFile(File file, String content, String encoding) {
		saveFile(file, content, encoding, false);
	}

	public static void saveFile(File file, String content, String encoding, boolean append) {
		try {
			FileOutputStream output = new FileOutputStream(file, append);
			Writer writer = StringUtil.empty(encoding) ? new OutputStreamWriter(output) : new OutputStreamWriter(output, encoding);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 得到相对路径
	 */
	public static String getRelativePath(File baseDir, File file) {
		if (baseDir.equals(file)) {
			return "";
		}
		if (baseDir.getParentFile() == null) {
			return file.getAbsolutePath().substring(baseDir.getAbsolutePath().length());
		}
		return file.getAbsolutePath().substring(baseDir.getAbsolutePath().length() + 1);
	}

	public static InputStream getInputStream(String file) throws FileNotFoundException {
		InputStream inputStream = null;
		if (file.startsWith("classpath:")) {
			inputStream = FileUtil.class.getClassLoader().getResourceAsStream(file.substring("classpath:".length()));
		} else {
			inputStream = new FileInputStream(file);
		}
		return inputStream;
	}

	public static File mkdir(String dir, String file) {
		if (dir == null)
			throw new IllegalArgumentException("dir must be not null");
		File result = new File(dir, file);
		parnetMkdir(result);
		return result;
	}

	public static void parnetMkdir(File outputFile) {
		if (outputFile.getParentFile() != null) {
			outputFile.getParentFile().mkdirs();
		}
	}

	public static File getFileByClassLoader(String resourceName) throws IOException {
		Enumeration<URL> urls = FileUtil.class.getClassLoader().getResources(resourceName);
		while (urls.hasMoreElements()) {
			return new File(urls.nextElement().getFile());
		}
		throw new FileNotFoundException(resourceName);
	}

	/**
	 * 获取文件后缀
	 * 
	 * @param filename
	 * @return
	 */
	public static String getExtension(String filename) {
		if (filename == null) {
			return null;
		}
		int index = filename.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return filename.substring(index);
		}
	}

	/**
	 * Deletes a directory recursively.
	 *
	 * @param directory
	 *            directory to delete
	 * @throws IOException
	 *             in case deletion is unsuccessful
	 */
	public static void deleteDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		cleanDirectory(directory);
		if (!directory.delete()) {
			String message = "Unable to delete directory " + directory + ".";
			throw new IOException(message);
		}
	}

	/**
	 * Deletes a file, never throwing an exception. If file is a directory,
	 * delete it and all sub-directories.
	 * <p>
	 * The difference between File.delete() and this method are:
	 * <ul>
	 * <li>A directory to be deleted does not have to be empty.</li>
	 * <li>No exceptions are thrown when a file or directory cannot be deleted.</li>
	 * </ul>
	 *
	 * @param file
	 *            file or directory to delete, can be <code>null</code>
	 * @return <code>true</code> if the file or directory was deleted, otherwise
	 *         <code>false</code>
	 * @since Commons IO 1.4
	 */
	public static boolean deleteQuietly(File file) {
		if (file == null) {
			return false;
		}
		try {
			if (file.isDirectory()) {
				cleanDirectory(file);
			}
		} catch (Exception e) {
		}

		try {
			return file.delete();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Cleans a directory without deleting it.
	 *
	 * @param directory
	 *            directory to clean
	 * @throws IOException
	 *             in case cleaning is unsuccessful
	 */
	public static void cleanDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) { // null if security restricted
			throw new IOException("Failed to list contents of " + directory);
		}

		IOException exception = null;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception) {
			throw exception;
		}
	}

	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			boolean filePresent = file.exists();
			if (!file.delete()) {
				if (!filePresent) {
					throw new FileNotFoundException("File does not exist: " + file);
				}
				String message = "Unable to delete file: " + file;
				throw new IOException(message);
			}
		}
	}

	/**
	 * 判断文件是否存在
	 */
	public static boolean exists(String filePath) {
		return new File(filePath).exists();
	}

	public static boolean isFile(String filePath) {
		return new File(filePath).exists() && new File(filePath).isFile();
	}

	public static boolean isDir(String filePath) {
		return new File(filePath).exists() && new File(filePath).isDirectory();
	}

	/**
	 * 获取文件后缀
	 */
	public static String getSuffix(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	/**
	 * 获取文件名
	 */
	public static String getFileName(String filePath) {
		return filePath.substring(filePath.lastIndexOf("/") + 1);
	}

	/**
	 * 获取文件不带后缀的全路径
	 */
	public static String getNoSuffixFilePath(String filePath) {
		return filePath.substring(0, filePath.lastIndexOf("."));
	}

}
