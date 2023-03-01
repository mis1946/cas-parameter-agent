package org.rmj.cas.parameter.fx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.callback.IFXML;
import org.rmj.cas.parameter.agent.XMCategory;
import org.rmj.cas.parameter.agent.XMInventoryType;

public class CategoryController implements Initializable, IFXML{

    @FXML
    private VBox VBoxForm;
    @FXML
    private Button btnExit;
    @FXML
    private AnchorPane anchorField;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField03;
    @FXML
    private CheckBox Check04;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnActivate;
    @FXML
    private FontAwesomeIconView glyphExit;
    @FXML
    private Label lblHeader;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*Initialize class*/
        poRecord = new XMCategory(poGRider, poGRider.getBranchCode(), false);
                
        /*Set action event handler for the buttons*/
        btnBrowse.setOnAction(this::cmdButton_Click);
        btnCancel.setOnAction(this::cmdButton_Click);
        btnClose.setOnAction(this::cmdButton_Click);
        btnExit.setOnAction(this::cmdButton_Click);
        btnNew.setOnAction(this::cmdButton_Click);
        btnSave.setOnAction(this::cmdButton_Click);
        btnSearch.setOnAction(this::cmdButton_Click);
        btnUpdate.setOnAction(this::cmdButton_Click);
        btnActivate.setOnAction(this::cmdButton_Click);
                
        /*Add listener to text fields*/
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);        
        
        pnEditMode = EditMode.UNKNOWN;
        
        clearFields();
        initButton(pnEditMode);
        
        pbLoaded = true;
    }    

    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        
        switch (event.getCode()){
            case F3:
                if (lnIndex == 3){
                    XMInventoryType loInvType = new XMInventoryType(poGRider, poGRider.getBranchCode(), true);
                    JSONObject lsResult = loInvType.searchInvType(txtField03.getText(), false);
                    loadInvType(lsResult);
                }
                break;
            case ENTER:
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        
        switch (lsButton){
            case "btnBrowse":
                if (poRecord.browseRecord("%", false)){
                    loadRecord();
                    pnEditMode = poRecord.getEditMode();
                }
                break;
            case "btnCancel":
                if(ShowMessageFX.OkayCancel(getStage(), null, pxeModuleName, "Do you want to disregard changes?")== true){
                    clearFields();
                    pnEditMode = EditMode.UNKNOWN;
                    break;
                } else return;
            case "btnClose":
            case "btnExit":
                unloadForm();
                return;
            case "btnNew":
                if (poRecord.newRecord()){ 
                    loadRecord();
                    pnEditMode = poRecord.getEditMode();
                } else{
                    ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                    return;
                }
                break;
            case "btnSave":
                if (poRecord.saveRecord()){
                    openRecord(psOldRec);
                    ShowMessageFX.Information(getStage(), null, pxeModuleName, "Record Save Successfully.");
                }else{
                    ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                    return;
                }
                break;
            case "btnSearch":
                break;
            case "btnUpdate":
                if (poRecord.getMaster(1) != null && !poRecord.getMaster(1).toString().equals("")){
                    if (poRecord.updateRecord()){
                        pnEditMode = poRecord.getEditMode();
                    }
                }
                break;
            case "btnActivate":
                if (poRecord.getMaster(1) != null && !poRecord.getMaster(1).toString().equals("")){
                    if (btnActivate.getText().equals("Activate")){
                        if (poRecord.activateRecord(poRecord.getMaster(1).toString())){
                            openRecord(psOldRec);
                            ShowMessageFX.Information(getStage(), null, pxeModuleName, "Record Activated Successfully.");
                        }else{
                            ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                            return;
                        }
                    } else{
                        if (poRecord.deactivateRecord(poRecord.getMaster(1).toString())){
                            openRecord(psOldRec);
                            ShowMessageFX.Information(getStage(), null, pxeModuleName, "Record Deactivated Successfully.");
                        }else{
                            ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                            return;
                        }
                    }
                }
                break;
            default:
                ShowMessageFX.Warning(getStage(), null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                return;
        }
        
        initButton(pnEditMode);
    } 
    
    private void unloadForm(){
        CommonUtils.closeStage(btnNew);
    }
    
    private Stage getStage(){
        return (Stage) btnNew.getScene().getWindow();
    }
    
    
    private void openRecord(String fsRecordID){
        if (poRecord.openRecord(fsRecordID)){
            loadRecord();
            pnEditMode = poRecord.getEditMode();
        }
    }
    
    private void initButton(int fnValue){
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        
        btnCancel.setVisible(lbShow);
        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        lblHeader.setVisible(lbShow);
                
        btnClose.setVisible(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnActivate.setVisible(!lbShow);
        btnUpdate.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        
        txtField01.setDisable(!lbShow);
        txtField02.setDisable(!lbShow);
        txtField03.setDisable(!lbShow);
        Check04.setDisable(true);
        
        if (lbShow)
            txtField02.requestFocus();
        else
            btnNew.requestFocus();
    }
    
    private void clearFields(){
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");
        
        Check04.selectedProperty().setValue(false);
        btnActivate.setText("Activate");
        
        psOldRec = "";
        psInvType = "";
    }
    
    private void loadRecord(){
        txtField01.setText((String) poRecord.getMaster(1));
        txtField02.setText((String) poRecord.getMaster(2));
        
        /*Load the inventory type*/
        if (poRecord.getMaster(3) != null && !poRecord.getMaster(3).toString().isEmpty()){
            XMInventoryType loInvType = new XMInventoryType(poGRider, poGRider.getBranchCode(), true);
            JSONObject lsResult = loInvType.searchInvType((String) poRecord.getMaster(3), true);
            loadInvType(lsResult);
        } else
            txtField03.setText("");
        
        boolean lbCheck = poRecord.getMaster("cRecdStat").toString().equals("1");
        Check04.selectedProperty().setValue(lbCheck);
        
        if (poRecord.getMaster("cRecdStat").toString().equals("1"))
            btnActivate.setText("Deactivate");
        else
            btnActivate.setText("Activate");
        
        psOldRec = txtField01.getText();
    }
            
    public void setGRider(GRider foGRider){this.poGRider = foGRider;}
    
    private final String pxeModuleName = "CategoryController";
    private static GRider poGRider;
    private XMCategory poRecord;
    
    private int pnEditMode;
    private boolean pbLoaded = false;
    private String psOldRec;
    private String psInvType = ""; /*search description container*/
   
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                case 3:
                    /*if (!txtField02.getText().isEmpty()){
                        if (!txtField02.getText().equals(psInvType)){
                            String lsResult = qsParameter.getInvType(poGRider, txtField02.getText(), 1);
                            loadInvType(lsResult);
                        }
                    }*/
                    
                    break;
                case 2:
                    if (lsValue.length() > 32) lsValue = lsValue.substring(0, 32);
                    
                    poRecord.setMaster(lnIndex, lsValue);
                    txtField.setText((String)poRecord.getMaster(lnIndex));
                    break;
                default:
                    ShowMessageFX.Warning(getStage(), null, pxeModuleName, "Text field with name " + txtField.getId() + " not registered.");
            }
        } else
            txtField.selectAll();
    };
    
    private void loadInvType(JSONObject fsResult){
        if (fsResult != null && !fsResult.isEmpty()){
            psInvType = (String) fsResult.get("sDescript");
            poRecord.setMaster(3, (String) fsResult.get("sInvTypCd"));
            txtField03.setText(psInvType);
        } else {
            psInvType = "";
            poRecord.setMaster(3, "");
            txtField03.setText("");
        }
    }
}
