package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.UsuarioEntity;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;
import com.example.demo.utils.Utilitarios;

import jakarta.servlet.http.HttpSession;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public void crearUsuario(UsuarioEntity usuarioEntity, Model model, MultipartFile foto) {
		
		//guardarfoto
				String nombrefoto = Utilitarios.guardarImagen(foto);
				usuarioEntity.setUrlImagen(nombrefoto);
				
				//hash password
				String passwordHash = Utilitarios.extraerHash(usuarioEntity.getPassword());
				usuarioEntity.setPassword(passwordHash);
				
				//guarda usuario
				usuarioRepository.save(usuarioEntity);
				
				//responde a la vista
				model.addAttribute("registroCorrecto", "Registro Correcto");
				model.addAttribute("usuario", new UsuarioEntity());
		
	}

	@Override
	public boolean validarUsuario(UsuarioEntity usuarioEntity, HttpSession session) {
		
		UsuarioEntity usuarioEncontradoPorCorreo = 
				usuarioRepository.findByCorreo(usuarioEntity.getCorreo());
		
		if(usuarioEncontradoPorCorreo == null) {
			return false;
		}
		if(!Utilitarios.checkPassword(usuarioEntity.getPassword(),
				usuarioEncontradoPorCorreo.getPassword())) {
			return false;
		}
		
		session.setAttribute("usuario", usuarioEncontradoPorCorreo.getCorreo());
		
		
		return true;
	}

	@Override
	public UsuarioEntity buscarUsuarioPorCorreo(String correo) {
		
		return usuarioRepository.findByCorreo(correo);
	}

	@Override
	public String obtenerNombreUsuarioActual(HttpSession session) {
		
		Object usuarioObj = session.getAttribute("usuario");
        if (usuarioObj instanceof UsuarioEntity) {
            UsuarioEntity usuario = (UsuarioEntity) usuarioObj;
            return usuario.getNombres(); 
        }
        return null;
		
	}

}
