/*
 * The MIT License (MIT)
 *
 * Copyright (C) 2017 Humble Potato II
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.humblepotato2.packagesigner;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Toast;

import com.android.signapk.SignApk;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import au.com.ninthavenue.patterns.android.dialogs.FileChooser;

public class PackageSigner extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText pkg_path, out_path;
    private Button btn_package, btn_output;
    private FloatingActionButton fab_sign;
    private String input, output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initKeys();  // #Initialize keys
        configureToolbar();  // #Toolbar configuration
        configureTextFields();  // #Text fields configuration
        configureActions();  // #Main actions configuration
    }

    /**
     * #Initialize keys
     */
    private void initKeys() {

        File publicKey = new File("/data/data/" + getPackageName() + "/keys/testkey.x509.pem");
        File privateKey = new File("/data/data/" + getPackageName() + "/keys/testkey.pk8");

        if (publicKey.exists() && privateKey.exists()) return;

        InputStream publicAssetsKey = null;
        InputStream privateAssetsKey = null;
        OutputStream publicKeyOutput = null;
        OutputStream privateKeyOutput = null;

        try {
            File keys = new File("/data/data/" + getPackageName() + "/keys");

            if (!keys.exists()) keys.mkdir();  // Create keys directory if not exist.

            // Extract public and private testkey(s).
            publicAssetsKey = getClass().getResourceAsStream("/assets/keys/testkey.x509.pem");
            privateAssetsKey = getClass().getResourceAsStream("/assets/keys/testkey.pk8");
            publicKeyOutput = new FileOutputStream("/data/data/" + getPackageName() + "/keys/testkey.x509.pem");
            privateKeyOutput = new FileOutputStream("/data/data/" + getPackageName() + "/keys/testkey.pk8");

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
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), R.string.exc_fileNotFound, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), R.string.exc_ioInitKeys, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * #Toolbar configuration
     */
    private void configureToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.ver_name);
        setSupportActionBar(toolbar);
    }

    /**
     * Toolbar menus
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mnu_reference) {
            Intent intent = new Intent(PackageSigner.this, Reference.class);
            startActivity(intent);
        } else if (id == R.id.mnu_donate) {
            Uri donateURL = Uri.parse("https://www.paypal.me/HumblePotato2");
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, donateURL);
            startActivity(launchBrowser);
        } else if (id == R.id.mnu_about) {
            AlertDialog.Builder about = new AlertDialog.Builder(this);
            about.setTitle(R.string.abt_dialogTitle);
            about.setMessage(R.string.abt_message);
            about.setPositiveButton(R.string.abt_positiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = about.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * #Text fields configuration
     */
    private void configureTextFields() {

        /* Allow horizontally scrolling of text fields. */
        // Package path (pkg_path)
        pkg_path = (EditText) findViewById(R.id.pkg_path);
        pkg_path.setScroller(new Scroller(getApplicationContext()));
        pkg_path.setMaxLines(1);
        pkg_path.setHorizontallyScrolling(true);
        pkg_path.setMovementMethod(new ScrollingMovementMethod());

        // Output path (out_path)
        out_path = (EditText) findViewById(R.id.out_path);
        out_path.setScroller(new Scroller(getApplicationContext()));
        out_path.setMaxLines(1);
        out_path.setHorizontallyScrolling(true);
        out_path.setMovementMethod(new ScrollingMovementMethod());
    }

    /**
     * #Main actions configuration
     */
    private void configureActions() {

        // Package selection (btn_package)
        btn_package = (Button) findViewById(R.id.btn_package);
        btn_package.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FileChooser(PackageSigner.this).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) {
                        // Using Apache Commons IO FilenameUtils to separate the base name
                        // and the package extension to be able to change its output name.
                        String pkg_name = FilenameUtils.getBaseName(file.getName());
                        String pkg_extension = FilenameUtils.getExtension(file.getName());
                        String inp_package = file.getParent() + "/" + pkg_name + "." + pkg_extension;
                        String out_package = file.getParent() + "/" + pkg_name + "_signed." + pkg_extension;
                        pkg_path.setText(inp_package);
                        if (out_path.getText().toString().equals("")) out_path.setText(out_package);
                    }
                }).showDialog();
            }
        });

        // Output seletion (btn_output)
        btn_output = (Button) findViewById(R.id.btn_output);
        btn_output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FileChooser(PackageSigner.this).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) {
                        String out_package = file.getAbsolutePath();
                        out_path.setText(out_package);
                    }
                }).showDialog();
            }
        });

        // Signing package (fab_sign)
        fab_sign = (FloatingActionButton) findViewById(R.id.fab_sign);
        fab_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pkg_path.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.msg_nullInputPackage, Toast.LENGTH_LONG).show();
                    return;
                } else if (out_path.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.msg_nullOutputPackage, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), R.string.msg_signingProcessStarted, Toast.LENGTH_LONG).show();
                // Will get the input and output packages' absolute path
                // based on what the text fields' texts are currently set,
                // and will pass it to SignApk class as String arguments.
                input = pkg_path.getText().toString();
                output = out_path.getText().toString();
                // To make the toast messages appear correctly, delay
                // the signing process at least 1 second so the timing
                // will display the toasts duration properly.
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SignApk signApk = new SignApk(input, output);
                            Toast.makeText(getApplicationContext(), R.string.msg_succeedSigningPackage, Toast.LENGTH_LONG).show();
                        } catch (IOException | GeneralSecurityException ex) {
                            Toast.makeText(getApplicationContext(), R.string.msg_failedSigningPackage, Toast.LENGTH_LONG).show();
                        }
                    }
                }, 1000);
            }
        });
    }
}
