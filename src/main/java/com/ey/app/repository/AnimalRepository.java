package com.ey.app.repository;

import com.ey.app.model.entity.Animal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, UUID> {

  long count();
}
