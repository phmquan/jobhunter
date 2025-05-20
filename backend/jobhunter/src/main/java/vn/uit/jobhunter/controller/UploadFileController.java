package vn.uit.jobhunter.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.uit.jobhunter.domain.response.ResUploadFileDTO;
import vn.uit.jobhunter.service.FileService;
import vn.uit.jobhunter.util.annotation.ApiMessage;
import vn.uit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UploadFileController {

    private final FileService fileService;

    @Value("${upload-file.base-uri}")
    private String baseURI;

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<?> upload(
        @RequestParam(name="file", required=false) MultipartFile file,
        @RequestParam("folder") String folder
    )throws URISyntaxException, IOException,vn.uit.jobhunter.util.error.StorageException,IdInvalidException{
        //validate file
        if(file==null||file.isEmpty()){
            throw new vn.uit.jobhunter.util.error.StorageException("File khong duoc de trong");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        List<String> allowedMimeTypes = Arrays.asList(
            "application/pdf", 
            "image/jpeg", 
            "image/png", 
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );

        // Validate extension
        boolean isValidExtension = allowedExtensions.stream().anyMatch(ext -> fileName.toLowerCase().endsWith("." + ext));
        if (!isValidExtension) {
            throw new IdInvalidException("Invalid file type based on extension.");
        }

        // Validate MIME type
        String contentType = file.getContentType();
        if (!allowedMimeTypes.contains(contentType)) {
            throw new IdInvalidException("Invalid file type based on MIME type.") ;
        }

        // Check file size
        long maxSize = 5 * 1024 * 1024; // 5 MB in bytes
        if (file.getSize() > maxSize) {
	//todo
        }

        //create folder if not exist
        this.fileService.createUploadFolder(baseURI+folder);
        //store file
        String uploadedFile=this.fileService.store(file, folder);
        ResUploadFileDTO uploadFileDTO=new ResUploadFileDTO(uploadedFile,Instant.now());
        
        return ResponseEntity.ok(uploadFileDTO);
    }
}
