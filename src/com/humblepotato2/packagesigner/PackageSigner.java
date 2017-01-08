/**
 * @author Humble Potato II
 * 
 * External libraries used:
 * - @library Apache Commons IO
 *   @version 2.5
 *   @license Apache 2.0
 *   @link http://commons.apache.org/proper/commons-io/
 */
package com.humblepotato2.packagesigner;

import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
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
import org.apache.commons.io.FilenameUtils;

public class PackageSigner extends JFrame {
    
    /**
     * Variables
     */
    private JButton jButton1, jButton2, jButton3, jButton4, jButton5;
    private JFileChooser jFileChooser1, jFileChooser2;
    private ImageIcon dialogIcon;
    private JMenu jMenu1, jMenu2;
    private JMenuBar jMenuBar;
    private JMenuItem jMenuItem1, jMenuItem2, jMenuItem3, jMenuItem4, jMenuItem5, jMenuItem6;
    private JPanel jPanel;
    private JProgressBar jProgressBar;
    private JScrollPane jScrollPane;
    private JPopupMenu.Separator jSeparator;
    private JTextArea jTextArea;
    private JToolBar jToolBar;
    private KeyStroke keyStroke;
    private DateFormat dateFormat;
    private Date date;
    private String today;
    private SignPackage signPackage;
    
    /**
     * Class constructor calls initUI() method
     */
    public PackageSigner() {
        initUI();
    }
    
    /**
     * Initialize User Interface
     */
    @SuppressWarnings("unchecked")                         
    private void initUI() {
        
        initObjects();  // Calls initObjects() method
        
        /**
         * JFrame main window
         */
        setTitle("Package Signer 2.0");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        
        /**
         * jTextArea & jScrollPane
         */
        jTextArea.setText(today + "Package Signer version 2.0 \n");
        jTextArea.setEditable(false);
        jTextArea.setColumns(20);
        jTextArea.setRows(5);
        jScrollPane.setViewportView(jTextArea);
        
        /**
         * jMenuBar, jMenu & jMenuItem(s)
         */
        // jMenu1 (Main actions)
        jMenu1.setText("File");
        
        // jMenuItem1 (Package selection)
        jMenuItem1.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_archive_18.png")));
        jMenuItem1.setText("Package     ");
        jMenuItem1.setToolTipText("Browse for new package");
        jMenuItem1.setMnemonic('P');
        jMenuItem1.setAccelerator(keyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  // Shortcut key (CTRL + P)
        jMenuItem1.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPackage();  // Action performed calls selectPackage() method (same as jButton1)
            }
        });
        jMenu1.add(jMenuItem1);

        // jMenuItem2 (Output selection)
        jMenuItem2.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_folder_18.png")));
        jMenuItem2.setText("Output");
        jMenuItem2.setToolTipText("Browse for new output folder");
        jMenuItem2.setMnemonic('O');
        jMenuItem2.setAccelerator(keyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  // Shortcut key (CTRL + O)
        jMenuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectOutput();  // Action performed calls selectOutput() method (same as jButton2)
            }
        });
        jMenu1.add(jMenuItem2);
        
        // jMenuItem3 (Signing package)
        jMenuItem3.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_sign_18.png")));
        jMenuItem3.setText("Sign");
        jMenuItem3.setToolTipText("Sign the package");
        jMenuItem3.setEnabled(false);
        jMenuItem3.setMnemonic('S');
        jMenuItem3.setAccelerator(keyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));  // Shortcut key (CTRL + S)
        jMenuItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // This item was disabled by default but will be enabled once a Package and an Output
                // folder is specified. And will be disabled again once you start Signing the Package.
                // The same function goes to jButton3 so they will both become enabled, then disabled.
                JMenuItem source = (JMenuItem) e.getSource();
                if (source == jMenuItem3) {
                    jMenuItem3.setEnabled(false);
                    jButton3.setEnabled(false);
                    jButton4.setEnabled(true);
                }
                // Execute the package signing process and set the jProgressBar to Indeterminate()
                signPackage = new SignPackage();
                signPackage.execute();
                jProgressBar.setIndeterminate(true);
            }
        });
        jMenu1.add(jMenuItem3);
        
        jMenu1.add(jSeparator);  // Popup menu separator.

        // jMenuItem4 (Exit the program)
        jMenuItem4.setText("Exit");
        jMenuItem4.setToolTipText("");
        jMenuItem4.setMnemonic('X');
        jMenuItem4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar.add(jMenu1);
        
        // jMenu2 (Instructions and about)
        jMenu2.setText("Help");

        // jMenuItem5 (Display instructions dialog)
        jMenuItem5.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_help_18.png")));
        jMenuItem5.setText("Instructions");
        jMenuItem5.setMnemonic('I');
        jMenuItem5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String instructions = "Instructions\n\n"+
                                      "1. Specify the Package you want to sign.\n"+
                                      "2. Choose an Output directory where to save it.   \n"+
                                      "3. Hit the Sign button to start signing it.\n\n";
                dialogIcon = new ImageIcon(getClass().getResource("/drawable/ic_help_36.png"));
                JOptionPane.showMessageDialog(rootPane, instructions, "Help", JOptionPane.QUESTION_MESSAGE, dialogIcon);
            }
        });
        jMenu2.add(jMenuItem5);

        // jMenuItem6 (Display about dialog)
        jMenuItem6.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_info_18.png")));
        jMenuItem6.setText("About");
        jMenuItem6.setMnemonic('A');
        jMenuItem6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String about = "Package Signer 2.0\n"+
                               "Created by Humble Potato II";
                dialogIcon = new ImageIcon(getClass().getResource("/drawable/ic_info_36.png"));
                JOptionPane.showMessageDialog(rootPane, about, "About", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuBar.add(jMenu2);

        setJMenuBar(jMenuBar);
        
        /**
         * jToolbar & jButton(s)
         */
        jToolBar.setFloatable(false);

        // jButton1 (Package selection same function as jMenuItem1)
        jButton1.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_archive_36.png")));
        jButton1.setToolTipText("Specify a package");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPackage();  // Action performed calls selectPackage() method (same function as jMenuItem1)
            }
        });
        jToolBar.add(jButton1);

        // jButton2 (Output selection same fuction as jMenuItem2)
        jButton2.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_folder_36.png")));
        jButton2.setToolTipText("Specify an output directory");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectOutput();  // Action performed calls selectOutput() method (same function as jMenuItem2)
            }
        });
        jToolBar.add(jButton2);

        // jButton3 (Signing package same function as jMenuItem3)
        jButton3.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_sign_36.png")));
        jButton3.setToolTipText("Sign the package");
        jButton3.setEnabled(false);
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action performed (same function as jMenuItem3)
                JButton source = (JButton) e.getSource();
                if (source == jButton3) {
                    jMenuItem3.setEnabled(false);
                    jButton3.setEnabled(false);
                    jButton4.setEnabled(true);
                }
                signPackage = new SignPackage();
                signPackage.execute();
                jProgressBar.setIndeterminate(true);
            }
        });
        jToolBar.add(jButton3);

        // jButton4 (Cancel package signing process)
        jButton4.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_cancel_36.png")));
        jButton4.setToolTipText("Cancel signing operation");
        jButton4.setEnabled(false);
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cancel and complete the package signing operation,
                // then print out the cancellation message to jTextArea.
                signPackage.cancel(true);
                signPackage.done();
                jTextArea.append(today + "Package signing stopped (the signing operation was cancelled) \n");
            }
        });
        jToolBar.add(jButton4);

        // jButton5 (Clear log messages)
        jButton5.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_clear_36.png")));
        jButton5.setToolTipText("Clear log message");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea.setText(today + "Package Signer version 2.0\n");
            }
        });
        jToolBar.add(jButton5);
        
        /**
         * Group jScrollPane and jProgressBar to jPanelLayout
         */
        GroupLayout jPanelLayout = new GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane)
                    .addComponent(jProgressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        /**
         * Group jToolBar and jPanel to layout
         */
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar, GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
            .addComponent(jPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        pack();  // Pack all layouts and components to JFrame.
    }
    
    /**
     * Initialize objects
     */
    private void initObjects() {
        
        jToolBar = new JToolBar();
        jButton1 = new JButton();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jButton4 = new JButton();
        jButton5 = new JButton();
        jFileChooser1 = new JFileChooser();
        jFileChooser2 = new JFileChooser();
        jPanel = new JPanel();
        jScrollPane = new JScrollPane();
        jTextArea = new JTextArea();
        jProgressBar = new JProgressBar();
        jMenuBar = new JMenuBar();
        jMenu1 = new JMenu();
        jMenuItem1 = new JMenuItem();
        jMenuItem2 = new JMenuItem();
        jMenuItem3 = new JMenuItem();
        jSeparator = new JPopupMenu.Separator();
        jMenuItem4 = new JMenuItem();
        jMenu2 = new JMenu();
        jMenuItem5 = new JMenuItem();
        jMenuItem6 = new JMenuItem();
        // Set the current date.
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = new Date();
        today = " " + dateFormat.format(date) + " - ";
        
        /**
         * jFileChooser1 & jFileChooser2
         */
        // jFileChooser1 (Package selection dialog)
        jFileChooser1.setCurrentDirectory(new File(System.getProperty("user.dir")));
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("(*.apk, *.zip)", "apk", "zip");  // Set the File filter to .APK and .ZIP only.
        jFileChooser1.setFileFilter(fileFilter);
        jFileChooser1.setAcceptAllFileFilterUsed(false);  // Disable All files filter.
        
        // jFileChooser2 (Output selection dialog)
        jFileChooser2.setCurrentDirectory(new File(System.getProperty("user.dir")));
        jFileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  // Set selection to Directories only.
    }
    
    /**
     * Package selection method
     */
    private void selectPackage() {
        // Display a File selection dialog with a custom File type filter and on
        // APPROVE_OPTION, print out the full path of the selected file to jTextArea.
        int returnValue = jFileChooser1.showOpenDialog(rootPane);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String file = jFileChooser1.getSelectedFile().getAbsolutePath();
            jTextArea.append(today + "Selected package included (" + file + ") \n");
        }
    }
    
    
    /**
     * Output selection method
     */
    private void selectOutput() {
        // Display a dialog message if you haven't specified a Package yet, then return.
        if (jFileChooser1.getSelectedFile() == null) {
            dialogIcon = new ImageIcon(getClass().getResource("/drawable/ic_warning_36.png"));
            JOptionPane.showMessageDialog(rootPane, "Please specify a package first.", "Output", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
            return;
        }
        // Display a File selection dialog but show Directories only, and on
        // APPROVE_OPTION, print out the full path of the selected folder to jTextArea.
        int returnValue = jFileChooser2.showOpenDialog(rootPane);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String folder = jFileChooser2.getSelectedFile().getAbsolutePath();
            jTextArea.append(today + "Selected output assigned (" + folder + ") \n");
            jMenuItem3.setEnabled(true);
            jButton3.setEnabled(true);
        }
    }
    
    
    /**
     * Signing package
     */
    private class SignPackage extends SwingWorker<Integer, String> {
        
        private int status;
        
        @Override
        protected Integer doInBackground() throws Exception {
            
            /**
             * Get selected Package, Output and current user.directory paths...
             */
            File selectedPackage = jFileChooser1.getSelectedFile();
            File selectedOutput = jFileChooser2.getSelectedFile();
        
            String file = selectedPackage.getAbsolutePath();  // Get selected Package path.
            String folder = selectedOutput.getAbsolutePath();  // Get selected Output path.
            // Using Apache Commons IO library to get the
            // base name of the Package and its extension.
            String name = FilenameUtils.getBaseName(selectedPackage.getName());
            String extension = FilenameUtils.getExtension(selectedPackage.getName());
            String path = System.getProperty("user.dir");
            String separator = System.getProperty("file.separator");
            
            // System command to execute.
            String[] command = {"java",
                                "-Xmx1024m",
                                "-jar",
                                "" + path + separator + "assets" + separator + "signapk.jar",
                                "-w",
                                "" + path + separator + "assets" + separator + "testkey.x509.pem",
                                "" + path + separator + "assets" + separator + "testkey.pk8",
                                "" + file + "",
                                "" + folder + separator + name + "-signed." + extension +""};
            
            try {
                // Execute the system command with ProcessBuilder.
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                if (!isCancelled()) {
                    status = process.waitFor();  // Wait for the process to finish.
                }
                // Print out a success message to jTextArea if the process was successful.
                // Print out an error message to jTextArea if the process was failed, then
                // display an error dialog message and reset all selections to null.
                if (status == 0) {
                    jTextArea.append(today + "Package signing successful (" + folder + separator + name + "-signed." + extension + ") \n");
                } else {
                    String cause = "Known issues\n\n"+
                                   "- The package specified may be damaged.\n"+
                                   "- The package specified may be renamed or removed.\n"+
                                   "- The output directory may be renamed or removed.\n"+
                                   "- The assets folder may be renamed or removed.\n"+
                                   "- The assets' file(s) may be modified or removed.\n"+
                                   "- The libraries' folder may be renamed or removed.\n"+
                                   "- The libraries' file(s) may be renamed or removed.   \n\n";
                    dialogIcon = new ImageIcon(getClass().getResource("/drawable/ic_error_36.png"));
                    jTextArea.append(today + "Package signing failed (an error occured during the process) \n");
                    JOptionPane.showMessageDialog(rootPane, cause, "ERROR", JOptionPane.ERROR_MESSAGE, dialogIcon);
                    jFileChooser1.setSelectedFile(null);
                    jFileChooser2.setSelectedFile(null);
                }
                process.destroy();
            } catch (IOException | InterruptedException | HeadlessException ex) {
                ex.printStackTrace(System.err);
            }
            
            return status;
        }
        
        
        @Override
        protected void done() {
            // When process ends, disable jMenuItem3, jButton3, and jButton4.
            // And set Indeterminate jProgressBar to false indicating that there's no current process.
            jMenuItem3.setEnabled(false);
            jButton3.setEnabled(false);
            jButton4.setEnabled(false);
            jProgressBar.setIndeterminate(false);
        }
    }
    
    
    /**
     * Main class' method
     */
    public static void main(String args[]) {
        // Create and run the program.
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PackageSigner().setVisible(true);
            }
        });
    }
}
