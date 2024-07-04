package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.ProductoEntity;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.ProductoService;

@Service
public class ProductoServiceImpl implements ProductoService{

	@Autowired
	private ProductoRepository productoRepository;
	
	@Override
	public List<ProductoEntity> obtenerTodos() {
		
		return productoRepository.findAll();
	}

	@Override
	public ProductoEntity obtenerPorId(Integer id) {
		
		return productoRepository.findById(id).get();
	}

	@Override
	public ProductoEntity guardarActualizarProducto(ProductoEntity producto) {
		
		return productoRepository.save(producto);
	}

	@Override
	public void eliminar(Integer id) {
		productoRepository.deleteById(id);
		
	}

}
