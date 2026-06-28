package com.example.ProyectoBoletera.dominio.repository;

import com.example.ProyectoBoletera.dominio.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    List<Boleto> findByEventoId(Long eventoId);
}
