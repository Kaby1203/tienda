package com.tienda.service;

import com.tienda.domain.Producto;
import com.tienda.repositorio.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;  // ← AGREGADO

    // Constructor modificado para incluir FirebaseStorageService
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
        System.out.println("=== GUARDANDO CATEGORÍA CON FIREBASE ===");

        // 1. Primero guardamos la categoría para obtener el ID
        producto = productoRepository.save(producto);
        System.out.println("Categoría guardada en BD con ID: " + producto.getIdProducto());

        // 2. Si hay imagen, la subimos a Firebase
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                System.out.println("Subiendo imagen a Firebase para categoría ID: " + producto.getIdProducto());

                // Subir a Firebase usando el servicio
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile,
                        "producto",
                        producto.getIdProducto()
                );

                System.out.println("Imagen subida a Firebase. URL: " + rutaImagen);

                // 3. Actualizar la categoría con la URL de Firebase
                producto.setRutaImagen(rutaImagen);
                productoRepository.save(producto);
                System.out.println("Categoría actualizada con URL de Firebase");

            } catch (IOException e) {
                System.err.println("ERROR al subir imagen a Firebase: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Error al subir la imagen a Firebase", e);
            }
        } else {
            System.out.println("No se recibió imagen para esta categoría");
        }

        System.out.println("=== FIN GUARDADO CATEGORÍA ===\n");
    }

    @Transactional
    public void delete(Integer idProducto) {
        System.out.println("=== ELIMINANDO CATEGORÍA ID: " + idProducto + " ===");

        Optional<Producto> productoOpt = productoRepository.findById(idProducto);

        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();

            // Opcional: Eliminar la imagen de Firebase si existe
            if (producto.getRutaImagen() != null && !producto.getRutaImagen().isEmpty()) {
                System.out.println("La categoría tenía imagen: " + producto.getRutaImagen());
                // Aquí podrías agregar lógica para eliminar de Firebase si lo deseas
            }

            productoRepository.deleteById(idProducto);
            System.out.println("Categoría eliminada de BD");
        } else {
            throw new IllegalArgumentException("La categoría con ID " + idProducto + " no existe.");
        }

        System.out.println("=== FIN ELIMINACIÓN ===\n");
    }
}
