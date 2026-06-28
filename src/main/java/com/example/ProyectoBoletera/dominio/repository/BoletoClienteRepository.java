package com.example.ProyectoBoletera.dominio.repository;


import com.example.ProyectoBoletera.dominio.model.BoletoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoletoClienteRepository extends JpaRepository<BoletoCliente, Long> {
    Optional<BoletoCliente> findByCodigoQr(String codigoQr);
    List<BoletoCliente> findByCompraId(Long compraId);
}
