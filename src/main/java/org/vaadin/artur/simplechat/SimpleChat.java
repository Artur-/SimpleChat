package org.vaadin.artur.simplechat;

import java.text.DateFormat;
import java.util.Date;

import org.vaadin.artur.simplechat.Broadcaster.MessageListener.MessageEvent;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Property;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UIDetachedException;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@DesignRoot
public class SimpleChat extends VerticalLayout {

    private VerticalLayout chatLog;
    private TextField chatMessage;

    public SimpleChat() {
        Design.read(this);
        // Listener on text field for sending chat messages
        chatMessage.addValueChangeListener(this::onTextInput);
        Broadcaster.addMessageListener(this::onMessage);
    }

    private void onMessage(MessageEvent event) {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);

        // Text and class to add to chat log
        String labelText = df.format(new Date()) + ": " + event.getMessage();
        String labelClass = "chat-" + event.getSenderColor();

        try {
            // Lock UI before making any changes to avoid race conditions
            getUI().access(() -> {
                Label messageLabel = new Label(labelText);
                messageLabel.setStyleName(labelClass);
                chatLog.addComponent(messageLabel);
            });
        } catch (UIDetachedException e) {
            Broadcaster.removeMessageListener(this::onMessage);
        }

    }

    @Override
    public SimpleChatUI getUI() {
        return (SimpleChatUI) super.getUI();
    }

    public void onTextInput(Property.ValueChangeEvent event) {
        TextField field = (TextField) event.getProperty();
        if (!"".equals(field.getValue())) {
            Broadcaster.sendMessage(field.getValue(), getUI());
            field.setValue("");
        }
    }

}
