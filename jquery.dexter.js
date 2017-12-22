/*
 * jquery.dexter.js
 *
 * 作成：千葉　哲嗣
 * */
(function($) {
	"use strict";

	// フォーム入力値の監視間隔（ミリ秒）
	var observeFrequency = 300;

	var normalizeResponseXML = function(document) {
		if (document.normalize)
			document.normalize();
		return $(document);
	};

	var toBoolean = function(target) {
		return target == "true";
	};

	/*
	 * dexterURL: Dexter Servlet のURL submitElement: submit ボタン options:
	 * submitControlFunction: submit ボタンを独自に制御する場合使用
	 * messageDrawOnControlFunction: エラーメッセージ表示対象要素を制御する場合に使用
	 */
	$.fn.dexter = function(dexterURL, submitElement, options) {
		var dexter = $(this);

		options = options || {};

		var messagePosition;

		// 検証メッセージの表示位置を下に変更する
		dexter.changeMessagePositionToBottom = function() {
			messagePosition = [ 0, 3 ];

			return dexter;
		};

		// 検証メッセージの表示位置を右に変更する
		dexter.changeMessagePositionToRight = function() {
			messagePosition = [ 2, 1 ];

			return dexter;
		};

		// デフォルトは下位置
		dexter.changeMessagePositionToBottom();

		// 検証メッセージの表示位置を変更する場合はこの関数を上書きすること
		dexter.adjustPosition = function(element, messageArea) {
			var offset = element.offset();
			var position = [ offset.left, offset.top ];
			position.push(position[0] + element.outerWidth(true));
			position.push(position[1] + element.outerHeight(true));
			messageArea.css({
				left : position[messagePosition[0]] + "px",
				top : position[messagePosition[1]] + "px"
			});
		};

		var rawDescriptionArea;

		var descriptionArea;

		var hideDescription = function() {
			if (descriptionArea != null)
				descriptionArea.hide();
		};

		// 説明のスタイルを変更する場合はこの関数を上書きすること
		dexter.decorateDescription = function(validatorNodes) {
			var descriptionBody = "<table>";
			for (var i = 0; i < validatorNodes.length; i++) {
				var node = $(validatorNodes[i]);
				var myDescription = node.text();
				descriptionBody += "<tr><th>" + node.attr("name")
						+ "</th><td>&nbsp;:&nbsp;</td><td>"
						+ (myDescription ? myDescription : "") + "</td></tr>";
			}

			descriptionBody += "</table>";

			descriptionArea.html(descriptionBody);
		};

		var timeoutID;

		var showDescription = function(x, y, validatorNodes, element) {
			if (timeoutID)
				clearTimeout(timeoutID);

			// z-indexがうまく効かないブラウザ用にvalidationMessage要素より後に作成するようにする
			if (!rawDescriptionArea) {
				rawDescriptionArea = document.createElement("div");
				document.body.appendChild(rawDescriptionArea);

				descriptionArea = $(rawDescriptionArea);
				descriptionArea.addClass("dexterDescription");
			}

			dexter.decorateDescription(validatorNodes);

			// iframe内の場合、スクロールバーを出さないように
			// 取敢えず(0, 0)にしておく
			descriptionArea.css({
				left : "0px",
				top : "0px"
			});

			// 先にshowしないと、offsetWidthが取れない
			descriptionArea.show();

			// 画面からはみ出した時の調整
			if (x + 5 + descriptionArea.outerWidth() > $(document).outerWidth()) {
				var left = $(document).width() - descriptionArea.outerWidth();
				descriptionArea.css({
					left : (left < 0 ? 0 : left) + "px"
				});
			} else {
				descriptionArea.css({
					left : (x + 5) + "px"
				});
			}

			// フォーム要素にかかった時の調整
			var bottomPosition = element.outerHeight() + element.offset().top;
			if (y + 10 < bottomPosition) {
				descriptionArea.css({
					top : bottomPosition + "px"
				});
			} else {
				descriptionArea.css({
					top : (y + 10) + "px"
				});
			}

			timeoutID = setTimeout(hideDescription, 3000);
		};

		dexter.toggleDescription = function(element) {
			if (timeoutID)
				clearTimeout(timeoutID);

			if (descriptionArea != null
					&& descriptionArea.css("display") != "none") {
				descriptionArea.hide();
				return;
			}

			element = $(element);

			var offset = element.offset();
			var position = [ offset.left, offset.top ];

			position.push(position[0] + element.outerWidth());
			position.push(position[1] + element.outerHeight());

			executeDescriptionInternal(position[messagePosition[0]] - 5,
					position[messagePosition[1]] - 10, element);

			return dexter;
		};

		var hideDescription = function() {
			if (descriptionArea != null)
				descriptionArea.hide();
		};

		// 説明表示を自動的にする／しない
		var autoDescription = true;

		dexter.deisableAutoDescription = function() {
			autoDescription = false;

			return dexter;
		};

		dexter.enableAutoDescription = function() {
			autoDescription = true;

			return dexter;
		};

		var hideMessages;

		var changeSubmitElement = function(disabled) {
			if (options.submitControlFunction) {
				options.submitControlFunction(disabled, submitElement);
			} else {
				$(submitElement).attr("disabled", disabled ? "disabled" : null);
			}
		};

		changeSubmitElement(true);

		var elements = dexter.find(":input");

		// HTMLのダイナミックな変更等で検証メッセージの表示位置がずれた場合、全ての位置調整を行う
		dexter.adjustAllValidationMessages = function() {
			elements.each(function() {
				var element = $(this);

				var drawOn = element.data("dexterMessageDrawOn");

				if (drawOn)
					dexter.adjustPosition(drawOn, element
							.data("dexterMessageArea"));
			});

			return dexter;
		};

		$(window).resize(dexter.adjustAllValidationMessages);

		var getMessageArea = function(element) {
			var messageArea = element.data("dexterMessageArea");
			if (!messageArea) {
				messageArea = document.createElement("div");
				document.body.appendChild(messageArea);

				messageArea = $(messageArea);

				messageArea.addClass("dexterValidationMessage");
				messageArea.mouseover(dexter.adjustAllValidationMessages);

				element.data("dexterMessageArea", messageArea);
			}

			return messageArea;
		};

		var drawValidationMessage = function(element, message) {
			var currentDrawOn;
			if (options.messageDrawOnControlFunction) {
				currentDrawOn = options.messageDrawOnControlFunction(drawOn);
			} else {
				currentDrawOn = element;
			}

			element.data("dexterValidationMessage", message);
			element.data("dexterMessageDrawOn", currentDrawOn);

			var messageArea = getMessageArea(element);

			if (rawDescriptionArea) {
				document.body.removeChild(rawDescriptionArea);
				rawDescriptionArea = null;
				descriptionArea = null;
			}

			messageArea.html(message);

			dexter.adjustPosition(currentDrawOn, messageArea);

			if (hideMessages) {
				messageArea.hide();
			} else {
				messageArea.show();
			}
		};

		var validate = function(element, targetValue) {
			$.ajax({
				url : dexterURL,
				method : "POST",
				data : {
					id : dexter.attr("id"),
					action : dexter.attr("action"),
					name : element.attr("name"),
					validate : "true",
					value : targetValue
				},
				timeout : 10000
			}).done(function(data, status, xhr) {
				var response = normalizeResponseXML(data);

				if (!toBoolean(response.find("resultAvailable").text()))
					return;

				var valid = toBoolean(response.find("valid").text());
				element.data("dexterValidationFailure", !valid);

				if (valid) {
					getMessageArea(element).hide();
					decideSubmitElement();
					element.removeClass("dexterValidationFailure");
					return;
				} else {
					changeSubmitElement(true);
					element.addClass("dexterValidationFailure");
				}

				var message = "** " + response.find("validationMessage").text() + " **";

				drawValidationMessage(element, message);
			});
		};

		// start後に検証対象が増減した場合にrefreshする
		dexter.refresh = function() {
			elements = dexter.find(":input");
			return dexter;
		};

		// 検証メッセージのあるもののみ表示する
		dexter.showAllValidationMessages = function() {
			hideMessages = false;

			elements.each(function() {
				var element = $(this);
				if (element.data("dexterValidationFailure"))
					var area = $(this).data("dexterMessageArea");
				if (area)
					area.show();
			});

			return dexter;
		};

		// 全ての検証メッセージを非表示にする
		dexter.hideAllValidationMessages = function() {
			hideMessages = true;

			elements.each(function() {
				var area = $(this).data("dexterMessageArea");
				if (area)
					area.hide();
			});

			return dexter;
		};

		var executeDescriptionInternal = function(x, y, element) {
			element = $(element);

			$.ajax({
				url : dexterURL,
				method : "POST",
				data : {
					id : dexter.attr("id"),
					action : dexter.attr("action"),
					name : element.attr("name"),
					validate : "false"
				},
				timeout : 10000
			}).done(function(data, status, xhr) {
				var response = normalizeResponseXML(data);

				if (!toBoolean(response.find("resultAvailable").text()))
					return;

				var validatorNodes = response.find("descriptions").children();

				if (validatorNodes)
					showDescription(x, y, validatorNodes, element);
			});
		};

		var executeDescription = function(event) {
			executeDescriptionInternal(event.pageX, event.pageY, event.target);
		};

		var decideSubmitElement = function() {
			var failure = false;

			elements.each(function() {
				if ($(this).data("dexterValidationFailure")) {
					failure = true;
					return false;
				}
			});

			changeSubmitElement(failure);
		};

		var executeValidation = function() {
			elements.each(function() {
				var element = $(this);

				var getArea = function() {
					return element.data("dexterMessageArea");
				};

				// 画面上入力欄が非表示になっている場合、メッセージも隠す
				if (!element.is(":visible")) {
					var area = getArea();
					if (area)
						area.hide();
					return;
				} else {
					if (element.data("dexterValidationFailure")) {
						var area = getArea();
						if (area)
							area.show();
					}
				}

				var value = element.val();
				if (element.data("dexterOldValue") != value) {
					validate(element, value);
					element.data("dexterOldValue", value);
				}
			});
		};

		dexter.start = function() {
			elements.each(function() {
				if (autoDescription)
					$(this).mouseover(executeDescription).mouseout(
							hideDescription);
			});

			executeValidation();
			setInterval(executeValidation, observeFrequency);

			return dexter;
		};

		return dexter;
	};
})(jQuery);
