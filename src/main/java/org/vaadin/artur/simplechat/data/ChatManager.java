package org.vaadin.artur.simplechat.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatManager {

    private static ChatManager instance = new ChatManager();

    private List<Chatter> chatters = new CopyOnWriteArrayList<>();

    public static ChatManager get() {
        return instance;
    }

    public static List<Chatter> getChatters() {
        return get().chatters;
    }

    public static void sendMessage(Chatter from, String message) {
        for (Chatter chatter : getChatters()) {
            chatter.listener.message(from, message);
        }
    }

    public static void registerChatter(Chatter chatter) {
        if (chatter.name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (chatter.imageUrl == null) {
            throw new IllegalArgumentException("Image url cannot be null");
        }
        for (Chatter c : getChatters()) {
            c.listener.newChatter(chatter);
        }
        getChatters().add(chatter);
    }

    public static void unregisterChatter(Chatter chatter) {
        System.out.println("Unregister: " + chatter.name);
        getChatters().remove(chatter);
        for (Chatter c : getChatters()) {
            c.listener.removeChatter(chatter);
        }

    }
    // public static void addChatListener(ChatListener chatListener) {
    // get().chatters.put(chatListener, null);
    // }

    // public static void removeChatListener(ChatListener chatListener) {
    // get().chatters.remove(chatListener);
    //
    // }
}
