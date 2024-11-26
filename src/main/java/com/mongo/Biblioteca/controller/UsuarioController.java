package com.mongo.Biblioteca.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.mongo.Biblioteca.exception.NotFoundException;
import com.mongo.Biblioteca.model.Libro;
import com.mongo.Biblioteca.model.Prestamo;
import com.mongo.Biblioteca.model.Usuario;
import com.mongo.Biblioteca.repository.LibroRepository;
import com.mongo.Biblioteca.repository.PrestamoRepository;
import com.mongo.Biblioteca.repository.UsuarioRepository;

@Controller
public class UsuarioController {

	@Autowired
	UsuarioRepository repository;
	@Autowired
	LibroRepository lRepository;
	@Autowired
	PrestamoRepository pRepository;
	
	@GetMapping("/usuarios/vista")
	public String vistaUsuario() {
		return "usuarioView";
	}
	
	@GetMapping("/usuarios/prestar")
	public String usuariosPrestar(Model model) {
		List<Libro> libros = lRepository.findAll();
		model.addAttribute("libros", libros);
		model.addAttribute("prestamo", new Prestamo());
		return "prestarLibros";
	}
	
	@GetMapping("/usuarios/prestamos")
	public String usuariosPrestamos(Model model, @SessionAttribute("usuario") Usuario usuario) {
		List<Prestamo> prestamos = pRepository.findByUsuario_IdAndEstadoNot(usuario.getId(), "devuelto");
		model.addAttribute("prestamos", prestamos);
		return "misPrestamos";
	}
	
	@GetMapping("/usuarios")
	public String usuarioList(Model model) {
		List<Usuario> usuarios = repository.findAll();
		model.addAttribute("usuarios", usuarios);
		return "usuariosList";
	}
	
	@GetMapping("/usuarios/new")
	public String usuariosNew(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("titulo", "Agregar usuario");
		model.addAttribute("tipo", "nuevo");
		return "usuariosForm";
	}
	
	@PostMapping("/usuarios/save")
	public String usuariosSave(@ModelAttribute("usuarios") Usuario usuario) {
		if(usuario.getId().isEmpty()) {
			usuario.setId(null);
		}
		
		repository.save(usuario);
		return "redirect:/usuarios";
	}
	
	@GetMapping("/usuarios/edit/{id}")
	public String usuariosEdit(@PathVariable("id") String id, Model model) {
		model.addAttribute("usuario", repository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado")));
		model.addAttribute("titulo", "Editar usuario");
		model.addAttribute("tipo", "antiguo");
		return "usuariosForm";
	}
	
	@GetMapping("/usuarios/delete/{id}")
	public String usuariosDelete(@PathVariable("id") String id) {
		repository.findById(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
		repository.deleteById(id);
		return "redirect:/usuarios";
	}
}
