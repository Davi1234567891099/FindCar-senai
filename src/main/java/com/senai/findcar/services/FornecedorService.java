package com.senai.findcar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.senai.findcar.exceptions.FornecedorComVeiculosException;
import com.senai.findcar.models.Fornecedor;
import com.senai.findcar.models.Veiculo;
import com.senai.findcar.repository.VeiculoRepository;

@Service
public class FornecedorService {

    private static final String FORNECEDOR_COM_VEICULOS = "Fornecedor possui veículos vinculados, não é possível excluir";

    @Autowired
    private VeiculoRepository veiculoRepository;

    public void verificarFornecedorComVeiculos(Fornecedor fornecedor) {
        List<Veiculo> veiculos = veiculoRepository.findByFornecedor(fornecedor);
        if(veiculos != null && !veiculos.isEmpty()) {
            throw new FornecedorComVeiculosException(FORNECEDOR_COM_VEICULOS);
        }
    }
}
