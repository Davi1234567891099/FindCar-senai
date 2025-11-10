package com.senai.findcar.utils;


public class Utils {

	public static void validarPaginacao(Integer pagina, Integer numElementos) {
    	if(pagina < 0) {
    		pagina = 0;
    	}
    	
    	if(numElementos < 0) {
    		numElementos = 0;
    	}
	}
}
