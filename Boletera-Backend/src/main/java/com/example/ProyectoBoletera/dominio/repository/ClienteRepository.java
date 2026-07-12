package com.example.ProyectoBoletera.dominio.repository;

import com.example.ProyectoBoletera.dominio.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}