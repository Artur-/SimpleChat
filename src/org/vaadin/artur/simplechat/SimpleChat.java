package org.vaadin.artur.simplechat;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.vaadin.artur.simplechat.Broadcaster.MessageListener;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UIDetachedException;
import com.vaadin.ui.VerticalLayout;

public class SimpleChat extends VerticalLayout {

	private Panel chatPanel;
	private VerticalLayout chatLog;
	private TextField chatMessage;

	public SimpleChat() {
		createUI();
		setupLogic();
		setLocale(Locale.ENGLISH);
	}

	private void createUI() {
		setSizeFull();
		setMargin(true);

		chatPanel = new Panel("Chat");
		chatPanel.setSizeFull();

		chatLog = new VerticalLayout();
		chatLog.setMargin(true);
		chatLog.setWidth("100%");
		chatLog.setHeight(null);
		chatPanel.setContent(chatLog);

		chatMessage = new TextField();
		chatMessage.setImmediate(true);
		chatMessage.setWidth("100%");
		chatMessage.setInputPrompt("Write your message and press enter");

		addComponent(chatPanel);
		setExpandRatio(chatPanel, 1);
		addComponent(chatMessage);
	}

	private void setupLogic() {
		chatMessage.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				TextField field = (TextField) event.getProperty();
				if (!"".equals(field.getValue())) {
					Broadcaster.sendMessage(field.getValue());
					field.setValue("");
				}
			}
		});

		Broadcaster.addMessageListener(new MessageListener() {
			@Override
			public void messageReceived(final MessageEvent event) {
				try {
					getUI().access(new Runnable() {
						@Override
						public void run() {
							DateFormat df = DateFormat
									.getTimeInstance(DateFormat.MEDIUM);
							Label messageLabel = new Label(df
									.format(new Date())
									+ ": "
									+ event.getMessage());
							chatLog.addComponent(messageLabel);
						}
					});
				} catch (UIDetachedException e) {
					Broadcaster.removeMessageListener(this);
				}
			}
		});
	}

}
