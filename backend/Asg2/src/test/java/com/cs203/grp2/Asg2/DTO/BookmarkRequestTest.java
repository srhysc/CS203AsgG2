package com.cs203.grp2.Asg2.DTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkRequestTest {

    @Test
    void testDefaultConstructor() {
        // Act
        BookmarkRequest request = new BookmarkRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getSavedResponse());
        assertNull(request.getBookmarkName());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        BookmarkRequest request = new BookmarkRequest();
        LandedCostResponse response = new LandedCostResponse(
            "USA", "China", "Crude Oil", "270900",
            50.0, 5000.0, 10.0, 500.0, 5.0, 250.0, 5750.0, "USD", 0.0, null
        );

        // Act
        request.setSavedResponse(response);
        request.setBookmarkName("My Favorite Route");

        // Assert
        assertNotNull(request.getSavedResponse());
        assertEquals("USA", request.getSavedResponse().getImportingCountry());
        assertEquals("My Favorite Route", request.getBookmarkName());
    }

    @Test
    void testSetBookmarkName_WithNull() {
        // Arrange
        BookmarkRequest request = new BookmarkRequest();

        // Act
        request.setBookmarkName(null);

        // Assert
        assertNull(request.getBookmarkName());
    }

    @Test
    void testSetSavedResponse_WithNull() {
        // Arrange
        BookmarkRequest request = new BookmarkRequest();

        // Act
        request.setSavedResponse(null);

        // Assert
        assertNull(request.getSavedResponse());
    }

    @Test
    void testSetBookmarkName_WithEmptyString() {
        // Arrange
        BookmarkRequest request = new BookmarkRequest();

        // Act
        request.setBookmarkName("");

        // Assert
        assertEquals("", request.getBookmarkName());
    }

    @Test
    void testSetBookmarkName_WithLongName() {
        // Arrange
        BookmarkRequest request = new BookmarkRequest();
        String longName = "This is a very long bookmark name that someone might use to describe their favorite route";

        // Act
        request.setBookmarkName(longName);

        // Assert
        assertEquals(longName, request.getBookmarkName());
    }
}
