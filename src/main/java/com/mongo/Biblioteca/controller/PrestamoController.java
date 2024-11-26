package com.mongo.Biblioteca.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.mongo.Biblioteca.exception.NotFoundException;
import com.mongo.Biblioteca.model.Libro;
import com.mongo.Biblioteca.model.Prestamo;
import com.mongo.Biblioteca.model.Usuario;
import com.mongo.Biblioteca.repository.LibroRepository;
import com.mongo.Biblioteca.repository.PrestamoRepository;
import com.mongo.Biblioteca.repository.UsuarioRepository;

@Controller
public class PrestamoController {

	@Autowired
	PrestamoRepository repository;
	@Autowired
	UsuarioRepository uRepository;
	@Autowired
	LibroRepository lRepository;

	@GetMapping("/prestamos")
	public String prestamosList(Model model) {
		List<Prestamo> prestamos = repository.findAll();
		model.addAttribute("prestamos", prestamos);
		return "prestamosList";
	}

	@PostMapping("/prestamos/save/{libroId}")
	public String prestamosSave(@ModelAttribute("prestamoN") Prestamo prestamo,
			@SessionAttribute("usuario") Usuario usuario, @PathVariable("libroId") String id,
			@RequestParam("cantidad") int cantidad, @RequestParam("estado") String estado) {
		if (prestamo.getId().isEmpty()) {
			prestamo.setId(null);
		}

		Usuario usuarioP = uRepository.findById(usuario.getId())
				.orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
		Libro libroP = lRepository.findById(id).orElseThrow(() -> new NotFoundException("Libro no encontrado"));

		libroP.setCantidad(libroP.getCantidad() - cantidad);

		prestamo.setUsuario(usuarioP);
		prestamo.setLibro(libroP);
		prestamo.setFechaPrestamo(LocalDate.now());
		prestamo.setFechaDevolucion(LocalDate.now().plusDays(15));
		prestamo.setCantidad(cantidad);
		prestamo.setEstado(estado);

		lRepository.save(libroP);
		repository.save(prestamo);
		return "redirect:/usuarios/prestar";
	}

	@GetMapping("/prestamos/estado/{id}")
	public String prestamosEstado(@PathVariable("id") String id, @RequestParam("estado") String estado) {
		Prestamo prestamo = repository.findById(id).orElseThrow(() -> new NotFoundException("Prestamo no encontrado"));
		prestamo.setEstado(estado);
		repository.save(prestamo);
		return "redirect:/prestamos";
	}
}
