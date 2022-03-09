package org.emgs.appmockito.ejemplos.services;

import org.emgs.appmockito.ejemplos.models.Examen;
import org.emgs.appmockito.ejemplos.repositories.ExamenRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//mock solo de metodos publicos o default dentro del mismo package, no privados ni estaticos o finals
class ExamenServiceImplTest {

    @Test
    void findExamenPorNombre() {
        ExamenRepository repository = mock(ExamenRepository.class); //nombre de interfaz o clase a simular
        ExamenService service = new ExamenServiceImpl(repository);
        List<Examen> datos = Arrays.asList(new Examen(5L,"Lenguaje C"), new Examen(7L,"Java"));
        when(repository.findAll()).thenReturn(datos); //aqui se maquetan los datos que tiene ExamenServiceImpl sin necesidad de crear una clase del repositorio

        Optional<Examen> examen = service.findExamenPorNombre("Lenguaje C");
        assertTrue(examen.isPresent());
        assertEquals(5L,examen.orElseThrow().getId());
        assertEquals("Lenguaje C",examen.get().getNombre());
    }
    @Test
    void findExamenPorNombreListaVacia() {
        ExamenRepository repository = mock(ExamenRepository.class);
        ExamenService service = new ExamenServiceImpl(repository);
        List<Examen> datos = Collections.emptyList(); //lista vacia maquetada
        when(repository.findAll()).thenReturn(datos);

        Optional<Examen> examen = service.findExamenPorNombre("Lenguaje C");
        assertTrue(examen.isPresent());
        assertEquals(5L,examen.orElseThrow().getId());
        assertEquals("Lenguaje C",examen.get().getNombre());
    }

}