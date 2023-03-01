package org.rmj.cas.parameter.fx;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.rmj.appdriver.agentfx.callback.IFXML;

public class ParameterFX extends Application {
    public final static String pxeStageIcon = "org/rmj/appdriver/agentfx/styles/64.png";
    public final static String pxeMainFormTitle = "Parameter";
    
    private double xOffset = 0; 
    private double yOffset = 0;
    
    private IFXML poFXML;
    private String psForm;
    
    public void setFXController(IFXML foFXML){
        poFXML = foFXML;
    }
    
    public void setFXMLForm(String fsFXML){
        psForm = fsFXML;
    }
    
    @Override
    public void start(Stage stage) throws Exception {       
        if (poFXML == null || psForm.equals("")) {
            System.err.println("UNSET FXML Form or Controller...");
            return;
        }
        
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(psForm));
        
        //the controller class to the main interface
        fxmlLoader.setController(poFXML);
        
        //load the main interface
        Parent parent = fxmlLoader.load();
        
        parent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        
        parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        
        //set the main interface as the scene
        Scene scene = new Scene(parent);
        
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(pxeStageIcon));
        stage.setTitle(pxeMainFormTitle);
        stage.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
