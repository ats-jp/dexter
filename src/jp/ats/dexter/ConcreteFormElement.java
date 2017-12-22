package jp.ats.dexter;

import jp.ats.dexter.annotation.Required;
import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
class ConcreteFormElement implements FormElement {

	private final String id;

	private final ValidatorSource[] validators;

	private final Required required;

	public ConcreteFormElement(
		String id,
		ValidatorSource[] validators,
		Required required) {
		this.id = id;
		this.validators = validators.clone();
		this.required = required;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ValidatorSource[] getValidators() {
		return validators.clone();
	}

	@Override
	public Required getRequiredAnnotation() {
		return required;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
