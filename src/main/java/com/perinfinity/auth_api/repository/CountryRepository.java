package com.perinfinity.auth_api.repository;

import com.perinfinity.auth_api.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findAllByActiveTrue();

    Optional<Country> findByCode(String code);
}
