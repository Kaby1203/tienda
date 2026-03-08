package com.tienda.service;

import com.tienda.domain.Producto;
import com.tienda.repositorio.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal; 

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(ProductoRepository productoRepository,
            FirebaseStorageService firebaseStorageService) {
        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) {
            return productoRepository.findByActivoTrue();
        } else {
            return productoRepository.findAll();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        System.out.println("=== GUARDANDO PRODUCTO CON FIREBASE ===");

       
        producto = productoRepository.save(producto);
        System.out.println("Producto guardado en BD con ID: " + producto.getIdProducto());

       
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                System.out.println("Subiendo imagen a Firebase para producto ID: " + producto.getIdProducto());

           
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile,
                        "producto",
                        producto.getIdProducto()
                );

                System.out.println("Imagen subida a Firebase. URL: " + rutaImagen);

           
                producto.setRutaImagen(rutaImagen);
                productoRepository.save(producto);
                System.out.println("Producto actualizado con URL de Firebase");

            } catch (IOException e) {
                System.err.println("ERROR al subir imagen a Firebase: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error al subir la imagen a Firebase", e);
            }
        } else {
            System.out.println("No se recibió imagen para este producto");
        }

        System.out.println("=== FIN GUARDADO PRODUCTO ===\n");
    }

    @Transactional
    public void delete(Integer idProducto) {
        System.out.println("=== ELIMINANDO PRODUCTO ID: " + idProducto + " ===");

        Optional<Producto> productoOpt = productoRepository.findById(idProducto);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();

        
            if (producto.getRutaImagen() != null && !producto.getRutaImagen().isEmpty()) {
                System.out.println("El producto tenía imagen: " + producto.getRutaImagen());
  
            }

            productoRepository.deleteById(idProducto);
            System.out.println("Producto eliminado de BD");
        } else {
            throw new IllegalArgumentException("El producto con ID " + idProducto + " no existe.");
        }

        System.out.println("=== FIN ELIMINACIÓN ===\n");
    }
    
    @Transactional(readOnly = true)
    public List<Producto> consultaDerivada(BigDecimal precioInf, BigDecimal precioSup) {
        return productoRepository.findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
    }
    
    @Transactional(readOnly = true)
    public List<Producto> consultaJPQL(BigDecimal precioInf, BigDecimal precioSup) {
        return productoRepository.consultaJPQL(precioInf, precioSup);
    }
    
     @Transactional(readOnly = true)
    public List<Producto> consultaSQL(BigDecimal precioInf, BigDecimal precioSup) {
        return productoRepository.consultaSQL(precioInf, precioSup);
    }
    
    
}