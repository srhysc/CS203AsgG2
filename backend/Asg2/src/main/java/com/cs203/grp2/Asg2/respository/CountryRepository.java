package com.cs203.grp2.Asg2.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203.grp2.Asg2.models.Country;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    Optional<Country> findByiso3n(int iso3n);
    Optional<Country> findByNameIgnoreCase(String name);  // find by name non case sensitive

}
