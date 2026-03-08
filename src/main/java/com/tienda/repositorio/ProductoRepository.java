package com.tienda.repositorio;

import com.tienda.domain.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    public List<Producto> findByActivoTrue();
    
    //Consulta derivada que recupera los productos dentro de un rango de precios
    //Y los ordena por el precio del producto ascendentemente
    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(BigDecimal precioInf, BigDecimal precioSup);
 
    //Consulta jpql que recupera los productos dentro de un rango de precios
    //Y los ordena por el precio del producto ascendentemente
    @Query(value="SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(@Param("precioInf") BigDecimal precioInf, 
    @Param("precioSup") BigDecimal precioSup);
    
    //Consulta SQL que recupera los productos dentro de un rango de precios
    //Y los ordena por el precio del producto ascendentemente
    @Query(nativeQuery=true,
    value="SELECT * FROM producto WHERE precio BETWEEN :precioInf AND :precioSup ORDER BY precio ASC")
    public List<Producto> consultaSQL(@Param("precioInf") BigDecimal precioInf, 
    @Param("precioSup") BigDecimal precioSup);
}