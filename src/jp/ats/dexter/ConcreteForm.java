package jp.ats.dexter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;

import jp.ats.dexter.annotation.Required;
import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
class ConcreteForm implements Form {

	private final Map<String, FormElement> elementMap = new LinkedHashMap<>();

	private final Class<? extends Jspod> formClass;

	public ConcreteForm(Class<? extends Jspod> formClass) {
		this.formClass = formClass;
		Field[] fields = formClass.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);

			Annotation[] annotations = field.getAnnotations();

			Set<Annotation> annotationSet = new LinkedHashSet<>();
			LinkedList<ValidatorSource> results = new LinkedList<>();

			Required required = null;
			for (Annotation annotation : annotations) {
				Required result = traverseAnnotation(
					annotationSet,
					results,
					annotation);

				if (required == null) required = result;
			}

			String id = field.getName();
			elementMap.put(
				id,
				new ConcreteFormElement(
					id,
					results.toArray(new ValidatorSource[results.size()]),
					required));
		}
	}

	@Override
	public FormElement[] getFormElements() {
		Collection<FormElement> elements = elementMap.values();
		return elements.toArray(new FormElement[elements.size()]);
	}

	@Override
	public FormElement getFormElement(String id) {
		return elementMap.get(Dexter.normalizeId(id));
	}

	@Override
	public Jspod newJspod(ServletRequest request) {
		try {
			Constructor<? extends Jspod> constructor = formClass
				.getDeclaredConstructor(Map.class);
			return constructor.newInstance(request.getParameterMap());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private Required traverseAnnotation(
		Set<Annotation> checker,
		List<ValidatorSource> validators,
		Annotation annotation) {
		Class<? extends Annotation> annotationType = annotation
			.annotationType();

		if (annotationType.equals(Required.class)) return (Required) annotation;

		Validation validation = annotationType
			.getDeclaredAnnotation(Validation.class);
		if (validation == null) return null;

		if (!checker.add(annotation))
			throw new IllegalStateException("Annotation のチェーンに循環があります");

		for (Annotation sub : annotationType.getAnnotations())
			traverseAnnotation(checker, validators, sub);

		// Validator クラスが設定されていないことがありうる
		//実行順序を、アノテーションにつけられたアノテーションから先に実行するためここで追加
		Class<? extends Validator<?>> validatorClass = validation.value();
		if (validatorClass != null) validators
			.add(new ConcreteValidatorSource(annotation, validatorClass));

		return null;
	}
}
