package com.mongo.Biblioteca.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mongo.Biblioteca.exception.NotFoundException;
import com.mongo.Biblioteca.model.Libro;
import com.mongo.Biblioteca.repository.LibroRepository;

@Controller
public class LibroController {

	@Autowired
	LibroRepository repository;

	@GetMapping("/libros")
	public String librosList(Model model) {
		List<Libro> libros = repository.findAll();
		model.addAttribute("libros", libros);
		return "librosList";
	}

	@GetMapping("/libros/new")
	public String librosNew(Model model) {
		model.addAttribute("libro", new Libro());
		model.addAttribute("titulo", "Agregar Libro");
		return "librosForm";
	}

	@PostMapping("/libros/save")
	public String librosSave(@ModelAttribute("libros") Libro libro) {
		if (libro.getId().isEmpty()) {
			libro.setId(null);
		}
		try {

			if (!libro.getArchivoFoto().isEmpty() && libro.getArchivoFoto() != null) {
				libro.setImagen(libro.getArchivoFoto().getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		}

		if (libro.getCantidad() > 0) {
			libro.setRestock(false);
		}

		repository.save(libro);
		return "redirect:/libros";

	}

	@GetMapping("/libros/edit/{id}")
	public String librosEdit(@PathVariable("id") String id, Model model) {
		model.addAttribute("libro",
				repository.findById(id).orElseThrow(() -> new NotFoundException("Libro no encontrado")));
		model.addAttribute("titulo", "Editar Libro");
		return "librosForm";
	}

	@GetMapping("/libros/delete/{id}")
	public String librosDelete(@PathVariable("id") String id) {
		repository.findById(id).orElseThrow(() -> new NotFoundException("Libro no encontrado"));
		repository.deleteById(id);
		return "redirect:/libros";
	}

	@GetMapping("/libros/restock/{id}")
	public String librosRestock(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
		Libro libro = repository.findById(id).orElseThrow(() -> new NotFoundException("Libro no encontrado"));
		if (libro.getCantidad() > 0) {
			redirectAttributes.addFlashAttribute("mensaje", "Todavía hay stock del libro, pedir cuando se acabe.");
		} else {
			libro.setRestock(true);
			repository.save(libro);
			redirectAttributes.addFlashAttribute("mensaje", "El libro ha sido marcado para restock exitosamente.");
		}
		return "redirect:/libros";
	}
}
