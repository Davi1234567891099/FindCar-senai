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

import com.senai.findcar.models.Fornecedor;
import com.senai.findcar.repository.FornecedorRepository;
import com.senai.findcar.services.FornecedorService;

@Controller
public class FornecedorController {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private FornecedorService fornecedorService;

    @GetMapping("/fornecedores")
    public String listarFornecedores(Model model,
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

        List<Fornecedor> fornecedores = hasFilters
                ? fornecedorRepository.search(nome, cnpj, cpf, telefone, email)
                : fornecedorRepository.findAll();

        model.addAttribute("fornecedores", fornecedores);
        model.addAttribute("fornecedor", new Fornecedor());
        return "fornecedores";
    }

    @PostMapping("/fornecedores/novo")
    public String novoFornecedor(Fornecedor fornecedor) {
        String cnpj = fornecedor.getCnpj() != null ? fornecedor.getCnpj().trim() : null;
        String cpf = fornecedor.getCpf() != null ? fornecedor.getCpf().trim() : null;
        if (cnpj != null && cnpj.isEmpty()) cnpj = null;
        if (cpf != null && cpf.isEmpty()) cpf = null;
        fornecedor.setCnpj(cnpj);
        fornecedor.setCpf(cpf);

        if (cnpj != null) {
            Optional<Fornecedor> cnpjOpt = fornecedorRepository.findByCnpj(cnpj);
            if (cnpjOpt.isPresent()) {
                return "redirect:/fornecedores?error=cnpj_existente";
            }
        }
        if (cpf != null) {
            Optional<Fornecedor> cpfOpt = fornecedorRepository.findByCpf(cpf);
            if (cpfOpt.isPresent()) {
                return "redirect:/fornecedores?error=cpf_existente";
            }
        }
        fornecedorRepository.save(fornecedor);
        return "redirect:/fornecedores?success=cadastrado";
    }

    @PostMapping("/fornecedores/atualizar/{id}")
    public String atualizarFornecedor(@PathVariable("id") Long id, Fornecedor fornecedor) {
        fornecedor.setId(id);

        String cnpj = fornecedor.getCnpj() != null ? fornecedor.getCnpj().trim() : null;
        String cpf = fornecedor.getCpf() != null ? fornecedor.getCpf().trim() : null;
        if (cnpj != null && cnpj.isEmpty()) cnpj = null;
        if (cpf != null && cpf.isEmpty()) cpf = null;
        fornecedor.setCnpj(cnpj);
        fornecedor.setCpf(cpf);

        if (cnpj != null) {
            Optional<Fornecedor> cnpjOpt = fornecedorRepository.findByCnpj(cnpj);
            if (cnpjOpt.isPresent() && !cnpjOpt.get().getId().equals(id)) {
                return "redirect:/fornecedores?error=cnpj_existente";
            }
        }
        if (cpf != null) {
            Optional<Fornecedor> cpfOpt = fornecedorRepository.findByCpf(cpf);
            if (cpfOpt.isPresent() && !cpfOpt.get().getId().equals(id)) {
                return "redirect:/fornecedores?error=cpf_existente";
            }
        }

        fornecedorRepository.save(fornecedor);
        return "redirect:/fornecedores?success=atualizado";
    }

    @GetMapping("/fornecedores/excluir/{id}")
    public String excluirFornecedor(@PathVariable("id") Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado"));

        fornecedorService.verificarFornecedorComVeiculos(fornecedor);
        fornecedorRepository.delete(fornecedor);
        return "redirect:/fornecedores?success=excluido";
    }
}


