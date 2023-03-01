package org.rmj.cas.parameter.fx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.CommonUtils;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.callback.IFXML;
import org.rmj.cas.client.application.ClientFX;
import org.rmj.cas.client.base.XMClient;
import org.rmj.cas.parameter.agent.XMBranch;
import org.rmj.cas.parameter.agent.XMSupplier;
import org.rmj.cas.parameter.agent.XMTerm;

public class SupplierController implements Initializable, IFXML {
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
    private TextField txtField80;
    @FXML
    private TextField txtField81;
    @FXML
    private TextField txtField03;
    @FXML
    private TextField txtField06;
    @FXML
    private TextField txtField04;
    @FXML
    private TextField txtField07;
    @FXML
    private TextField txtField05;
    @FXML
    private TextField txtField08;
    @FXML
    private TextField txtField09;
    @FXML
    private TextField txtField10;
    @FXML
    private TextField txtField14;
    @FXML
    private TextField txtField16;
    @FXML
    private TextField txtField12;
    @FXML
    private TextField txtField13;
    @FXML
    private TextArea txtField11;
    @FXML
    private CheckBox Check18;
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
        poRecord = new XMSupplier(poGRider, poGRider.getBranchCode(), false);
                
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
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField04.focusedProperty().addListener(txtField_Focus);
        txtField05.focusedProperty().addListener(txtField_Focus);
        txtField06.focusedProperty().addListener(txtField_Focus);
        txtField07.focusedProperty().addListener(txtField_Focus);
        txtField08.focusedProperty().addListener(txtField_Focus);
        txtField09.focusedProperty().addListener(txtField_Focus);
        txtField10.focusedProperty().addListener(txtField_Focus);
        txtField12.focusedProperty().addListener(txtField_Focus);
        txtField13.focusedProperty().addListener(txtField_Focus);
        txtField14.focusedProperty().addListener(txtField_Focus);
        txtField16.focusedProperty().addListener(txtField_Focus);
        txtField80.focusedProperty().addListener(txtField_Focus);
        txtField81.focusedProperty().addListener(txtField_Focus);
        txtField11.focusedProperty().addListener(txtArea_Focus);
        
        txtField03.setOnKeyPressed(this::txtField_KeyPressed);
        txtField04.setOnKeyPressed(this::txtField_KeyPressed);
        txtField05.setOnKeyPressed(this::txtField_KeyPressed);
        txtField06.setOnKeyPressed(this::txtField_KeyPressed);
        txtField07.setOnKeyPressed(this::txtField_KeyPressed);
        txtField08.setOnKeyPressed(this::txtField_KeyPressed);
        txtField09.setOnKeyPressed(this::txtField_KeyPressed);
        txtField10.setOnKeyPressed(this::txtField_KeyPressed);
        txtField12.setOnKeyPressed(this::txtField_KeyPressed);
        txtField13.setOnKeyPressed(this::txtField_KeyPressed);
        txtField14.setOnKeyPressed(this::txtField_KeyPressed);
        txtField16.setOnKeyPressed(this::txtField_KeyPressed);
        txtField80.setOnKeyPressed(this::txtField_KeyPressed);
        txtField81.setOnKeyPressed(this::txtField_KeyPressed);
        txtField11.setOnKeyPressed(this::txtFieldArea_KeyPressed);
        
        pnEditMode = EditMode.UNKNOWN;
        
        clearFields();
        initButton(pnEditMode);
        
        pbLoaded = true;
    }

    private void txtFieldArea_KeyPressed(KeyEvent event){
        if (event.getCode() == ENTER){ 
            event.consume();
            CommonUtils.SetNextFocus((TextArea)event.getSource());
        }
    }    
    
    private void txtField_KeyPressed(KeyEvent event){
        TextField txtField = (TextField)event.getSource();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        if (event.getCode() == F3) getMaster(lnIndex);
        if (event.getCode() == ENTER){
            switch (lnIndex){
                case 80: /*sClientID*/
                    if (!psClientNm.equals(txtField.getText())){ getMaster(lnIndex);} break;
                case 81: /*sBranchCd*/
                    if (!psBranchNm.equals(txtField.getText())){ getMaster(lnIndex);} break;
                case 12: /*sTermIDx*/
                    if (!psTermName.equals(txtField.getText())){ getMaster(lnIndex);} break;
            }
                    
            CommonUtils.SetNextFocus(txtField);
        } 
    }
    
    private void getMaster(int fnIndex){        
        JSONObject lsResult;
        switch (fnIndex){
            case 80: /*sClientID*/
                XMClient loClient = new XMClient(poGRider, poGRider.getBranchCode(), true);
                lsResult = loClient.SearchClient(txtField80.getText(), false); break;
            case 81: /*sBranchCd*/
                XMBranch loBranch = new XMBranch(poGRider, poGRider.getBranchCode(), true);
                lsResult = loBranch.searchBranch(txtField81.getText(), false); break;
            case 12: /*sTermIDxx*/
                XMTerm loTerm = new XMTerm(poGRider, poGRider.getBranchCode(), true);
                lsResult = loTerm.searchTerm(txtField12.getText(), false); break;
            default:
                return;
        }
        loadMaster(fnIndex, lsResult);
    }
    
    private void loadMaster(int fnIndex, JSONObject fsResult){
        switch (fnIndex){
            case 80:
                if (fsResult != null && !fsResult.isEmpty()){
                    psClientNm = (String) fsResult.get("sClientNm");
                    poRecord.setMaster(1, (String) fsResult.get("sClientID"));
                } else {
                    ClientFX oClient = new ClientFX(); //initialize main class
                    ClientFX.poGRider = this.poGRider;
                    ClientFX.pnClientTp = 1;
                    
                    try {
                        CommonUtils.showModal(oClient);
                    } catch (Exception ex) {
                        Logger.getLogger(SupplierController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    if (oClient.getClientID().equals("")){
                        psClientNm = "";
                        poRecord.setMaster(1, "");
                    } else{
                        psClientNm = oClient.getClientNm();
                        poRecord.setMaster(1, oClient.getClientID());
                    }
                }
                
                txtField01.setText((String) poRecord.getMaster("sClientID"));
                txtField80.setText(psClientNm);
                psOldRec = txtField01.getText();
                break;
            case 81:
                if (fsResult != null && !fsResult.isEmpty()){
                    psBranchNm = (String) fsResult.get("sBranchNm");
                    poRecord.setMaster(2, (String) fsResult.get("sBranchCd"));
                    
                    txtField02.setText((String) poRecord.getMaster("sBranchCd"));
                    txtField81.setText(psBranchNm);
                } else {
                    psBranchNm = "";
                    poRecord.setMaster(2, "");
                    txtField02.setText("");
                    txtField81.setText("");
                } psOldBrc = txtField02.getText(); break;
            case 12:
                if (fsResult != null && !fsResult.isEmpty()){
                    psTermName = (String) fsResult.get("sDescript");
                    poRecord.setMaster(fnIndex, (String) fsResult.get("sTermCode"));
                    txtField12.setText(psTermName);
                } else {
                    psTermName = "";
                    poRecord.setMaster(fnIndex, "");
                    txtField12.setText("");
                } break;
            default:
        }
    }
    
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button)event.getSource()).getId();
        
        switch (lsButton){
            case "btnBrowse":
                if (poRecord.browseRecord("%", poGRider.getBranchCode(), false)){
                    loadRecord();
                    pnEditMode = poRecord.getEditMode();
                }else{
                    ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                    return;
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
                    clearFields(); 
                    loadRecord(); 
                    
                    XMBranch loBranch = new XMBranch(poGRider, poGRider.getBranchCode(), true);
                    loadMaster(81, loBranch.searchBranch(poGRider.getBranchCode(), true));
                    txtField81.setDisable(!txtField81.getText().isEmpty());
                    
                    pnEditMode = poRecord.getEditMode();
                }else{
                    ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                    return;
                }
                break;
            case "btnSave":
                if (poRecord.saveRecord()){
                    openRecord(psOldRec, psOldBrc);
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
                        if (poRecord.activateRecord(poRecord.getMaster(1).toString(), poRecord.getMaster(2).toString())){
                            openRecord(psOldRec, psOldBrc);
                            ShowMessageFX.Information(getStage(), null, pxeModuleName, "Record Activated Successfully.");
                        }else{
                            ShowMessageFX.Information(getStage(), poRecord.getErrMsgx(), pxeModuleName, poRecord.getMessage());
                            return;
                        }
                    } else{
                        if (poRecord.deactivateRecord(poRecord.getMaster(1).toString(), poRecord.getMaster(2).toString())){
                            openRecord(psOldRec, psOldBrc);
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
    
    
    private void openRecord(String fsRecordID, String fsBranchCd){
        if (poRecord.openRecord(fsRecordID, fsBranchCd)){
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
        txtField08.setDisable(!lbShow);
        txtField09.setDisable(!lbShow);
        txtField10.setDisable(!lbShow);
        txtField12.setDisable(!lbShow);
        txtField13.setDisable(!lbShow);
        txtField14.setDisable(!lbShow);
        txtField16.setDisable(!lbShow);
        txtField11.setDisable(!lbShow);
        
        txtField80.setDisable(fnValue != EditMode.ADDNEW);
        txtField81.setDisable(fnValue != EditMode.ADDNEW);

        Check18.setDisable(true);
        
        if (lbShow)
            if (!txtField80.isDisabled())
                txtField80.requestFocus();
            else
                txtField03.requestFocus();
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
        txtField08.setText("");
        txtField09.setText("");
        txtField10.setText("");
        txtField12.setText("");
        txtField13.setText("0.00");
        txtField14.setText("0.00");
        txtField11.setText("");
        txtField80.setText("");
        txtField81.setText("");
        
        Check18.selectedProperty().setValue(false);
        btnActivate.setText("Activate");
        
        psOldRec = "";
        psBranchNm = "";
        psClientNm = "";
        psTermName = "";
    }
    
    private void loadRecord(){
        txtField01.setText((String) poRecord.getMaster(1));
        txtField02.setText((String) poRecord.getMaster(2));
        txtField03.setText((String) poRecord.getMaster(3));
        txtField04.setText((String) poRecord.getMaster(4));
        txtField05.setText((String) poRecord.getMaster(5));
        txtField06.setText((String) poRecord.getMaster(6));
        txtField07.setText((String) poRecord.getMaster(7));
        txtField08.setText((String) poRecord.getMaster(8));
        txtField09.setText((String) poRecord.getMaster(9));
        txtField10.setText((String) poRecord.getMaster(10));
        txtField11.setText((String) poRecord.getMaster(11));
        
        txtField13.setText(String.valueOf(poRecord.getMaster(13)));
        txtField14.setText(String.valueOf(poRecord.getMaster(14)));
        txtField16.setText(CommonUtils.xsDateMedium((Date) poRecord.getMaster(16)));
        
        JSONObject lsResult;
        /*sClientID*/
        if (poRecord.getMaster(1) != null && !poRecord.getMaster(1).toString().isEmpty()){
            XMClient loClient = new XMClient(poGRider, poGRider.getBranchCode(), true);
            lsResult = loClient.SearchClient((String) poRecord.getMaster(1), true);
            loadMaster(80, lsResult);
        }
        
        /*sBranchCD*/
        if (poRecord.getMaster(2) != null && !poRecord.getMaster(2).toString().isEmpty()){
            XMBranch loBranch = new XMBranch(poGRider, poGRider.getBranchCode(), true);
            lsResult = loBranch.searchBranch((String) poRecord.getMaster(2), true);
            loadMaster(81, lsResult);
        }
        
        /*sTermIDxx*/
        if (poRecord.getMaster(12) != null && !poRecord.getMaster(12).toString().isEmpty()){
            XMTerm loTerm = new XMTerm(poGRider, poGRider.getBranchCode(), true);
            lsResult = loTerm.searchTerm((String) poRecord.getMaster(12), true);
            loadMaster(12, lsResult);
        }
                 
        boolean lbCheck = poRecord.getMaster("cRecdStat").toString().equals("1");
        Check18.selectedProperty().setValue(lbCheck);
        
        if (poRecord.getMaster("cRecdStat").toString().equals("1"))
            btnActivate.setText("Deactivate");
        else
            btnActivate.setText("Activate");
        
        psOldRec = txtField01.getText();
        psOldBrc = txtField02.getText();
    }
    
    public void setGRider(GRider foGRider){this.poGRider = foGRider;}
    
    private final String pxeModuleName = "SupplierController";
    private static GRider poGRider;
    private XMSupplier poRecord;
    
    private int pnEditMode;
    private int pnIndex = -1;
    private boolean pbLoaded = false;
    
    private String psOldRec;
    private String psOldBrc;
    private String psBranchNm;
    private String psClientNm;
    private String psTermName;
    
    private final String pxeDateFormat = "yyyy-MM-dd";
    private final String pxeDateDefault = "1900-01-01";
   
    final ChangeListener<? super Boolean> txtField_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
            
        if(!nv){ /*Lost Focus*/
            switch (lnIndex){
                case 80: /*sClientID*/
                    txtField.setText(psClientNm); break;
                case 81: /*sBranchID*/
                    txtField.setText(psBranchNm); break;
                case 3:
                case 4:
                case 5:
                case 9:
                case 10:
                    if (lsValue.length() > 30) lsValue = lsValue.substring(0, 30);
                    
                    poRecord.setMaster(lnIndex, lsValue);
                    txtField.setText((String)poRecord.getMaster(lnIndex));
                    break;
                case 6:
                case 7:
                case 8:
                    if (lsValue.length() > 15) lsValue = lsValue.substring(0, 15);
                    
                    poRecord.setMaster(lnIndex, lsValue);
                    txtField.setText((String)poRecord.getMaster(lnIndex));
                    break;
                case 11:
                    if (lsValue.length() > 128) lsValue = lsValue.substring(0, 128);
                    
                    poRecord.setMaster(lnIndex, lsValue);
                    txtField.setText((String)poRecord.getMaster(lnIndex));
                    break;
                case 12: /*sTermIDxx*/
                    txtField.setText(psTermName); return;
                case 13: /*nDiscount*/
                    double lnDisc = 0;
                    try {
                        /*this must be numeric*/
                        lnDisc = Double.parseDouble(lsValue);
                    } catch (Exception e) {
                        ShowMessageFX.Warning(getStage(), "Please input numbers only.", pxeModuleName, e.getMessage());
                        txtField.requestFocus();
                    }
                    
                    if (lnDisc > 1){
                        ShowMessageFX.Warning(getStage(), "Discount value must not be grater than 1.", pxeModuleName, "Convert the percentage into decimal form.");
                        txtField.setText(String.valueOf(poRecord.getMaster(lnIndex)));
                        txtField.requestFocus();
                        return;
                    }
                    
                    poRecord.setMaster(lnIndex, lnDisc);
                    txtField.setText(String.valueOf(poRecord.getMaster(lnIndex)));
                    break;
                case 14: /*nCredLimt*/
                    double lnValue = 0;
                    try {
                        /*this must be numeric*/
                        lnValue = Double.parseDouble(lsValue);
                    } catch (Exception e) {
                        ShowMessageFX.Warning(getStage(), "Please input numbers only.", pxeModuleName, e.getMessage());
                        txtField.requestFocus();
                    }
                    
                    poRecord.setMaster(lnIndex, lnValue);
                    txtField.setText(String.valueOf(poRecord.getMaster(lnIndex)));
                    break;
                case 16: /*dCltSince*/
                    if (CommonUtils.isDate(txtField.getText(), pxeDateFormat)){
                        poRecord.setMaster(lnIndex, CommonUtils.toDate(txtField.getText()));
                    } else{
                        ShowMessageFX.Warning(getStage(), "Invalid date entry.", pxeModuleName, "Date format must be yyyy-MM-dd (e.g. 1991-07-07)");
                        poRecord.setMaster(lnIndex, CommonUtils.toDate(pxeDateDefault));
                    }
                    
                    txtField.setText(CommonUtils.xsDateLong((Date)poRecord.getMaster(lnIndex))); /*get the value from the class*/
                    break;
                default:
                    ShowMessageFX.Warning(getStage(), null, pxeModuleName, "Text field with name " + txtField.getId() + " not registered.");
            }
            pnIndex = -1;
        } else
            switch (lnIndex){
                case 16: /*dCltSince*/
                    try{
                        txtField.setText(CommonUtils.xsDateShort(lsValue));
                    }catch(ParseException e){
                        ShowMessageFX.Error(getStage(), e.getMessage(), pxeModuleName, null);
                    }
                    txtField.selectAll();
                    break;
                default:
            }
            pnIndex = lnIndex;
            txtField.selectAll();
    };
    
    final ChangeListener<? super Boolean> txtArea_Focus = (o,ov,nv)->{
        if (!pbLoaded) return;
        
        TextArea txtField = (TextArea)((ReadOnlyBooleanPropertyBase)o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();
        
        if (lsValue == null) return;
        
        if(!nv){ /*Lost Focus*/            
            switch (lnIndex){
                case 11: /*sRemarksx*/
                    poRecord.setMaster(lnIndex, lsValue);
                    txtField.setText(String.valueOf(poRecord.getMaster(lnIndex)));
            }
        }else{ 
            pnIndex = -1;
            txtField.selectAll();
        }
    };
}
