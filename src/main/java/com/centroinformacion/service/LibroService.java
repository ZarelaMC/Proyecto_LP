package com.centroinformacion.service;

import com.centroinformacion.entity.Libro;

public interface LibroService {
	public abstract Libro insertaActualizaLibro(Libro obj);
	public abstract boolean existeLibroConSerie(String serie);
}
