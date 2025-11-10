package com.senai.findcar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.senai.findcar.models.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    
    Optional<Fornecedor> findByCnpj(String cnpj);
    Optional<Fornecedor> findByCpf(String cpf);
    Optional<Fornecedor> findFirstByNomeIgnoreCase(String nome);

    @Query("""
        SELECT f FROM Fornecedor f
        WHERE (:nome IS NULL OR LOWER(f.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:cnpj IS NULL OR f.cnpj LIKE CONCAT('%', :cnpj, '%'))
          AND (:cpf IS NULL OR f.cpf LIKE CONCAT('%', :cpf, '%'))
          AND (:telefone IS NULL OR LOWER(f.telefone) LIKE LOWER(CONCAT('%', :telefone, '%')))
          AND (:email IS NULL OR LOWER(f.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    List<Fornecedor> search(
        @Param("nome") String nome,
        @Param("cnpj") String cnpj,
        @Param("cpf") String cpf,
        @Param("telefone") String telefone,
        @Param("email") String email
    );
}
