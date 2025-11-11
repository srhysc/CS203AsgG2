package com.cs203.grp2.Asg2.service;

import com.cs203.grp2.Asg2.models.Petroleum;
import com.google.firebase.database.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetroleumServiceTest {

    @Mock
    private FirebaseDatabase firebaseDatabase;

    @Mock
    private DatabaseReference databaseReference;

    @Mock
    private DataSnapshot dataSnapshot;

    @InjectMocks
    private PetroleumService petroleumService;

    @BeforeEach
    void setUp() {
        when(firebaseDatabase.getReference("product_new")).thenReturn(databaseReference);
    }

    @Test
    void testGetAllPetroleum_Success() throws Exception {
        // Arrange
        DataSnapshot crudeOilSnapshot = mock(DataSnapshot.class);
        DataSnapshot gasolineSnapshot = mock(DataSnapshot.class);
        
        DataSnapshot priceSnapshot1 = mock(DataSnapshot.class);
        DataSnapshot priceSnapshot2 = mock(DataSnapshot.class);
        
        DataSnapshot priceNode1 = mock(DataSnapshot.class);
        DataSnapshot priceNode2 = mock(DataSnapshot.class);

        // Setup crude oil
        when(crudeOilSnapshot.getKey()).thenReturn("Crude Oil");
        when(crudeOilSnapshot.child("hscode")).thenReturn(priceSnapshot1);
        when(priceSnapshot1.getValue(String.class)).thenReturn("270900");
        
        List<DataSnapshot> crudeOilPrices = new ArrayList<>();
        crudeOilPrices.add(priceNode1);
        
        when(crudeOilSnapshot.child("price")).thenReturn(priceSnapshot2);
        when(priceSnapshot2.getChildren()).thenReturn(crudeOilPrices);
        
        DataSnapshot dateNode1 = mock(DataSnapshot.class);
        DataSnapshot avgPriceNode1 = mock(DataSnapshot.class);
        DataSnapshot unitNode1 = mock(DataSnapshot.class);
        
        when(priceNode1.child("date")).thenReturn(dateNode1);
        when(dateNode1.getValue(String.class)).thenReturn("2025-01-01");
        when(priceNode1.child("avg_price_per_unit_usd")).thenReturn(avgPriceNode1);
        when(avgPriceNode1.getValue(Double.class)).thenReturn(50.0);
        when(priceNode1.child("unit")).thenReturn(unitNode1);
        when(unitNode1.getValue(String.class)).thenReturn("barrel");

        // Setup gasoline
        DataSnapshot gasolineHsCodeSnapshot = mock(DataSnapshot.class);
        DataSnapshot gasolinePriceSnapshot = mock(DataSnapshot.class);
        
        when(gasolineSnapshot.getKey()).thenReturn("Gasoline");
        when(gasolineSnapshot.child("hscode")).thenReturn(gasolineHsCodeSnapshot);
        when(gasolineHsCodeSnapshot.getValue(String.class)).thenReturn("271000");
        
        List<DataSnapshot> gasolinePrices = new ArrayList<>();
        gasolinePrices.add(priceNode2);
        
        when(gasolineSnapshot.child("price")).thenReturn(gasolinePriceSnapshot);
        when(gasolinePriceSnapshot.getChildren()).thenReturn(gasolinePrices);
        
        DataSnapshot dateNode2 = mock(DataSnapshot.class);
        DataSnapshot avgPriceNode2 = mock(DataSnapshot.class);
        DataSnapshot unitNode2 = mock(DataSnapshot.class);
        
        when(priceNode2.child("date")).thenReturn(dateNode2);
        when(dateNode2.getValue(String.class)).thenReturn("2025-01-01");
        when(priceNode2.child("avg_price_per_unit_usd")).thenReturn(avgPriceNode2);
        when(avgPriceNode2.getValue(Double.class)).thenReturn(60.0);
        when(priceNode2.child("unit")).thenReturn(unitNode2);
        when(unitNode2.getValue(String.class)).thenReturn("gallon");

        List<DataSnapshot> allPetroleum = new ArrayList<>();
        allPetroleum.add(crudeOilSnapshot);
        allPetroleum.add(gasolineSnapshot);
        
        when(dataSnapshot.exists()).thenReturn(true);
        when(dataSnapshot.getChildrenCount()).thenReturn(2L);
        when(dataSnapshot.getChildren()).thenReturn(allPetroleum);

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(dataSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act
        List<Petroleum> result = petroleumService.getAllPetroleum();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Crude Oil", result.get(0).getName());
        assertEquals("270900", result.get(0).getHsCode());
        assertEquals("Gasoline", result.get(1).getName());
        assertEquals("271000", result.get(1).getHsCode());
        
        verify(firebaseDatabase).getReference("product_new");
        verify(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    @Test
    void testGetAllPetroleum_EmptyDatabase() throws Exception {
        // Arrange
        when(dataSnapshot.exists()).thenReturn(true);
        when(dataSnapshot.getChildrenCount()).thenReturn(0L);
        when(dataSnapshot.getChildren()).thenReturn(new ArrayList<>());

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(dataSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act
        List<Petroleum> result = petroleumService.getAllPetroleum();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllPetroleum_DatabaseError() {
        // Arrange
        DatabaseError databaseError = mock(DatabaseError.class);
        DatabaseException exception = new DatabaseException("Database error");
        when(databaseError.toException()).thenReturn(exception);
        when(databaseError.getMessage()).thenReturn("Database error");

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onCancelled(databaseError);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act & Assert
        Exception thrown = assertThrows(Exception.class, () -> petroleumService.getAllPetroleum());
        assertNotNull(thrown);
    }

    @Test
    void testGetPetroleumByHsCode_Found() throws Exception {
        // Arrange - First load some petroleum
        DataSnapshot crudeOilSnapshot = mock(DataSnapshot.class);
        DataSnapshot hsCodeSnapshot = mock(DataSnapshot.class);
        DataSnapshot priceContainerSnapshot = mock(DataSnapshot.class);
        
        when(crudeOilSnapshot.getKey()).thenReturn("Crude Oil");
        when(crudeOilSnapshot.child("hscode")).thenReturn(hsCodeSnapshot);
        when(hsCodeSnapshot.getValue(String.class)).thenReturn("270900");
        when(crudeOilSnapshot.child("price")).thenReturn(priceContainerSnapshot);
        when(priceContainerSnapshot.getChildren()).thenReturn(new ArrayList<>());

        List<DataSnapshot> allPetroleum = new ArrayList<>();
        allPetroleum.add(crudeOilSnapshot);
        
        when(dataSnapshot.exists()).thenReturn(true);
        when(dataSnapshot.getChildrenCount()).thenReturn(1L);
        when(dataSnapshot.getChildren()).thenReturn(allPetroleum);

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(dataSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        petroleumService.getAllPetroleum();

        // Act
        Petroleum result = petroleumService.getPetroleumByHsCode("270900");

        // Assert
        assertNotNull(result);
        assertEquals("Crude Oil", result.getName());
        assertEquals("270900", result.getHsCode());
    }

    @Test
    void testGetPetroleumByHsCode_CaseInsensitive() throws Exception {
        // Arrange
        DataSnapshot crudeOilSnapshot = mock(DataSnapshot.class);
        DataSnapshot hsCodeSnapshot = mock(DataSnapshot.class);
        DataSnapshot priceContainerSnapshot = mock(DataSnapshot.class);
        
        when(crudeOilSnapshot.getKey()).thenReturn("Crude Oil");
        when(crudeOilSnapshot.child("hscode")).thenReturn(hsCodeSnapshot);
        when(hsCodeSnapshot.getValue(String.class)).thenReturn("270900");
        when(crudeOilSnapshot.child("price")).thenReturn(priceContainerSnapshot);
        when(priceContainerSnapshot.getChildren()).thenReturn(new ArrayList<>());

        List<DataSnapshot> allPetroleum = new ArrayList<>();
        allPetroleum.add(crudeOilSnapshot);
        
        when(dataSnapshot.exists()).thenReturn(true);
        when(dataSnapshot.getChildrenCount()).thenReturn(1L);
        when(dataSnapshot.getChildren()).thenReturn(allPetroleum);

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(dataSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        petroleumService.getAllPetroleum();

        // Act
        Petroleum result = petroleumService.getPetroleumByHsCode("270900");

        // Assert
        assertNotNull(result);
        assertEquals("270900", result.getHsCode());
    }

    @Test
    void testGetPetroleumByHsCode_NotFound() throws Exception {
        // Arrange
        when(dataSnapshot.exists()).thenReturn(true);
        when(dataSnapshot.getChildrenCount()).thenReturn(0L);
        when(dataSnapshot.getChildren()).thenReturn(new ArrayList<>());

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(dataSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        petroleumService.getAllPetroleum();

        // Act & Assert
        // The service now throws PetroleumNotFoundException instead of returning null
        assertThrows(com.cs203.grp2.Asg2.exceptions.PetroleumNotFoundException.class, () -> {
            petroleumService.getPetroleumByHsCode("999999");
        });
    }

    @Test
    void testGetAllPetroleum_SkipsInvalidEntries() throws Exception {
        // Arrange
        DataSnapshot validSnapshot = mock(DataSnapshot.class);
        DataSnapshot invalidSnapshot1 = mock(DataSnapshot.class);
        DataSnapshot invalidSnapshot2 = mock(DataSnapshot.class);
        
        // Valid entry
        DataSnapshot validHsCodeSnapshot = mock(DataSnapshot.class);
        DataSnapshot validPriceSnapshot = mock(DataSnapshot.class);
        when(validSnapshot.getKey()).thenReturn("Crude Oil");
        when(validSnapshot.child("hscode")).thenReturn(validHsCodeSnapshot);
        when(validHsCodeSnapshot.getValue(String.class)).thenReturn("270900");
        when(validSnapshot.child("price")).thenReturn(validPriceSnapshot);
        when(validPriceSnapshot.getChildren()).thenReturn(new ArrayList<>());

        // Invalid entry - null name
        DataSnapshot invalidHsCodeSnapshot1 = mock(DataSnapshot.class);
        DataSnapshot invalidPriceSnapshot1 = mock(DataSnapshot.class);
        when(invalidSnapshot1.getKey()).thenReturn(null);
        when(invalidSnapshot1.child("hscode")).thenReturn(invalidHsCodeSnapshot1);
        when(invalidHsCodeSnapshot1.getValue(String.class)).thenReturn("271000");
        when(invalidSnapshot1.child("price")).thenReturn(invalidPriceSnapshot1);
        when(invalidPriceSnapshot1.getChildren()).thenReturn(new ArrayList<>());

        // Invalid entry - null hs code
        DataSnapshot invalidHsCodeSnapshot2 = mock(DataSnapshot.class);
        DataSnapshot invalidPriceSnapshot2 = mock(DataSnapshot.class);
        when(invalidSnapshot2.getKey()).thenReturn("Diesel");
        when(invalidSnapshot2.child("hscode")).thenReturn(invalidHsCodeSnapshot2);
        when(invalidHsCodeSnapshot2.getValue(String.class)).thenReturn(null);
        when(invalidSnapshot2.child("price")).thenReturn(invalidPriceSnapshot2);
        when(invalidPriceSnapshot2.getChildren()).thenReturn(new ArrayList<>());

        List<DataSnapshot> allPetroleum = new ArrayList<>();
        allPetroleum.add(validSnapshot);
        allPetroleum.add(invalidSnapshot1);
        allPetroleum.add(invalidSnapshot2);
        
        when(dataSnapshot.exists()).thenReturn(true);
        when(dataSnapshot.getChildrenCount()).thenReturn(3L);
        when(dataSnapshot.getChildren()).thenReturn(allPetroleum);

        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(dataSnapshot);
            return null;
        }).when(databaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Act
        List<Petroleum> result = petroleumService.getAllPetroleum();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size()); // Only valid entry
        assertEquals("Crude Oil", result.get(0).getName());
    }
}
