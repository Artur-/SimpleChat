package org.vaadin.artur.simplechat;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.vaadin.event.EventRouter;

public class StaticMessager implements Messager {

    private static StaticMessager instance = new StaticMessager();

    private EventRouter router = new EventRouter();
    private Lock lock = new ReentrantLock();

    @Override
    public void sendMessage(String message) {
        lock.lock();
        try {
            router.fireEvent(new MessageEvent(this, message));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addMessageListener(MessageListener messageListener) {
        lock.lock();
        try {
            router.addListener(MessageEvent.class, messageListener,
                    MessageListener.METHOD);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeMessageListener(MessageListener messageListener) {
        lock.lock();
        try {
            router.removeListener(MessageEvent.class, messageListener,
                    MessageListener.METHOD);
        } finally {
            lock.unlock();
        }
    }

    public static StaticMessager get() {
        return instance;
    }
}
