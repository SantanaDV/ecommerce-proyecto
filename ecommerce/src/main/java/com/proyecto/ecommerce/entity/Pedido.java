package com.proyecto.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @NotNull(message = "La fecha del pedido es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "El total del pedido es obligatorio")
    @Column(nullable = false)
    private Double total;

    @NotNull(message = "El estado del pedido es obligatorio")
    @Column(nullable = false)
    private String estado; // Ejemplos: "pendiente", "procesado", "enviado"

    //Relacion: Muchos pedidos pertenencen a un único usuario (cliente)
    @ManyToOne
    @JoinColumn(name="id_usuario", nullable = false)
    @JsonIgnoreProperties("pedidos")
    private Usuario usuario;

    //Relación: Un pedido puede tener varios registros de PedidoProducto
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoProducto> pedidoProductos;
}
