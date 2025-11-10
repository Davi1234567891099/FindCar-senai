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

import com.senai.findcar.models.Funcionario;
import com.senai.findcar.repository.FuncionarioRepository;
import com.senai.findcar.services.FuncionarioService;

@Controller
public class FuncionarioController {

	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private FuncionarioService funcionarioService;

	@GetMapping("/funcionarios")
	public String listarFuncionarios(Model model,
									 @RequestParam(name = "success", required = false) String success,
									 @RequestParam(name = "error", required = false) String error,
									 @RequestParam(name = "nome", required = false) String nome,
									 @RequestParam(name = "cpf", required = false) String cpf,
									 @RequestParam(name = "cargo", required = false) String cargo,
									 @RequestParam(name = "telefone", required = false) String telefone,
									 @RequestParam(name = "email", required = false) String email) {
		boolean hasFilters =
				(nome != null && !nome.isBlank()) ||
				(cpf != null && !cpf.isBlank()) ||
				(cargo != null && !cargo.isBlank()) ||
				(telefone != null && !telefone.isBlank()) ||
				(email != null && !email.isBlank());

		nome = (nome != null && !nome.isBlank()) ? nome : null;
		cpf = (cpf != null && !cpf.isBlank()) ? cpf : null;
		cargo = (cargo != null && !cargo.isBlank()) ? cargo : null;
		telefone = (telefone != null && !telefone.isBlank()) ? telefone : null;
		email = (email != null && !email.isBlank()) ? email : null;

		List<Funcionario> funcionarios = hasFilters
				? funcionarioRepository.search(nome, cpf, cargo, telefone, email)
				: funcionarioRepository.findAll();

		model.addAttribute("funcionarios", funcionarios);
		model.addAttribute("funcionario", new Funcionario());
		return "funcionarios";
	}

	@PostMapping("/funcionarios/novo")
	public String novoFuncionario(Funcionario funcionario) {
		String cpf = funcionario.getCpf() != null ? funcionario.getCpf().trim() : null;
		if (cpf != null && cpf.isEmpty()) cpf = null;
		funcionario.setCpf(cpf);

		if (cpf != null) {
			Optional<Funcionario> cpfOpt = funcionarioRepository.findByCpf(cpf);
			if (cpfOpt.isPresent()) {
				return "redirect:/funcionarios?error=cpf_existente";
			}
		}
		funcionarioRepository.save(funcionario);
		return "redirect:/funcionarios?success=cadastrado";
	}

	@PostMapping("/funcionarios/atualizar/{id}")
	public String atualizarFuncionario(@PathVariable("id") Long id, Funcionario funcionario) {
		funcionario.setId(id);

		String cpf = funcionario.getCpf() != null ? funcionario.getCpf().trim() : null;
		if (cpf != null && cpf.isEmpty()) cpf = null;
		funcionario.setCpf(cpf);

		if (cpf != null) {
			Optional<Funcionario> cpfOpt = funcionarioRepository.findByCpf(cpf);
			if (cpfOpt.isPresent() && !cpfOpt.get().getId().equals(id)) {
				return "redirect:/funcionarios?error=cpf_existente";
			}
		}
		funcionarioRepository.save(funcionario);
		return "redirect:/funcionarios?success=atualizado";
	}

	@GetMapping("/funcionarios/excluir/{id}")
	public String excluirFuncionario(@PathVariable("id") Long id) {
		Funcionario funcionario = funcionarioRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
		funcionarioService.verificarFuncionarioComVenda(funcionario);
		funcionarioRepository.delete(funcionario);
		return "redirect:/funcionarios?success=excluido";
	}
}


