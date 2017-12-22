package jp.ats.dexter.validator;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import jp.ats.dexter.AbstractValidator;
import jp.ats.dexter.Validator;
import jp.ats.dexter.annotation.Date;

/**
 * @author 千葉 哲嗣
 */
public class DateValidator extends AbstractValidator
	implements Validator<Date> {

	private SimpleDateFormat format;

	@Override
	public void initialize(Date validation) {
		setName(validation.name());
		setDescription(validation.description());
		setValidationMessage(validation.message());
		setFormat(validation.format());
	}

	@Override
	public boolean validate() {
		String value = getValue();
		ParsePosition position = new ParsePosition(0);
		format.parse(value, position);
		if (position.getErrorIndex() != -1
			|| position.getIndex() != value.length()) return false;
		return true;
	}

	private void setFormat(String format) {
		try {
			this.format = new SimpleDateFormat(format);
			this.format.setLenient(false);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(
				"format " + format + " に誤りがあります",
				e);
		}
	}

	@Override
	public void reset() {
		super.reset();
		format = null;
	}
}
