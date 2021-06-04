package by.epam.lab.libs;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest {
    @ParameterizedTest
	@ValueSource(strings = {"1", "25", "100", "1535", "83585"})
	void testIsPositiveNumber_ShouldReturnTrue(String str) {
		assertTrue(StringUtils.isPositiveNumber(str));
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"-1", "number", "", "string"})
	void testIsPositiveNumber_ShouldReturnFalse(String str) {
		assertFalse(StringUtils.isPositiveNumber(str));
	}
}
