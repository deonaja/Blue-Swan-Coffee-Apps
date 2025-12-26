package com.blueswancoffee.repository;

import com.blueswancoffee.model.Barista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BaristaRepository extends JpaRepository<Barista, UUID> {
}
