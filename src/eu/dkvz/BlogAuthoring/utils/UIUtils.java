/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dkvz.BlogAuthoring.utils;

import java.util.Optional;
import javafx.scene.control.*;

/**
 *
 * @author Alain
 */
public final class UIUtils {
    
    public static void errorAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.showAndWait();
    }
    
    public static void warningAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.showAndWait();
    }
    
    public static void infoAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.showAndWait();
    }
    
    public static boolean confirmDialog(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    
    public static String inputDialog(String message, String title, String defaultValue) {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle(title);
        dialog.setHeaderText("");
        dialog.setContentText(message);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        } else {
            return null;
        }
    }
    
    public static void insertBlocAtCaret(TextInputControl textField, String bloc) {
        // We could check if there's a selection and erase it.
        textField.insertText(textField.getCaretPosition(), bloc);
    }
    
    public static void surroundSelectionWithBloc(TextInputControl textField, String before, String after) {
        // If there is a selection, surround it with before and after.
        // If no selection, just insert before, a line feed, and after at the caret position.
        if (textField.getSelection().getLength() > 0) {
            // Anchor can either be before caret position or ahead.
            // This is before people can select from A to B or B to A equally.
            int caret = textField.getCaretPosition();
            int anchor = textField.getAnchor();
            if (anchor < caret) {
                textField.insertText(anchor, before);
                textField.insertText(caret + before.length(), after);
            } else {
                textField.insertText(caret, before);
                textField.insertText(anchor + before.length(), after);
            }
        } else {
            textField.insertText(textField.getCaretPosition(), before.concat(after));
            textField.positionCaret(textField.getCaretPosition() - after.length());
        }
    }
    
}
