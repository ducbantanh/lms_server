package lms.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RandomTokensTest {

    @Test
    void tokensHaveExpectedLengthAndAreUnique() {
        assertEquals(43, RandomTokens.sessionToken().length());
        assertEquals(22, RandomTokens.publicId().length());
        assertNotEquals(RandomTokens.sessionToken(), RandomTokens.sessionToken());
    }

    @Test
    void sha256IsStableHex() {
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
                RandomTokens.sha256Hex("hello"));
    }

    @Test
    void constantTimeEqualsHandlesNull() {
        assertTrue(RandomTokens.constantTimeEquals("a", "a"));
        assertFalse(RandomTokens.constantTimeEquals("a", "b"));
        assertFalse(RandomTokens.constantTimeEquals(null, "a"));
    }
}
