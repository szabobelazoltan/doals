package hu.szbz.hbc.doals.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissionTest {
    @Test
    void test_matches() {
        final int input = 3;
        assertTrue(Permission.READ.matches(input));
        assertFalse(Permission.DELETE.matches(input));
    }

    @Test
    void test_mapToCombinationString() {
        final String result = Permission.mapToCombinationString(3);
        assertEquals("rw", result);
    }

    @Test
    void test_mapToCode() {
        final int result = Permission.mapToCode(Permission.READ, Permission.WRITE, Permission.DELETE);
        assertEquals(7, result);
    }
}
