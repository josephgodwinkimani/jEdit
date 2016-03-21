/*
 * The MIT License
 *
 * Copyright 2016 Joseph Godwin Kimani.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.schongeproductions.texteditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import Licenses.*;
import java.awt.Image;
import java.awt.Toolkit;


@SuppressWarnings("serial")
public class EditorGUI extends JFrame implements ActionListener {

     public static void main(String[]args) {
        new EditorGUI();
        
    }

    //============================================
    // FIELDS
    //============================================

    // Menus
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu aboutMenu;
    private JMenuItem newFile, openFile, saveFile, saveAsFile, pageSetup, printFile, exit;
    private JMenuItem undoEdit, redoEdit, selectAll, copy, paste, cut;
    private JMenuItem aboutMe, license;


    // Actual Window ==================
    private JFrame editorWindow;
   //==================================
    // Where to edit text..
    private Border textBorder;
    private JScrollPane scroll;
    private JTextArea textArea;
    private Font textFont;

    // Window
    private JFrame window;

    // Printing
    private PrinterJob job;
    public PageFormat format;

    // Is File Saved/Opened
    private boolean opened = false;
    private boolean saved = false;

    // Record Open File for quick saving
    private File openedFile;

    // Undo manager for managing the storage of the undos
    // so that the can be redone if requested
    private UndoManager undo;
   
    //============================================
    // CONSTRUCTOR
    //============================================

    public EditorGUI() {
        super("jEdit");

       
        fileMenu();
        editMenu();
        aboutMenu();

        
        createTextArea();

        
        undoMan();

        
        createEditorWindow();
    }

    private JFrame createEditorWindow() {
        editorWindow = new JFrame("jEdit");
        editorWindow.setVisible(true);
        editorWindow.setExtendedState(Frame.MAXIMIZED_BOTH);
        editorWindow.setDefaultCloseOperation(EXIT_ON_CLOSE);
        editorWindow.setJMenuBar(createMenuBar());
        editorWindow.add(scroll, BorderLayout.CENTER);
        editorWindow.pack();
        editorWindow.setLocationRelativeTo(null);
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/src/res/favicon.ico"));
        setIconImage(image);
        return editorWindow;
    }

    private JTextArea createTextArea() {
        textArea = new JTextArea(50, 70);
        textArea.setEditable(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(textBorder, BorderFactory.createEmptyBorder(2, 5, 0, 0)));

        textFont = new Font("Courier", 0, 14);
        textArea.setFont(textFont);

        scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        return textArea;        
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(aboutMenu);
        
        return menuBar;
    }

    private UndoManager undoMan() {
        // Listener for undo and redo functions to document
        undo = new UndoManager();
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {

            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo.addEdit(e.getEdit());
            }
        });

        return undo;
    }

    private void fileMenu() {
        // Create File Menu
        fileMenu = new JMenu("File");
        fileMenu.setPreferredSize(new Dimension(40, 20));

        // Add file menu items
        newFile = new JMenuItem("New");
        newFile.addActionListener(this);
        newFile.setPreferredSize(new Dimension(100, 20));
        newFile.setEnabled(true);

        openFile = new JMenuItem("Open...");
        openFile.addActionListener(this);
        openFile.setPreferredSize(new Dimension(100, 20));
        openFile.setEnabled(true);

        saveFile = new JMenuItem("Save");
        saveFile.addActionListener(this);
        saveFile.setPreferredSize(new Dimension(100, 20));
        saveFile.setEnabled(true);

        saveAsFile = new JMenuItem("Save As...");
        saveAsFile.addActionListener(this);
        saveAsFile.setPreferredSize(new Dimension(100, 20));
        saveAsFile.setEnabled(true);

        pageSetup = new JMenuItem("Page Setup...");
        pageSetup.addActionListener(this);
        pageSetup.setPreferredSize(new Dimension(100, 20));
        pageSetup.setEnabled(true);

        printFile = new JMenuItem("Print...");
        printFile.addActionListener(this);
        printFile.setPreferredSize(new Dimension(100, 20));
        printFile.setEnabled(true);

        exit = new JMenuItem("Exit");
        exit.addActionListener(this);
        exit.setPreferredSize(new Dimension(100, 20));
        exit.setEnabled(true);

        // Add items to menu
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(saveAsFile);
        fileMenu.add(pageSetup);
        fileMenu.add(printFile);
        fileMenu.add(exit);
    }

    private void editMenu() {
        editMenu = new JMenu("Edit");
        editMenu.setPreferredSize(new Dimension(40, 20));

        // Add file menu items
        undoEdit = new JMenuItem("Undo");
        undoEdit.addActionListener(this);
        undoEdit.setPreferredSize(new Dimension(100, 20));
        undoEdit.setEnabled(true);

        redoEdit = new JMenuItem("Redo");
        redoEdit.addActionListener(this);
        redoEdit.setPreferredSize(new Dimension(100, 20));
        redoEdit.setEnabled(true);

        selectAll = new JMenuItem("Select All");
        selectAll.addActionListener(this);
        selectAll.setPreferredSize(new Dimension(100, 20));
        selectAll.setEnabled(true);

        copy = new JMenuItem("Copy");
        copy.addActionListener(this);
        copy.setPreferredSize(new Dimension(100, 20));
        copy.setEnabled(true);

        paste = new JMenuItem("Paste");
        paste.addActionListener(this);
        paste.setPreferredSize(new Dimension(100, 20));
        paste.setEnabled(true);

        cut = new JMenuItem("Cut");
        cut.addActionListener(this);
        cut.setPreferredSize(new Dimension(100, 20));
        cut.setEnabled(true);

        // Add items to menu
        editMenu.add(undoEdit);
        editMenu.add(redoEdit);
        editMenu.add(selectAll);
        editMenu.add(copy);
        editMenu.add(paste);
        editMenu.add(cut);
    }

    private void aboutMenu() {
        
        aboutMenu = new JMenu("About");
        aboutMenu.setPreferredSize(new Dimension(40, 20));
        
        aboutMe = new JMenuItem("Author");
        aboutMe .addActionListener(this);
        aboutMe .setPreferredSize(new Dimension(100, 20));
        aboutMe .setEnabled(true);
        
        license = new JMenuItem("License");
        license .addActionListener(this);
        license .setPreferredSize(new Dimension(100, 20));
        license .setEnabled(true);
        
        aboutMenu.add(aboutMe);
        aboutMenu.add(license);
        
    }
    // Method for saving files - Removes duplication of code
    private void saveFile(File filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(textArea.getText());
            writer.close();
            saved = true;
            window.setTitle("JavaText - " + filename.getName());
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    // Method for quick saving files
    private void quickSave(File filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(textArea.getText());
            writer.close();
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    // Method for opening files
    private void openingFiles(File filename) {
        try {
            openedFile = filename;
            FileReader reader = new FileReader(filename);
            textArea.read(reader, null);
            opened = true;
            window.setTitle("jEdit - " + filename.getName());
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if(event.getSource() == newFile) {
            new EditorGUI();
        } else if(event.getSource() == openFile) {
            JFileChooser open = new JFileChooser();
            open.showOpenDialog(null);
            File file = open.getSelectedFile();                
            openingFiles(file);
        } else if(event.getSource() == saveFile) {
            JFileChooser save = new JFileChooser();
            File filename = save.getSelectedFile();
            if(opened == false && saved == false) {
                save.showSaveDialog(null);
                int confirmationResult;
                if(filename.exists()) {
                    confirmationResult = JOptionPane.showConfirmDialog(saveFile, "Replace existing file?");
                    if(confirmationResult == JOptionPane.YES_OPTION) {
                        saveFile(filename);                        
                    }
                } else {
                    saveFile(filename);
                }
            } else {
                quickSave(openedFile);
            }
        } else if(event.getSource() == saveAsFile) {
            JFileChooser saveAs = new JFileChooser();
            saveAs.showSaveDialog(null);
            File filename = saveAs.getSelectedFile();
            int confirmationResult;
            if(filename.exists()) {
                confirmationResult = JOptionPane.showConfirmDialog(saveAsFile, "Replace existing file?");
                if(confirmationResult == JOptionPane.YES_OPTION) {
                    saveFile(filename);                        
                }
            } else {
                saveFile(filename);
            }
        } else if(event.getSource() == pageSetup) {
            job = PrinterJob.getPrinterJob();
            format = job.pageDialog(job.defaultPage());    
        } else if(event.getSource() == printFile) {
            job = PrinterJob.getPrinterJob();
            if(job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException err) {
                    err.printStackTrace();
                }
            }
        } else if(event.getSource() == exit) {
            System.exit(0);
        } else if(event.getSource() == undoEdit) {
            try {
                undo.undo();
            } catch(CannotUndoException cu) {
                cu.printStackTrace();
            }
        } else if(event.getSource() == redoEdit) {
            try {
                undo.redo();
            } catch(CannotUndoException cur) {
                cur.printStackTrace();
            }
        } else if(event.getSource() == selectAll) {
            textArea.selectAll();
        }  else if(event.getSource() == copy) {
            textArea.copy();
        } else if(event.getSource() == paste) {
            textArea.paste();
        } else if(event.getSource() == cut) {
            textArea.cut();
        } else if(event.getSource() == aboutMe) {
           About frame = new About();
           frame.setVisible(true);
           setDefaultCloseOperation(About.DISPOSE_ON_CLOSE);
        } else if(event.getSource () == license) {
            License frame = new License ();
            frame.setVisible(true);
            setDefaultCloseOperation(Licnese.DISPOSE_ON_CLOSE);
        }
        
    }

    //============================================
    // GETTERS AND SETTERS
    //============================================

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextArea text) {
        textArea = text;
    }
}