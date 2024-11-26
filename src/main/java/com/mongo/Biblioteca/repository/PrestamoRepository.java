package com.mongo.Biblioteca.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mongo.Biblioteca.model.Prestamo;

@Repository
public interface PrestamoRepository extends MongoRepository<Prestamo, String>{

	public List<Prestamo> findByUsuario_IdAndEstadoNot(String id, String estado);
}
