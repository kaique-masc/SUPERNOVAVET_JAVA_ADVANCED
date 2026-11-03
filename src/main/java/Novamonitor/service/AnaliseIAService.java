
package Novamonitor.service;

import Novamonitor.entity.Pet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class AnaliseIAService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String modelo;

    private final RestClient restClient = RestClient.create();

    public String analisarPet(Pet pet) {

        String prompt = """
                Você é o assistente inteligente do SuperNovaVet.

                Analise os dados do pet e forneça orientações
                preventivas e personalizadas.

                Não realize diagnósticos veterinários.
                Não invente informações clínicas.
                Se necessário, recomende avaliação veterinária.

                Dados do pet:
                Nome: %s
                Espécie: %s
                Idade: %d anos
                Nível de risco: %s

                Responda em português com esta estrutura:

                PRIORIDADE:
                BAIXA, MÉDIA ou ALTA.

                MOTIVOS:
                Explique os fatores considerados.

                RECOMENDAÇÕES AO TUTOR:
                Forneça orientações preventivas.

                RECOMENDAÇÕES PARA A CLÍNICA:
                Informe possíveis necessidades de acompanhamento.

                RESUMO:
                Apresente um resumo simples.
                """.formatted(
                pet.getNome(),
                pet.getEspecie(),
                pet.getIdade(),
                pet.getNivelRisco()
        );

        Map<String, Object> corpo = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        try {
            Map resposta = restClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/"
                            + modelo + ":generateContent")
                    .header("x-goog-api-key", apiKey)
                    .body(corpo)
                    .retrieve()
                    .body(Map.class);

            if (resposta == null) {
                return "A IA não retornou uma resposta.";
            }

            List candidatos = (List) resposta.get("candidates");

            if (candidatos == null || candidatos.isEmpty()) {
                return "A IA não gerou uma análise.";
            }

            Map candidato = (Map) candidatos.get(0);
            Map conteudo = (Map) candidato.get("content");
            List partes = (List) conteudo.get("parts");
            Map primeiraParte = (Map) partes.get(0);

            return (String) primeiraParte.get("text");

        } catch (Exception e) {
            return "Não foi possível realizar a análise com IA. "
                    + "Verifique a conexão e a configuração do Gemini.";
        }
    }
}
