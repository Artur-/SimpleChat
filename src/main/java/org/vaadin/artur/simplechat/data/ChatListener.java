package org.vaadin.artur.simplechat.data;

public interface ChatListener {

    void newChatter(Chatter chatter);

    void message(Chatter from, String message);

    void removeChatter(Chatter chatter);

}
