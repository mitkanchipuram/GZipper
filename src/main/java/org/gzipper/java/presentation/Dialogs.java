/*
 * Copyright (C) 2018 Matthias Fussenegger
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.gzipper.java.presentation;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.gzipper.java.i18n.I18N;

/**
 *
 * @author Matthias Fussenegger
 */
public final class Dialogs {

    /**
     * Brings up a dialog that consists of the specified parameters.
     *
     * @param type the type of the alert.
     * @param title the title of the dialog.
     * @param header the header text of the dialog.
     * @param content the content text of the dialog.
     * @param theme the CSS theme to be applied.
     * @param icon the icon to be shown in the title.
     * @param buttonTypes the buttons to be added.
     * @return an {@link Optional} to indicate which button has been pressed.
     */
    public static Optional<ButtonType> showDialog(Alert.AlertType type, String title,
            String header, String content, CSS.Theme theme, Image icon, ButtonType... buttonTypes) {

        final Alert alert = new Alert(type, content, buttonTypes);
        final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        stage.getIcons().add(icon);
        alert.setTitle(title);
        alert.setHeaderText(header);
        CSS.load(theme, alert.getDialogPane().getScene());
        return alert.showAndWait();
    }

    /**
     * Brings up a confirmation dialog that consists of the specified
     * parameters.
     *
     * @param title the title of the dialog.
     * @param header the header text of the dialog.
     * @param content the content text of the dialog.
     * @param theme the CSS theme to be applied.
     * @param icon the icon to be shown in the title.
     * @return an {@link Optional} to indicate which button has been pressed.
     */
    public static Optional<ButtonType> showConfirmationDialog(String title,
            String header, String content, CSS.Theme theme, Image icon) {

        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content,
                ButtonType.YES, ButtonType.NO);
        final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

        stage.getIcons().add(icon);
        alert.setTitle(title);
        alert.setHeaderText(header);

        // changing default button to {@code ButtonType.NO} to avoid accidential press of return key
        Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        yesButton.setDefaultButton(false);
        noButton.setDefaultButton(true);
        CSS.load(theme, alert.getDialogPane().getScene());
        return alert.showAndWait();
    }

    /**
     * Brings up a {@link TextInputDialog} which allows the user to enter a
     * regular expression (pattern). The pattern will also be validated. So if
     * the returned {@link Optional} holds a result, it is guaranteed
     * that the result is a valid regular expression.
     *
     * @param theme the CSS theme to be applied.
     * @param icon the icon to be shown in the title.
     * @return an {@link Optional} which holds the pattern as string.
     */
    public static Optional<String> showPatternInputDialog(CSS.Theme theme, Image icon) {

        final TextInputDialog dialog = new TextInputDialog();
        final Button confirmButton = (Button) dialog
                .getDialogPane().lookupButton(ButtonType.OK);
        final Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();

        dialog.setTitle(I18N.getString("applyFilterMenuItem.text"));
        dialog.setHeaderText(I18N.getString("applyFilterMenuHeader.text"));
        dialog.setContentText(I18N.getString("applyFilterMenuContent.text"));
        stage.getIcons().add(icon);
        dialog.setResizable(true);

        // key released event to validate regex
        dialog.getEditor().setOnKeyReleased((KeyEvent event) -> {
            String text = ((TextField) event.getSource()).getText();
            try { // validate regex
                Pattern pattern = Pattern.compile(text);
                confirmButton.setDisable(false);
            } catch (PatternSyntaxException ex) {
                confirmButton.setDisable(true);
            }
        });
        CSS.load(theme, dialog.getDialogPane().getScene());
        return dialog.showAndWait();
    }
}
