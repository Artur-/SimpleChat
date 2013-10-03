package org.vaadin.artur.simplechat;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Push
public class SimpleChat extends UI {

    private VerticalLayout chatLog;
    private TextField chatMessage;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = SimpleChat.class)
    public static class Servlet extends VaadinServlet {

    }

    @Override
    protected void init(VaadinRequest request) {
        createUI();
        setupLogic();
        setLocale(Locale.ENGLISH);
    }

    private void createUI() {
        VerticalLayout main = new VerticalLayout();
        main.setSizeFull();
        main.setMargin(true);

        Panel chatPanel = new Panel("Chat");
        chatPanel.setSizeFull();
        main.addComponent(chatPanel);
        main.setExpandRatio(chatPanel, 1);

        chatLog = new VerticalLayout();
        chatLog.setMargin(true);
        chatLog.setSizeUndefined();
        chatPanel.setContent(chatLog);

        chatMessage = new TextField();
        chatMessage.setImmediate(true);
        chatMessage.setWidth("100%");
        chatMessage.setInputPrompt("Write your message and press enter");
        main.addComponent(chatMessage);
        setContent(main);
    }

    private void setupLogic() {
        final Messenger messenger = Messenger.get();

        chatMessage.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                TextField field = (TextField) event.getProperty();
                if (!"".equals(field.getValue())) {
                    messenger.sendMessage(field.getValue());
                    field.setValue("");
                }
            }
        });

        messenger.addMessageListener(new MessageListener() {
            @Override
            public void messageReceived(final MessageEvent event) {

                access(new Runnable() {
                    @Override
                    public void run() {
                        DateFormat df = DateFormat
                                .getTimeInstance(DateFormat.MEDIUM);
                        Label l = new Label(df.format(new Date()) + ": "
                                + event.getMessage());
                        l.setSizeUndefined();
                        chatLog.addComponent(l);
                    }
                });
            }
        });
    }

}