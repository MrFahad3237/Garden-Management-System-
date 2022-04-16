package TestCode.bil.observer;

import Controller.observer.Channel;
import Controller.observer.ChannelObserver;
import Controller.observer.ChannelObservable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChannelObservableTest {
    private ChannelObservable observable;

    @Mock
    private ChannelObserver observer;

    @Before
    public void setUp() throws Exception {
        observable = new ChannelObservable();
    }


    @Test
    public void testNotifyObserversWithMessage() {
        insertObserver();

        ArgumentCaptor<ChannelObservable> observableCaptor = ArgumentCaptor.forClass(ChannelObservable.class);

        observable.notifyObserversWithMessage(Channel.LOGIN, "msg");

        verify(observer).update(observableCaptor.capture(), any(), anyString());
        assertEquals("update was called with correct observable", observable, observableCaptor.getValue());
    }

    @Test
    public void testNotifyObserversWithMessageCheckChannel() {
        insertObserver();

        ArgumentCaptor<Channel> channelCaptor = ArgumentCaptor.forClass(Channel.class);

        observable.notifyObserversWithMessage(Channel.LOGIN, "msg");

        verify(observer).update(any(), channelCaptor.capture(), anyString());
        assertEquals("update was called with correct channel", Channel.LOGIN,  channelCaptor.getValue());
    }

    private void insertObserver() {
        observable.addObserverToChannel(Channel.LOGIN, observer);
    }
}