package com.example.ProyectoBoletera.dominio.repository;

import com.example.ProyectoBoletera.dominio.model.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {
    List<Asiento> findByZonaIdOrderByFilaAscNumeroAsc(Long zonaId);
    void deleteByZonaId(Long zonaId);
}
