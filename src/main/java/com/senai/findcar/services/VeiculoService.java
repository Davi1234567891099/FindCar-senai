package com.senai.findcar.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.senai.findcar.exceptions.VeiculoJaExistenteException;
import com.senai.findcar.models.Veiculo;
import com.senai.findcar.repository.VeiculoRepository;


public class VeiculoService {

    final String VEICULO_JA_EXISTENTE = "Veiculo já existente, por favor utilize o botão de editar";

    @Autowired
    private VeiculoRepository veiculoRepository;

    public void verificarVeiculoExistente(Veiculo veiculo) {
        boolean veiculoExistente = false;
        Optional<Veiculo> veiculoOpt = veiculoRepository.findById(veiculo.getPlaca());
        if(veiculoOpt.isPresent()) {
            veiculoExistente = true;
        } else {
            Optional<Veiculo> veiculoChassiOpt = veiculoRepository.findByChassi(veiculo.getChassi());
            if(veiculoChassiOpt.isPresent()) {
                veiculoExistente = true;
            }
        }
        
        if(veiculoExistente) {
            throw new VeiculoJaExistenteException(VEICULO_JA_EXISTENTE);
        }
    }
}
