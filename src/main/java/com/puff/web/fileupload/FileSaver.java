package com.puff.web.fileupload;

public interface FileSaver<T> {

	T save(FileUpload fileUpload);

}