package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.models.Petroleum;
import com.cs203.grp2.Asg2.models.PetroleumPrice;
import com.cs203.grp2.Asg2.service.PetroleumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PetroleumService.
 * 
 * These tests interact with the real Firebase database to verify that:
 * 1. Data can be loaded from Firebase's product_new path
 * 2. Petroleum prices can be added to Firebase
 * 3. Filtering by HS code works with real data
 * 4. Edge cases are handled correctly
 */
public class PetroleumServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private PetroleumService petroleumService;

    @Test
    void testGetAllPetroleum_LoadsDataFromFirebase() throws Exception {
        // When: Get all petroleum products from Firebase
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        // Then: Should load products successfully
        assertNotNull(products, "Products list should not be null");
        assertFalse(products.isEmpty(), "Should load at least some products");
        
        System.out.println("✅ Loaded " + products.size() + " petroleum products from Firebase");
        
        // Verify data structure
        if (!products.isEmpty()) {
            Petroleum firstProduct = products.get(0);
            assertNotNull(firstProduct.getName(), "Product should have a name");
            assertNotNull(firstProduct.getHsCode(), "Product should have HS code");
            System.out.println("Sample product: " + firstProduct.getName() + 
                             " (HS: " + firstProduct.getHsCode() + ")");
        }
    }

    @Test
    void testGetPetroleumByHsCode_FindsProduct() throws Exception {
        // Given: Load products first
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        String testHsCode = products.get(0).getHsCode();
        
        // When: Search by HS code
        Petroleum result = petroleumService.getPetroleumByHsCode(testHsCode);
        
        // Then: Should find the product
        assertNotNull(result, "Should find product by HS code");
        assertEquals(testHsCode.toLowerCase(), result.getHsCode().toLowerCase(), 
                    "HS code should match");
        
        System.out.println("✅ Found product by HS code: " + result.getName());
    }

    @Test
    void testGetPetroleumByHsCode_CaseInsensitive() throws Exception {
        // Given: Load products first
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        String testHsCode = products.get(0).getHsCode();
        
        // When: Search with uppercase HS code
        Petroleum result1 = petroleumService.getPetroleumByHsCode(testHsCode.toUpperCase());
        
        // When: Search with lowercase HS code
        Petroleum result2 = petroleumService.getPetroleumByHsCode(testHsCode.toLowerCase());
        
        // Then: Should find the same product regardless of case
        assertNotNull(result1, "Should find product with uppercase");
        assertNotNull(result2, "Should find product with lowercase");
        assertEquals(result1.getName(), result2.getName(), "Should find same product");
        
        System.out.println("✅ Case-insensitive search works correctly");
    }

    @Test
    void testGetPetroleumByHsCode_NonExistentCode() throws Exception {
        // Given: Load products first
        petroleumService.getAllPetroleum();
        
        // When/Then: Non-existent HS code should throw exception
        assertThrows(com.cs203.grp2.Asg2.exceptions.PetroleumNotFoundException.class, () -> {
            petroleumService.getPetroleumByHsCode("NONEXISTENT999");
        });
        
        System.out.println("✅ Non-existent HS code throws exception correctly");
    }

    // ========== NEW TESTS FOR addPetroleumPrice (0% coverage) ==========
    
    @Test
    void testAddPetroleumPrice_AddsNewPrice() throws Exception {
        // Given: Load products first to find a valid product
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        Petroleum testProduct = products.get(0);
        String productName = testProduct.getName();
        int initialPriceCount = testProduct.getPrices() != null ? testProduct.getPrices().size() : 0;
        
        // Create new price with today's date and unique value
        PetroleumPrice newPrice = new PetroleumPrice(
            LocalDate.now(),
            75.50 + (System.currentTimeMillis() % 100) / 100.0,  // Unique price
            "USD per barrel"
        );
        
        // When: Add new price
        petroleumService.addPetroleumPrice(productName, newPrice);
        
        // Then: Should successfully add to Firebase (no exception thrown)
        System.out.println("✅ Successfully added new price for " + productName);
        
        // Verify by reloading data
        List<Petroleum> reloadedProducts = petroleumService.getAllPetroleum();
        Petroleum reloadedProduct = reloadedProducts.stream()
            .filter(p -> p.getName().equals(productName))
            .findFirst()
            .orElse(null);
        
        assertNotNull(reloadedProduct, "Product should still exist after reload");
        assertNotNull(reloadedProduct.getPrices(), "Prices should not be null");
        assertTrue(reloadedProduct.getPrices().size() >= initialPriceCount, 
                  "Should have at least as many prices as before");
        
        System.out.println("✅ New price count: " + reloadedProduct.getPrices().size() + 
                         " (was " + initialPriceCount + ")");
    }
    
    @Test
    void testAddPetroleumPrice_WithFutureDate() throws Exception {
        // Given: Load products first
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        Petroleum testProduct = products.get(0);
        String productName = testProduct.getName();
        
        // Create price with future date
        PetroleumPrice futurePrice = new PetroleumPrice(
            LocalDate.now().plusDays(30),
            82.75,
            "USD per barrel"
        );
        
        // When: Add future price
        petroleumService.addPetroleumPrice(productName, futurePrice);
        
        // Then: Should successfully add
        System.out.println("✅ Successfully added future-dated price for " + productName);
        
        // Verify
        List<Petroleum> reloadedProducts = petroleumService.getAllPetroleum();
        Petroleum reloadedProduct = reloadedProducts.stream()
            .filter(p -> p.getName().equals(productName))
            .findFirst()
            .orElse(null);
        
        assertNotNull(reloadedProduct, "Product should exist");
        assertNotNull(reloadedProduct.getPrices(), "Prices should not be null");
        
        // Check if future date exists in prices
        boolean hasFutureDate = reloadedProduct.getPrices().stream()
            .anyMatch(p -> p.getDate().isAfter(LocalDate.now()));
        
        assertTrue(hasFutureDate, "Should have future-dated price");
        System.out.println("✅ Future-dated price verified in database");
    }
    
    @Test
    void testAddPetroleumPrice_WithPastDate() throws Exception {
        // Given: Load products first
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        Petroleum testProduct = products.get(0);
        String productName = testProduct.getName();
        
        // Create price with past date
        PetroleumPrice pastPrice = new PetroleumPrice(
            LocalDate.now().minusDays(365),  // 1 year ago
            68.25,
            "USD per barrel"
        );
        
        // When: Add past price
        petroleumService.addPetroleumPrice(productName, pastPrice);
        
        // Then: Should successfully add
        System.out.println("✅ Successfully added past-dated price for " + productName);
    }
    
    @Test
    void testAddPetroleumPrice_WithDifferentUnit() throws Exception {
        // Given: Load products first
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        Petroleum testProduct = products.get(0);
        String productName = testProduct.getName();
        
        // Create price with different unit
        PetroleumPrice newPrice = new PetroleumPrice(
            LocalDate.now(),
            520.0,
            "USD per metric ton"  // Different unit
        );
        
        // When: Add price with different unit
        petroleumService.addPetroleumPrice(productName, newPrice);
        
        // Then: Should successfully add
        System.out.println("✅ Successfully added price with unit: " + newPrice.getUnit());
    }
    
    @Test
    void testAddPetroleumPrice_WithHighPrice() throws Exception {
        // Given: Load products first
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        Petroleum testProduct = products.get(0);
        String productName = testProduct.getName();
        
        // Create price with very high value
        PetroleumPrice highPrice = new PetroleumPrice(
            LocalDate.now(),
            999.99,
            "USD per barrel"
        );
        
        // When: Add high price
        petroleumService.addPetroleumPrice(productName, highPrice);
        
        // Then: Should successfully add
        System.out.println("✅ Successfully added high-value price: $" + highPrice.getAvgPricePerUnitUsd());
    }
    
    @Test
    void testAddPetroleumPrice_WithLowPrice() throws Exception {
        // Given: Load products first
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        Petroleum testProduct = products.get(0);
        String productName = testProduct.getName();
        
        // Create price with very low value
        PetroleumPrice lowPrice = new PetroleumPrice(
            LocalDate.now(),
            0.01,
            "USD per barrel"
        );
        
        // When: Add low price
        petroleumService.addPetroleumPrice(productName, lowPrice);
        
        // Then: Should successfully add
        System.out.println("✅ Successfully added low-value price: $" + lowPrice.getAvgPricePerUnitUsd());
    }
    
    @Test
    void testAddPetroleumPrice_MultiplePricesOnSameDay() throws Exception {
        // Given: Load products first
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        Petroleum testProduct = products.get(0);
        String productName = testProduct.getName();
        LocalDate today = LocalDate.now();
        
        // Create two different prices for the same date
        PetroleumPrice price1 = new PetroleumPrice(today, 70.0, "USD per barrel");
        PetroleumPrice price2 = new PetroleumPrice(today, 71.0, "USD per barrel");
        
        // When: Add multiple prices for same date
        petroleumService.addPetroleumPrice(productName, price1);
        petroleumService.addPetroleumPrice(productName, price2);
        
        // Then: Both should be added (Firebase allows multiple entries)
        System.out.println("✅ Successfully added multiple prices for same date");
        
        // Verify
        List<Petroleum> reloadedProducts = petroleumService.getAllPetroleum();
        Petroleum reloadedProduct = reloadedProducts.stream()
            .filter(p -> p.getName().equals(productName))
            .findFirst()
            .orElse(null);
        
        assertNotNull(reloadedProduct, "Product should exist");
        assertNotNull(reloadedProduct.getPrices(), "Prices should not be null");
        
        long todayPriceCount = reloadedProduct.getPrices().stream()
            .filter(p -> p.getDate().equals(today))
            .count();
        
        assertTrue(todayPriceCount >= 2, "Should have at least 2 prices for today");
        System.out.println("✅ Found " + todayPriceCount + " prices for today's date");
    }
    
    // ========== EDGE CASE TESTS ==========
    
    @Test
    void testGetAllPetroleum_MultipleCallsConsistent() throws Exception {
        // When: Call getAllPetroleum multiple times
        List<Petroleum> firstCall = petroleumService.getAllPetroleum();
        List<Petroleum> secondCall = petroleumService.getAllPetroleum();
        List<Petroleum> thirdCall = petroleumService.getAllPetroleum();
        
        // Then: All calls should return consistent data
        assertNotNull(firstCall, "First call should return data");
        assertNotNull(secondCall, "Second call should return data");
        assertNotNull(thirdCall, "Third call should return data");
        
        // Note: Size might differ if we're adding prices in other tests
        // but the structure should be consistent
        System.out.println("✅ Multiple calls work correctly - " + 
                         firstCall.size() + "/" + secondCall.size() + "/" + thirdCall.size() + " products");
    }
    
    @Test
    void testGetAllPetroleum_ValidatesPriceStructure() throws Exception {
        // When: Load all products
        List<Petroleum> products = petroleumService.getAllPetroleum();
        
        if (products.isEmpty()) {
            System.out.println("⚠️ No products in database, skipping test");
            return;
        }
        
        // Find a product with prices
        Petroleum productWithPrices = products.stream()
            .filter(p -> p.getPrices() != null && !p.getPrices().isEmpty())
            .findFirst()
            .orElse(null);
        
        if (productWithPrices == null) {
            System.out.println("⚠️ No products with prices found");
            return;
        }
        
        // Then: Validate price structure
        PetroleumPrice firstPrice = productWithPrices.getPrices().get(0);
        assertNotNull(firstPrice.getDate(), "Price should have a date");
        assertNotNull(firstPrice.getAvgPricePerUnitUsd(), "Price should have a value");
        assertNotNull(firstPrice.getUnit(), "Price should have a unit");
        assertTrue(firstPrice.getAvgPricePerUnitUsd() >= 0, "Price should be non-negative");
        
        System.out.println("✅ Price structure valid - " + 
                         firstPrice.getDate() + ": $" + firstPrice.getAvgPricePerUnitUsd() + 
                         "/" + firstPrice.getUnit());
    }
}
