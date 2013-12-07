package org.vaadin.artur.simplechat;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import org.vaadin.artur.simplechat.Broadcaster.MessageListener;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.UIDetachedException;
import com.vaadin.ui.VerticalLayout;

@Push
@Theme("SimpleChat")
public class SimpleChatUI extends UI {

	private Panel chatPanel;
	private VerticalLayout chatLog;
	private TextField chatMessage;
	private VerticalLayout mainLayout;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SimpleChatUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		createUI();
		setupLogic();
		setLocale(Locale.ENGLISH);
	}

	private void createUI() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setMargin(true);

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

		mainLayout.addComponent(chatPanel);
		mainLayout.setExpandRatio(chatPanel, 1);
		mainLayout.addComponent(chatMessage);

		setContent(mainLayout);
	}

	private void setupLogic() {
		chatMessage.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				TextField field = (TextField) event.getProperty();
				if (!"".equals(field.getValue())) {
					Broadcaster.sendMessage(field.getValue(), SimpleChatUI.this);
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
							messageLabel.setStyleName("chat-"
									+ event.getSenderColor());
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