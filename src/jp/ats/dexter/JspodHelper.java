package jp.ats.dexter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ats.substrate.U;
import jp.ats.substrate.util.MapMap;

public class JspodHelper {

	private static final Pattern indexedNamePattern = Pattern
		.compile("^([^\\[\\]]+)\\[([^\\[\\]]+)\\]$");

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter
		.ofPattern("yyyy/MM/dd");

	private static final DateTimeFormatter timeFormatter = DateTimeFormatter
		.ofPattern("HH:mm");

	public static MapMap<String, String, String> convert(
		Map<String, String[]> parameters,
		Set<String> indices) {
		MapMap<String, String, String> result = new MapMap<>();

		for (Entry<String, String[]> entry : parameters.entrySet()) {
			String key = entry.getKey();

			Matcher matcher = indexedNamePattern.matcher(key);
			if (matcher.matches()) {
				String index = matcher.group(2);
				result.get(matcher.group(1)).put(index, entry.getValue()[0]);
				indices.add(index);
			} else {
				String[] values = entry.getValue();
				for (int i = 0; i < values.length; i++) {
					String index = String.valueOf(i);
					result.get(key).put(index, values[i]);
					indices.add(index);
				}
			}
		}

		return result;
	}

	public static String stringOf(Object value) {
		if (value == null) return "";
		return value.toString();
	}

	public static Map<String, String> toString(Map<String, String> value) {
		return value;
	}

	public static Map<String, Integer> toInt(Map<String, String> value) {
		if (value == null) return null;

		Map<String, Integer> map = new HashMap<>();
		for (Entry<String, String> entry : value.entrySet()) {
			map.put(entry.getKey(), toInt(entry.getValue()));
		}

		return map;
	}

	public static Map<String, Long> toLong(Map<String, String> value) {
		if (value == null) return null;

		Map<String, Long> map = new HashMap<>();
		for (Entry<String, String> entry : value.entrySet()) {
			map.put(entry.getKey(), toLong(entry.getValue()));
		}

		return map;
	}

	public static Map<String, Boolean> toBoolean(Map<String, String> value) {
		if (value == null) return null;

		Map<String, Boolean> map = new HashMap<>();
		for (Entry<String, String> entry : value.entrySet()) {
			map.put(entry.getKey(), toBoolean(entry.getValue()));
		}

		return map;
	}

	public static Map<String, BigDecimal> toDecimal(Map<String, String> value) {
		if (value == null) return null;

		Map<String, BigDecimal> map = new HashMap<>();
		for (Entry<String, String> entry : value.entrySet()) {
			map.put(entry.getKey(), toDecimal(entry.getValue()));
		}

		return map;
	}

	public static Map<String, LocalDate> toDate(Map<String, String> value) {
		if (value == null) return null;

		Map<String, LocalDate> map = new HashMap<>();
		for (Entry<String, String> entry : value.entrySet()) {
			map.put(entry.getKey(), toDate(entry.getValue()));
		}

		return map;
	}

	public static Map<String, LocalTime> toTime(Map<String, String> value) {
		if (value == null) return null;

		Map<String, LocalTime> map = new HashMap<>();
		for (Entry<String, String> entry : value.entrySet()) {
			map.put(entry.getKey(), toTime(entry.getValue()));
		}

		return map;
	}

	private static int toInt(String value) {
		if (!U.isAvailable(value)) return 0;
		return Integer.parseInt(value.replace(",", ""));
	}

	private static long toLong(String value) {
		if (!U.isAvailable(value)) return 0;
		return Long.parseLong(value.replace(",", ""));
	}

	private static boolean toBoolean(String value) {
		return Boolean.parseBoolean(value);
	}

	private static BigDecimal toDecimal(String value) {
		if (!U.isAvailable(value)) return null;
		return new BigDecimal(value.replace(",", ""));
	}

	private static LocalDate toDate(String value) {
		if (!U.isAvailable(value)) return null;
		return LocalDate.parse(value, dateFormatter);
	}

	private static LocalTime toTime(String value) {
		if (!U.isAvailable(value)) return null;
		return LocalTime.parse(value, timeFormatter);
	}
}
