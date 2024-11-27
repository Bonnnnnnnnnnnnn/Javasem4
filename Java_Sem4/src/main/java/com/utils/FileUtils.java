package com.utils;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
//	 public static String uploadFileImage(MultipartFile file, String folderName) {
//	        try {
//	            String folderUpload = System.getProperty("user.dir") + "/" + folderName;
//	            Files.createDirectories(Paths.get(folderUpload)); // Ensure directory exists
//	            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//	            String filePath = String.format("%s/%s", folderUpload, fileName);
//	            byte[] data = file.getBytes();
//
//	            try (FileOutputStream fout = new FileOutputStream(filePath);
//	                 BufferedOutputStream buf = new BufferedOutputStream(fout)) {
//	                buf.write(data);
//	            }
//
//	            return fileName;
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	            return "";
//	        }
//	    }
	public static String uploadFileImage(MultipartFile file, String parentFolder, String subFolder) {
	    try {
	        Path uploadPath = subFolder != null ? Paths.get(parentFolder, subFolder) : Paths.get(parentFolder);
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }
	        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
	        Path filePath = uploadPath.resolve(fileName);
	        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	        return subFolder != null ? subFolder + "/" + fileName : fileName;
	    } catch (IOException e) {
	        throw new RuntimeException("Lỗi khi upload file: " + file.getOriginalFilename(), e);
	    }
	}

	    public static String getBase64EncodedImage(String folderName, String imageName) {
	        String folderUpload = System.getProperty("user.dir") + "/" + folderName;
	        String filePath = folderUpload + "/" + imageName;

	        try (FileInputStream fin = new FileInputStream(filePath)) {
	            byte[] data = fin.readAllBytes();
	            return Base64.getEncoder().encodeToString(data);
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return "";
	    }
	    public static String deleteFile(String folderName, String fileName) {
	        try {
	            Path pathFile = Path.of(System.getProperty("user.dir"), folderName, fileName);
	            Files.deleteIfExists(pathFile);
	            return "file deleted";
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "file delete failed";
	        }
	    }


}
