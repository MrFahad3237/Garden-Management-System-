package TestCode.model;

import model.Plot;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PlotTest {
    private Plot plot1 = new Plot(10, 10, 0, 0);
    private Plot plot2 = new Plot(10, 10, 0, 0);
    private Plot plot3 = new Plot(10, 10, 0, 0);

    @Before
    public void setUp() throws Exception {
        plot1.setId(1L);
        plot2.setId(1L);
        plot3.setId(2L);
    }

    @Test
    public void testToString() {
        String toString = plot1.toString();
        assertTrue("toString() must contain these infos",toString.contains("width") && toString.contains("height"));
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(plot1, plot3);
    }

    @Test
    public void testNotEqualsNull() {
        assertNotEquals(plot1, null);
    }

    @Test
    public void testEqualsSameObject() {
        assertEquals("same object", plot1, plot1);
    }

    @Test
    public void testEquals() {
        assertEquals("same id", plot1, plot2);
    }

    @Test
    public void testHashCode() {
        assertSame("same id",plot1.hashCode(), plot2.hashCode());
    }
}