package ru.foobarbaz.neuralnetwork.som.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import ru.foobarbaz.neuralnetwork.function.distance.DistanceFunction;
import ru.foobarbaz.neuralnetwork.som.logic.SelfOrganizingMap;
import ru.foobarbaz.neuralnetwork.som.model.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

public class KohonenNetworkController {
    @FXML private GridPane mapPane;
    @FXML private TextField nameField;
    @FXML private TextField costField;
    @FXML private TextField powerField;
    @FXML private TextField volumeField;
    @FXML private TextField massField;
    @FXML private TextField capacityField;
    @FXML private ComboBox<String> propertyComboBox;
    @FXML private Canvas canvasField;

    private SelfOrganizingMap selfOrganizingMap;
    private List<Vehicle> dataSet;
    private GraphicsContext graphicsContext;

    public void initialize(SelfOrganizingMap selfOrganizingMap, List<Vehicle> dataSet){
        this.selfOrganizingMap = selfOrganizingMap;
        this.dataSet = dataSet;
        graphicsContext=canvasField.getGraphicsContext2D();
        propertyComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                drawKohonen(getClusteredList(), newValue);
            }
        });
    }

    private void drawKohonen(Map<Integer, List<Vehicle>> clusteredList, String prop) {
        ToDoubleFunction<Vehicle> f;
        switch (prop){
            case "Цена":{
                f=m->m.getCost();
                break;
            }
            case "Мощность двигателя":{
                f=m->m.getEnginePower();
                break;
            }
            case "Объем двигателя":{
                f=m->m.getEngineVolume();
                break;
            }
            case "Масса":{
                f=m->m.getMass();
                break;
            }
            case "Вместимость":{
                f=m->m.getCapacity();
                break;
            }
            default:{
                throw new IllegalArgumentException("НЕТ ТАКОГО СВОЙСТВА!1!!!");
            }
        }
        double max=dataSet.stream().mapToDouble(f).max().getAsDouble();
        int cols=(int)Math.ceil(selfOrganizingMap.getClusters()/Math.floor(Math.sqrt(selfOrganizingMap.getClusters())));
        int rows=(int)Math.ceil((double)selfOrganizingMap.getClusters()/cols);
        int num=-1;
        graphicsContext.clearRect(0,0,canvasField.getWidth(),canvasField.getHeight());
        for(int i=0;i<rows;i++){
            for (int j=0;j<cols;j++){
                if(cols*i+j>=selfOrganizingMap.getClusters())
                    break;
                double color=max*0.2;
                if(clusteredList.get(++num)!=null)
                    color=(int)(clusteredList.
                            get(num).
                            stream().
                            mapToDouble(f).
                            average().
                            getAsDouble());
                graphicsContext.setFill(Color.rgb(234, 46, 161, color/max));
                graphicsContext.fillRect(j*50,i*50,50,50);
                graphicsContext.strokeRect(j*50,i*50,50,50);
                graphicsContext.save();
            }
        }

    }

    public void readNewVehicle(){
        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setName(nameField.getText());
            vehicle.setCost(Integer.parseInt(costField.getText()));
            vehicle.setEnginePower(Float.parseFloat(powerField.getText()));
            vehicle.setEngineVolume(Float.parseFloat(volumeField.getText()));
            vehicle.setMass(Integer.parseInt(massField.getText()));
            vehicle.setCapacity(Integer.parseInt(capacityField.getText()));
            dataSet.add(vehicle);
        } catch (RuntimeException ex){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UTILITY);
            alert.setHeaderText("Введены некорректные данные!");
            alert.showAndWait();
        }
    }
    public Map<Integer, List<Vehicle>> getClusteredList(){
        Map<Integer , List<Vehicle>> map= new HashMap<>();
        for (Vehicle vehicle:dataSet) {
            int num=selfOrganizingMap.process(vehicle.getNormalizedData());
            if(map.get(num)==null){
                ArrayList<Vehicle> a=new ArrayList<>();
                a.add(vehicle);
                map.put(num, a);
                continue;
            }
            map.get(num).add(vehicle);
        }
        return map;
    }
}