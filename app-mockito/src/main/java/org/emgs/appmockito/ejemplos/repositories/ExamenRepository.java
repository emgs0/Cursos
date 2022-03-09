package org.emgs.appmockito.ejemplos.repositories;

import org.emgs.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findAll();
}
