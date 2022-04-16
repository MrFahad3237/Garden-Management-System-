package bll.util;

import model.Garden;
import model.Plant;
import model.PlantedPlant;
import model.Plot;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ReportIntegrationTest {
    private ReportGenerator reportGenerator;
    private List<PlantedPlant> plantedPlants = new ArrayList<>();
    private List<Plot> plots = new ArrayList<>();
    private Garden garden;

    @Before
    public void setUp() throws Exception {
        loadData();
        garden = new Garden(new HashSet<>(plots), new HashSet<>(plantedPlants));
        reportGenerator = new ReportGenerator();
    }

    @Test
    public void testReportPdf() {
        final String selectedPath = "src/main/resources/garden.pdf";
        File file = new File(selectedPath);
        if (file.exists()) {
            file.delete();
        }

        reportGenerator.generateReport(ReportType.PDF).generateFileWithGarden(selectedPath, plantedPlants, plots, garden);
        assertTrue("file was saved", file.exists());
    }

    @Test
    public void testReportTxt() {
        final String selectedPath = "src/main/resources/garden.txt";
        File file = new File(selectedPath);
        if (file.exists()) {
            file.delete();
        }

        reportGenerator.generateReport(ReportType.TXT).generateFileWithGarden(selectedPath, plantedPlants, plots, garden);
        assertTrue("file was saved", new File(selectedPath).exists());
    }

    public void loadData() {
        Plant plant = new Plant("a", 1, 1, 1, 5);
        Plant plant2 = new Plant("b", 1, 1, 1, 0);

        plantedPlants.add(new PlantedPlant(0, 0, plant));
        plantedPlants.add(new PlantedPlant(2, 2, plant2));
        plantedPlants.add(new PlantedPlant(3, 3, plant2));

        plots.add(new Plot(6, 4, 0, 0));
        plots.add(new Plot(4, 2, 10, 10));
        plots.add(new Plot(2, 3, 0, 10));
        plots.add(new Plot(4, 2, 10, 0));

        plots.add(new Plot(2, 2, 6, 6));
        plots.add(new Plot(2, 2, 9, 6));
        plots.add(new Plot(4, 4, 4, 9));
    }
}
