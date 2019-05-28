package org.vaadin.artur.simplechat;

import java.text.DateFormat;
import java.util.Date;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.component.WebComponentExporter;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.webcomponent.WebComponent;
import com.vaadin.flow.component.webcomponent.WebComponentDefinition;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import org.vaadin.artur.simplechat.Broadcaster.MessageEvent;

@Push
@Route("")
public class SimpleChatComponent extends VerticalLayout {
    private static final String DEFAULT_ROOM = "all";

    @Push
    public static class Exporter extends WebComponentExporter<SimpleChatComponent> {

        public Exporter() {
            super("simple-chat");
            addProperty("room", DEFAULT_ROOM).onChange((component, value) -> {
                component.setRoom(value);
            });        }

        @Override
        public void configureInstance(WebComponent<SimpleChatComponent> webComponent, SimpleChatComponent component) {

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

        chatMessage.addValueChangeListener(e -> {
            this.onTextInput(e.getSource());
        });
        registration = Broadcaster.addMessageListener(this::onMessage);

    }

    private void onMessage(MessageEvent event) {
        System.out.println("Received message for " + event.getRoom() + " in chat for " + this.room);
        if (!this.room.equals(event.getRoom())) {
            return;
        }
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);

        // Text and class to add to chat log
        String labelText = df.format(new Date()) + ": " + event.getMessage();
        String labelClass = "chat-" + event.getSenderColor();

        try {
            // Lock UI before making any changes to avoid race conditions
            getUI().get().access(() -> {
                Div messageLabel = new Div();
                messageLabel.setText(labelText);
                messageLabel.setClassName(labelClass);
                chatLog.add(messageLabel);
                getUI().get().getPage().executeJavaScript("$0.scrollTop=12938192;", chatLog);
                flash(chatLog);
            });
        } catch (UIDetachedException e) {
            registration.remove();
        }

    }

    private void flash(Component c) {
        c.getUI().get().getPage().executeJavaScript(
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
