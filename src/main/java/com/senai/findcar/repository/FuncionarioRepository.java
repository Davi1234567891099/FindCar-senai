package com.senai.findcar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.senai.findcar.models.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
	Optional<Funcionario> findByCpf(String cpf);

	java.util.List<Funcionario> findByCargoIn(java.util.List<String> cargos);

	@Query("""
		SELECT f FROM Funcionario f
		WHERE (:nome IS NULL OR LOWER(f.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
		  AND (:cpf IS NULL OR f.cpf LIKE CONCAT('%', :cpf, '%'))
		  AND (:cargo IS NULL OR LOWER(f.cargo) LIKE LOWER(CONCAT('%', :cargo, '%')))
		  AND (:telefone IS NULL OR LOWER(f.telefone) LIKE LOWER(CONCAT('%', :telefone, '%')))
		  AND (:email IS NULL OR LOWER(f.email) LIKE LOWER(CONCAT('%', :email, '%')))
	""")
	List<Funcionario> search(
		@Param("nome") String nome,
		@Param("cpf") String cpf,
		@Param("cargo") String cargo,
		@Param("telefone") String telefone,
		@Param("email") String email
	);
}


