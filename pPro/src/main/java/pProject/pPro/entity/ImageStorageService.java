//package pProject.pPro.entity;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.UUID;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//@Component  // 또는 @Service
//public class ImageStorageService {
//
//    private static final String UPLOAD_DIR = "C:/myproject/uploads/";
//
//    public String saveImage(MultipartFile imageFile){
//        File dir = new File(UPLOAD_DIR);
//        if (!dir.exists()) dir.mkdirs();
//
//        String originalFilename = imageFile.getOriginalFilename();
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String savedFileName = UUID.randomUUID() + extension;
//
//        File savedFile = new File(UPLOAD_DIR + savedFileName);
//        try {
//        	
//        imageFile.transferTo(savedFile);
//        }catch (Exception e) {
//        	e.printStackTrace();
//			// TODO: handle exception
//		}
//
//        return "/uploads/" + savedFileName;
//    }
//}