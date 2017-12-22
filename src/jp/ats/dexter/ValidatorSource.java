package jp.ats.dexter;

import java.lang.annotation.Annotation;

/**
 * @author 千葉 哲嗣
 */
public interface ValidatorSource {

	Annotation getAnnotation();

	Class<? extends Validator<?>> getValidatorClass();
}
