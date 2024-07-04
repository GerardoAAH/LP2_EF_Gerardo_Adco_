package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.entity.CategoriaEntity;
import com.example.demo.entity.ProductoEntity;
import com.example.demo.entity.UsuarioEntity;
import com.example.demo.service.CategoriaService;
import com.example.demo.service.ProductoService;
import com.example.demo.service.UsuarioService;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import com.itextpdf.html2pdf.HtmlConverter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ProductoController {

	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private CategoriaService categoriaService;
	
	//Lista
	@GetMapping("/listar")
    public String listarProductos(HttpSession session, Model model) {
		
		
		if(session.getAttribute("usuario") == null) {
			return "redirect:/";
		}
		//Nombre del usuario
		String nombre = session.getAttribute("usuario").toString();
		UsuarioEntity usuarioEntity = usuarioService.buscarUsuarioPorCorreo(nombre);
		model.addAttribute("nombreUsuario",usuarioEntity.getNombres());
		
		

        // Obtener lista de productos 
        List<ProductoEntity> productos = productoService.obtenerTodos();
        model.addAttribute("productos", productos);

        return "lista_productos"; 
    }
	//Agregar Producto
	@GetMapping("/agregar")
    public String mostrarFormularioAgregar(Model model,HttpSession session) {
        
		if(session.getAttribute("usuario") == null) {
			return "redirect:/";
		}
		//Nombre del usuario
		String nombre = session.getAttribute("usuario").toString();
		UsuarioEntity usuarioEntity = usuarioService.buscarUsuarioPorCorreo(nombre);
		model.addAttribute("nombreUsuario",usuarioEntity.getNombres());
		
		ProductoEntity producto = new ProductoEntity();
        model.addAttribute("producto", producto);

        List<CategoriaEntity> categorias = categoriaService.obtenerAllCategorias();
        model.addAttribute("categorias", categorias);

        return "agregar_producto"; 
    }
	//Para guardar y actualizar
	@PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute("producto") ProductoEntity producto) {
        productoService.guardarActualizarProducto(producto);
        return "redirect:/listar";
    }
	
	//editar
	@GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model,HttpSession session) {
		if(session.getAttribute("usuario") == null) {
			return "redirect:/";
		}
		//Nombre del usuario
		String nombre = session.getAttribute("usuario").toString();
		UsuarioEntity usuarioEntity = usuarioService.buscarUsuarioPorCorreo(nombre);
		model.addAttribute("nombreUsuario",usuarioEntity.getNombres());
		
		
		ProductoEntity producto = productoService.obtenerPorId(id);
        model.addAttribute("producto", producto);

        List<CategoriaEntity> categorias = categoriaService.obtenerAllCategorias();
        model.addAttribute("categorias", categorias);

        return "editar_producto"; 
    }
    //Detalle
	@GetMapping("/detalle/{id}")
    public String verDetalleProducto(@PathVariable Integer id, Model model,HttpSession session) {
		if(session.getAttribute("usuario") == null) {
			return "redirect:/";
		}
		//Nombre del usuario
		String nombre = session.getAttribute("usuario").toString();
		UsuarioEntity usuarioEntity = usuarioService.buscarUsuarioPorCorreo(nombre);
		model.addAttribute("nombreUsuario",usuarioEntity.getNombres());
		
		ProductoEntity producto = productoService.obtenerPorId(id);
        model.addAttribute("producto", producto);
        return "detalle_producto"; 
    }
   //Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id) {
        productoService.eliminar(id);
        return "redirect:/listar";
    }
    
    //Generar PDF
    @GetMapping("/generar-pdf")
    public void generarPdf(HttpSession session, HttpServletResponse response) throws IOException {
        // Verificar si el usuario está autenticado
        if (session.getAttribute("usuario") == null) {
            response.sendRedirect("/");
            return;
        }
        
        String nombreUsuario = obtenerNombreUsuarioLogeado(session);   
        List<ProductoEntity> productos = productoService.obtenerTodos();
        String html = generarHtmlProductos(productos, nombreUsuario);
    
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=lista_productos.pdf");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(html, baos);
        baos.close();

       
        OutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
    }

    
    private String obtenerNombreUsuarioLogeado(HttpSession session) {
        String nombreUsuario = "Usuario Desconocido"; 
        Object usuario = session.getAttribute("usuario");
        if (usuario != null) {
            String nombre = usuario.toString();
            UsuarioEntity usuarioEntity = usuarioService.buscarUsuarioPorCorreo(nombre);
            if (usuarioEntity != null) {
                nombreUsuario = usuarioEntity.getNombres();
            }
        }
        return nombreUsuario;
    }

    private String generarHtmlProductos(List<ProductoEntity> productos, String nombreUsuario) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html lang=\"es\">\n");
        htmlBuilder.append("<head>\n");
        htmlBuilder.append("<meta charset=\"UTF-8\">\n");
        htmlBuilder.append("<title>Lista de Productos</title>\n");
        htmlBuilder.append("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css\">\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");
        htmlBuilder.append("<div class=\"container\">\n");
        htmlBuilder.append("<h1>Listado de Productos</h1>\n");
        htmlBuilder.append("<table class=\"table\">\n");
        htmlBuilder.append("<thead>\n");
        htmlBuilder.append("<tr>\n");
        htmlBuilder.append("<th>ID</th>\n");
        htmlBuilder.append("<th>Nombre</th>\n");
        htmlBuilder.append("<th>Precio</th>\n");
        htmlBuilder.append("<th>Stock</th>\n");
        htmlBuilder.append("<th>Categoría</th>\n");
        htmlBuilder.append("</tr>\n");
        htmlBuilder.append("</thead>\n");
        htmlBuilder.append("<tbody>\n");

        for (ProductoEntity producto : productos) {
            htmlBuilder.append("<tr>\n");
            htmlBuilder.append("<td>").append(producto.getProductoId()).append("</td>\n");
            htmlBuilder.append("<td>").append(producto.getNombre()).append("</td>\n");
            htmlBuilder.append("<td>").append(producto.getPrecio()).append("</td>\n");
            htmlBuilder.append("<td>").append(producto.getStock()).append("</td>\n");
            htmlBuilder.append("<td>").append(producto.getCategoria().getNombreCategoria()).append("</td>\n");
            htmlBuilder.append("</tr>\n");
        }

        htmlBuilder.append("</tbody>\n");
        htmlBuilder.append("</table>\n");
        
        htmlBuilder.append("<p>Reporte Generado Por: "+"</p>\n");
        htmlBuilder.append("").append(nombreUsuario).append("</p>\n");

        htmlBuilder.append("</div>\n");
        htmlBuilder.append("</body>\n");
        htmlBuilder.append("</html>");

        return htmlBuilder.toString();
    }
	
	
}
