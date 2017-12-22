package jp.ats.dexter;

import java.text.MessageFormat;

import jp.ats.substrate.U;

/**
 * @author 千葉 哲嗣
 */
public abstract class AbstractValidator {

	private String name;

	private String message;

	private String messageTemplate;

	private String description;

	private String descriptionTemplate;

	private String value;

	protected AbstractValidator() {
		reset();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		description = description == null ? "" : description;
		descriptionTemplate = getDescriptionTemplate();
		descriptionTemplate = descriptionTemplate == null
			? ""
			: descriptionTemplate;
		if (description.length() == 0 && descriptionTemplate.length() > 0)
			description = MessageFormat
				.format(descriptionTemplate, getDescriptionArguments());

		return description;
	}

	public void setValidationMessage(String message) {
		this.message = message;
	}

	public String getValidationMessage() {
		message = message == null ? "" : message;
		messageTemplate = getMessageTemplate();
		messageTemplate = messageTemplate == null ? "" : messageTemplate;
		if (message.length() == 0 && messageTemplate.length() > 0)
			message = MessageFormat
				.format(messageTemplate, getMessageArguments());
		return message;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public String getMessageTemplate() {
		return messageTemplate;
	}

	public String getDescriptionTemplate() {
		return descriptionTemplate;
	}

	public void setDescriptionTemplate(String descriptionTemplate) {
		this.descriptionTemplate = descriptionTemplate;
	}

	public void reset() {
		message = null;
		messageTemplate = null;
		description = null;
		descriptionTemplate = null;
		value = null;
	}

	public abstract boolean validate();

	@Override
	public String toString() {
		return U.toString(this);
	}

	protected Object[] getMessageArguments() {
		return U.OBJECT_EMPTY_ARRAY;
	}

	protected Object[] getDescriptionArguments() {
		return U.OBJECT_EMPTY_ARRAY;
	}
}
