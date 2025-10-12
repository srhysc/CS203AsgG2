package com.cs203.grp2.Asg2.petroleum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetroleumRepository extends JpaRepository<Petroleum, String> {
    
}
