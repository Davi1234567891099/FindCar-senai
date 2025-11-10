package com.senai.findcar.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.senai.findcar.models.Cliente;
import com.senai.findcar.repository.ClienteRepository;
import com.senai.findcar.services.ClienteService;

@Controller
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/clientes")
    public String listarClientes(Model model,
                                 @RequestParam(name = "success", required = false) String success,
                                 @RequestParam(name = "error", required = false) String error,
                                 @RequestParam(name = "nome", required = false) String nome,
                                 @RequestParam(name = "cnpj", required = false) String cnpj,
                                 @RequestParam(name = "cpf", required = false) String cpf,
                                 @RequestParam(name = "telefone", required = false) String telefone,
                                 @RequestParam(name = "email", required = false) String email) {
        boolean hasFilters =
            (nome != null && !nome.isBlank()) ||
            (cnpj != null && !cnpj.isBlank()) ||
            (cpf != null && !cpf.isBlank()) ||
            (telefone != null && !telefone.isBlank()) ||
            (email != null && !email.isBlank());

        nome = (nome != null && !nome.isBlank()) ? nome : null;
        cnpj = (cnpj != null && !cnpj.isBlank()) ? cnpj : null;
        cpf = (cpf != null && !cpf.isBlank()) ? cpf : null;
        telefone = (telefone != null && !telefone.isBlank()) ? telefone : null;
        email = (email != null && !email.isBlank()) ? email : null;

        List<Cliente> clientes = hasFilters
                ? clienteRepository.search(nome, cnpj, cpf, telefone, email)
                : clienteRepository.findAll();

        model.addAttribute("clientes", clientes);
        model.addAttribute("cliente", new Cliente());
        return "clientes";
    }

    @PostMapping("/clientes/novo")
    public String novoCliente(Cliente cliente) {
        String cnpj = cliente.getCnpj() != null ? cliente.getCnpj().trim() : null;
        String cpf = cliente.getCpf() != null ? cliente.getCpf().trim() : null;
        if (cnpj != null && cnpj.isEmpty()) cnpj = null;
        if (cpf != null && cpf.isEmpty()) cpf = null;
        cliente.setCnpj(cnpj);
        cliente.setCpf(cpf);

        if (cnpj != null) {
            Optional<Cliente> cnpjOpt = clienteRepository.findByCnpj(cnpj);
            if (cnpjOpt.isPresent()) {
                return "redirect:/clientes?error=cnpj_existente";
            }
        }
        if (cpf != null) {
            Optional<Cliente> cpfOpt = clienteRepository.findByCpf(cpf);
            if (cpfOpt.isPresent()) {
                return "redirect:/clientes?error=cpf_existente";
            }
        }
        clienteRepository.save(cliente);
        return "redirect:/clientes?success=cadastrado";
    }

    @PostMapping("/clientes/atualizar/{id}")
    public String atualizarCliente(@PathVariable("id") Long id, Cliente cliente) {
        cliente.setId(id);

        String cnpj = cliente.getCnpj() != null ? cliente.getCnpj().trim() : null;
        String cpf = cliente.getCpf() != null ? cliente.getCpf().trim() : null;
        if (cnpj != null && cnpj.isEmpty()) cnpj = null;
        if (cpf != null && cpf.isEmpty()) cpf = null;
        cliente.setCnpj(cnpj);
        cliente.setCpf(cpf);

        if (cnpj != null) {
            Optional<Cliente> cnpjOpt = clienteRepository.findByCnpj(cnpj);
            if (cnpjOpt.isPresent() && !cnpjOpt.get().getId().equals(id)) {
                return "redirect:/clientes?error=cnpj_existente";
            }
        }
        if (cpf != null) {
            Optional<Cliente> cpfOpt = clienteRepository.findByCpf(cpf);
            if (cpfOpt.isPresent() && !cpfOpt.get().getId().equals(id)) {
                return "redirect:/clientes?error=cpf_existente";
            }
        }
        clienteRepository.save(cliente);
        return "redirect:/clientes?success=atualizado";
    }

    @GetMapping("/clientes/excluir/{id}")
    public String excluirCliente(@PathVariable("id") Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        clienteService.verificarClienteComVenda(cliente);
        clienteRepository.delete(cliente);
        return "redirect:/clientes?success=excluido";
    }
}


