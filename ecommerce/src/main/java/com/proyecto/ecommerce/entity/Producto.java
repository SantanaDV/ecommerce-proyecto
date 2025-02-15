package com.proyecto.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(nullable = false)
    private String nombre;

    // La descripción es opcional, por lo que no se marca como obligatoria.
    @Column
    private String descripcion;

    @NotNull(message = "El precio del producto es obligatorio")
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Column(nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PedidoProducto> pedidoProductos;

}
