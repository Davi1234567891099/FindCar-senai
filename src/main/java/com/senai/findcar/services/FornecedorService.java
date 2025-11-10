package com.senai.findcar.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.senai.findcar.exceptions.FornecedorComVeiculosException;
import com.senai.findcar.models.Fornecedor;
import com.senai.findcar.models.Veiculo;
import com.senai.findcar.repository.VeiculoRepository;

@Service
public class FornecedorService {

    final String FORNECEDOR_COM_VEICULOS = "Fornecedor possui veículos vinculados, não é possível excluir";

    @Autowired
    private VeiculoRepository veiculoRepository;

    public void verificarFornecedorComVeiculos(Fornecedor fornecedor) {
        Optional<Veiculo> veiculoOpt = veiculoRepository.findByFornecedor(fornecedor);
        if(veiculoOpt.isPresent()) {
            throw new FornecedorComVeiculosException(FORNECEDOR_COM_VEICULOS);
        }
    }
}
