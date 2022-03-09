package org.emgs.appmockito.ejemplos.services;

import org.emgs.appmockito.ejemplos.models.Examen;
import org.emgs.appmockito.ejemplos.repositories.ExamenRepository;

import java.util.Optional;

public class ExamenServiceImpl implements ExamenService{

    private ExamenRepository examenRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        //optional valida si existe un elemento en la busqueda compatible con el inyectado
        return examenRepository.findAll()
                .stream()
                .filter(e-> e.getNombre().equals(nombre))
                .findFirst();
    }
}
