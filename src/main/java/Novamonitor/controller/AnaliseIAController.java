
package Novamonitor.controller;

import Novamonitor.entity.Pet;
import Novamonitor.repository.PetRepository;
import Novamonitor.service.AnaliseIAService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/ia")
public class AnaliseIAController {

    private final PetRepository petRepository;
    private final AnaliseIAService analiseIAService;

    public AnaliseIAController(
            PetRepository petRepository,
            AnaliseIAService analiseIAService) {

        this.petRepository = petRepository;
        this.analiseIAService = analiseIAService;
    }

    @GetMapping("/analisar/{id}")
    public Map<String, String> analisar(
            @PathVariable Long id,
            Authentication authentication) {

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pet não encontrado"
                ));

        boolean tutorDoPet = pet.getTutor() != null
                && pet.getTutor().getEmail()
                .equalsIgnoreCase(authentication.getName());

        boolean profissional = authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN")
                                || a.getAuthority().equals("ROLE_VETERINARIO")
                );

        if (!tutorDoPet && !profissional) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem acesso a este pet"
            );
        }

        String resultado = analiseIAService.analisarPet(pet);

        return Map.of(
                "pet", pet.getNome(),
                "analise", resultado
        );
    }
}
