package com.senai.findcar.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.senai.findcar.exceptions.VeiculoJaExistenteException;
import com.senai.findcar.models.Veiculo;
import com.senai.findcar.repository.FornecedorRepository;
import com.senai.findcar.repository.VeiculoRepository;


@Controller
public class VeiculoController {

    final String VEICULO_JA_EXISTENTE = "Veiculo já existente, por favor utilize o botão de editar";

    @Autowired
    private VeiculoRepository veiculoRepository;
    
    @Autowired
    private FornecedorRepository fornecedorRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/veiculos")
    public String listarVeiculos(
            Model model,
            @RequestParam(name = "placa", required = false) String placa,
            @RequestParam(name = "chassi", required = false) String chassi,
            @RequestParam(name = "marca", required = false) String marca,
            @RequestParam(name = "modelo", required = false) String modelo,
            @RequestParam(name = "minValor", required = false) BigDecimal minValor,
            @RequestParam(name = "maxValor", required = false) BigDecimal maxValor,
            @RequestParam(defaultValue = "0", required = false) int page, 
            @RequestParam(defaultValue = "50", required = false) int size
    ) {
        boolean hasFilters =
                (placa != null && !placa.isBlank()) ||
                (chassi != null && !chassi.isBlank()) ||
                (marca != null && !marca.isBlank()) ||
                (modelo != null && !modelo.isBlank()) ||
                (minValor != null) ||
                (maxValor != null);

        // Normaliza entradas em branco para null, evitando LIKE '%%'
        placa = (placa != null && !placa.isBlank()) ? placa : null;
        chassi = (chassi != null && !chassi.isBlank()) ? chassi : null;
        marca = (marca != null && !marca.isBlank()) ? marca : null;
        modelo = (modelo != null && !modelo.isBlank()) ? modelo : null;

        // Corrige range invertido
        if (minValor != null && maxValor != null && minValor.compareTo(maxValor) > 0) {
            java.math.BigDecimal tmp = minValor;
            minValor = maxValor;
            maxValor = tmp;
        }

        Page<Veiculo> resultados =
                hasFilters
                        ? veiculoRepository.search(placa, chassi, marca, modelo, minValor, maxValor, PageRequest.of(page, size))
                        : veiculoRepository.findAll(PageRequest.of(page, size));

        List<Veiculo> filtrados = resultados.getContent()
                .stream()
                .filter(v -> !v.isVendido())
                .toList();

        Page<Veiculo> paginaFiltrada = new PageImpl<>(
                filtrados,
                resultados.getPageable(),
                resultados.getTotalElements()
        );

        model.addAttribute("veiculos", paginaFiltrada);
        model.addAttribute("veiculo", new Veiculo());
        model.addAttribute("fornecedores", fornecedorRepository.findAll());
        return "veiculos";
    }

    @PostMapping("/veiculos/novo")
    public String novoVeiculo(Veiculo veiculo) {
    	Optional<Veiculo> veiculoOpt = veiculoRepository.findById(veiculo.getPlaca());
    	if(veiculoOpt.isPresent()) { //jogar isso num validator ou service futuramente
    		throw new VeiculoJaExistenteException(VEICULO_JA_EXISTENTE);
    	}

        Optional<Veiculo> veiculoChassiOpt = veiculoRepository.findByChassi(veiculo.getChassi());
    	if(veiculoChassiOpt.isPresent()) {
    		throw new VeiculoJaExistenteException(VEICULO_JA_EXISTENTE);
    	}

        Long fornecedorId = veiculo.getFornecedor() != null ? veiculo.getFornecedor().getId() : null;
        if (fornecedorId == null) {
            return "redirect:/veiculos?error=fornecedor_obrigatorio";
        }
        var fornecedorOpt = fornecedorRepository.findById(fornecedorId);
        if (fornecedorOpt.isEmpty()) {
            return "redirect:/veiculos?error=fornecedor_invalido";
        }
        veiculo.setFornecedor(fornecedorOpt.get());

        veiculoRepository.save(veiculo);
        return "redirect:/veiculos?success=cadastrado";
    }

    @PostMapping("/veiculos/atualizar/{placa}")
    public String atualizarVeiculo(@PathVariable("placa") String placa, Veiculo veiculo) {
        Long fornecedorId = veiculo.getFornecedor() != null ? veiculo.getFornecedor().getId() : null;
        if (fornecedorId == null) {
            return "redirect:/veiculos?error=fornecedor_obrigatorio";
        }
        var fornecedorOpt = fornecedorRepository.findById(fornecedorId);
        if (fornecedorOpt.isEmpty()) {
            return "redirect:/veiculos?error=fornecedor_invalido";
        }
        veiculo.setFornecedor(fornecedorOpt.get());
        veiculoRepository.save(veiculo);
        return "redirect:/veiculos?success=atualizado";
    }

    @GetMapping("/veiculos/{placa}")
    public String exibirVeiculo(@PathVariable("placa") String placa, Model model) {
        Veiculo veiculo = veiculoRepository.findById(placa).orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        model.addAttribute("veiculo", veiculo);
        return "veiculo-detalhes";
    }

    @GetMapping("/veiculos/excluir/{placa}")
    public String excluirVeiculo(@PathVariable("placa") String placa) {
        Veiculo veiculo = veiculoRepository.findById(placa).orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        veiculoRepository.delete(veiculo);
        return "redirect:/veiculos?success=excluido";
    }

}
