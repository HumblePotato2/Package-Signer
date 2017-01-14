/*
 * @name Package Signer 3.0
 * @author Humble Potato II
 *
 * External library used:
 * - @library Apache Commons IO
 *   @version 2.5
 *   @license Apache 2.0
 *   @link http://commons.apache.org/proper/commons-io/
 */
package com.humblepotato2.packagesigner;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
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

import orig.SignApk;

public class PackageSigner extends JFrame {
    
    /**
     * Initialize variables.
     */
    private GroupLayout layoutA, layoutMain;
    private ImageIcon icon;
    private JButton jButton1, jButton2, jButton3, jButton4;
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
    private SignPackage signPackage;
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
        
        this.setTitle("Package Signer 3.0");
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
        initAssets();  // #Initialize keys
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
        
        jButton1 = new JButton();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jButton4 = new JButton();
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
     * #Initialize keys
     */
    private void initAssets() {
        
        InputStream publicAssetsKey = null;
        InputStream privateAssetsKey = null;
        OutputStream publicKeyOutput = null;
        OutputStream privateKeyOutput = null;
        
        try {
            // Extract public and private testkey(s).
            publicAssetsKey = getClass().getResourceAsStream("/assets/keys/testkey.x509.pem");
            privateAssetsKey = getClass().getResourceAsStream("/assets/keys/testkey.pk8");
            publicKeyOutput = new FileOutputStream("testkey.x509.pem");
            privateKeyOutput = new FileOutputStream("testkey.pk8");
            
            byte[] buffer = new byte[1024];
            int length = 0;
            
            while ((length = publicAssetsKey.read(buffer)) > 1) {
                publicKeyOutput.write(buffer, 0, length);
            }
            while ((length = privateAssetsKey.read(buffer)) > 1) {
                privateKeyOutput.write(buffer, 0, length);
            }
            
            publicKeyOutput.flush();
            publicKeyOutput.close();
            privateKeyOutput.flush();
            privateKeyOutput.close();
            publicAssetsKey.close();
            privateAssetsKey.close();
            
            // Testkey(s) will be deleted on exit.
            File publicKeyTemp = new File("testkey.x509.pem");
            File privateKeyTemp = new File("testkey.pk8");
            publicKeyTemp.deleteOnExit();
            privateKeyTemp.deleteOnExit();
        } catch (IOException ex) {
            message = date + "Failed to initialize assets.";
            icon = new ImageIcon(getClass().getResource("/drawable/ic_error_36.png"));
            JOptionPane.showMessageDialog(rootPane, message, "Error", JOptionPane.ERROR_MESSAGE, icon);
            System.exit(0);
        }
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
        jMenuItem1.setToolTipText("Browse for a new package");
        jMenuItem1.setAccelerator(keyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                packageSelection.choosePackage();
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
        jMenuItem3.setToolTipText("Sign the package");
        jMenuItem3.setAccelerator(keyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        jMenuItem3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signPackage = new SignPackage();
                signPackage.execute();
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
        jMenuItem5.setText("?");
        jMenuItem5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message = "Instructions\n"+
                          "1. Specify the Package you want to sign.\n"+
                          "2. Choose an Output directory where to save it.\n"+
                          "3. Hit the Sign button to start signing it.\n\n"+
                          "Shortcut keys\n"+
                          "- Package selection > ⌘ or CTRL + P (Windows/Linux/Mac)   \n"+
                          "- Output selection > ⌘ or CTRL + O (Windows/Linux/Mac)\n"+
                          "- Signing package > ⌘ or CTRL + S (Windows/Linux/Mac)\n\n";
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
                message = "Package Signer 3.0\n"+
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
        jButton1.setToolTipText("Browse for a new package");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                packageSelection.choosePackage();
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
        jButton3.setToolTipText("Sign the package");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signPackage = new SignPackage();
                signPackage.execute();
            }
        });
        
        // Clear log message (jButton4)
        jButton4.setIcon(new ImageIcon(getClass().getResource("/drawable/ic_clear_36.png")));
        jButton4.setToolTipText("Clear log message");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(SwingConstants.BOTTOM);
        jButton4.addActionListener(new ActionListener() {
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
        jToolBar.add(jButton4);  // Clear log message
    }
    
    /**
     * #Text box configuration
     */
    private void configureTextBox() {
        
        /* JTextArea & JScrollPane */
        jTextArea.setEditable(false);
        jTextArea.setText(date + "Package Signer version 3.0 \n");
        jScrollPane.setViewportView(jTextArea);
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
     * Package selection
     */
    private class PackageSelection {
        
        private FileNameExtensionFilter filter;
        
        protected void choosePackage() {
            
            filter = new FileNameExtensionFilter("(*.apk, *.zip)", "apk", "zip");
            
            jFileChooser1.setCurrentDirectory(new File(System.getProperty("user.dir")));
            jFileChooser1.setAcceptAllFileFilterUsed(false);  // Disable All Files filter.
            jFileChooser1.setFileFilter(filter);  // Sets File extension filter to APK and ZIP only.
            
            int response = jFileChooser1.showOpenDialog(rootPane);
            
            if (response == JFileChooser.APPROVE_OPTION) {
                String name = jFileChooser1.getSelectedFile().getName();
                message = date + "Selected package included (" + name + ") \n";
                jTextArea.append(message);
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
     */
    private class SignPackage extends SwingWorker<Integer, String> {
        
        @Override
        protected Integer doInBackground() throws Exception {
            
            File includedPackage = jFileChooser1.getSelectedFile();
            
            String inputPackage = includedPackage.getAbsolutePath();
            String folder = jFileChooser2.getSelectedFile().getAbsolutePath();
            // Using Apache Commons IO FilenameUtils to separate the base name
            // and the package extension to be able to change the output name.
            String name = FilenameUtils.getBaseName(includedPackage.getName());
            String extension = FilenameUtils.getExtension(includedPackage.getName());
            String separator = System.getProperty("file.separator");  // Use the system default path separator.
            String outputPackage = folder + separator + name + "-signed." + extension;
            
            try {
                jProgressBar.setIndeterminate(true);
                new SignApk(inputPackage, outputPackage);
                message = date + "Package signing successful (" + name + "-signed." + extension + ") \n";
                jTextArea.append(message);
            } catch (IOException | GeneralSecurityException ex) {
                message = "Known issues\n"+
                          "- The testkey(s) are not found.\n"+
                          "- The package specified may be damaged.\n"+
                          "- The package specified may be renamed or removed.   \n"+
                          "- The output directory may be renamed or removed.\n\n";
                icon = new ImageIcon(getClass().getResource("/drawable/ic_error_36.png"));
                JOptionPane.showMessageDialog(rootPane, message, "Error", JOptionPane.ERROR_MESSAGE, icon);
                message = date + "Package signing failed (fatal error occured) \n";
                jTextArea.append(message);
                jProgressBar.setIndeterminate(false);
            }
            
            return 0;
        }
        
        @Override
        protected void done() {
            jMenuItem2.setEnabled(false);
            jMenuItem3.setEnabled(false);
            jButton2.setEnabled(false);
            jButton3.setEnabled(false);
            jProgressBar.setIndeterminate(false);
        }
    }
}
