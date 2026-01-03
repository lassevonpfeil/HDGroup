package org.lassevonpfeil.hdgroup.controller;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.lassevonpfeil.hdgroup.command.SetMarkerCommand;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import org.lassevonpfeil.hdgroup.model.DocumentSession;
import org.lassevonpfeil.hdgroup.model.Page;
import org.lassevonpfeil.hdgroup.service.DocumentLoader;
import org.lassevonpfeil.hdgroup.service.MarkerService;
import org.lassevonpfeil.hdgroup.service.PageNavigator;
import org.lassevonpfeil.hdgroup.service.UndoManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    //@FXML
    //private ImageView imageView;
    @FXML private Label documentName;
    @FXML private Label pageLabel;
    @FXML private Label markerIndicator;
    @FXML private ScrollPane scrollPane;
    @FXML private ImageView imageView;

    private double zoomFactor = 1.0;
    private static final double ZOOM_STEP = 0.1;


    private DocumentSession session;
    private PageNavigator navigator;
    private MarkerService markerService = new MarkerService();
    private UndoManager undoManager = new UndoManager();
    private DocumentLoader documentLoader = new DocumentLoader();
    // MainController.java
    private final List<Integer> markers = new ArrayList<>();


    public void loadDocument(File file) throws IOException {

        // 🔹 HIER: altes Dokument schließen
        if (session != null && session.getDocument() != null) {
            session.getDocument().close();
        }

        // 🔹 neues Dokument laden
        session = documentLoader.load(file);
        navigator = new PageNavigator(session.getPages());
        showCurrentPage();
    }


    private void handleKeyPressed(KeyEvent e) {

        switch (e.getCode()) {
            case RIGHT:
            case PAGE_DOWN:
                nextPage();
                break;

            case LEFT:
            case PAGE_UP:
                previousPage();
                break;

            case M:
                setMarker();
                break;

            case Z:
                if (e.isControlDown()) undo();
                break;

            case Y:
                if (e.isControlDown()) redo();
                break;

            default:
                // nichts
        }
    }


    @FXML
    public void initialize() {
        scrollPane.setOnScroll(event -> {
            if (event.isControlDown()) {
                if (event.getDeltaY() > 0) {
                    zoomFactor += ZOOM_STEP;
                } else {
                    zoomFactor = Math.max(0.1, zoomFactor - ZOOM_STEP);
                }
                imageView.setScaleX(zoomFactor);
                imageView.setScaleY(zoomFactor);
                event.consume();
            }
        });
        scrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            imageView.setFitWidth(newVal.getWidth());
            imageView.setFitHeight(newVal.getHeight());
        });

    }


    @FXML
    void nextPage() {
        if (navigator.next()) showCurrentPage();
    }

    @FXML
    void previousPage() {
        if (navigator.previous()) showCurrentPage();
    }

    @FXML
    void setMarker() {

        int index = navigator.getIndex();
        undoManager.execute(
                new SetMarkerCommand(markerService, index)
        );

        updateMarkerIndicator();

        // automatisch weiter
        if (navigator.next()) {
            showCurrentPage();
        }
    }

    @FXML
    void undo() {
        undoManager.undo();
        updateMarkerIndicator();
    }

    @FXML
    void redo() {
        undoManager.redo();
        updateMarkerIndicator();
    }

    @FXML
    private void openPdf() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("PDF auswählen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Dateien", "*.pdf")
        );

        File file = chooser.showOpenDialog(scrollPane.getScene().getWindow());
        if (file == null) return;

        showDocumentName(file);

        try {
            loadDocument(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exportPdfs() {

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Export-Ordner auswählen");

        File dir = chooser.showDialog(scrollPane.getScene().getWindow());
        if (dir == null) return;

        try {
            exportMarkedPdfs(dir);

            new Alert(
                    Alert.AlertType.INFORMATION,
                    "Export erfolgreich abgeschlossen"
            ).showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(
                    Alert.AlertType.ERROR,
                    "Fehler beim Export:\n" + e.getMessage()
            ).showAndWait();
        }
    }




    private void showDocumentName(File file) {
        documentName.setText(file.getName());
    }

    private Image renderPage(Page page, float dpi) throws IOException {

        PDFRenderer renderer = new PDFRenderer(session.getDocument());
        BufferedImage img = renderer.renderImageWithDPI(page.getIndex(), dpi);

        return SwingFXUtils.toFXImage(img, null);
    }




    private void showCurrentPage() {
        try {
            Page page = navigator.current();
            imageView.setImage(renderPage(page, 150)); // Vorschau

            pageLabel.setText(
                    (navigator.getIndex() + 1) + " / " + navigator.total()
            );

            updateMarkerIndicator();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void updateMarkerIndicator() {
        markerIndicator.setVisible(
                markerService.hasMarker(navigator.getIndex())
        );
    }

    private void addPageToPdf(PDDocument doc, Page page) throws IOException {

        PDFRenderer renderer = new PDFRenderer(session.getDocument());

        BufferedImage img =
                renderer.renderImageWithDPI(page.getIndex(), 300); // Export-DPI

        PDPage pdfPage = new PDPage();
        doc.addPage(pdfPage);

        PDImageXObject ximage =
                LosslessFactory.createFromImage(doc, img);

        try (PDPageContentStream content =
                     new PDPageContentStream(doc, pdfPage)) {

            content.drawImage(
                    ximage,
                    0,
                    0,
                    pdfPage.getMediaBox().getWidth(),
                    pdfPage.getMediaBox().getHeight()
            );
        }
    }



    private void exportMarkedPdfs(File outputDir) throws IOException {

        if (session == null || session.getPages().isEmpty()) return;

        PDDocument currentDoc = null;
        int markerCounter = 0;

        List<Page> pages = session.getPages(); // 🔹 EINMAL sauber holen

        for (int i = 0; i < pages.size(); i++) {

            boolean isMarker = markerService.hasMarker(i);

            if (isMarker) {

                if (currentDoc != null) {
                    currentDoc.save(
                            new File(outputDir, "Dokument_" + markerCounter + ".pdf")
                    );
                    currentDoc.close();
                }

                markerCounter++;
                currentDoc = new PDDocument();
            }

            if (currentDoc != null) {
                addPageToPdf(currentDoc, pages.get(i));
            }
        }

        if (currentDoc != null) {
            currentDoc.save(
                    new File(outputDir, "Dokument_" + markerCounter + ".pdf")
            );
            currentDoc.close();
        }
    }


}
