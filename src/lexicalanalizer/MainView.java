package lexicalanalizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class MainView extends JFrame implements ActionListener {

    JMenuBar mb;
    JMenu mFile;
    JMenuItem miOpenFile;

    JFileChooser fcInput, fcOutput;
    File fSourceCode;
    FileWriter saved, savedOutput;
    Scanner scanner;
    JPanel pane;
    JTextArea taSourceCode, taConsole;
    JTable tAnalysisResult;
    DefaultTableModel dtmAnalysisResult;

    JScrollPane spSourceCode, spAnalysisResult, spConsole;

    JButton bAnalyze, bClean, bExportAnalysisResult;

    LexicalAnalyzer lexicalAnalyzer;
    List<Token> tokens;

    public MainView() {
        config();
        initComponents();
        taConsole.append("Iniciado con exito");

    }

    private void config() {
        this.setTitle("Analalizador Léxico v0.0.1");
        this.setSize(1200, 660);
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        mb = new JMenuBar();
        mFile = new JMenu("Archivo");
        miOpenFile = new JMenuItem("Abrir");
        miOpenFile.addActionListener(this);
        mFile.add(miOpenFile);
        mb.add(mFile);

        pane = new JPanel();
        pane.setLayout(null);

        fcInput = new JFileChooser();
        fcInput.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fcInput.setFileFilter(new FileNameExtensionFilter("Archivos Java", "java"));

        this.setJMenuBar(mb);

        pane.add(new JLabel("Código fuente")).setBounds(25, 20, 100, 25);
        tAnalysisResult = new JTable();

        spAnalysisResult = new JScrollPane(tAnalysisResult);
        pane.add(spAnalysisResult).setBounds(625, 50, 550, 350);

        pane.add(new JLabel("Resultado análisis")).setBounds(625, 20, 130, 25);
        taSourceCode = new JTextArea();
        spSourceCode = new JScrollPane(taSourceCode);
        pane.add(spSourceCode).setBounds(25, 50, 550, 350);

        bAnalyze = new JButton("Analizar");
        pane.add(bAnalyze).setBounds(25, 420, 100, 25);
        bAnalyze.addActionListener(this);

        bClean = new JButton("Limpiar");
        pane.add(bClean).setBounds(145, 420, 100, 25);
        bClean.addActionListener(this);

        bExportAnalysisResult = new JButton("Exportar .txt");
        pane.add(bExportAnalysisResult).setBounds(625, 420, 120, 25);
        bExportAnalysisResult.addActionListener(this);

        taConsole = new JTextArea();
        spConsole = new JScrollPane(taConsole);
        pane.add(spConsole).setBounds(25, 470, 1150, 100);

        lexicalAnalyzer = new LexicalAnalyzer();
        tokens = new ArrayList<>();

        bAnalyze.setEnabled(false);
        bClean.setEnabled(false);
        bExportAnalysisResult.setEnabled(false);

        loadTable(tokens);

        this.setContentPane(pane);

    }

    private void loadTable(List<Token> tokens) {
        dtmAnalysisResult = new DefaultTableModel();
        dtmAnalysisResult.addColumn("Token");
        dtmAnalysisResult.addColumn("Lexema");
        dtmAnalysisResult.addColumn("Linea");
        dtmAnalysisResult.addColumn("Columna");
        Object row[] = new Object[4];

        for (Token t : tokens) {
            row[0] = t.getToken();
            row[1] = t.getLexeme();
            row[2] = t.getLine();
            row[3] = t.getColumn();

            if ("ERROR".equals(t.getLexeme())) {
                taConsole.append("\nError en Linea: " + t.getLine() + " Columna: " + t.getColumn());
            }

            dtmAnalysisResult.addRow(row);
        }

        tAnalysisResult.setModel(dtmAnalysisResult);
    }

    private String loadResultText(List<Token> tokens) {
        String txt = "";
        txt = tokens.stream().map(t -> t.getToken() + "|" + t.getLexeme() + "\n").reduce(txt, String::concat);
        return txt;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origin = e.getSource();
        if (origin == miOpenFile) {
            switch (fcInput.showOpenDialog(this)) {
                case 0:
                    taConsole.append("\nArchivo seleccionado");
                    fSourceCode = fcInput.getSelectedFile();

                    taConsole.append("\nObteniendo " + fSourceCode.getAbsolutePath());
                    taSourceCode.setText("");
                    try {
                        scanner = new Scanner(fSourceCode);
                        while (scanner.hasNext()) {
                            taSourceCode.insert(scanner.nextLine() + "\n", taSourceCode.getText().length());
                        }

                        bAnalyze.setEnabled(true);
                        bClean.setEnabled(true);
                    } catch (FileNotFoundException ex) {
                    }

                    break;
            }

        } else if (origin == bAnalyze) {

            taConsole.append("\nAnalizando código fuente");
            try {
                saved = new FileWriter(fcInput.getSelectedFile());
                saved.write(taSourceCode.getText());
                saved.close();
                lexicalAnalyzer.setSourceCode(fSourceCode);
                tokens = lexicalAnalyzer.analyze();
                loadTable(tokens);
                taConsole.append("\nCódigo fuente analizado con exito");
                bExportAnalysisResult.setEnabled(true);
            } catch (Exception ex) {
                taConsole.append("\nError al analizar código fuente");
            }

        } else if (origin == bClean) {
            taConsole.append("\nLimpiando código fuente");
            taSourceCode.setText("");
            fSourceCode = null;
            tokens = new ArrayList<>();
            loadTable(tokens);
            lexicalAnalyzer.setSourceCode(fSourceCode);
            bAnalyze.setEnabled(false);
            bClean.setEnabled(false);
            bExportAnalysisResult.setEnabled(false);
        } else if (origin == bExportAnalysisResult) {
            taConsole.append("\nGenerando txt");
            fcOutput = new JFileChooser();
            fcOutput.setFileFilter(new FileNameExtensionFilter("Archivo de texto", "txt"));
            fcOutput.showSaveDialog(this);
            if (fcOutput.getSelectedFile() != null) {
                try {
                    savedOutput = new FileWriter(fcOutput.getSelectedFile());
                    savedOutput.write(loadResultText(tokens));
                    savedOutput.close();
                    taConsole.append("\ntxt generado con exito");
                } catch (IOException ex) {
                    taConsole.append("\ntxt txt no generado");

                }
            }
        }

    }

}
