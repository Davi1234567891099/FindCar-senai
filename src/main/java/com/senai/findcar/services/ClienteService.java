package com.senai.findcar.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.senai.findcar.exceptions.VendaVinculadaException;
import com.senai.findcar.models.Cliente;
import com.senai.findcar.models.Venda;
import com.senai.findcar.repository.VendaRepository;

@Service
public class ClienteService {

    final String CLIENTE_COM_VENDA = "Cliente possui vendas vinculadas, não é possível excluir";

    @Autowired
    private VendaRepository vendaRepository;

    public void verificarClienteComVenda(Cliente cliente) {
        Optional<Venda> vendaOpt = vendaRepository.findByCliente(cliente);
        if(vendaOpt.isPresent()) {
            throw new VendaVinculadaException(CLIENTE_COM_VENDA);
        }
    }
}
