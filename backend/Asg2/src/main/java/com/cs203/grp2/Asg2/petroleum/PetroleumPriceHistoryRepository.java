package com.cs203.grp2.Asg2.petroleum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetroleumPriceHistoryRepository extends JpaRepository<PetroleumPriceHistory, Long> {
    @Query("""
    SELECT p FROM PetroleumPriceHistory p
    WHERE p.petroleum.hsCode = :hsCode
    AND p.effectiveDate <= :targetDate
    ORDER BY p.effectiveDate DESC
    """)
    Optional<PetroleumPriceHistory> findLatestPriceBeforeDate(String hsCode, LocalDate targetDate);

    @Query("""
    SELECT p FROM PetroleumPriceHistory p
    WHERE p.petroleum.hsCode = :hsCode
    ORDER BY p.effectiveDate DESC
    """)
    Optional<PetroleumPriceHistory> findMostRecentPrice(String hsCode);
}
