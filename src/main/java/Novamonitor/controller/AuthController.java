package Novamonitor.controller;

import Novamonitor.entity.Tutor;
import Novamonitor.repository.TutorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody Tutor dados) {

        if (tutorRepository.findByEmail(dados.getEmail()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", "Já existe uma conta com este e-mail."));
        }

        Tutor tutor = new Tutor();
        tutor.setNome(dados.getNome());
        tutor.setEmail(dados.getEmail());
        tutor.setTelefone(dados.getTelefone());
        tutor.setSenha(passwordEncoder.encode(dados.getSenha()));
        tutor.setPerfil("TUTOR");

        tutorRepository.save(tutor);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("email", tutor.getEmail(), "perfil", "TUTOR"));
    }

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {

        String perfil = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority.getAuthority().replace("ROLE_", ""))
                .orElse("");

        Map<String, String> usuario = new LinkedHashMap<>();
        usuario.put("email", authentication.getName());
        usuario.put("perfil", perfil);

        return usuario;
    }
}