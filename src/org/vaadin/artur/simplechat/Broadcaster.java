package org.vaadin.artur.simplechat;

import java.lang.reflect.Method;
import java.util.EventObject;

import org.vaadin.artur.simplechat.Broadcaster.MessageListener.MessageEvent;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.EventRouter;
import com.vaadin.util.ReflectTools;

public class Broadcaster {

	private static Broadcaster instance = new Broadcaster();

	private static String[] colors = new String[] { "red", "green", "blue",
			"magenta", "black" };

	private EventRouter router = new EventRouter();

	public interface MessageListener {
		public static final Method METHOD = ReflectTools.findMethod(
				MessageListener.class, "messageReceived", MessageEvent.class);

		public static class MessageEvent extends EventObject {
			String message;
			private String senderColor;

			public MessageEvent(String senderColor, String message) {
				super(new Object());
				this.message = message;
				this.senderColor = senderColor;
			}

			public String getMessage() {
				return message;
			}

			public String getSenderColor() {
				return senderColor;
			}

		}

		public void messageReceived(MessageEvent messageEvent);
	}

	public static void sendMessage(String message, SimpleChatUI source) {
		instance.router.fireEvent(new MessageEvent(getColor(source), message));
	}

	private static String getColor(SimpleChatUI source) {
		return colors[source.hashCode() % colors.length];
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