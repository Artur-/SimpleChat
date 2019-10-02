package org.vaadin.artur.simplechat;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

import org.slf4j.LoggerFactory;

public class Broadcaster {

	private static Broadcaster instance = new Broadcaster();

	private static String[] colors = new String[] { "red", "green", "blue", "magenta", "black" };

	private ComponentEventBus router = new ComponentEventBus(new Div());
	private ConcurrentHashMap<String, CopyOnWriteArrayList<MessageEvent>> lastMessages = new ConcurrentHashMap<>();

	public static class MessageEvent extends ComponentEvent<Div> {
		String message;
		private String senderColor;
		private String room;

		public MessageEvent(String senderColor, String room, String message) {
			super(new Div(), false);
			this.message = message;
			this.senderColor = senderColor;
			this.room = room;
		}

		public String getMessage() {
			return message;
		}

		public String getSenderColor() {
			return senderColor;
		}

		public String getRoom() {
			return room;
		}

	}

	public static void sendMessage(String room, String message, SimpleChatComponent source) {
		LoggerFactory.getLogger(Broadcaster.class).info("Sending message '" + message + "' in chat for " + room);
		MessageEvent event = new MessageEvent(getColor(source), room, message);
		instance.router.fireEvent(event);
		CopyOnWriteArrayList<MessageEvent> last = instance.lastMessages.computeIfAbsent(room,
				r -> new CopyOnWriteArrayList<>());
		last.add(event);
		if (last.size() > 15) {
			last.remove(0);
		}
	}

	private static String getColor(SimpleChatComponent source) {
		return colors[source.hashCode() % colors.length];
	}

	public static Registration addMessageListener(ComponentEventListener<MessageEvent> messageListener) {
		return instance.router.addListener(MessageEvent.class, messageListener);
	}

	public static List<MessageEvent> getLastMessages(String room) {
		return instance.lastMessages.getOrDefault(room, new CopyOnWriteArrayList<>());
	}

}