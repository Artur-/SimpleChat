package org.vaadin.artur.simplechat;

import java.text.DateFormat;
import java.util.Date;

import org.vaadin.artur.simplechat.data.ChatListener;
import org.vaadin.artur.simplechat.data.ChatManager;
import org.vaadin.artur.simplechat.data.Chatter;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Property;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UIDetachedException;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

@DesignRoot
public class SimpleChat extends VerticalLayout {

    private VerticalLayout chatLog;
    private HorizontalLayout chatterList;
    private TextField chatMessage;
    private Panel chatPanel;
    private Chatter chatter;

    public SimpleChat() {
        Design.read(this);
        // Listener on text field for sending chat messages
        chatMessage.addValueChangeListener(this::onTextInput);
        // ChatManager.addChatListener(chatListener);
        for (Chatter c : ChatManager.getChatters()) {
            onNewChatter(c);
        }
    }

    protected void onNewChatter(Chatter chatter) {
        chatterList.addComponent(new ChatterIndicator(chatter));
    }

    protected void onRemoveChatter(Chatter chatter) {
        for (Component c : chatterList) {
            ChatterIndicator chatterIndicator = ((ChatterIndicator) c);
            if (chatterIndicator.isChatter(chatter)) {
                chatterList.removeComponent(chatterIndicator);
                return;
            }
        }
        System.err.println("Chatter " + chatter + " not found in chatterList");

    }

    @Override
    public SimpleChatUI getUI() {
        return (SimpleChatUI) super.getUI();
    }

    private void onMessage(Chatter from, String message) {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);

        // Text and class to add to chat log
        String labelText = df.format(new Date()) + ": " + message;
        String labelClass = from.styleName;

        HorizontalLayout hl = new HorizontalLayout();
        hl.setDefaultComponentAlignment(Alignment.TOP_LEFT);

        Image img = new Image();
        img.setSource(new ExternalResource(from.imageUrl));
        img.setHeight("20px");
        img.setStyleName(from.styleName);
        hl.addComponent(img);
        Label messageLabel = new Label(labelText);
        messageLabel.setStyleName(labelClass);

        hl.addComponent(messageLabel);
        chatLog.addComponent(hl);
        chatPanel.setScrollTop(Integer.MAX_VALUE);
    }

    public void onTextInput(Property.ValueChangeEvent event) {
        TextField field = (TextField) event.getProperty();
        if (!"".equals(field.getValue())) {
            ChatManager.sendMessage(this.chatter, field.getValue());
            field.setValue("");
        }
    }

    public void login(String name, String imageUrl) {
        this.chatter = new Chatter(name, imageUrl);
        chatter.listener = new ChatListener() {

            @Override
            public void newChatter(Chatter chatter) {
                // Lock UI before making any changes to avoid race conditions
                safe(() -> {
                    onNewChatter(chatter);
                });
            }

            @Override
            public void message(Chatter from, String message) {
                // Lock UI before making any changes to avoid race conditions
                safe(() -> {
                    onMessage(from, message);
                });
            }

            @Override
            public void removeChatter(Chatter chatter) {
                // Lock UI before making any changes to avoid race conditions
                safe(() -> {
                    onRemoveChatter(chatter);
                });
            }

            private void safe(Runnable runnable) {
                try {
                    getUI().access(() -> {
                        runnable.run();
                    });
                } catch (UIDetachedException e) {
                    ChatManager.unregisterChatter(chatter);
                }

            }
        };

        ChatManager.registerChatter(chatter);
        onNewChatter(chatter);
    }

}
