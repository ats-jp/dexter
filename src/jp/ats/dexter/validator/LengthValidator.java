package jp.ats.dexter.validator;

import java.text.MessageFormat;

import jp.ats.dexter.Validator;
import jp.ats.dexter.annotation.Length;

/**
 * @author 千葉 哲嗣
 */
public class LengthValidator implements Validator<Length> {

	private String name;

	private String description;

	private String message;

	private int length;

	private String value;

	@Override
	public void initialize(Length validation) {
		length = validation.length();
		name = validation.name();
		description = validation.description();
		message = validation.message();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return format(description, length);
	}

	@Override
	public String getValidationMessage() {
		return format(message, length);
	}

	@Override
	public boolean validate() {
		return (value == null ? "" : value).length() == length;
	}

	@Override
	public void reset() {
		length = 0;
		value = null;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	private static String format(String template, int length) {
		return new MessageFormat(template).format(new Object[] { length });
	}
}
