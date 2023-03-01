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
import static javafx.scene.input.KeyCode.F3;
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
import org.rmj.cas.parameter.agent.XMBrand;
import org.rmj.cas.parameter.agent.XMCategoryLevel2;
import org.rmj.cas.parameter.agent.XMInventoryType;
import org.rmj.cas.parameter.agent.XMModel;

public class ModelController implements Initializable, IFXML{

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
    private TextField txtField04;
    @FXML
    private TextField txtField05;
    @FXML
    private TextField txtField06;
    @FXML
    private TextField txtField07;
    @FXML
    private CheckBox Check09;
    @FXML
    private CheckBox Check08;
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
    public void initialize(URL url, ResourceBundle rb){
        /*Initialize class*/
        poRecord = new XMModel(poGRider, poGRider.getBranchCode(), false);
                
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
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField05.focusedProperty().addListener(txtField_Focus);
        txtField06.focusedProperty().addListener(txtField_Focus);
        txtField07.focusedProperty().addListener(txtField_Focus);
        
        /*Add keypress event for field with search*/
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField05.setOnKeyPressed(this::txtField_KeyPressed);
        txtField06.setOnKeyPressed(this::txtField_KeyPressed);
        txtField07.setOnKeyPressed(this::txtField_KeyPressed);
        
        pnEditMode = EditMode.UNKNOWN;
        
        clearFields();
        initButton(pnEditMode);
        
        pbLoaded = true;
    }    
    
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8, 10));
        
        switch (event.getCode()){
            case F3:
                JSONObject lsResult;
                switch (lnIndex){
                    case 2:
                        XMInventoryType loType = new XMInventoryType(poGRider, poGRider.getBranchCode(), true);
                        lsResult = loType.searchInvType(txtField02.getText(), false);
                        loadMaster(2, lsResult);
                        break;
                    case 6:
                        XMBrand loBrand = new XMBrand(poGRider, poGRider.getBranchCode(), true);
                        lsResult = loBrand.searchBrand(txtField06.getText(), false);
                        loadMaster(6, lsResult);
                        break;
                    case 7:
                        XMCategoryLevel2 loCategory = new XMCategoryLevel2(poGRider, poGRider.getBranchCode(), true);
                        lsResult = loCategory.searchCategory(txtField07.getText(), false);
                        loadMaster(7, lsResult);
                        break;
                    default:
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
                    loadRecord(); pnEditMode = poRecord.getEditMode();
                }else{
                    ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                    return;
                } 
                break;
            case "btnSave":
                /*send other master info*/
                poRecord.setMaster(8, Check08.selectedProperty().get() == true ? "1" : "0");
                
                if (poRecord.saveRecord()){
                    ShowMessageFX.Information(getStage(), null, pxeModuleName, "Record Save Successfully.");
                    openRecord(psOldRec);
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
        txtField04.setDisable(!lbShow);
        txtField05.setDisable(!lbShow);
        txtField06.setDisable(!lbShow);
        txtField07.setDisable(!lbShow);
        Check08.setDisable(!lbShow);
        Check09.setDisable(true);
        
        if (lbShow)
            txtField02.requestFocus();
        else
            btnNew.requestFocus();
    }
    
    private void clearFields(){
        txtField01.setText("");
        txtField02.setText("");
        txtField03.setText("");
        txtField04.setText("");
        txtField05.setText("");
        txtField06.setText("");
        txtField07.setText("");
        
        Check08.selectedProperty().setValue(false);
        Check09.selectedProperty().setValue(false);
        btnActivate.setText("Activate");
        
        psOldRec = "";
        psInvType = "";
        psCategrNm = "";
    }
    
    private void loadRecord(){
        txtField01.setText((String) poRecord.getMaster(1));
        txtField03.setText((String) poRecord.getMaster(3));
        txtField04.setText((String) poRecord.getMaster(4));
        txtField05.setText((String) poRecord.getMaster(5));
        
        JSONObject lsResult;
        
        /*Load the inventory type*/
        if (poRecord.getMaster(2) != null && !poRecord.getMaster(2).toString().isEmpty()){
            XMInventoryType loType = new XMInventoryType(poGRider, poGRider.getBranchCode(), true);
            lsResult = loType.searchInvType((String) poRecord.getMaster(2), true);
            loadMaster(2, lsResult);
        } else
            txtField02.setText("");
        
        /*Load the brand*/
        if (poRecord.getMaster(6) != null && !poRecord.getMaster(6).toString().isEmpty()){
            XMBrand loBrand = new XMBrand(poGRider, poGRider.getBranchCode(), true);
            lsResult = loBrand.searchBrand((String) poRecord.getMaster(6), true);
            loadMaster(6, lsResult);
        } else
            txtField06.setText("");
        
        /*Load the main category*/
        if (poRecord.getMaster(7) != null && !poRecord.getMaster(7).toString().isEmpty()){
            XMCategoryLevel2 loCategory = new XMCategoryLevel2(poGRider, poGRider.getBranchCode(), true);
            lsResult = loCategory.searchCategory((String) poRecord.getMaster(7), true);
            loadMaster(7, lsResult);
        } else
            txtField07.setText("");
        
        boolean lbCheck;
        lbCheck = poRecord.getMaster("cEndOfLfe").toString().equals("1");
        Check08.selectedProperty().setValue(lbCheck);
        lbCheck = poRecord.getMaster("cRecdStat").toString().equals("1");
        Check09.selectedProperty().setValue(lbCheck);
        
        if (poRecord.getMaster("cRecdStat").toString().equals("1"))
            btnActivate.setText("Deactivate");
        else
            btnActivate.setText("Activate");
        
        psOldRec = txtField01.getText();
    }
            
    public void setGRider(GRider foGRider){this.poGRider = foGRider;}
    
    private final String pxeModuleName = "ModelController";
    private static GRider poGRider;
    private XMModel poRecord;
    
    private int pnEditMode;
    private boolean pbLoaded = false;
    private String psOldRec;
    
     /*search description container*/
    private String psInvType = "";
    private String psBrandNme = "";
    private String psCategrNm = "";
   
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                case 2:
                    if (lsValue.equals("")){
                        psInvType = "";
                        poRecord.setMaster(2, "");
                    }else
                        txtField.setText(psInvType);
                    return;
                case 6:
                    if (lsValue.equals("")){ 
                        psBrandNme = "";
                        poRecord.setMaster(6, "");
                    }else
                        txtField.setText(psBrandNme);
                    return;
                case 7:
                    if (lsValue.equals("")){ 
                        psCategrNm = "";
                        poRecord.setMaster(7, "");
                    }else
                        txtField.setText(psCategrNm);
                    return;
                case 3: if (lsValue.length() > 32) lsValue = lsValue.substring(0, 32); break;
                case 4: 
                case 5: if (lsValue.length() > 64) lsValue = lsValue.substring(0, 64); break;                    
                default:
                    ShowMessageFX.Warning(getStage(), null, pxeModuleName, "Text field with name " + txtField.getId() + " not registered.");
                    return;
            }
            
            poRecord.setMaster(lnIndex, lsValue);
            txtField.setText((String)poRecord.getMaster(lnIndex));
        } else
            txtField.selectAll();
    };
    
    private void loadMaster(int fnIndex, JSONObject fsResult){
        switch (fnIndex){
            case 2:
                if (fsResult != null){
                    psInvType = (String) fsResult.get("sDescript");
                    poRecord.setMaster(2, (String) fsResult.get("sInvTypCd"));
                    txtField02.setText(psInvType);
                } else {
                    psInvType = "";
                    poRecord.setMaster(2, "");
                    txtField02.setText("");
                }
                break;
            case 6:
                if (fsResult != null){
                    psBrandNme = (String) fsResult.get("sDescript");
                    poRecord.setMaster(6, (String) fsResult.get("sBrandCde"));
                    txtField06.setText(psBrandNme);
                } else {
                    psBrandNme = "";
                    poRecord.setMaster(6, "");
                    txtField06.setText("");
                }
                break;
            case 7:
                if (fsResult != null){
                    psCategrNm = (String) fsResult.get("sDescript");
                    poRecord.setMaster(7, (String) fsResult.get("sCategrCd"));
                    txtField07.setText(psCategrNm);
                } else {
                    psCategrNm = "";
                    poRecord.setMaster(7, "");
                    txtField07.setText("");
                }
                break;
        }
    }
}
