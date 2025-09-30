package com.cs203.grp2.Asg2.petroleum;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface PetroleumRepository extends JpaRepository<Petroleum, String> {
    
}
