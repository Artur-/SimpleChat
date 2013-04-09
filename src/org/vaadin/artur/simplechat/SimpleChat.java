package org.vaadin.artur.simplechat;

import java.text.DateFormat;
import java.util.Date;

import org.vaadin.artur.simplechat.Messager.MessageEvent;
import org.vaadin.artur.simplechat.Messager.MessageListener;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SimpleChat extends UI {

    private VerticalLayout chatLog;
    private TextField chatMessage;

    @Override
    protected void init(VaadinRequest request) {
        createUI();
        setupLogic();
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
        final Messager messager = StaticMessager.get();

        chatMessage.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                TextField field = (TextField) event.getProperty();
                if (!"".equals(field.getValue())) {
                    messager.sendMessage(field.getValue());
                    field.setValue("");
                }
            }
        });

        messager.addMessageListener(new MessageListener() {

            @Override
            public void messageReceived(final MessageEvent event) {

                runSafely(new Runnable() {
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