package Novamonitor.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "ch_tutor")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tutor")
    private Long id;

    @NotBlank(message = "O nome do tutor é obrigatório")
    @Column(name = "nm_tutor")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Column(name = "ds_email")
    private String email;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(
            regexp = "\\d{10,11}",
            message = "O telefone deve conter 10 ou 11 números"
    )
    @Column(name = "nr_telefone")
    private String telefone;

    @NotBlank(message = "A senha é obrigatória")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "ds_senha")
    private String senha;

    @Column(name = "ds_perfil")
    private String perfil;

    public Tutor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}