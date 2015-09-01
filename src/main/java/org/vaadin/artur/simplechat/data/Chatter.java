package org.vaadin.artur.simplechat.data;

public class Chatter {
    public String name;
    public String styleName;
    public String imageUrl;
    public ChatListener listener;

    private static String[] colors = new String[] { "red", "green", "blue",
            "magenta", "black", "yellow" };

    public Chatter(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
        String color = colors[Math.abs(name.hashCode()) % colors.length];
        styleName = "chat-" + color;

    }
}
