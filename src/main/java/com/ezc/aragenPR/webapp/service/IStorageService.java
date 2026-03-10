package com.ezc.aragenPR.webapp.service;


import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface IStorageService {

	void init();

	void store(MultipartFile file);

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename);

	void deleteAll();

	String saveFileTemporarily(MultipartFile file);



	void saveFile(String uuid, Integer docId,String userId);


	String saveFilePermanently(Integer docId, MultipartFile File, String docType,String docItemType);

	ResponseEntity<Resource> downloadFile(String fileName, Integer docNo, String docType,String docItemType, String action);

	void deleteFile(String filename, Integer docNo, String docType, String docItemType);

	boolean deleteTemporaryFile(String filename);

	void saveFile(String uuid, Integer docId, String userId, String docType);

}
