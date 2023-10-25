package com.centroinformacion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.centroinformacion.entity.Sala;
import com.centroinformacion.repository.SalaRepository;

@Service
public class SalaServiceImp implements SalaService {
	@Autowired	
	private SalaRepository repository;
	
	@Override
	public Sala insertaActualizaSala(Sala obj) {
		return repository.save(obj);
	}
	@Override
	public Sala insertaRegistraSala(Sala obj) {
		return repository.save(obj);
	}

	@Override
	public List<Sala> listaPorNumero(String numero) {
		return repository.findByNumero(numero);
	}
	
	@Override
	public List<Sala> listPorNumerolike(String filtro) {
		return repository.listPorNumerolike(filtro);
	}
	@Override
	public Optional<Sala> buscarSala(int idSala) {
		return repository.findById(idSala);
	}
	@Override
	public List<Sala> listaPorNumeroIgualRegistra(String nombre) {
		return repository.listaPorNumeroIgualRegistra(nombre);

	}

	@Override
	public List<Sala> listaPorNumeroIgualActualiza(String nombre,int id) {
		return repository.listaPorNumeroIgualActualiza(nombre, id);

	}
	
}
