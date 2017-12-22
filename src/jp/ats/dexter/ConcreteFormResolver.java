package jp.ats.dexter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
class ConcreteFormResolver implements FormResolver {

	private static final Pattern pattern = Pattern.compile("[^/]+$");

	private String prefix;

	private String suffix;

	@Override
	public synchronized void initialize(ServletConfig config) {
		prefix = config.getServletContext()
			.getInitParameter("dexter-formclass-package");
		if (!U.isAvailable(prefix))
			throw new IllegalStateException("dexter-formclass-package は必須です");

		if (!prefix.endsWith(".")) prefix += ".";

		suffix = config.getInitParameter("suffix");
		if (suffix == null) suffix = ".dx";
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	@Override
	public Form resolve(String id) {
		Matcher matcher = pattern.matcher(id);
		matcher.find();
		id = matcher.group();

		int index = id.indexOf(suffix);
		if (index > 0) id = id.substring(0, index);

		id = prefix + id;

		Class<? extends Jspod> formClass;
		try {
			formClass = cast(Class.forName(id));
		} catch (ClassNotFoundException e) {
			// クラスが存在しない場合、Dexter の対象ではない
			return null;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return new ConcreteForm(formClass);
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Jspod> cast(Class<?> clazz) {
		return (Class<? extends Jspod>) clazz;
	}
}
