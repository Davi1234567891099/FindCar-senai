package com.senai.findcar.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.senai.findcar.models.Cliente;
import com.senai.findcar.models.Funcionario;
import com.senai.findcar.models.Venda;

public interface VendaRepository extends JpaRepository<Venda, Long> {
	boolean existsByVeiculo_Placa(String placa);

	@Query("""
		SELECT v FROM Venda v
		WHERE (:placa IS NULL OR LOWER(v.veiculo.placa) LIKE LOWER(CONCAT('%', :placa, '%')))
		  AND (:marca IS NULL OR LOWER(v.veiculo.marca) LIKE LOWER(CONCAT('%', :marca, '%')))
		  AND (:modelo IS NULL OR LOWER(v.veiculo.modelo) LIKE LOWER(CONCAT('%', :modelo, '%')))
		  AND (:cliente IS NULL OR LOWER(v.cliente.nome) LIKE LOWER(CONCAT('%', :cliente, '%')))
		  AND (:funcionario IS NULL OR LOWER(v.funcionario.nome) LIKE LOWER(CONCAT('%', :funcionario, '%')))
		  AND (:dataInicio IS NULL OR v.dataVenda >= :dataInicio)
		  AND (:dataFim IS NULL OR v.dataVenda <= :dataFim)
		ORDER BY v.dataVenda DESC
	""")
	List<Venda> search(
			@Param("placa") String placa,
			@Param("marca") String marca,
			@Param("modelo") String modelo,
			@Param("cliente") String cliente,
			@Param("funcionario") String funcionario,
			@Param("dataInicio") LocalDate dataInicio,
			@Param("dataFim") LocalDate dataFim
	);

	List<Venda> findAllByOrderByDataVendaDesc();

	Optional<Venda> findByCliente(Cliente cliente);

	Optional<Venda> findByFuncionario(Funcionario funcionario);
}


