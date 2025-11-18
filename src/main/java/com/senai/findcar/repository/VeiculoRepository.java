package com.senai.findcar.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.senai.findcar.models.Fornecedor;
import com.senai.findcar.models.Veiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, String> {

	List<Veiculo> findByFornecedor(Fornecedor fornecedor);
	Optional<Veiculo> findByChassi(String chassi);

	@Query("""
		SELECT v FROM Veiculo v
		WHERE (:placa IS NULL OR LOWER(v.placa) LIKE LOWER(CONCAT('%', :placa, '%')))
		  AND (:chassi IS NULL OR LOWER(v.chassi) LIKE LOWER(CONCAT('%', :chassi, '%')))
		  AND (:marca IS NULL OR LOWER(v.marca) LIKE LOWER(CONCAT('%', :marca, '%')))
		  AND (:modelo IS NULL OR LOWER(v.modelo) LIKE LOWER(CONCAT('%', :modelo, '%')))
		  AND (:minValor IS NULL OR v.valor >= :minValor)
		  AND (:maxValor IS NULL OR v.valor <= :maxValor)
	""")
	Page<Veiculo> search(
			@Param("placa") String placa,
			@Param("chassi") String chassi,
			@Param("marca") String marca,
			@Param("modelo") String modelo,
			@Param("minValor") BigDecimal minValor,
			@Param("maxValor") BigDecimal maxValor,
			Pageable pageable
	);
}
