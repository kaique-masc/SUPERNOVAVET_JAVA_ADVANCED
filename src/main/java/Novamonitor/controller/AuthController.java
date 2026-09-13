package Novamonitor.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

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