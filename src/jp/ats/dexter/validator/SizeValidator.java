package jp.ats.dexter.validator;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import jp.ats.dexter.Validator;
import jp.ats.dexter.annotation.Size;

/**
 * @author 千葉 哲嗣
 */
public class SizeValidator implements Validator<Size> {

	private String name;

	private String description;

	private String message;

	private String charset;

	private int size;

	private String value;

	@Override
	public void initialize(Size validation) {
		size = validation.size();
		charset = validation.charset();
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
		return format(description, size);
	}

	@Override
	public String getValidationMessage() {
		return format(message, size);
	}

	@Override
	public boolean validate() {
		String myValue = value == null ? "" : value;
		if (charset == null || charset.length() == 0)
			return myValue.getBytes().length == size;
		try {
			return myValue.getBytes(charset).length == size;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(charset + " は不正な文字セットです", e);
		}
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public void reset() {
		charset = null;
		size = 0;
		value = null;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	private static String format(String template, int size) {
		return new MessageFormat(template).format(new Object[] { size });
	}
}
