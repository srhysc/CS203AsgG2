package com.cs203.grp2.Asg2.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConvertableTest {

    private Convertable convertable;
    private List<ConvertTo> convertToList;

    @BeforeEach
    void setUp() {
        convertToList = new ArrayList<>();
        convertToList.add(new ConvertTo("271000", "Gasoline", 45));
        convertToList.add(new ConvertTo("271200", "Diesel", 30));
        
        convertable = new Convertable("270900", "Crude Oil", convertToList);
    }

    @Test
    void testDefaultConstructor() {
        Convertable c = new Convertable();
        assertNotNull(c);
    }

    @Test
    void testParameterizedConstructor() {
        assertNotNull(convertable);
        assertEquals("270900", convertable.getHscode());
        assertEquals("Crude Oil", convertable.getName());
        assertEquals(convertToList, convertable.getTo());
    }

    @Test
    void testGetHscode() {
        assertEquals("270900", convertable.getHscode());
    }

    @Test
    void testSetHscode() {
        convertable.setHscode("270800");
        assertEquals("270800", convertable.getHscode());
    }

    @Test
    void testGetName() {
        assertEquals("Crude Oil", convertable.getName());
    }

    @Test
    void testSetName() {
        convertable.setName("Heavy Crude");
        assertEquals("Heavy Crude", convertable.getName());
    }

    @Test
    void testGetTo() {
        List<ConvertTo> retrieved = convertable.getTo();
        assertEquals(2, retrieved.size());
        assertEquals("271000", retrieved.get(0).getHscode());
        assertEquals("271200", retrieved.get(1).getHscode());
    }

    @Test
    void testSetTo() {
        List<ConvertTo> newList = new ArrayList<>();
        newList.add(new ConvertTo("271500", "Jet Fuel", 20));
        
        convertable.setTo(newList);
        assertEquals(newList, convertable.getTo());
        assertEquals(1, convertable.getTo().size());
    }

    @Test
    void testWithEmptyConvertToList() {
        Convertable c = new Convertable("270900", "Crude Oil", new ArrayList<>());
        assertNotNull(c.getTo());
        assertEquals(0, c.getTo().size());
    }

    @Test
    void testWithNullHscode() {
        Convertable c = new Convertable(null, "Product", convertToList);
        assertNull(c.getHscode());
    }

    @Test
    void testWithNullName() {
        Convertable c = new Convertable("270900", null, convertToList);
        assertNull(c.getName());
    }

    @Test
    void testWithNullConvertToList() {
        Convertable c = new Convertable("270900", "Crude Oil", null);
        assertNull(c.getTo());
    }

    @Test
    void testModifyConvertToList() {
        List<ConvertTo> retrieved = convertable.getTo();
        retrieved.add(new ConvertTo("271500", "Jet Fuel", 20));
        
        assertEquals(3, convertable.getTo().size());
    }
}
