package com.example.ProyectoBoletera.dominio.repository;


import com.example.ProyectoBoletera.dominio.model.Evento;
import com.example.ProyectoBoletera.dominio.enums.CategoriaEvento;
import com.example.ProyectoBoletera.dominio.enums.EstadoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByEstado(EstadoEvento estado);
    List<Evento> findByCategoria(CategoriaEvento categoria);
    List<Evento> findByAdministradorId(Long administradorId);
    List<Evento> findByInactivoFalse();
    long countByInactivoTrue(); //para contar eventos inactivos
}
