package jp.ats.dexter.validator;

import jp.ats.dexter.RangeValidator;
import jp.ats.dexter.Validator;
import jp.ats.dexter.annotation.LengthRange;
import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public class LengthRangeValidator extends RangeValidator
	implements Validator<LengthRange> {

	private String name;

	@Override
	public void initialize(LengthRange validation) {
		name = validation.name();
		setMaximum(validation.max());
		setMinimum(validation.min());
	}

	@Override
	public void reset() {
		super.reset();
		name = null;
	}

	@Override
	protected long getValidationTarget() {
		return getValue().length();
	}

	@Override
	protected String getValidationTargetName() {
		return name;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
