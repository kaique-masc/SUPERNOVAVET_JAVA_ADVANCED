package Novamonitor.controller;

import Novamonitor.entity.Tutor;
import Novamonitor.repository.PetRepository;
import Novamonitor.repository.TutorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/tutores")
public class TutorController {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Tutor> listar() {
        return tutorRepository.findAll();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public Tutor buscar(@PathVariable Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado"));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    @ResponseStatus(HttpStatus.CREATED)
    public Tutor cadastrar(@Valid @RequestBody Tutor tutor) {

        if (tutorRepository.findByEmail(tutor.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Já existe um tutor com este e-mail."
            );
        }

        tutor.setPerfil("TUTOR");
        tutor.setSenha(passwordEncoder.encode(tutor.getSenha()));

        return tutorRepository.save(tutor);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','VETERINARIO')")
    public Tutor atualizar(
            @PathVariable Long id,
            @RequestBody Tutor dados
    ) {

        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado"));

        if (dados.getNome() == null || dados.getNome().isBlank()
                || dados.getEmail() == null || dados.getEmail().isBlank()
                || dados.getTelefone() == null || !dados.getTelefone().matches("\\d{10,11}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe nome, e-mail e telefone com 10 ou 11 números."
            );
        }

        tutor.setNome(dados.getNome());
        tutor.setEmail(dados.getEmail());
        tutor.setTelefone(dados.getTelefone());

        if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
            tutor.setSenha(passwordEncoder.encode(dados.getSenha()));
        }

        tutor.setPerfil("TUTOR");

        return tutorRepository.save(tutor);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {

        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor não encontrado"));

        if (petRepository.existsByTutorId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este tutor possui pets vinculados."
            );
        }

        try {
            tutorRepository.delete(tutor);
            tutorRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este tutor possui registros vinculados."
            );
        }
    }
}