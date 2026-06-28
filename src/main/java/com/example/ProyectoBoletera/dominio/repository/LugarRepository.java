package com.example.ProyectoBoletera.dominio.repository;

import com.example.ProyectoBoletera.dominio.model.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LugarRepository extends JpaRepository<Lugar, Long> {
    List<Lugar> findByCiudad(String ciudad);
}
