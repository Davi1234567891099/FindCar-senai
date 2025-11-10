package com.senai.findcar.exceptions;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VeiculoJaExistenteException.class)
    public String handleVeiculoJaExistente(VeiculoJaExistenteException ex, Model model) {
        model.addAttribute("erro", ex.getMessage());
        return "erro-veiculo-existente"; 
    }
}