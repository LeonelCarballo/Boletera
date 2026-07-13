package com.example.ProyectoBoletera.dominio.repository;


import com.example.ProyectoBoletera.dominio.model.BoletoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoletoClienteRepository extends JpaRepository<BoletoCliente, Long> {
    Optional<BoletoCliente> findByCodigoQr(String codigoQr);
    List<BoletoCliente> findByCompraId(Long compraId);

    boolean existsByBoletoIdAndAsientoId(Long boletoId, Long asientoId);

    @Query("select bc.asiento.id from BoletoCliente bc where bc.boleto.id = :boletoId and bc.asiento is not null")
    List<Long> findAsientoIdsOcupadosPorBoleto(@Param("boletoId") Long boletoId);
}
