package com.ezc.aragenPR.webapp.service;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ezc.aragenPR.webapp.config.StorageProperties;
import com.ezc.aragenPR.webapp.error.StorageException;
import com.ezc.aragenPR.webapp.error.StorageFileNotFoundException;
import com.ezc.aragenPR.webapp.model.EzUploadDocument;
import com.ezc.aragenPR.webapp.model.Users;
import com.ezc.aragenPR.webapp.persistance.dao.EzUploadDocumentRepo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@PropertySource("classpath:storage.properties")
public class FileSystemStorageService implements IStorageService {

	private final Path rootLocation;
	
	 @Value("${app.file.upload-dir}")
	   private String uploadDir;
	 
	 @Value("${file.upload.permanent-dir}")
	    private String permanentDir;
	 
	@Autowired
	EzUploadDocumentRepo ezUploadDocumentRepo;


	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
        
        if(properties.getLocation().trim().length() == 0){
            throw new StorageException("File upload location can not be Empty."); 
        }

		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = this.rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			System.out.println("path::"+destinationFile);
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
				.filter(path -> !path.equals(this.rootLocation))
				.map(this.rootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

	@Override
	public String saveFileTemporarily(MultipartFile file) {
		  // Create the directory if it doesn't exist
		log.debug("uploadDir::"+uploadDir);       
		Path uploadPath = Paths.get(uploadDir);
		log.debug("uploadPath::"+uploadPath);  
        if (!Files.exists(uploadPath)) {
            try {
				Files.createDirectories(uploadPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        // Generate a unique filename using UUID
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() +"@@"+ originalFilename;

        // Save the file temporarily
        Path filePath = uploadPath.resolve(uniqueFilename);
        try {
			Files.write(filePath, file.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}

        return uniqueFilename; // Return the unique filename
	}
	
	@Override
	public boolean deleteTemporaryFile(String filename) {
        Path filePath = Paths.get(uploadDir, filename);
        log.debug("filePath::"+filePath);

       

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.debug("Deleted file: " + filePath);
                return true;
            } else {
            	 log.debug("File not found: " + filePath);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }
    }

	@Override
	public void deleteFile(String filename,Integer docNo,String docType,String docItemType) { 
		String deletePath="";
		deletePath=permanentDir+File.separator+docNo+File.separator+docType+File.separator+docItemType;
		 Path filePath = Paths.get(deletePath).resolve(filename);
	        try {
				Files.deleteIfExists(filePath);
				ezUploadDocumentRepo.deleteFileEntryByIdType(filename,docNo,docType,docItemType);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	@Override
	public void saveFile(String uuid,Integer docId,String userId) {
		  // Create the directory if it doesn't exist
		String[] uuIdArr= uuid.split("@@");
		String originalFileName=uuIdArr[1];
		String pathDir="";
		String docType="PPAP_INITIATION";
	
		log.debug("permanentDir::"+permanentDir);        
		  File tempFile = findTempFile(uuid);
		  // Define permanent location 
		    //Path permanentPath = Paths.get(PERMANENT_DIR, newFileName); 
		pathDir=permanentDir+File.separator+docId; 
		EzUploadDocument ezUploadDocument= new EzUploadDocument();
		ezUploadDocument.setDocNo(docId);
		ezUploadDocument.setDocType(docType);
		ezUploadDocument.setFileName(originalFileName);
		ezUploadDocument.setServerFileName(pathDir+File.separator+originalFileName);
		ezUploadDocument.setItemNo(docType); 
		ezUploadDocument.setUploadedBy(userId);
		ezUploadDocument.setUploadedOn(new Date());
		ezUploadDocumentRepo.save(ezUploadDocument);
	
        Path permanentPath = Paths.get(pathDir, originalFileName);
        try {
			Files.createDirectories(permanentPath.getParent());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Move file to permanent location  
        try {
			Files.move(tempFile.toPath(), permanentPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}

        // Cleanup temporary file 
        tempFile.delete();
       

		
	}
	
	@Override
	public void saveFile(String uuid, Integer docId, String userId, String docType) {
	    // Split UUID to extract original filename
	    String[] uuIdArr = uuid.split("@@");
	    String originalFileName = uuIdArr[1];
	    File tempFile = findTempFile(uuid);
	    
	    String pathDir = permanentDir + File.separator + docId 
	                     + File.separator + docType 
	                     + File.separator + docType;

	    // Save file metadata to database
	    EzUploadDocument ezUploadDocument = new EzUploadDocument();
	    ezUploadDocument.setDocNo(docId);
	    ezUploadDocument.setDocType(docType);
	    ezUploadDocument.setFileName(originalFileName);
	    ezUploadDocument.setServerFileName(pathDir + File.separator + originalFileName);
	    ezUploadDocument.setItemNo(docType);
	    ezUploadDocument.setUploadedBy(userId);
	    ezUploadDocument.setUploadedOn(new Date());
	    ezUploadDocumentRepo.save(ezUploadDocument);

	   
	    Path permanentPath = Paths.get(pathDir, originalFileName);

	    try {
	        
	        Files.createDirectories(permanentPath.getParent());
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    log.debug("permanentPath => " + permanentPath);

	    try {
	        // Move file to permanent directory
	        Files.move(tempFile.toPath(), permanentPath, StandardCopyOption.REPLACE_EXISTING);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // Delete temporary file
	    tempFile.delete();
	}

	
	@Override
	public String saveFilePermanently(Integer docId,MultipartFile file,String docType,String docItemType) {
		  // Create the directory if it doesn't exist
	
		log.debug("saveFilePermanently method :::docId::"+docId);
		log.debug("docType::"+docType);  
		log.debug("docItemType::"+docItemType);  
		log.debug("permanentDir::"+permanentDir); 
		
		
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 Users userObj = (Users)authentication.getPrincipal();
		
		  
		EzUploadDocument ezUploadDocument= new EzUploadDocument(); 
		ezUploadDocument.setDocNo(docId);
		ezUploadDocument.setDocType(docType); 
		ezUploadDocument.setFileName(file.getOriginalFilename());
		ezUploadDocument.setServerFileName(permanentDir+File.separator+docId+File.separator+docType+File.separator+docItemType+File.separator+file.getOriginalFilename());
		ezUploadDocument.setItemNo(docItemType);
		ezUploadDocument.setUploadedBy(userObj.getUserId());
		ezUploadDocument.setUploadedOn(new Date());
		ezUploadDocumentRepo.save(ezUploadDocument);
        Path permanentPath = Paths.get(permanentDir+File.separator+docId+File.separator+docType+File.separator+docItemType, file.getOriginalFilename());
        try {
			Files.createDirectories(permanentPath.getParent());
			
		    Files.copy(file.getInputStream(), permanentPath);

           log.debug("File saved successfully:: at: " + permanentPath.toString()); 
        } catch (IOException e) {
            // Handle exceptions that occur during file saving
            log.debug("Error saving the file: " + e.getMessage());
            e.printStackTrace();
           
        }
    
        return permanentPath.toString();
		
	}
	
	 private File findTempFile(String uuid) {
	        String tempFilePath = uploadDir + File.separator + uuid;
	        return new File(tempFilePath);
	    }

	@Override
	public ResponseEntity<Resource> downloadFile(String fileName, Integer docNo, String docType,String docItemType,String action) {
		  try {
				String downloadPath="";
				downloadPath=permanentDir+File.separator+docNo+File.separator+docType+File.separator+docItemType;
				if(docType.equalsIgnoreCase("PPAP_INITIATION")) {
					downloadPath=permanentDir+File.separator+docNo;
				}
	            Path filePath = Paths.get(downloadPath).resolve(fileName).normalize();
	            Resource resource = new UrlResource(filePath.toUri());

	            if (!resource.exists()) { 
	                throw new FileNotFoundException("File not found: " + fileName); 
	            }

	         // Detect content type
	            String contentType = Files.probeContentType(filePath);
	            if (contentType == null) {
	                contentType = "application/octet-stream"; // Default
	            }

	            // Debugging - Print MIME type
	            log.debug("Detected Content-Type: " + contentType);

	            // Ensure inline display for supported file types
	            boolean isViewable = contentType.startsWith("image/") || contentType.equals("application/pdf") || contentType.startsWith("text/");
	            String headerValue = isViewable ? "inline" : "attachment";  // Ensure proper header

	            if ("download".equalsIgnoreCase(action)) {
	                headerValue = "attachment";
	            }

	            return ResponseEntity.ok()
	                    .contentType(MediaType.parseMediaType(contentType))
	                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue + "; filename=\"" + fileName + "\"")
	                    .body(resource);
	        } catch (Exception e) { 
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	        }
		
	}

	
	
	  
}
