package jp.ats.dexter;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.ats.dexter.Dexter.ValidatorResult;
import jp.ats.substrate.U;
import jp.ats.substrate.util.CollectionMap;

/**
 * フォームの一括送信値をチェックするサーブレットです。
 *
 * @author 千葉 哲嗣
 */
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 7312884963673581017L;

	private FormResolver formResolver;

	private String encoding;

	@Override
	public void init(ServletConfig config) throws ServletException {
		formResolver = Utilities.createFormResolver(config);
		encoding = Utilities.readEncodingConfig(config.getServletContext());
	}

	@Override
	protected void doGet(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		execute(request, response);
	}

	@Override
	protected void doPost(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		execute(request, response);
	}

	private void execute(
		HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(encoding);

		Form form = formResolver
			.resolve(Utilities.getFormKey(request.getRequestURI(), request));

		//設定が存在しない場合
		if (form == null) throw new IllegalStateException(
			"設定が存在しません: " + request.getRequestURI());

		CollectionMap<FormElement, ValidatorResult> result = Dexter
			.getInstance()
			.execute(form, U.<String, String[]>cast(request.getParameterMap()));

		//validation に問題があった場合
		if (result != null) throw new DexterValidationException(result.clone());

		Jspod jspod = form.newJspod(request);

		Control control = jspod.getClass().getAnnotation(Control.class);
		Class<? extends Consumer<? extends Jspod>> consumerClass = control
			.consumer();

		Consumer<? extends Jspod> consumer;
		try {
			consumer = consumerClass.newInstance();

			consumerClass.getMethod("consume", Jspod.class)
				.invoke(consumer, jspod);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		String forward;
		if (consumer.hasForward()) {
			forward = consumer.getForward();
		} else {
			forward = control.forward();
		}

		if (control.redirect()) {
			response.sendRedirect(forward);
			return;
		}

		request.getRequestDispatcher(forward).forward(request, response);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
