package jp.ats.dexter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.ats.dexter.annotation.Required;
import jp.ats.dexter.validator.RequiredValidator;
import jp.ats.substrate.U;
import jp.ats.substrate.util.CollectionMap;

/**
 * @author 千葉 哲嗣
 */
public class Dexter {

	private static final ThreadLocal<Map<Class<? extends Validator<?>>, Validator<?>>> validators = new ThreadLocal<Map<Class<? extends Validator<?>>, Validator<?>>>() {

		@Override
		protected Map<Class<? extends Validator<?>>, Validator<?>> initialValue() {
			return new HashMap<>();
		}
	};

	private static final List<ValidatorResult> emptyValidatorResultList = Collections
		.unmodifiableList(new ArrayList<ValidatorResult>());

	private static final Dexter instance = new Dexter();

	public static Dexter getInstance() {
		return instance;
	}

	private Dexter() {}

	public CollectionMap<FormElement, ValidatorResult> execute(
		Form form,
		Map<String, String[]> valueMap) {
		valueMap = normalizeMap(valueMap);

		CollectionMap<FormElement, ValidatorResult> resultMap = null;

		for (FormElement element : form.getFormElements()) {
			resultMap = collectResults(
				resultMap,
				element,
				valueMap.get(element.getId()));
		}

		return resultMap;
	}

	public ValidatorResult execute(String value, FormElement element) {
		Required required = element.getRequiredAnnotation();
		if (required != null) {
			RequiredValidator validator = new RequiredValidator();
			validator.initialize(required);
			validator.setValue(value);
			if (!validator.validate()) {
				return new ValidatorResult(
					validator.getValidationMessage(),
					validator.getName(),
					validator.getDescription(),
					value);
			}
		}

		if (value.length() == 0) return null;

		for (ValidatorSource validation : element.getValidators()) {
			ValidatorResult result = validate(
				value,
				prepareValidator(validation));

			if (result != null) return result;
		}

		return null;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	static String normalizeId(String id) {
		if (!U.isAvailable(id)) return id;
		return id.replaceFirst("\\[[^\\[\\]]+\\]$", "");
	}

	static Validator<?> prepareValidator(ValidatorSource source) {
		Map<Class<? extends Validator<?>>, Validator<?>> map = validators.get();
		Class<? extends Validator<?>> validatorClass = source
			.getValidatorClass();
		Validator<? extends Annotation> validator = map.get(validatorClass);

		if (validator == null) {
			try {
				validator = validatorClass.newInstance();
			} catch (Exception e) {
				throw new IllegalStateException(
					validatorClass.getName() + " のインスタンス化に失敗しました",
					e);
			}

			map.put(validatorClass, validator);
		}

		validator.reset();

		try {
			validator.getClass()
				.getDeclaredMethod("initialize", Annotation.class)
				.invoke(validator, source.getAnnotation());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return validator;
	}

	private static Map<String, String[]> normalizeMap(
		Map<String, String[]> valueMap) {
		Map<String, String[]> result = new HashMap<>();
		List<String> buffer = new LinkedList<>();
		for (Entry<String, String[]> entry : valueMap.entrySet()) {
			String key = normalizeId(entry.getKey());

			String[] current = result.get(key);
			if (current != null) buffer.addAll(Arrays.asList(current));

			buffer.addAll(Arrays.asList(valueMap.get(key)));

			result.put(key, buffer.toArray(new String[buffer.size()]));

			buffer.clear();
		}

		return result;
	}

	private CollectionMap<FormElement, ValidatorResult> collectResults(
		CollectionMap<FormElement, ValidatorResult> resultMap,
		FormElement element,
		String[] values) {
		List<ValidatorResult> myResults = execute(values, element);

		if (myResults.size() == 0) return resultMap;

		if (resultMap == null) resultMap = CollectionMap.newInstance();
		resultMap.get(element).addAll(myResults);

		return resultMap;
	}

	private List<ValidatorResult> execute(
		String[] values,
		FormElement element) {
		if (values == null || values.length == 0)
			return emptyValidatorResultList;
		List<ValidatorResult> results = new LinkedList<ValidatorResult>();

		for (String value : values) {
			ValidatorResult result = execute(value, element);
			if (result != null) results.add(result);
		}

		return results;
	}

	private ValidatorResult validate(String value, Validator<?> validator) {
		validator.setValue(Utilities.care(value));

		if (validator.validate()) return null;

		ValidatorResult result = new ValidatorResult(
			validator.getValidationMessage(),
			validator.getName(),
			validator.getDescription(),
			value);

		return result;
	}

	public static class ValidatorResult {

		private final String validationMessage;

		private final String name;

		private final String description;

		private final String value;

		private ValidatorResult(
			String validationMessage,
			String name,
			String description,
			String value) {
			this.validationMessage = validationMessage;
			this.name = name;
			this.description = description;
			this.value = value;
		}

		public String getDescription() {
			return description;
		}

		public String getName() {
			return name;
		}

		public String getValidationMessage() {
			return validationMessage;
		}

		public String getValue() {
			return value;
		}
	}
}
