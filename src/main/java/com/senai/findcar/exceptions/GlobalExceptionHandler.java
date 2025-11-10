package com.senai.findcar.exceptions;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    final String ERRO_VEICULO_EXISTENTE = "erro-veiculo-existente";
    final String ERRO_FORNECEDOR_COM_VEICULOS = "erro-excluir-fornecedor-com-veiculo";
    final String ERRO_VENDA_VINCULADA = "erro-excluir-venda-vinculada";

    @ExceptionHandler(VeiculoJaExistenteException.class)
    public String handleVeiculoJaExistente(VeiculoJaExistenteException ex, Model model) {
        model.addAttribute("erro", ex.getMessage());
        return ERRO_VEICULO_EXISTENTE; 
    }

    @ExceptionHandler(FornecedorComVeiculosException.class)
    public String handleExcluirFornecedorComVeiculo(FornecedorComVeiculosException ex, Model model) {
        model.addAttribute("erro", ex.getMessage());
        return ERRO_FORNECEDOR_COM_VEICULOS; 
    }

    @ExceptionHandler(VendaVinculadaException.class)
    public String handleExcluirVendaVinculada(VendaVinculadaException ex, Model model) {
        model.addAttribute("erro", ex.getMessage());
        return ERRO_VENDA_VINCULADA; 
    }

}