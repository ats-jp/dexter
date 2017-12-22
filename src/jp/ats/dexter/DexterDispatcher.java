package jp.ats.dexter;

import javax.servlet.http.HttpServletRequest;

import jp.ats.dexter.Dexter.ValidatorResult;
import jp.ats.substrate.U;
import jp.ats.substrate.util.CollectionMap;

public class DexterDispatcher {

	public static <T extends Jspod> T dispatch(Class<T> jspodClass) {
		Form form = new ConcreteForm(jspodClass);

		HttpServletRequest request = DexterManager.getRequest();
		if (request == null) throw new IllegalStateException(
			DexterManager.class.getSimpleName() + " 外で使用しているため、リクエストが取得できません");

		CollectionMap<FormElement, ValidatorResult> result = Dexter
			.getInstance()
			.execute(form, U.<String, String[]>cast(request.getParameterMap()));

		//validation に問題があった場合
		if (result != null) throw new DexterValidationException(result.clone());

		@SuppressWarnings("unchecked")
		T jspod = (T) form.newJspod(request);

		return jspod;
	}

	public static <T extends Jspod> void dispatch(T jspod) {
		Form form = new ConcreteForm(jspod.getClass());

		HttpServletRequest request = DexterManager.getRequest();
		if (request == null) throw new IllegalStateException(
			DexterManager.class.getSimpleName() + " 外で使用しているため、リクエストが取得できません");

		CollectionMap<FormElement, ValidatorResult> result = Dexter
			.getInstance()
			.execute(form, U.<String, String[]>cast(request.getParameterMap()));

		//validation に問題があった場合
		if (result != null) throw new DexterValidationException(result.clone());

		jspod.apply(request.getParameterMap());
	}
}
