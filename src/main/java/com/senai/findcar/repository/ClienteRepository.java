package com.senai.findcar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.senai.findcar.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCnpj(String cnpj);
    Optional<Cliente> findByCpf(String cpf);

    @Query("""
        SELECT c FROM Cliente c
        WHERE (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
          AND (:cnpj IS NULL OR c.cnpj LIKE CONCAT('%', :cnpj, '%'))
          AND (:cpf IS NULL OR c.cpf LIKE CONCAT('%', :cpf, '%'))
          AND (:telefone IS NULL OR LOWER(c.telefone) LIKE LOWER(CONCAT('%', :telefone, '%')))
          AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    List<Cliente> search(
        @Param("nome") String nome,
        @Param("cnpj") String cnpj,
        @Param("cpf") String cpf,
        @Param("telefone") String telefone,
        @Param("email") String email
    );
}


