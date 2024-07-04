package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.ProductoEntity;

public interface ProductoService {
	
	List<ProductoEntity> obtenerTodos();

    ProductoEntity obtenerPorId(Integer id);

    ProductoEntity guardarActualizarProducto(ProductoEntity producto);

    void eliminar(Integer id);
}
