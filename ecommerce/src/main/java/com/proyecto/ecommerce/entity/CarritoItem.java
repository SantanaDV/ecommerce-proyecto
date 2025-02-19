package com.proyecto.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CarritoItem {
    private Producto producto;
    private Integer cantidad;

    public CarritoItem(Producto producto, Integer cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

}
