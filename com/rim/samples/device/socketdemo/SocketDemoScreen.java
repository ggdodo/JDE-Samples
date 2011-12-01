/*
 * SocketDemoScreen.java
 *
 * Copyright � 1998-2011 Research In Motion Limited
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.rim.samples.device.socketdemo;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A MainScreen class to allow for user interaction.
 */
class SocketDemoScreen extends MainScreen {
    private final EditField _hostField;
    private final CheckboxField _useDirectTcpField;
    private final RichTextField _statusField;
    private final StringBuffer _message;
    private boolean _threadRunning = false;

    // Constructor
    SocketDemoScreen() {
        setTitle(new LabelField("Socket Demo", DrawStyle.ELLIPSIS
                | Field.USE_ALL_WIDTH));

        add(new RichTextField(
                "Enter local host name in the field below and select 'Go' from the menu.",
                Field.NON_FOCUSABLE));
        add(new SeparatorField(SeparatorField.LINE_HORIZONTAL));

        // Need to get the local host name from the user because access to
        // 'localhost' and 127.0.0.1 is restricted.
        _hostField = new EditField("Local Host: ", "");
        add(_hostField);

        _useDirectTcpField =
                new CheckboxField("Use Direct TCP",
                        RadioInfo.getNetworkType() == RadioInfo.NETWORK_IDEN);
        add(_useDirectTcpField);

        _statusField = new RichTextField(Field.NON_FOCUSABLE);
        add(_statusField);

        _message = new StringBuffer();
    }

    /**
     * Method to display a message to the user.
     * 
     * @param msg
     *            The message to display.
     */
    void updateDisplay(final String msg) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                _message.append(msg);
                _message.append('\n');
                _statusField.setText(_message.toString());
            }
        });
    }

    /**
     * Returns the text entered by the user.
     * 
     * @return text entered by the user.
     */
    String getHostFieldText() {
        return _hostField.getText();
    }

    /**
     * Indicates whether the direct TCP checkbox is checked.
     * 
     * @return True if checkbox is checked, otherwise false.
     */
    boolean isDirectTCP() {
        return _useDirectTcpField.getChecked();
    }

    /**
     * Setter for boolean _threadRunning
     * 
     * @param running
     *            True if a ConnectThread is running, otherwise false.
     */
    void setThreadRunning(final boolean running) {
        _threadRunning = running;
    }

    /**
     * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        // If a ConnectThread is running we won't add our menu item
        if (!_threadRunning) {
            menu.add(_go);
        }
        super.makeMenu(menu, instance);
    }

    /**
     * Prevent the save dialog from being displayed, nothing to save.
     * 
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        return true;
    }

    /**
     * Handles the user pressing ENTER while the 'use direct tcp' CheckboxField
     * has focus.
     * 
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     * 
     */
    protected boolean keyChar(final char key, final int status, final int time) {
        if (key == Characters.ENTER) {
            final Field fieldWithFocus = getFieldWithFocus();

            if (fieldWithFocus == _useDirectTcpField) {
                if (_useDirectTcpField.getChecked()) {
                    _useDirectTcpField.setChecked(false);
                } else {
                    _useDirectTcpField.setChecked(true);
                }

                return true; // We've consumed the event.
            }
        }

        return super.keyChar(key, status, time); // We'll let super handle the
                                                 // event.
    }

    /**
     * An anonymous MenuItem class.
     */

    private final MenuItem _go = new MenuItem("Go", 11000, 0) {
        public void run() {
            // Don't do anything unless there is a host name in the _host field.
            if (_hostField.getText().length() > 0) {
                new ConnectThread().start();
                _threadRunning = true;
            } else {
                Dialog.ask(Dialog.D_OK, "Please enter a valid host name");
            }
        }
    };
}