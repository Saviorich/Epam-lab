package by.epam.lab.utils;

import java.util.Arrays;
import static by.epam.lab.libs.StringUtils.isPositiveNumber;

public class Utils {
	
	public static boolean isAllPositiveNumbers(String... str) {
		return Arrays.stream(str).allMatch(s -> isPositiveNumber(s));
	}
}
