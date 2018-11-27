package game;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class CustomJTextField extends JTextField{
    private static final long serialVersionUID = 1L;
    private int _rowNumber = -1;
    public static final String TEXT_PROPERTY = "text";

    public CustomJTextField(int nbColumns, int rowNumber) {
        super(nbColumns);
        _rowNumber = rowNumber;
        this.setDocument(new MyDocument());
    }

    @SuppressWarnings("serial")
    private class MyDocument extends PlainDocument {
        private boolean ignoreEvents = false;

        @Override
        public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String oldValue = CustomJTextField.this.getText();
            this.ignoreEvents = true;
            super.replace(offset, length, text, attrs);
            this.ignoreEvents = false;
            String newValue = CustomJTextField.this.getText();
            if (!oldValue.equals(newValue)) {
                CustomJTextField.this.firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
                Game.listCockroachName.set(_rowNumber, newValue);
            }
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            String oldValue = CustomJTextField.this.getText();
            super.remove(offs, len);
            String newValue = CustomJTextField.this.getText();
            if (!ignoreEvents && !oldValue.equals(newValue)){
                CustomJTextField.this.firePropertyChange(TEXT_PROPERTY, oldValue, newValue);
                Game.listCockroachName.set(_rowNumber, newValue);
            }
        }
    }
}