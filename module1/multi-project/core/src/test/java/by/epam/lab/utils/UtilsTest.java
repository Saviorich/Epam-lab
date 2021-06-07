package by.epam.lab.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UtilsTest {
	@ParameterizedTest
    @CsvSource({"5,25,2042,536523,9999999", "0,1,2,3,4"})
    void testIsAllPositiveNumbers_ShouldReturnTrue(String arg1, String arg2, String arg3, String arg4, String arg5) {
        assertTrue(Utils.isAllPositiveNumbers(arg1, arg2, arg3, arg4, arg5));
    }
	
	@ParameterizedTest
    @CsvSource({"args,full,of,random,words", "0,1,2,-3,4"})
    void testIsAllPositiveNumbers_ShouldReturnFalse(String arg1, String arg2, String arg3, String arg4, String arg5) {
        assertFalse(Utils.isAllPositiveNumbers(arg1, arg2, arg3, arg4, arg5));
    }
}