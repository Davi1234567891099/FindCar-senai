package com.senai.findcar.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.senai.findcar.models.Cliente;
import com.senai.findcar.models.Funcionario;
import com.senai.findcar.models.Veiculo;
import com.senai.findcar.models.Venda;
import com.senai.findcar.repository.ClienteRepository;
import com.senai.findcar.repository.FuncionarioRepository;
import com.senai.findcar.repository.VeiculoRepository;
import com.senai.findcar.repository.VendaRepository;

@Controller
public class VendaController {

	@Autowired
	private VendaRepository vendaRepository;
	@Autowired
	private VeiculoRepository veiculoRepository;
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@GetMapping("/vendas")
	public String listarVendas(Model model,
							   @RequestParam(name = "success", required = false) String success,
							   @RequestParam(name = "error", required = false) String error,
							   @RequestParam(name = "placa", required = false) String placa,
							   @RequestParam(name = "marca", required = false) String marca,
							   @RequestParam(name = "modelo", required = false) String modelo,
							   @RequestParam(name = "cliente", required = false) String cliente,
							   @RequestParam(name = "funcionario", required = false) String funcionario,
							   @RequestParam(name = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
							   @RequestParam(name = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
		boolean hasFilters =
				(placa != null && !placa.isBlank()) ||
				(marca != null && !marca.isBlank()) ||
				(modelo != null && !modelo.isBlank()) ||
				(cliente != null && !cliente.isBlank()) ||
				(funcionario != null && !funcionario.isBlank()) ||
				(dataInicio != null) ||
				(dataFim != null);

		placa = (placa != null && !placa.isBlank()) ? placa : null;
		marca = (marca != null && !marca.isBlank()) ? marca : null;
		modelo = (modelo != null && !modelo.isBlank()) ? modelo : null;
		cliente = (cliente != null && !cliente.isBlank()) ? cliente : null;
		funcionario = (funcionario != null && !funcionario.isBlank()) ? funcionario : null;

		List<Venda> vendas = hasFilters
				? vendaRepository.search(placa, marca, modelo, cliente, funcionario, dataInicio, dataFim)
				: vendaRepository.findAllByOrderByDataVendaDesc();
		List<Veiculo> veiculos = veiculoRepository.findAll().stream().filter(v -> !v.isVendido()).toList();
		List<Cliente> clientes = clienteRepository.findAll();
		List<Funcionario> funcionarios = funcionarioRepository.findByCargoIn(java.util.Arrays.asList("Gerente", "Vendedor"));

		model.addAttribute("vendas", vendas);
		model.addAttribute("veiculosDisponiveis", veiculos);
		model.addAttribute("clientes", clientes);
		model.addAttribute("funcionarios", funcionarios);
		model.addAttribute("venda", new Venda());
		return "vendas";
	}

	@PostMapping("/vendas/novo")
	public String novaVenda(Venda venda) {
		// Validar e materializar entidades
		String placa = venda.getVeiculo() != null ? venda.getVeiculo().getPlaca() : null;
		Long clienteId = venda.getCliente() != null ? venda.getCliente().getId() : null;
		Long funcionarioId = venda.getFuncionario() != null ? venda.getFuncionario().getId() : null;
		if (placa == null || clienteId == null || funcionarioId == null) {
			return "redirect:/vendas?error=dados_invalidos";
		}
		Optional<Veiculo> veiculoOpt = veiculoRepository.findById(placa);
		Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
		Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(funcionarioId);
		if (veiculoOpt.isEmpty() || clienteOpt.isEmpty() || funcionarioOpt.isEmpty()) {
			return "redirect:/vendas?error=dados_invalidos";
		}

		// Impedir venda duplicada do mesmo veículo
		if (vendaRepository.existsByVeiculo_Placa(placa) || veiculoOpt.get().isVendido()) {
			return "redirect:/vendas?error=veiculo_ja_vendido";
		}

		Funcionario func = funcionarioOpt.get();
		String cargo = func.getCargo() != null ? func.getCargo() : "";
		if (!cargo.equalsIgnoreCase("Gerente") && !cargo.equalsIgnoreCase("Vendedor")) {
			return "redirect:/vendas?error=funcionario_invalido";
		}

		Veiculo veic = veiculoOpt.get();
		venda.setVeiculo(veic);
		venda.setCliente(clienteOpt.get());
		venda.setFuncionario(funcionarioOpt.get());

		// Data default para hoje se não informada
		if (venda.getDataVenda() == null) {
			venda.setDataVenda(LocalDate.now());
		}
		// Valor da venda deve existir e ser positivo
		BigDecimal vv = venda.getValorVenda();
		if (vv == null || vv.compareTo(BigDecimal.ZERO) <= 0) {
			return "redirect:/vendas?error=valor_invalido";
		}

		// marca veículo como vendido
		veic.setVendido(true);
		veiculoRepository.save(veic);
		vendaRepository.save(venda);
		return "redirect:/vendas?success=cadastrado";
	}

	@org.springframework.web.bind.annotation.GetMapping("/vendas/excluir/{id}")
	public String excluirVenda(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
		Optional<Venda> vendaOpt = vendaRepository.findById(id);
		if (vendaOpt.isEmpty()) {
			return "redirect:/vendas?error=dados_invalidos";
		}
		Venda venda = vendaOpt.get();
		Veiculo veic = venda.getVeiculo();
		// liberar veículo
		if (veic != null) {
			veic.setVendido(false);
			veiculoRepository.save(veic);
		}
		vendaRepository.delete(venda);
		return "redirect:/vendas?success=excluido";
	}
}


