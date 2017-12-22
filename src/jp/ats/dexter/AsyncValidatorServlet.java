package jp.ats.dexter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.MessageFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ats.dexter.Dexter.ValidatorResult;
import jp.ats.dexter.annotation.Required;
import jp.ats.dexter.validator.RequiredValidator;
import jp.ats.substrate.U;

/**
 * Ajax による入力欄別の入力チェックを行うサーブレットです。
 *
 * @author 千葉 哲嗣
 */
public class AsyncValidatorServlet extends HttpServlet {

	private static final long serialVersionUID = -5077243280267764574L;

	private static final String resultTemplate;

	private FormResolver formResolver;

	private String encoding;

	private boolean useFormAction;

	static {
		URL url = U.getResourcePath(AsyncValidatorServlet.class, "xml");
		try {
			resultTemplate = new String(U.readBytes(url.openStream()));
		} catch (IOException e) {
			throw new Error(e);
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		formResolver = Utilities.createFormResolver(config);
		encoding = Utilities.readEncodingConfig(config.getServletContext());

		String useFormActionString = config.getServletContext()
			.getInitParameter("use-formaction");
		if (U.isAvailable(useFormActionString))
			useFormAction = Boolean.valueOf(useFormActionString);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	@Override
	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(encoding);

		String formKey = useFormAction
			? Utilities.getFormKey(request.getParameter("action"), request)
			: request.getParameter("id");

		//line 1
		//form の action
		Form form = formResolver.resolve(formKey);
		if (form == null) {
			write(response, false, "", false, "");
			return;
		}

		//line 2
		//form element の name
		FormElement element = form.getFormElement(request.getParameter("name"));
		if (element == null) {
			write(response, false, "", false, "");
			return;
		}

		//line 3
		//validation を行うか
		boolean validate = Boolean
			.parseBoolean(request.getParameter("validate"));

		String validatorDescriptions;
		boolean valid;
		String validationMessage;

		Dexter dexter = Dexter.getInstance();

		if (validate) {
			validatorDescriptions = "";

			ValidatorResult result = dexter
				.execute(request.getParameter("value"), element);

			valid = result == null;
			validationMessage = result == null
				? ""
				: result.getValidationMessage();
		} else {
			StringBuilder builder = new StringBuilder();

			Required required = element.getRequiredAnnotation();
			if (required != null) {
				RequiredValidator requiredValidator = new RequiredValidator();
				requiredValidator.initialize(required);
				append(builder, requiredValidator);
			}

			for (ValidatorSource source : element.getValidators()) {
				Validator<?> validator = Dexter.prepareValidator(source);
				append(builder, validator);
			}

			validatorDescriptions = builder.toString();

			valid = false;
			validationMessage = "";
		}

		write(response, true, validatorDescriptions, valid, validationMessage);
	}

	private static void append(StringBuilder builder, Validator<?> validator) {
		builder.append("<validator name=\"");
		builder.append(escape(validator.getName()));
		builder.append("\">");
		builder.append(escape(validator.getDescription()));
		builder.append("</validator>");
	}

	private void write(
		HttpServletResponse response,
		boolean resultAvailable,
		String validatorDescriptions,
		boolean valid,
		String validationMessage) throws IOException {
		response.setContentType("text/xml");
		response.setCharacterEncoding(encoding);
		PrintWriter writer = new PrintWriter(
			new OutputStreamWriter(response.getOutputStream(), encoding));

		writer.write(
			MessageFormat.format(
				resultTemplate,
				resultAvailable,
				validatorDescriptions,
				valid,
				escape(validationMessage)));

		writer.flush();
	}

	private static String escape(String input) {
		if (input == null) return null;
		input = substitute(input, "&", "&amp;");
		input = substitute(input, "<", "&lt;");
		input = substitute(input, ">", "&gt;");
		input = substitute(input, "\"", "&quot;");
		return input;
	}

	private static String substitute(
		String input,
		String pattern,
		String replacement) {
		int index = input.indexOf(pattern);

		if (index == -1) {
			return input;
		}

		StringBuilder buffer = new StringBuilder();

		buffer.append(input.substring(0, index) + replacement);

		if (index + pattern.length() < input.length()) {
			String rest = input
				.substring(index + pattern.length(), input.length());
			buffer.append(substitute(rest, pattern, replacement));
		}
		return buffer.toString();
	}
}
