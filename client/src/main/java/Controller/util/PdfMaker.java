package Controller.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;
import model.Garden;
import model.PlantedPlant;
import model.Plot;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.logging.Level;

public class PdfMaker implements FileMaker {

    @Override
    public void generateFileWithGarden(String oldPath, List<PlantedPlant> plantedPlants, List<Plot> plots, Garden garden) {
        try {
            String path = oldPath;

            //check if contains .txt extension
            if (path.length() > 3 && !path.substring(path.length() - 4).equals(".pdf")) {
                path += ".pdf";
            }
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            Font redFont = FontFactory.getFont(FontFactory.COURIER, 16, Font.BOLD, new CMYKColor(0, 255, 0, 0));
            Font smallFont = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);

            document.add(new Paragraph("Garden Representation", redFont));

            document.add(new Paragraph("Garden width=" + garden.getWidth() + " height=" + garden.getHeight(), smallFont));

            document.add(Chunk.NEWLINE);
            Chunk chunk = new Chunk("Plots", redFont);
            document.add(chunk);

            com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);

            for (Plot plot : plots) {
                list.add(new ListItem(plot.toString(), smallFont));
            }
            document.add(list);

            document.add(Chunk.NEWLINE);
            chunk = new Chunk("Plants which are planted in the garden:", redFont);
            document.add(chunk);

            list = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
            for (PlantedPlant plantedPlant : plantedPlants) {
                list.add(plantedPlant.toString());
            }
            document.add(list);

            document.close();

        } catch (DocumentException | FileNotFoundException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }
}
