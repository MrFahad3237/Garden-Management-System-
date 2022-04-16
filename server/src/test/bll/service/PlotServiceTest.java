package bll.service;

import gateway.GenericGateway;
import model.Plot;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlotServiceTest {
    private Plot plot = new Plot(10, 10, 0, 0);
    private Plot plot2 = new Plot(20, 20, 0, 0);

    @Mock
    private GenericGateway<Plot> gateway;

    private PlotService service;

    @Before
    public void setUp() throws Exception {
        service = new PlotService(gateway);
        plot.setId(1L);
        plot2.setId(1L);
    }

    @Test
    public void testSave() {
        when(gateway.save(plot)).thenReturn(plot2);

        assertEquals("correct result", plot2, service.save(plot));
    }

}