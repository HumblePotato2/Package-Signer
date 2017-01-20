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

package humblepotato2.packagesigner;

import android.content.Context;
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
import android.util.Log;
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

    private static PackageSigner instance;
    private EditText inp_field, out_field;
    private String input, output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        instance = this;

        initKeys();  // #Initialize keys
        configureToolbar();  // #Toolbar configuration
        configureTextFields();  // #Text fields configuration
        configureActions();  // #Main actions configuration
    }

    // Set the application context as static so it can be used anywhere.
    // This should be safe as it will only be used in Toasts and as an
    // argument to SignApk class so no worries about memory leaks.
    public static Context context() { return instance.getApplicationContext(); }

    /**
     * #Initialize keys
     */
    private void initKeys() {

        // If both testkeys already exists, then skip this step.
        File publicKey = new File(getFilesDir().getPath() + "/keys/testkey.x509.pem");
        File privateKey = new File(getFilesDir().getPath() + "/keys/testkey.pk8");
        if (publicKey.exists() && privateKey.exists()) return;

        InputStream publicAssetsKey = null;
        InputStream privateAssetsKey = null;
        OutputStream publicKeyOutput = null;
        OutputStream privateKeyOutput = null;

        try {
            File keys = new File(getFilesDir().getPath() + "/keys");
            if (!keys.exists()) keys.mkdir();  // Create keys directory if not exist.

            // Extract public and private testkey(s).
            publicAssetsKey = getClass().getResourceAsStream("/assets/keys/testkey.x509.pem");
            privateAssetsKey = getClass().getResourceAsStream("/assets/keys/testkey.pk8");
            publicKeyOutput = new FileOutputStream(getFilesDir().getPath() + "/keys/testkey.x509.pem");
            privateKeyOutput = new FileOutputStream(getFilesDir().getPath() + "/keys/testkey.pk8");

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
            Log.e("Package Signer", "FileNotFoundException", e);
        } catch (IOException e) {
            Log.e("Package Signer", "IOException", e);
        }
    }

    /**
     * #Toolbar configuration
     */
    private void configureToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_version);
        setSupportActionBar(toolbar);
    }

    /**
     * Toolbar menus
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menus, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.mnu_instructions) {
            Intent intent = new Intent(PackageSigner.this, Instructions.class);
            startActivity(intent);
        } else if (id == R.id.mnu_reference) {
            Intent intent = new Intent(PackageSigner.this, Reference.class);
            startActivity(intent);
        } else if (id == R.id.mnu_donate) {
            Uri donateURL = Uri.parse("https://www.paypal.me/HumblePotato2");
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, donateURL);
            startActivity(launchBrowser);
        } else if (id == R.id.mnu_about) {
            AlertDialog.Builder about = new AlertDialog.Builder(this);
            about.setTitle(R.string.abt_aboutTitle);
            about.setMessage(R.string.abt_aboutMessage);
            about.setPositiveButton(R.string.abt_aboutPositiveButton, new DialogInterface.OnClickListener() {
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
        // Package field (inp+field)
        inp_field = (EditText) findViewById(R.id.inp_field);
        inp_field.setMaxLines(1);
        inp_field.setHorizontallyScrolling(true);
        inp_field.setHorizontalScrollBarEnabled(true);
        inp_field.setMovementMethod(new ScrollingMovementMethod());

        // Output field (out_field)
        out_field = (EditText) findViewById(R.id.out_field);
        out_field.setScroller(new Scroller(getApplicationContext()));
        out_field.setMaxLines(1);
        out_field.setHorizontallyScrolling(true);
        out_field.setHorizontalScrollBarEnabled(true);
        out_field.setMovementMethod(new ScrollingMovementMethod());
    }

    /**
     * #Main actions configuration
     */
    private void configureActions() {

        // Package selection (btn_package)
        Button btn_package = (Button) findViewById(R.id.btn_package);
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
                        inp_field.setText(inp_package);
                        // Auto generate the output package name only if the field is empty.
                        if (out_field.getText().toString().equals("")) out_field.setText(out_package);
                    }
                }).showDialog();
            }
        });

        // Output seletion (btn_output)
        Button btn_output = (Button) findViewById(R.id.btn_output);
        btn_output.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FileChooser(PackageSigner.this).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override
                    public void fileSelected(File file) {
                        String out_package = file.getAbsolutePath();
                        out_field.setText(out_package);
                    }
                }).showDialog();
            }
        });

        // Signing package (fab_sign)
        FloatingActionButton fab_sign = (FloatingActionButton) findViewById(R.id.fab_sign);
        fab_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If testkeys are not found, display a warning
                // message indicating that the testkeys are missing.
                File publicKey = new File(getFilesDir().getPath() + "/keys/testkey.x509.pem");
                File privateKey = new File(getFilesDir().getPath() + "/keys/testkey.pk8");
                if (!publicKey.exists() || !privateKey.exists()) {
                    AlertDialog.Builder missingKeys = new AlertDialog.Builder(PackageSigner.this);
                    missingKeys.setTitle(R.string.kys_missingKeyTitle);
                    missingKeys.setMessage(R.string.kys_missingKeyMessage);
                    missingKeys.setPositiveButton(R.string.kys_missingKeyPossitiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = missingKeys.create();
                    dialog.show();
                } else if (inp_field.getText().toString().equals("")) {
                    Toast.makeText(context(), R.string.msg_emptyInputField, Toast.LENGTH_LONG).show();
                    return;
                } else if (out_field.getText().toString().equals("")) {
                    Toast.makeText(context(), R.string.msg_emptyOutputField, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    // If both fields are not empty, display a toast indicating that the process is started.
                    Toast.makeText(context(), R.string.msg_signingStarted, Toast.LENGTH_LONG).show();
                }
                // Get the input and output packages' absolute path based
                // on what the text fields' texts are currently set, and
                // will pass them to SignApk class as String arguments.
                input = inp_field.getText().toString();
                output = out_field.getText().toString();
                // To make the toast messages appear correctly, delay
                // the signing process at least 1 second so the timing
                // will display the toasts duration properly.
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SignApk signApk = new SignApk(context(), input, output);
                            // Display a success toast once the process is finished.
                            Toast.makeText(context(), R.string.msg_signingSuccessful, Toast.LENGTH_LONG).show();
                        } catch (IOException | GeneralSecurityException ex) {
                            // If the process catches a General security or IO exception,
                            // this will be automatically considered as a failure, then
                            // display a toast indicating that the process is failed.
                            Toast.makeText(context(), R.string.msg_signingFailed, Toast.LENGTH_LONG).show();
                        }
                    }
                }, 1000);
            }
        });
    }
}
