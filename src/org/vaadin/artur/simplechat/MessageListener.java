package org.vaadin.artur.simplechat;

import java.lang.reflect.Method;
import java.util.EventObject;

import com.vaadin.util.ReflectTools;

public interface MessageListener {
    public static final Method METHOD = ReflectTools.findMethod(
            MessageListener.class, "messageReceived", MessageEvent.class);

    public static class MessageEvent extends EventObject {
        String message;

        public MessageEvent(Object source, String message) {
            super(source);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

    public void messageReceived(MessageEvent messageEvent);
}
