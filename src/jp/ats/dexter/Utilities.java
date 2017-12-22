package jp.ats.dexter;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 千葉 哲嗣
 */
public class Utilities {

	public static final String care(String target) {
		return target == null ? "" : target;
	}

	static FormResolver createFormResolver(ServletConfig config)
		throws ServletException {
		FormResolver resolver;

		String resolverClassParameter = config.getServletContext()
			.getInitParameter("dexter-formresolver-class");
		Class<?> resolverClass;
		try {
			if (resolverClassParameter == null) {
				resolverClass = ConcreteFormResolver.class;
			} else {
				resolverClass = Class.forName(resolverClassParameter);
			}

			resolver = (FormResolver) resolverClass.newInstance();
		} catch (Exception e) {
			throw new ServletException(e);
		}

		resolver.initialize(config);

		return resolver;
	}

	static String readEncodingConfig(ServletContext conntext) {
		String encoding = conntext.getInitParameter("dexter-encoding");
		if (encoding == null) return "UTF-8";
		return encoding;
	}

	static String getFormKey(String base, HttpServletRequest request) {
		try {
			String key = new URI(base).getPath();
			String contextPath = request.getContextPath();
			if (key.startsWith(contextPath)) {
				return key.substring(contextPath.length());
			}
			return key;
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
}
