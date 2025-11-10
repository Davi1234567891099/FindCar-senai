package com.senai.findcar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.senai.findcar.models.Veiculo;
import com.senai.findcar.repository.VeiculoRepository;

@Configuration
public class FindcarConfiguration {

    @Bean
    CommandLineRunner inserirDadosIniciais(VeiculoRepository veiculoRepository) {
		return args -> {
			Veiculo v1 = new Veiculo();
            v1.setPlaca("ABC-1234");
            v1.setMarca("Fiat");
            v1.setModelo("Uno");
            v1.setCor("Prata");
            v1.setAno("2012");
            v1.setValor(new BigDecimal("25000"));
            v1.setFornecedor("Barigui");
            v1.setObservacao("Usado em boas condicoes");
            
            Veiculo v2 = new Veiculo();
            v2.setPlaca("DBS-4567");
            v2.setMarca("Volkswagen");
            v2.setModelo("Gol quadrado stage 3");
            v2.setCor("Preto");
            v2.setAno("1995");
            v2.setValor(new BigDecimal("99999999"));
            v2.setObservacao("Modificado com mais de 500 mil investidos e 1500 cavalos");
            v2.setFornecedor("Rogerio veiculos");
            
            List<Veiculo> veiculos = new ArrayList<>();
            veiculos.add(v1);
            veiculos.add(v2);
            
            String[] marcas = {"Fiat", "Volkswagen", "Chevrolet", "Ford", "Honda", "Toyota", "Nissan", "Renault", "Peugeot", "Hyundai"};
            String[] modelos = {"Uno", "Gol", "Civic", "Corolla", "Ka", "Palio", "HB20", "Clio", "208", "Onix"};
            String[] cores = {"Prata", "Preto", "Branco", "Vermelho", "Azul", "Cinza", "Verde", "Amarelo"};
            String[] fornecedores = {"Barigui", "Rogerio Veiculos", "AutoMais", "Car House", "Veiculos Premium", "Top Motors"};

            Random random = new Random();

            for (int i = 0; i < 100; i++) {
                Veiculo v = new Veiculo();
                v.setPlaca(String.format("XYZ-%04d", i + 1000));
                v.setMarca(marcas[random.nextInt(marcas.length)]);
                v.setModelo(modelos[random.nextInt(modelos.length)]);
                v.setCor(cores[random.nextInt(cores.length)]);
                v.setAno(String.valueOf(2000 + random.nextInt(25))); // 2000–2024
                v.setValor(BigDecimal.valueOf(15000 + random.nextInt(100000)));
                v.setFornecedor(fornecedores[random.nextInt(fornecedores.length)]);
                v.setObservacao("Veiculo em boas condicoes, gerado automaticamente.");
                veiculos.add(v);
            }

            veiculoRepository.saveAll(veiculos);
		};
	}
}
