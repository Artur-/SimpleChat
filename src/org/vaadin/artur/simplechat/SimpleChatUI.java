package org.vaadin.artur.simplechat;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import org.vaadin.artur.simplechat.Broadcaster.MessageListener;
import org.vaadin.teemu.clara.Clara;
import org.vaadin.teemu.clara.binder.annotation.UiField;
import org.vaadin.teemu.clara.binder.annotation.UiHandler;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.UIDetachedException;
import com.vaadin.ui.VerticalLayout;

@Push
@Theme("SimpleChat")
public class SimpleChatUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SimpleChatUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@UiField
	private VerticalLayout chatLog;

	@Override
	protected void init(VaadinRequest request) {
		createUI();
		setupLogic();
		setLocale(Locale.ENGLISH);
	}

	private void createUI() {
		setContent(Clara.create("SimpleChat.xml", this));
	}

	@UiHandler("chatMessage")
	public void onTextInput(Property.ValueChangeEvent event) {
		TextField field = (TextField) event.getProperty();
		if (!"".equals(field.getValue())) {
			Broadcaster.sendMessage(field.getValue(), SimpleChatUI.this);
			field.setValue("");
		}
	}

	private void setupLogic() {
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