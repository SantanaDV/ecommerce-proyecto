package com.proyecto.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para recibir la información de un pedido con los productos incluidos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequest {

    @NotNull(message = "La fecha del pedido es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El total del pedido es obligatorio")
    private Double total;

    @NotNull(message = "El estado del pedido es obligatorio")
    private String estado;

    @NotNull(message = "Debe incluir al menos un producto en el pedido")
    private List<ProductoCantidadDTO> productos;  // Lista de productos con cantidad
}
