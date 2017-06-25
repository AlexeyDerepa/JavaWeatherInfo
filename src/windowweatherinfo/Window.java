/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package windowweatherinfo;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Alexey
 */
public class Window{
    WindowEngine engine;
    JPanel windowContent;
    GridLayout gridLayout;
    JTextField fieldCity;
    JTextField fieldKey;
    
    JLabel fieldTemperature;
    JLabel fieldHumidity;
    JLabel fieldPressure;
    JLabel fieldSpeed;
    JLabel fieldDegree;
    
    JButton go;

    public Window() {
        windowContent = new JPanel();
		
        gridLayout = new GridLayout(8, 2);

        windowContent.setLayout(gridLayout);

        fieldCity = new JTextField(10);
        fieldCity.setText("Kiev");
        windowContent.add(new JLabel("City : "));                
        windowContent.add(fieldCity);

        fieldKey = new JTextField(10);
        fieldKey.setText("b7b59940d7ef43ac24baa9fcd34d6d51");
        windowContent.add(new JLabel("Key: "));
        windowContent.add(fieldKey);

        fieldTemperature = new JLabel();
        windowContent.add(new JLabel("Температура Воздуха(°C): "));
        windowContent.add(fieldTemperature);

        fieldHumidity = new JLabel();
        windowContent.add(new JLabel("Влажность (%): "));
        windowContent.add(fieldHumidity);

        fieldPressure = new JLabel();
        windowContent.add(new JLabel("Давление (мм. рт. ст. ): "));
        windowContent.add(fieldPressure);

        fieldSpeed = new JLabel();
        windowContent.add(new JLabel("Скорость ветра: "));
        windowContent.add(fieldSpeed);

        fieldDegree = new JLabel();
        windowContent.add(new JLabel("Направление  ветра: "));
        windowContent.add(fieldDegree);


        engine = new WindowEngine(this);
        go = new JButton("Weather");
        go.addActionListener(engine);
        windowContent.add(go);
    }
    
    public void Show(){
        	

        JFrame frame = new JFrame("Window for Weather Info");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(windowContent);

        frame.setSize(600,400);
        frame.setVisible(true);
        

    }
}
