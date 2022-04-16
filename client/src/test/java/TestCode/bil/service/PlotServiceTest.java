package TestCode.bil.service;

import Controller.service.PlotService;
import Controller.execptions.ServerConnectionException;
import model.connection.ClientConnection;
import model.Plot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlotServiceTest {
    private PlotService service;
    private Plot plot = new Plot(1L, 10, 10, 0, 0);
    private Plot plot2 = new Plot(1L, 20, 20, 0, 0);

    @Mock
    private ClientConnection client;

    @Before
    public void setUp() throws Exception {
        service = new PlotService(client);
    }

    @Test
    public void testFindById() {
        when(client.receiveFromServer()).thenReturn(plot);

        assertEquals("correct return", plot, service.findById(1L));

        verify(client).sendToServer("findById#Plot");
        verify(client).sendToServer(1L);
    }

    @Test
    public void testFindAll() {
        List<Plot> list = Arrays.asList(plot);
        when(client.receiveFromServer()).thenReturn(list);

        assertEquals("correct return", list, service.findAll());

        verify(client).sendToServer("findAll#Plot");
    }

    @Test
    public void testSave() {

        when(client.receiveFromServer()).thenReturn(plot2);

        assertEquals("correct return", plot2, service.save(plot));

        verify(client).sendToServer("save#Plot");
        verify(client).sendToServer(plot);
    }

    @Test(expected = ServerConnectionException.class)
    public void testUpdateException() {
        when(client.receiveFromServer()).thenThrow(new ServerConnectionException("server down"));

        service.update(1L, plot);

        verify(client).sendToServer("update#Plot");
        verify(client).sendToServer(1L);
        verify(client).sendToServer(plot);
    }

    @Test
    public void testUpdate() {
        service.update(1L, plot);

        verify(client).sendToServer("update#Plot");
        verify(client).sendToServer(1L);
        verify(client).sendToServer(plot);
    }

    @Test
    public void testDelete() {
        service.delete(1L);

        verify(client).sendToServer("delete#Plot");
        verify(client).sendToServer(1L);
    }
}