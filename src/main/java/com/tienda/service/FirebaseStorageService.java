package com.tienda.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {
    @Value("${firebase.bucket.name}")
    private String bucketName;
    
    @Value("${firebase.storage.path}")
    private String storagePath;
    
    // Aquí se manejaría la inyección del cliente de Storage como un bean
    private final Storage storage;

    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }

    //Sube un archivo de imagen al almacenamiento de Firebase.    
    public String uploadImage(MultipartFile localFile, String folder, Integer id) throws IOException {
        String originalName = localFile.getOriginalFilename();
        String fileExtension = "";
        
        System.out.println("=== INICIANDO SUBIDA A FIREBASE ===");
        System.out.println("Archivo original: " + originalName);
        System.out.println("Bucket Name: " + bucketName);
        System.out.println("Storage Path: " + storagePath);
        System.out.println("Folder: " + folder);
        System.out.println("ID: " + id);
        
        if (originalName != null && originalName.contains(".")) {
            fileExtension = originalName.substring(originalName.lastIndexOf("."));
            System.out.println("Extensión detectada: " + fileExtension);
        }

        // Se genera el nombre del archivo con un formato consistente.
        String fileName = "img" + getFormattedNumber(id) + fileExtension;
        System.out.println("Nombre de archivo generado: " + fileName);
        
        String rutaCompleta = storagePath + "/" + folder + "/" + fileName;
        System.out.println("Ruta completa en Firebase: " + rutaCompleta);

        File tempFile = convertToFile(localFile);
        System.out.println("Archivo temporal creado: " + tempFile.getAbsolutePath());
        System.out.println("Tamaño del archivo temporal: " + tempFile.length() + " bytes");

        try {
            String url = uploadToFirebase(tempFile, folder, fileName);
            System.out.println("URL generada exitosamente: " + url);
            return url;
        } catch (Exception e) {
            System.out.println("ERROR durante la subida: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            // Asegura que el archivo temporal se elimine siempre.
            if (tempFile.exists()) {
                tempFile.delete();
                System.out.println("Archivo temporal eliminado");
            }
        }
    }

    //Convierte un MultipartFile a un archivo temporal en el servidor.
    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    //Sube el archivo al almacenamiento de Firebase y genera una URL firmada.     
    private String uploadToFirebase(File file, String folder, String fileName) throws IOException {
        // Definimos el ID del blob y su información
        String rutaCompleta = storagePath + "/" + folder + "/" + fileName;
        System.out.println("Creando BlobId con:");
        System.out.println("  - bucketName: " + bucketName);
        System.out.println("  - rutaCompleta: " + rutaCompleta);
        
        BlobId blobId = BlobId.of(bucketName, rutaCompleta);
        
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            mimeType = "image/jpeg"; // Valor por defecto para imágenes
        }
        System.out.println("MIME Type detectado: " + mimeType);
        
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(mimeType)
                .build();

        System.out.println("BlobInfo creado: " + blobInfo);
        System.out.println("Subiendo archivo a Firebase...");
        
        // Subimos el archivo. El objeto `storage` ya tiene las credenciales necesarias.
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        
        System.out.println("Archivo subido exitosamente a Firebase");

        // El objeto `storage` ya tiene las credenciales del servicio configuradas        
        // Se genera la URL firmada. Ahora con una caducidad de 5 años.
        System.out.println("Generando URL firmada...");
        String url = storage.signUrl(blobInfo, 1825, TimeUnit.DAYS).toString();
        System.out.println("URL firmada generada: " + url);
        
        return url;
    }

    /**
     * Genera un string numérico con un formato de 14 dígitos, rellenado con
     * ceros a la izquierda.
     */
    private String getFormattedNumber(long id) {
        return String.format("%014d", id);
    }
}