package com.tienda.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name="producto")
public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_producto")
    private Integer idProducto;
    private Integer idCategoria;
    
    @Column(nullable=false, length=50)
    @NotBlank(message= "La descripción no puede estar vacía")
    @Size(max=50, message= "La descripción no puede tener más de 50 caracteres")
    private String descripcion;
    
    @Column(columnDefinition="TEXT")
    private String detalle;
    
    @Column(precision = 12, scale = 2)
    @NotNull(message = "El precio no puede estar vacío")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio debe ser...")
    private BigDecimal precio;

    @NotNull(message = "Las existencias no pueden estar vacías")
    @Min(value = 0, message = "Las existencias deben ser mayor o igual a 0")
    private Integer existencias;

    @Column(length=1024)
    @Size(max=1024)
    private String rutaImagen;

    private boolean activo;
}