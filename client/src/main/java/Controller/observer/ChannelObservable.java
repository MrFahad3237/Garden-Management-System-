package Controller.observer;

import java.util.*;

public class ChannelObservable {
    protected Map<Channel, List<ChannelObserver>> observers = new HashMap<>();

    public synchronized void addObserverToChannel(Channel channel, ChannelObserver observer) {
        if (observer == null) {
            throw new NullPointerException();
        }

        if (!observers.containsKey(channel)) {
            observers.put(channel, new ArrayList<>());
        }

        if (!observers.get(channel).contains(observer)) {
            observers.get(channel).add(observer);
        }
    }

    public synchronized void removeObserverFromChannel(Channel channel, ChannelObserver observer) {
        if (observers.containsKey(channel)) {
            observers.get(channel).remove(observer);
        }
    }

    public synchronized void notifyObserversWithMessage(Channel channel, Object msg) {
        if (observers.containsKey(channel)) {
            for (ChannelObserver observer : observers.get(channel)) {
                observer.update(this, channel, msg);
            }
        }
    }
}
