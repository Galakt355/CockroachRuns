package game;

import javax.swing.*;
import java.awt.*;

public class CockroachNameFields extends JPanel{

    public CockroachNameFields() {
        JPanel grid = new JPanel();
        GridLayout layout = new GridLayout(0, 1, 0, 0);
        grid.setLayout(layout);

        for (int i=0; i<Game.CockroachNumber; i++){
            String name = generateName(i);
            final CustomJTextField textField = new CustomJTextField(10, i);
            Game.listCockroachName.add(name);
            textField.setText(name);
            grid.add(textField);
        }
        add(grid);
    }

    private String generateName(int i){
        return "Рад таракан № " + ++i;
    }
}