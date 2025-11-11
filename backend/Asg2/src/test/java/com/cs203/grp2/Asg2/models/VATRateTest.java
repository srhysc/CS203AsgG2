package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VATRateTest {

    private VATRate vatRate;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2024, 1, 1);
        vatRate = new VATRate(testDate, 7.0);
    }

    @Test
    void testDefaultConstructor() {
        VATRate rate = new VATRate();
        assertNotNull(rate);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(vatRate);
        assertEquals(testDate, vatRate.getDate());
        assertEquals(7.0, vatRate.getVATRate());
    }

    @Test
    void testGetDate() {
        assertEquals(LocalDate.of(2024, 1, 1), vatRate.getDate());
    }

    @Test
    void testGetVATRate() {
        assertEquals(7.0, vatRate.getVATRate());
    }

    @Test
    void testZeroRate() {
        VATRate zeroRate = new VATRate(testDate, 0.0);
        assertEquals(0.0, zeroRate.getVATRate());
    }

    @Test
    void testHighRate() {
        VATRate highRate = new VATRate(testDate, 25.0);
        assertEquals(25.0, highRate.getVATRate());
    }

    @Test
    void testDecimalRate() {
        VATRate decimalRate = new VATRate(testDate, 8.25);
        assertEquals(8.25, decimalRate.getVATRate());
    }

    @Test
    void testDifferentDates() {
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 6, 1);
        
        VATRate rate1 = new VATRate(date1, 7.0);
        VATRate rate2 = new VATRate(date2, 8.0);
        
        assertEquals(date1, rate1.getDate());
        assertEquals(date2, rate2.getDate());
    }

    @Test
    void testWithNullDate() {
        VATRate rate = new VATRate(null, 7.0);
        assertNull(rate.getDate());
    }

    @Test
    void testNegativeRate() {
        VATRate negativeRate = new VATRate(testDate, -5.0);
        assertEquals(-5.0, negativeRate.getVATRate());
    }
}
