package org.vaadin.artur.simplechat;

import java.text.DateFormat;
import java.util.Date;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.WebComponentExporter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.webcomponent.WebComponent;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import org.vaadin.artur.simplechat.Broadcaster.MessageEvent;

public class SimpleChatComponent extends VerticalLayout {
    private static final String DEFAULT_ROOM = "all";

    @Push
    @Theme(Lumo.class)
    public static class Exporter extends WebComponentExporter<SimpleChatComponent> {

        public Exporter() {
            super("simple-chat");
            addProperty("room", DEFAULT_ROOM).onChange((component, value) -> {
                component.setRoom(value);
            });
        }

        @Override
        protected void configureInstance(WebComponent<SimpleChatComponent> webComponent,
                SimpleChatComponent component) {
        }

    }

    private VerticalLayout chatLog;
    private TextField chatMessage;
    private Registration registration;
    private String room = DEFAULT_ROOM;
    private Div header;

    public SimpleChatComponent() {
        createUI();
    }

    private void setRoom(String room) {
        this.room = room;
        header.setText("Chat (" + room + ")");

        chatLog.removeAll();
        for (MessageEvent msg : Broadcaster.getLastMessages(room)) {
            addMessage(msg);
        }
    }

    private void createUI() {
        setHeight("100%");
        header = new Div();
        header.setText("Chat");
        chatLog = new VerticalLayout();
        Style style = chatLog.getElement().getStyle();
        style.set("overflow", "auto");
        style.set("flex", "1");
        style.set("transition", "background-color 100ms");
        chatMessage = new TextField();
        chatMessage.setWidth("100%");
        chatMessage.setPlaceholder("Write your message and press enter");
        add(header, chatLog, chatMessage);

        chatMessage.addKeyDownListener(Key.ENTER, e -> {
            this.onTextInput((TextField) e.getSource());
        });
        registration = Broadcaster.addMessageListener(this::onMessage);

    }

    private void onMessage(MessageEvent event) {
        if (!this.room.equals(event.getRoom())) {
            return;
        }
        try {
            // Lock UI before making any changes to avoid race conditions
            if (!getUI().isPresent())
                return;

            getUI().get().access(() -> {
                addMessage(event);
            });
        } catch (UIDetachedException e) {
            registration.remove();
        }

    }

    private void addMessage(MessageEvent event) {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);

        // Text and class to add to chat log
        String labelText = df.format(new Date()) + ": " + event.getMessage();
        String labelClass = "chat-" + event.getSenderColor();

        Div messageLabel = new Div();
        messageLabel.setText(labelText);
        messageLabel.setClassName(labelClass);
        chatLog.add(messageLabel);
        getElement().executeJs("$0.scrollTop=12938192;", chatLog);
        flash(chatLog);
    }

    private void flash(Component c) {
        getElement().executeJs(
                "$0.style.backgroundColor = '#bbf'; setTimeout(function() {$0.style.backgroundColor = 'white'}, 100)",
                c.getElement());
    }

    public void onTextInput(TextField field) {
        if (!"".equals(field.getValue())) {
            Broadcaster.sendMessage(this.room, field.getValue(), this);
            field.setValue("");
        }
    }

}
