package jp.ats.dexter;

import java.lang.annotation.Annotation;

/**
 * @author 千葉 哲嗣
 */
class ConcreteValidatorSource implements ValidatorSource {

	private final Annotation annotation;

	private final Class<? extends Validator<?>> validatorClass;

	ConcreteValidatorSource(
		Annotation annotation,
		Class<? extends Validator<?>> validatorClass) {
		this.annotation = annotation;
		this.validatorClass = validatorClass;
	}

	@Override
	public Annotation getAnnotation() {
		return annotation;
	}

	@Override
	public Class<? extends Validator<?>> getValidatorClass() {
		return validatorClass;
	}
}
