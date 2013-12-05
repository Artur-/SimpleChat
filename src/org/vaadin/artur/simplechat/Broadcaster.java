package org.vaadin.artur.simplechat;

import java.lang.reflect.Method;
import java.util.EventObject;

import org.vaadin.artur.simplechat.Broadcaster.MessageListener.MessageEvent;

import com.vaadin.event.EventRouter;
import com.vaadin.util.ReflectTools;

public class Broadcaster {

	private static Broadcaster instance = new Broadcaster();

	private EventRouter router = new EventRouter();

	public interface MessageListener {
		public static final Method METHOD = ReflectTools.findMethod(
				MessageListener.class, "messageReceived", MessageEvent.class);

		public static class MessageEvent extends EventObject {
			String message;

			public MessageEvent(String message) {
				super(new Object());
				this.message = message;
			}

			public String getMessage() {
				return message;
			}

		}

		public void messageReceived(MessageEvent messageEvent);
	}

	public static void sendMessage(String message) {
		instance.router.fireEvent(new MessageEvent(message));
	}

	public static void addMessageListener(MessageListener messageListener) {
		instance.router.addListener(MessageEvent.class, messageListener,
				MessageListener.METHOD);
	}

	public static void removeMessageListener(MessageListener messageListener) {
		instance.router.removeListener(MessageEvent.class, messageListener,
				MessageListener.METHOD);
	}

}