package com.senai.findcar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senai.findcar.models.Veiculo;

public interface VeiculoRepository extends JpaRepository<Veiculo, String> {
	
}
