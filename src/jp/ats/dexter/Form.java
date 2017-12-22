package jp.ats.dexter;

import javax.servlet.ServletRequest;

/**
 * @author 千葉 哲嗣
 */
public interface Form {

	FormElement[] getFormElements();

	FormElement getFormElement(String id);

	Jspod newJspod(ServletRequest request);
}
