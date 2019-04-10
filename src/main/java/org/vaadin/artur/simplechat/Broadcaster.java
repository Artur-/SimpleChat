package org.vaadin.artur.simplechat;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

public class Broadcaster {

	private static Broadcaster instance = new Broadcaster();

	private static String[] colors = new String[] { "red", "green", "blue", "magenta", "black" };

	private ComponentEventBus router = new ComponentEventBus(new Div());

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
		instance.router.fireEvent(new MessageEvent(getColor(source), room, message));
	}

	private static String getColor(SimpleChatComponent source) {
		return colors[source.hashCode() % colors.length];
	}

	public static Registration addMessageListener(ComponentEventListener<MessageEvent> messageListener) {
		return instance.router.addListener(MessageEvent.class, messageListener);
	}

}