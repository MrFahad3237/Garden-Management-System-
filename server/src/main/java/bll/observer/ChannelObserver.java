package bll.observer;

public interface ChannelObserver {
    void update(ChannelObservable o, Channel channnel, Object arg);
}
