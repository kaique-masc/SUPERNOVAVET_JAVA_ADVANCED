package Novamonitor.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Novamonitor.repository.PetProcedureRepository;

/**
 * Endpoints que executam as procedures/functions do Oracle (Database - Sprint 4).
 * Acesso: ADMIN e VETERINARIO (ver SecurityConfig).
 */
@RestController
@RequestMapping("/procedures")
public class PetProcedureController {

    private final PetProcedureRepository repo;

    public PetProcedureController(PetProcedureRepository repo) {
        this.repo = repo;
    }

    // POST /procedures/tutores?nome=Joao&email=joao@x.com&telefone=11900000000
    @PostMapping("/tutores")
    public Map<String, Object> inserirTutor(@RequestParam String nome, @RequestParam String email,
                                            @RequestParam(required = false) String telefone) {
        return Map.of("id_tutor", repo.inserirTutor(nome, email, telefone));
    }

    // POST /procedures/pets?nome=Bolt&especie=Cachorro&raca=Beagle&idade=9&peso=12.3&sexo=M&idTutor=1
    @PostMapping("/pets")
    public Map<String, Object> inserirPet(@RequestParam String nome, @RequestParam String especie,
                                          @RequestParam(required = false) String raca,
                                          @RequestParam(required = false) Integer idade,
                                          @RequestParam(required = false) Double peso,
                                          @RequestParam(required = false) String sexo,
                                          @RequestParam Long idTutor) {
        return Map.of("id_pet", repo.inserirPet(nome, especie, raca, idade, peso, sexo, idTutor));
    }

    // POST /procedures/pets/1/vacinas?vacina=V10&aplicacao=2026-10-01&proximaDose=2027-10-01
    @PostMapping("/pets/{idPet}/vacinas")
    public Map<String, Object> registrarVacina(@PathVariable Long idPet, @RequestParam String vacina,
                                               @RequestParam LocalDate aplicacao,
                                               @RequestParam(required = false) LocalDate proximaDose) {
        return Map.of("id_vacina", repo.registrarVacina(vacina, aplicacao, proximaDose, idPet));
    }

    // PUT /procedures/pets/1/peso?peso=33.0
    @PutMapping("/pets/{idPet}/peso")
    public Map<String, String> atualizarPeso(@PathVariable Long idPet, @RequestParam Double peso) {
        repo.atualizarPeso(idPet, peso);
        return Map.of("mensagem", "Peso atualizado");
    }

    // DELETE /procedures/pets/1
    @DeleteMapping("/pets/{idPet}")
    public Map<String, String> excluirPet(@PathVariable Long idPet) {
        repo.excluirPet(idPet);
        return Map.of("mensagem", "Pet excluido");
    }

    // GET /procedures/pets
    @GetMapping("/pets")
    public List<Map<String, Object>> listarPets() {
        return repo.listarPets();
    }

    // GET /procedures/risco?idade=8  -> function do Oracle
    @GetMapping("/risco")
    public Map<String, String> classificarRisco(@RequestParam Integer idade) {
        return Map.of("nivel_risco", repo.classificarRisco(idade));
    }

    // GET /procedures/dataset-json  -> JSON gerado pela function PL/SQL (o mesmo do MongoDB)
    @GetMapping(value = "/dataset-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public String datasetJson() {
        return repo.exportarDatasetJson();
    }

    /**
     * Converte os erros ORA-200xx levantados pelos packages em HTTP 400 com a
     * mensagem (mostra o tratamento de excecao do banco chegando na API).
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> erroBanco(DataAccessException e) {
        String msg = String.valueOf(e.getMostSpecificCause().getMessage());
        String primeiraLinha = msg.split("\n")[0];   // "ORA-20003: Tutor informado nao existe."
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", primeiraLinha));
    }
}
