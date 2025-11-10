# Integration Testing Setup

This document describes the integration testing infrastructure for service layer classes that interact with Firebase.

## Overview

Integration tests verify that service layer components work correctly with the actual Firebase Realtime Database. Unlike unit tests that use mocks, integration tests connect to a real Firebase instance to validate end-to-end functionality.

## Test Structure

### Base Class: `BaseFirebaseIntegrationTest`

All integration tests extend this abstract base class which provides:

- **Full Spring Boot context loading** via `@SpringBootTest`
- **Firebase connection management** with autowired `FirebaseDatabase`
- **Test lifecycle management** with setup/cleanup hooks
- **Utility methods** for Firebase operations:
  - `writeToFirebase(path, data)` - Write data synchronously
  - `readFromFirebase(path, clazz)` - Read data synchronously
  - `deleteFromFirebase(path)` - Delete data synchronously
  - `existsInFirebase(path)` - Check if path exists
  - `generateTestId()` - Generate unique test IDs

### Test Configuration

- **Profile**: `integration-test` (activated via `@ActiveProfiles`)
- **Properties**: `src/test/resources/application-integration-test.properties`
- **Web Environment**: Full servlet context (not mocked)
- **Firebase Credentials**: Uses `serviceAccountKey.json` from test resources

## Existing Integration Tests

### UserServiceIntegrationTest

Tests for user management and bookmark functionality:

| Test | Purpose |
|------|---------|
| `testCreateAndRetrieveUser` | Verify user creation and retrieval |
| `testGetOrCreateUser_ExistingUser` | Test idempotent user creation |
| `testAddBookmark` | Validate bookmark creation |
| `testGetBookmarks` | Retrieve multiple bookmarks |
| `testGetBookmarks_NoBookmarks` | Handle empty bookmark lists |
| `testMultipleBookmarksFromSameUser` | Test bookmark accumulation |
| `testGetUserById_NonExistentUser` | Handle missing users |

**Key Features:**
- Automatic test data cleanup after each test
- Unique test user IDs to avoid conflicts
- Tests bookmark persistence and retrieval
- Validates user role assignment

### CountryServiceIntegrationTest

Tests for country data loading and lookup:

| Test | Purpose |
|------|---------|
| `testGetAll_ShouldReturnCountries` | Verify Firebase data loading |
| `testGetCountryByCode_WithValidCode` | Test numeric code lookup |
| `testGetCountryByName_WithValidName` | Test name-based lookup |
| `testGetCountryByName_CaseInsensitive` | Validate case-insensitive search |
| `testGetCountryByCode_WithInvalidCode` | Handle invalid codes |
| `testGetCountryByName_WithInvalidName` | Handle invalid names |
| `testGetCountryByISO3_WithValidISO3` | Test ISO3 code lookup |
| `testCountryDataIntegrity` | Validate loaded data quality |
| `testServiceInitialization` | Verify eager loading on startup |

**Key Features:**
- Tests eager data loading (CountryService loads on startup)
- Validates in-memory caching functionality
- Tests multiple lookup methods (code, name, ISO3)
- Verifies data integrity

## Running Integration Tests

### Run All Tests

```bash
cd backend/Asg2
mvn test
```

### Run Only Integration Tests

```bash
mvn test -Dtest="*IntegrationTest"
```

### Run Specific Integration Test Class

```bash
mvn test -Dtest=UserServiceIntegrationTest
mvn test -Dtest=CountryServiceIntegrationTest
```

### Run with Spring Profile

```bash
mvn test -Dspring.profiles.active=integration-test
```

## Prerequisites

1. **Firebase Credentials**: Ensure `serviceAccountKey.json` exists in:
   - `src/main/resources/serviceAccountKey.json`
   - `src/test/resources/serviceAccountKey.json` (optional, falls back to main)

2. **Firebase Database**: Tests connect to your actual Firebase Realtime Database
   - Make sure the database URL is configured correctly
   - Consider using a separate test database to avoid affecting production data

3. **Network Connectivity**: Tests require internet access to reach Firebase servers

## Writing New Integration Tests

### Step 1: Create Test Class

```java
package com.cs203.grp2.Asg2.integration;

import com.cs203.grp2.Asg2.service.YourService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class YourServiceIntegrationTest extends BaseFirebaseIntegrationTest {

    @Autowired
    private YourService yourService;

    private String testDataId;

    @Override
    protected void setupTestData() throws Exception {
        // Create test data before each test
        testDataId = generateTestId();
        writeToFirebase("test-path/" + testDataId, yourTestData);
    }

    @Override
    protected void cleanupTestData() throws Exception {
        // Clean up test data after each test
        if (testDataId != null) {
            deleteFromFirebase("test-path/" + testDataId);
        }
    }

    @Test
    void testYourServiceMethod() throws Exception {
        // Arrange
        // ... setup test conditions

        // Act
        // ... call service method

        // Assert
        // ... verify results
    }
}
```

### Step 2: Add Test Data Cleanup

Always clean up test data in `cleanupTestData()` to:
- Avoid polluting the Firebase database
- Prevent test conflicts
- Maintain test isolation

### Step 3: Use Test ID Prefixes

Use `generateTestId()` to create unique identifiers:
- Prevents conflicts between parallel test runs
- Makes test data identifiable in Firebase console
- Includes timestamp and thread ID for uniqueness

## Best Practices

### DO:
✅ Use unique test IDs for all test data  
✅ Clean up test data in `@AfterEach` methods  
✅ Test both success and failure scenarios  
✅ Use descriptive test method names  
✅ Add assertions for expected behavior  
✅ Handle async operations properly  

### DON'T:
❌ Hard-code Firebase paths without test prefixes  
❌ Leave test data in Firebase after tests complete  
❌ Assume test execution order  
❌ Use production data paths  
❌ Skip cleanup on test failures  

## Test Data Management

### Test Data Prefixes

All test data uses the `test_` prefix to:
- Distinguish test data from production data
- Enable easy manual cleanup if needed
- Filter test data in Firebase console

### Cleanup Strategy

Integration tests follow a **two-level cleanup** approach:

1. **Per-Test Cleanup** (`@AfterEach`):
   - Deletes specific test data created during the test
   - Runs after each test method

2. **Manual Cleanup** (if needed):
   ```javascript
   // Firebase Console > Database Rules
   // Query for test_ prefixed data and delete
   ```

## Troubleshooting

### Tests Fail with "Firebase not initialized"

- **Cause**: Missing `serviceAccountKey.json`
- **Solution**: Ensure Firebase credentials file exists in resources folder

### Tests Time Out

- **Cause**: Network issues or slow Firebase response
- **Solution**: 
  - Check internet connectivity
  - Increase timeout in `BaseFirebaseIntegrationTest.TIMEOUT_SECONDS`

### Data Not Found in Tests

- **Cause**: Async data not persisted yet
- **Solution**: Use the provided utility methods which handle synchronization

### Test Data Not Cleaned Up

- **Cause**: Test threw exception before cleanup
- **Solution**: Cleanup runs in `@AfterEach`, but you can manually delete using Firebase console

## Coverage Improvements

Integration tests help improve coverage for:

- **UserService**: 18 branches (bookmark operations, user management)
- **CountryService**: 28 branches (data loading, lookups)
- **RefineryServiceImpl**: 70 branches (refinery operations)
- **WitsService**: 46 branches (WITS data integration)

These services are difficult to test with unit tests due to deep Firebase integration.

## Future Enhancements

Potential improvements to the integration testing setup:

1. **Firebase Emulator**: Use Firebase emulator for true test isolation
2. **Test Containers**: Containerize Firebase emulator for CI/CD
3. **Parallel Execution**: Enable safe parallel test execution
4. **Test Data Fixtures**: Create reusable test data sets
5. **Performance Tests**: Add integration tests for performance validation

## References

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Firebase Admin SDK](https://firebase.google.com/docs/admin/setup)
- [Firebase Emulator Suite](https://firebase.google.com/docs/emulator-suite)
