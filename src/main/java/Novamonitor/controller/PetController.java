package Novamonitor.controller;

import Novamonitor.entity.Pet;
import Novamonitor.entity.Tutor;
import Novamonitor.repository.PetRepository;
import Novamonitor.repository.TutorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private TutorRepository tutorRepository;



    @GetMapping
    public List<Pet> listar() {
        return petRepository.findAll();
    }



    @GetMapping("/meus")
    public List<Pet> listarMeusPets(Authentication authentication) {

        String emailUsuarioLogado = authentication.getName();

        return petRepository.findByTutorEmail(emailUsuarioLogado);
    }



    @GetMapping("/risco/{nivelRisco}")
    public List<Pet> buscarPorRisco(@PathVariable String nivelRisco) {

        return petRepository.findByNivelRisco(
                nivelRisco.toUpperCase()
        );
    }


    @GetMapping("/especie/{especie}")
    public List<Pet> buscarPorEspecie(@PathVariable String especie) {

        return petRepository.findByEspecie(especie);
    }


    @GetMapping("/criticos")
    public List<Pet> listarPetsCriticos() {

        return petRepository.findByNivelRisco("ALTO");
    }



    @GetMapping("/resumo-risco")
    public Map<String, Integer> resumoPorRisco() {

        Map<String, Integer> resumo =
                new LinkedHashMap<>();

        resumo.put(
                "BAIXO",
                petRepository.findByNivelRisco("BAIXO").size()
        );

        resumo.put(
                "MEDIO",
                petRepository.findByNivelRisco("MEDIO").size()
        );

        resumo.put(
                "ALTO",
                petRepository.findByNivelRisco("ALTO").size()
        );

        resumo.put(
                "TOTAL",
                petRepository.findAll().size()
        );

        return resumo;
    }



    @PostMapping
    public Pet cadastrar(
            @Valid @RequestBody Pet pet) {

        Tutor tutorRecebido = pet.getTutor();

        Optional<Tutor> tutorExistente =
                tutorRepository.findByEmail(
                        tutorRecebido.getEmail()
                );

        Tutor tutorFinal;

        if (tutorExistente.isPresent()) {

            tutorFinal =
                    tutorExistente.get();

        } else {

            tutorFinal =
                    tutorRepository.save(
                            tutorRecebido
                    );
        }

        pet.setTutor(tutorFinal);

        return petRepository.save(pet);
    }



    @PutMapping("/{id}")
    public Pet atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Pet petAtualizado) {

        Pet pet =
                petRepository
                        .findById(id)
                        .orElseThrow();

        pet.setNome(
                petAtualizado.getNome()
        );

        pet.setEspecie(
                petAtualizado.getEspecie()
        );

        pet.setIdade(
                petAtualizado.getIdade()
        );

        pet.setNivelRisco(
                petAtualizado.getNivelRisco()
        );

        return petRepository.save(pet);
    }


    @DeleteMapping("/{id}")
    public String deletar(
            @PathVariable Long id) {

        petRepository.deleteById(id);

        return "Pet deletado com sucesso!";
    }
}