package jp.ats.dexter.validator;

import jp.ats.dexter.Validator;
import jp.ats.dexter.annotation.Required;

/**
 * @author 千葉 哲嗣
 */
public class RequiredValidator implements Validator<Required> {

	private String name;

	private String description;

	private String message;

	private String value;

	@Override
	public void initialize(Required validation) {
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
		return description;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean validate() {
		return value.length() > 0;
	}

	@Override
	public String getValidationMessage() {
		return message;
	}

	@Override
	public void reset() {
		name = null;
		description = null;
		message = null;
		value = null;
	}
}
