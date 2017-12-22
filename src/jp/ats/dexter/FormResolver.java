package jp.ats.dexter;

import javax.servlet.ServletConfig;

/**
 * @author 千葉 哲嗣
 */
public interface FormResolver {

	void initialize(ServletConfig config);

	Form resolve(String id);
}
