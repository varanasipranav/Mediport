package com.ezc.aragenPR.webapp.service;
/*
 * package com.ezc.vip.webapp.service;
 * 
 * import org.springframework.beans.factory.annotation.Value;
 * 
 * import lombok.var;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.stereotype.Service; import
 * org.springframework.util.StringUtils; import
 * org.springframework.web.multipart.MultipartFile;
 * 
 * import com.ezc.vip.webapp.controller.FileUploadController; import
 * com.ezc.vip.webapp.error.FileStorageException; import
 * com.ezc.vip.webapp.error.MyFileNotFoundException; import
 * com.ezc.vip.webapp.error.StorageException; import
 * com.ezc.vip.webapp.model.DBFile; import
 * com.ezc.vip.webapp.persistance.dao.DBFileRepo;
 * 
 * import java.io.IOException; import java.nio.file.Files; import
 * java.nio.file.Paths; import java.nio.file.StandardCopyOption; import
 * lombok.extern.slf4j.Slf4j;
 * 
 * import org.slf4j.Logger; import org.slf4j.LoggerFactory;
 * 
 * @Slf4j
 * 
 * @Service public class DBFileStorageService {
 * 
 * @Autowired private DBFileRepo dbFileRepository;
 * 
 * @Value("${upload.path}") private String path;
 * 
 * public DBFile uploadFile(MultipartFile file) {
 * log.debug(file+"::file:1234:"); if (file.isEmpty()) { throw new
 * StorageException("Failed to store empty file"); }
 * 
 * try { var fileName = file.getOriginalFilename(); var is =
 * file.getInputStream();
 * 
 * Files.copy(is, Paths.get("C:/Users/kkakarlapudi/Desktop/HSIL/" + fileName),
 * StandardCopyOption.REPLACE_EXISTING);
 * 
 * log.debug(fileName+"::file:fileName:"+path);
 * 
 * if(fileName.contains("..")) { throw new
 * FileStorageException("Sorry! Filename contains invalid path sequence " +
 * fileName); }
 * 
 * DBFile dbFile = new DBFile(fileName, file.getContentType(),path);
 * 
 * return dbFileRepository.save(dbFile); } catch (IOException e) {
 * 
 * var msg = String.format("Failed to store file", file.getName());
 * 
 * throw new StorageException(msg, e); }
 * 
 * }
 * 
 * public DBFile storeFile(MultipartFile file) { // Normalize file name String
 * fileName = StringUtils.cleanPath(file.getOriginalFilename());
 * 
 * 
 * // Check if the file's name contains invalid characters
 * if(fileName.contains("..")) { throw new
 * FileStorageException("Sorry! Filename contains invalid path sequence " +
 * fileName); }
 * 
 * DBFile dbFile = new DBFile(fileName, file.getContentType(), path);
 * 
 * return dbFileRepository.save(dbFile);
 * 
 * }
 * 
 * public DBFile getFile(String fileId) { return
 * dbFileRepository.findById(fileId) .orElseThrow(() -> new
 * MyFileNotFoundException("File not found with id " + fileId)); } }
 */