/*
 * @name Package Signer 2.2
 * @author Humble Potato II
 *
 * External libraries used:
 * - @library Apache Commons IO
 *   @version 2.5
 *   @license Apache 2.0
 *   @link http://commons.apache.org/proper/commons-io/
 * 
 * - @library iHarder FileDrop
 *   @version 1.1
 *   @license Public Domain
 *   @link http://iharder.sourceforge.net/current/java/filedrop/
 */
package com.humblepotato2.packagesigner;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.iharder.dnd.FileDrop;
import org.apache.commons.io.FilenameUtils;

public class PackageSigner extends JFrame {
    
    /**
     * Initialize variables.
     */
    private DragAndDrop fileDrop;
    private GroupLayout layoutA, layoutMain;
    private ImageIcon icon;
    private JButton jButton1, jButton2, jButton3, jButton4, jButton5;
    private JFileChooser jFileChooser1, jFileChooser2;
    private JMenu jMenu1, jMenu2;
    private JMenuBar jMenuBar;
    private JMenuItem jMenuItem1, jMenuItem2, jMenuItem3, jMenuItem4, jMenuItem5, jMenuItem6;
    private JPanel jPanel;
    private JPopupMenu.Separator jSeparator;
    private JProgressBar jProgressBar;
    private JScrollPane jScrollPane;
    private JTextArea jTextArea;
    private JToolBar jToolBar;
    private KeyStroke keyStroke;
    private OutputSelection outputSelection;
    private PackageSelection packageSelection;
    private SignPackage signPackages;
    private String date, message;
    
    /**
     * Class constructor
     */
    public PackageSigner() {
        initUI();  // #Initialize User Interface
    }
    
    /**
     * #Initialize User Interface
     */
    private void initUI() {
        
        this.setTitle("Package Signer 2.2");
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        initComponents();  // #Initialize components
        
        this.setJMenuBar(jMenuBar);
        this.getContentPane().setLayout(layoutMain);
        this.pack();
    }
    
    /**
     * #Initialize components
     */
    private void initComponents() {

        initObjects();  // #Initialize objects
        configureSwingMenus();  // #Swing menus configuration
        configureToolbarActions();  // #Toolbar actions configuration
        configureTextBox();  // #Text box configuration
        assembleLayout();  // #Layout configuration
    }         
    
    /**
     * #Initialize objects
     */
    private void initObjects() {
        
        date = new SimpleDateFormat(" MM/dd/yyyy - ").format(new Date());
        
        fileDrop = new DragAndDrop();
        
        jButton1 = new JButton();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jButton4 = new JButton();
        jButton5 = new JButton();
        jFileChooser1 = new JFileChooser();
        jFileChooser2 = new JFileChooser();
        jMenu1 = new JMenu();
        jMenu2 = new JMenu();
        jMenuBar = new JMenuBar();
        jMenuItem1 = new JMenuItem();
        jMenuItem2 = new JMenuItem();
        jMenuItem3 = new JMenuItem();
        jMenuItem4 = new JMenuItem();
        jMenuItem5 = new JMenuItem();
        jMenuItem6 = new JMenuItem();
        jPanel = new JPanel();
        jProgressBar = new JProgressBar();
        jScrollPane = new JScrollPane();
        jSeparator = new JPopupMenu.Separator();
        jTextArea = new JTextArea();
        jToolBar = new JToolBar();
        
        outputSelection = new OutputSelection();
        packageSelection = new PackageSelection();
    }
    
    /**
     * #Swing menus configuration
     */
    private void configureSwingMenus() {
        
        /* JMenuItems */
        // Package selection (jMenuItem1)
        jMenuItem1.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_archive_18.png")));
        jMenuItem1.setMnemonic('P');
        jMenuItem1.setText("Package   ");
        jMenuItem1.setToolTipText("Browse for new package(s)");
        jMenuItem1.setAccelerator(keyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                packageSelection.choosePackages();
            }
        });
        
        // Output directory selection (jMenuItem2)
        jMenuItem2.setEnabled(false);
        jMenuItem2.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_folder_18.png")));
        jMenuItem2.setMnemonic('O');
        jMenuItem2.setText("Output");
        jMenuItem2.setToolTipText("Browse for an output directory");
        jMenuItem2.setAccelerator(keyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputSelection.assignOutput();
            }
        });
        
        // Signing package (jMenuItem3)
        jMenuItem3.setEnabled(false);
        jMenuItem3.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_sign_18.png")));
        jMenuItem3.setMnemonic('S');
        jMenuItem3.setText("Sign");
        jMenuItem3.setToolTipText("Sign the package(s)");
        jMenuItem3.setAccelerator(keyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				
                JMenuItem source = (JMenuItem) e.getSource();
                
                if (source == jMenuItem3) {
                    jMenuItem3.setEnabled(false);
                    jButton3.setEnabled(false);
                    jButton4.setEnabled(true);
                }
                
                signPackages = new SignPackage();
                signPackages.execute();
                jProgressBar.setIndeterminate(true);
            }
        });
        
        // Exit program (jMenuItem4)
        jMenuItem4.setMnemonic('X');
        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Instructions dialog (jMenuItem5)
        jMenuItem5.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_help_18.png")));
        jMenuItem5.setMnemonic('I');
        jMenuItem5.setText("?");
        jMenuItem5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message = "Instructions\n"+
                          "1. Specify the Package(s) you want to sign.\n"+
                          "2. Choose an Output directory where to save it.\n"+
                          "3. Hit the Sign button to start signing it.\n\n"+
                          "Shortcut keys\n"+
                          "- Package selection > ⌘ or CTRL + P (for Windows/Linux/Mac)\n"+
                          "- Output selection > ⌘ or CTRL + O (for Windows/Linux/Mac)\n"+
                          "- Signing package > ⌘ or CTRL + S (for Windows/Linux/Mac)\n\n"+
                          "Drag & Drop\n"+
                          "Supports dragging and dropping of package(s) into the text box.   \n\n";
                icon = new ImageIcon(getClass().getResource("/drawable/ic_help_36.png"));
                JOptionPane.showMessageDialog(rootPane, message, "Help", JOptionPane.QUESTION_MESSAGE, icon);
            }
        });
        
        // About dialog (jMenuItem6)
        jMenuItem6.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_info_18.png")));
        jMenuItem6.setMnemonic('A');
        jMenuItem6.setText("About   ");
        jMenuItem6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message = "Package Signer 2.2\n"+
                          "Created by Humble Potato II";
                icon = new ImageIcon(getClass().getResource("/drawable/ic_info_36.png"));
                JOptionPane.showMessageDialog(rootPane, message, "About", JOptionPane.INFORMATION_MESSAGE, icon);
            }
        });
        
        
        /* Add JMenuItems to JMenus */
        // File (jMenu1)
        jMenu1.setText("File");
        jMenu1.add(jMenuItem1);  // - Package
        jMenu1.add(jMenuItem2);  // - Output
        jMenu1.add(jMenuItem3);  // - Sign
        jMenu1.add(jSeparator);  // ----------
        jMenu1.add(jMenuItem4);  // - Exit
        
        // Help (jMenu2)
        jMenu2.setText("Help");
        jMenu2.add(jMenuItem5);  // - Instructions
        jMenu2.add(jMenuItem6);  // - About
        
        
        /* Add JMenus to JMenuBar */
        // JMenuBar (jMenu1 + jMenu2)
        jMenuBar.add(jMenu1);  // File
        jMenuBar.add(jMenu2);  // Help
    }
    
    /**
     * #Toolbar actions configuration
     */
    private void configureToolbarActions() {
        
        /* JButtons */
        // Package selection (jButton1)
        jButton1.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_archive_36.png")));
        jButton1.setToolTipText("Browse for new package(s)");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                packageSelection.choosePackages();
            }
        });
        
        // Output directory selection (jButton2)
        jButton2.setEnabled(false);
        jButton2.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_folder_36.png")));
        jButton2.setToolTipText("Browse for an output directory");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputSelection.assignOutput();
            }
        });
        
        // Signing package (jButton3)
        jButton3.setEnabled(false);
        jButton3.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_sign_36.png")));
        jButton3.setToolTipText("Sign the package(s)");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                
                if (source == jButton3) {
                    jMenuItem3.setEnabled(false);
                    jButton3.setEnabled(false);
                    jButton4.setEnabled(true);
                }
                
                signPackages = new SignPackage();
                signPackages.execute();
                jProgressBar.setIndeterminate(true);
            }
        });
        
        // Cancel sign process (jButton4)
        jButton4.setEnabled(false);
        jButton4.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_cancel_36.png")));
        jButton4.setToolTipText("Cancel signing process");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signPackages.cancel(true);
                signPackages.done();
            }
        });
        
        // Clear log message (jButton5)
        jButton5.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_clear_36.png")));
        jButton5.setToolTipText("Clear log message");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea.setText(null);
            }
        });
        
        
        /* Add JButtons to JToolBar */
        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);
        jToolBar.add(jButton1);  // Package selection
        jToolBar.add(jButton2);  // Output directory selection
        jToolBar.add(jButton3);  // Signing package
        jToolBar.add(jButton4);  // Cancel sign process
        jToolBar.add(jButton5);  // Clear log message
    }
    
    /**
     * #Text box configuration
     */
    private void configureTextBox() {
        
        /* JTextArea & JScrollPane */
        jTextArea.setEditable(false);
        jTextArea.setText(date + "Package Signer version 2.2 \n");
        jScrollPane.setViewportView(jTextArea);
        
        fileDrop.dropPackages();  // Drag and drop packages into text box.
    }
    
    /**
     * #Layout configuration
     */
    private void assembleLayout() {
        
        /* Group JProgressBar and JScrollPane to layoutA */
        layoutA = new GroupLayout(jPanel);
        layoutA.setHorizontalGroup(
            layoutA.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jProgressBar, GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
            .addComponent(jScrollPane, GroupLayout.Alignment.TRAILING)
        );
        layoutA.setVerticalGroup(
            layoutA.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layoutA.createSequentialGroup()
                .addComponent(jScrollPane, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        
        jPanel.setLayout(layoutA);  // Set layoutA as jPanel's layout.
        
        
        /* Group JToolBar and JPanel to layoutMain */
        layoutMain = new GroupLayout(this.getContentPane());
        layoutMain.setHorizontalGroup(
            layoutMain.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layoutMain.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layoutMain.setVerticalGroup(
            layoutMain.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layoutMain.createSequentialGroup()
                .addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        
        /* Create and display the program */
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PackageSigner().setVisible(true);
            }
        });
    }
    
    /**
     * FileDrop (A library used to support drag and drop function)
	 *
	 * Since the Package selection uses only the jFileChooser1
	 * to store the selected files, then we need to pass or set
	 * the dropped packages to jFileChooser1.
     */
    private class DragAndDrop {
        
        protected void dropPackages() {
            
            new FileDrop(null, jTextArea, new FileDrop.Listener() {
                
                @Override
                public void filesDropped(File[] packages) {
                    
                    jFileChooser1.setSelectedFiles(packages);
                    
                    for (int i = 0; i < packages.length; i++) {
                        message = date + "Dropped package included (" + packages[i].getName() + ") \n";
                        jTextArea.append(message);
                    }
                    
                    jMenuItem2.setEnabled(true);
                    jButton2.setEnabled(true);
                }
            });
        }
    }
    
    /**
     * Package selection
     */
    private class PackageSelection {
        
        private FileNameExtensionFilter filter;
        
        protected void choosePackages() {
            
            filter = new FileNameExtensionFilter("(*.apk, *.zip)", "apk", "zip");
            
            jFileChooser1.setCurrentDirectory(new File(System.getProperty("user.dir")));
            jFileChooser1.setAcceptAllFileFilterUsed(false);  // Disable All Files filter.
            jFileChooser1.setMultiSelectionEnabled(true);  // Support for multiple selection.
            jFileChooser1.setFileFilter(filter);  // Sets File extension filter to APK and ZIP only.
            
            int response = jFileChooser1.showOpenDialog(rootPane);
            
            if (response == JFileChooser.APPROVE_OPTION) {
                
                File[] chosenPackages = jFileChooser1.getSelectedFiles();
                
                for (int i = 0; i < chosenPackages.length; i++) {
                    String name = chosenPackages[i].getName();
                    message = date + "Selected package included (" + name + ") \n";
                    jTextArea.append(message);
                }
                
                jMenuItem2.setEnabled(true);
                jButton2.setEnabled(true);
            }
        }
    }
    
    /**
     * Output directory selection
     */
    private class OutputSelection {
        
        protected void assignOutput() {
            
            jFileChooser2.setCurrentDirectory(new File(System.getProperty("user.dir")));
            jFileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  // Show directories only.
            
            int response = jFileChooser2.showOpenDialog(rootPane);
            
            if (response == JFileChooser.APPROVE_OPTION) {
                String path = jFileChooser2.getSelectedFile().getAbsolutePath();
                message = date + "Selected output assigned (" + path + ") \n";
                jTextArea.append(message);
                
                jMenuItem3.setEnabled(true);
                jButton3.setEnabled(true);
            }
        }
    }
    
    /**
     * Signing package
	 *
	 * Using SwingWorker.doInBackground for the signing process.
     */
    private class SignPackage extends SwingWorker<Integer, String> {
        
        private Process process;
        private int status;
        
        @Override
        protected Integer doInBackground() throws Exception {
            
			/* Get the package(s) and output directory paths. */
            File[] includedPackages = jFileChooser1.getSelectedFiles();
            
            String folder = jFileChooser2.getSelectedFile().getAbsolutePath();  // Get the specified output directory path.
            String path = System.getProperty("user.dir");  // Get current running JAR path.
            String separator = System.getProperty("file.separator");  // Use the system default path separator.
            
			/* The FOR loop statement will handle the process for multiple packages. */
            for (int i = 0; i < includedPackages.length; i++) {
                
                String packages = includedPackages[i].getAbsolutePath();  // Get the current package's absolute path.
				// Using Commons IO FilenameUtils to separate
				// the base name and the package's extension.
                String name = FilenameUtils.getBaseName(includedPackages[i].getName());
                String extension = FilenameUtils.getExtension(includedPackages[i].getName());
                // This is basically, the system command that
				// will sign the package(s). So to access the
				// signapk.jar and the testkeys, get the current
				// "user.dir" path (this will return the path
				// where the current JAR is running), then use
				// the system default path separator (cause it
				// varies according to Operating Systems).
                String[] command = {"java",
                                    "-Xmx1024m",
                                    "-jar",
                                    "" + path + separator + "assets" + separator + "signapk.jar",
                                    "-w",
                                    "" + path + separator + "assets" + separator + "testkey.x509.pem",
                                    "" + path + separator + "assets" + separator + "testkey.pk8",
                                    "" + packages + "",
                                    "" + folder + separator + name + "-signed." + extension +""};
                
                try {
					// Using ProcessBuilder to execute the command. Which needs to be in array form.
                    ProcessBuilder processBuilder = new ProcessBuilder(command);
                    process = processBuilder.start();
                    if (!isCancelled()) {
                        message = date + "Processing package (" + name + "." + extension + ") \n";
                        jTextArea.append(message);
                        status = process.waitFor();
                        if (status == 0) {
                            message = date + "Signing package succeed (" + name + "-signed." + extension + ") \n";
                            jTextArea.append(message);
                            Thread.sleep(1000);
                        } else {
                            message = "Known issues\n"+
                                      "- The package specified is not valid.\n"+
                                      "- The package specified may be damaged.\n"+
                                      "- The package specified may be renamed or removed.\n"+
                                      "- The output directory may be renamed or removed.\n"+
                                      "- The assets' folder may be renamed or removed.\n"+
                                      "- The assets' file(s) may be modified or removed.\n"+
                                      "- The libraries' folder may be renamed or removed.\n"+
                                      "- The libraries' file(s) may be renamed or removed.   \n\n";
                            icon = new ImageIcon(getClass().getResource("/drawable/ic_error_36.png"));
                            JOptionPane.showMessageDialog(rootPane, message, "Error", JOptionPane.ERROR_MESSAGE, icon);
                            message = date + "Signing package failed (an error occured) \n";
                            jTextArea.append(message);
                            process.destroy();
                            return status;
                        }
                    } else {
                        message = date + "Signing package stopped (process was cancelled) \n";
                        jTextArea.append(message);
                        process.destroy();
                        return status;
                    }
                    
                    process.destroy();
                } catch (IOException | InterruptedException ex) {
                    if( process != null ) process.destroy();
                }
            }
            
            return status;
        }
        
        @Override
        protected void done() {
			// When the process ws completed, failed or cancelled,
			// set all selections and actions to their default state.
            jMenuItem2.setEnabled(false);
            jMenuItem3.setEnabled(false);
            jButton2.setEnabled(false);
            jButton3.setEnabled(false);
            jButton4.setEnabled(false);
            jProgressBar.setIndeterminate(false);
            jFileChooser1.setSelectedFiles(null);
            jFileChooser2.setSelectedFile(null);
        }
    }
}
