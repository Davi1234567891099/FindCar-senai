package com.senai.findcar.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.senai.findcar.exceptions.VeiculoJaExistenteException;
import com.senai.findcar.models.Veiculo;
import com.senai.findcar.repository.VeiculoRepository;
import com.senai.findcar.utils.Utils;


@Controller
public class VeiculoController {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/veiculos")
    public String listarVeiculos(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) { 
    	Utils.validarPaginacao(page, size);
    	
    	Page<Veiculo> veiculosPage = veiculoRepository.findAll(PageRequest.of(page, size)); //Limitar pelo front o numero de elementos por pagina
        model.addAttribute("veiculos", veiculosPage);												  //creio que 100 seja um bom limite
        model.addAttribute("veiculo", new Veiculo());
        return "veiculos";
    }

	@PostMapping("/veiculos/novo")
    public String novoVeiculo(Veiculo veiculo) {
    	Optional<Veiculo> veiculoOpt = veiculoRepository.findById(veiculo.getPlaca());
    	if(veiculoOpt.isPresent()) { //jogar isso num validator ou service futuramente
    		throw new VeiculoJaExistenteException("Veiculo ja existente, por favor utilize o botão de editar");
    	}
    	
        veiculoRepository.save(veiculo);
        return "redirect:/veiculos";
    }

    @PostMapping("/veiculos/atualizar/{placa}")
    public String atualizarVeiculo(@PathVariable("placa") String placa, Veiculo veiculo) {
        veiculoRepository.save(veiculo);
        return "redirect:/veiculos";
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
        return "redirect:/veiculos";
    }

}
