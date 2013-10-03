package org.vaadin.artur.simplechat;

import java.util.concurrent.locks.ReentrantLock;

import org.vaadin.artur.simplechat.MessageListener.MessageEvent;

import com.vaadin.event.EventRouter;

public class Messenger {

    private static Messenger instance = new Messenger();

    private EventRouter router = new EventRouter();
    private ReentrantLock lock = new ReentrantLock();

    public void sendMessage(String message) {
        lock.lock();
        try {
            router.fireEvent(new MessageEvent(this, message));
        } finally {
            lock.unlock();
        }
    }

    public void addMessageListener(MessageListener messageListener) {
        lock.lock();
        try {
            router.addListener(MessageEvent.class, messageListener,
                    MessageListener.METHOD);
        } finally {
            lock.unlock();
        }

    }

    public void removeMessageListener(MessageListener messageListener) {
        lock.lock();
        try {
            router.removeListener(MessageEvent.class, messageListener,
                    MessageListener.METHOD);
        } finally {
            lock.unlock();
        }

    }

    public static Messenger get() {
        return instance;
    }
}
