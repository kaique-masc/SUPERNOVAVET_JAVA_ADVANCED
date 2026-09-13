package Novamonitor.repository;

import Novamonitor.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByNivelRisco(String nivelRisco);

    List<Pet> findByEspecie(String especie);

    List<Pet> findByTutorEmail(String email);
}