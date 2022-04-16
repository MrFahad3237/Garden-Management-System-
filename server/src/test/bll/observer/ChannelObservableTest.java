package bll.observer;

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
    public void testAddObserverToChannel() {
        insertObserver();

        assertTrue("bll.observer was added to map", observable.observers.get(Channel.LOGIN).contains(observer));
    }

    @Test(expected = Exception.class)
    public void testAddObserverToChannelNull() {
        observable.addObserverToChannel(Channel.LOGIN, null);

        assertFalse("bll.observer was not added to map", observable.observers.containsKey(Channel.LOGIN));
    }

    @Test
    public void testAddObserverToChannelContainsDuplicate() {
        observable.addObserverToChannel(Channel.LOGIN, observer);
        observable.addObserverToChannel(Channel.LOGIN, observer);

        assertEquals("duplicate was not added", 1, observable.observers.get(Channel.LOGIN).size());
    }

    @Test
    public void testAddObserverToChannelContainsNotDuplicate() {
        ChannelObserver observer2 = mock(ChannelObserver.class);

        observable.addObserverToChannel(Channel.LOGIN, observer);
        observable.addObserverToChannel(Channel.LOGIN, observer2);

        assertTrue("new bll.observer was added", observable.observers.get(Channel.LOGIN).contains(observer2));
    }

    @Test
    public void testRemoveObserverFromChannel() {

        observable.addObserverToChannel(Channel.LOGIN, observer);
        observable.removeObserverFromChannel(Channel.LOGIN, observer);

        assertFalse("bll.observer was removed", observable.observers.get(Channel.LOGIN).contains(observer));
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