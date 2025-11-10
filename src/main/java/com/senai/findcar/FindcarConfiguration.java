package com.senai.findcar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.senai.findcar.models.Fornecedor;
import com.senai.findcar.models.Veiculo;
import com.senai.findcar.repository.FornecedorRepository;
import com.senai.findcar.repository.VeiculoRepository;

@Configuration
public class FindcarConfiguration {

    @Bean
    CommandLineRunner inserirDadosIniciais(VeiculoRepository veiculoRepository, FornecedorRepository fornecedorRepository) {
        return args -> {
            // ======= FORNECEDOR PADRÃO =======
            Fornecedor fornecedorPadrao = new Fornecedor();
            fornecedorPadrao.setNome("Barigui Veículos");
            fornecedorPadrao.setCnpj("12345678000199");
            fornecedorPadrao.setCpf("60247165085");
            fornecedorPadrao.setTelefone("(47) 3333-4444");
            fornecedorPadrao.setEmail("contato@bariguiveiculos.com.br");
            fornecedorPadrao.setEndereco("Av. Beira Rio, 123 - Blumenau/SC");
            fornecedorRepository.save(fornecedorPadrao);

            // ======= VEÍCULOS FIXOS =======
            Veiculo v1 = new Veiculo();
            v1.setPlaca("ABC-1234");
            v1.setMarca("Fiat");
            v1.setModelo("Uno");
            v1.setCor("Prata");
            v1.setAno("2012");
            v1.setValor(new BigDecimal("25000"));
            v1.setFornecedor(fornecedorPadrao);
            v1.setChassi(gerarChassi());
            v1.setObservacao("Usado em boas condicoes");

            Veiculo v2 = new Veiculo();
            v2.setPlaca("DBS-4567");
            v2.setMarca("Volkswagen");
            v2.setModelo("Gol quadrado stage 3");
            v2.setCor("Preto");
            v2.setAno("1995");
            v2.setValor(new BigDecimal("99999999"));
            v2.setFornecedor(fornecedorPadrao);
            v2.setChassi(gerarChassi());
            v2.setObservacao("Modificado com mais de 500 mil investidos e 1500 cavalos");

            List<Veiculo> veiculos = new ArrayList<>();
            veiculos.add(v1);
            veiculos.add(v2);

            // ======= VEÍCULOS GERADOS AUTOMATICAMENTE =======
            String[] marcas = {"Fiat", "Volkswagen", "Chevrolet", "Ford", "Honda", "Toyota", "Nissan", "Renault", "Peugeot", "Hyundai"};
            String[] modelos = {"Uno", "Gol", "Civic", "Corolla", "Ka", "Palio", "HB20", "Clio", "208", "Onix"};
            String[] cores = {"Prata", "Preto", "Branco", "Vermelho", "Azul", "Cinza", "Verde", "Amarelo"};

            Random random = new Random();

            for (int i = 0; i < 100; i++) {
                Veiculo v = new Veiculo();
                v.setPlaca(String.format("XYZ-%04d", i + 1000));
                v.setMarca(marcas[random.nextInt(marcas.length)]);
                v.setModelo(modelos[random.nextInt(modelos.length)]);
                v.setCor(cores[random.nextInt(cores.length)]);
                v.setAno(String.valueOf(2000 + random.nextInt(25))); // 2000–2024
                v.setValor(BigDecimal.valueOf(15000 + random.nextInt(100000)));
                v.setFornecedor(fornecedorPadrao);
                v.setChassi(gerarChassi());
                v.setObservacao("Veículo em boas condições, gerado automaticamente.");
                veiculos.add(v);
            }

            veiculoRepository.saveAll(veiculos);
        };
    }

    private static String gerarChassi() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";//A-Z 0-9
        StringBuilder chassi = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 17; i++) {
            chassi.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }

        return chassi.toString();
    }
}
