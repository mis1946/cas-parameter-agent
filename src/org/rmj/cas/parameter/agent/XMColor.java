/**
 * @author  Michael Cuison
 * @date    2018-04-19
 */
package org.rmj.cas.parameter.agent;

import org.json.simple.JSONObject;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.iface.XMRecord;
import org.rmj.appdriver.agentfx.ui.showFXDialog;
import org.rmj.cas.parameter.base.Color;
import org.rmj.cas.parameter.pojo.UnitColor;

public class XMColor implements XMRecord{
    public XMColor(GRider foGRider, String fsBranchCD, boolean fbWithParent){
        this.poGRider = foGRider;
        if (foGRider != null){
            this.pbWithParent = fbWithParent;
            this.psBranchCd = fsBranchCD;
            
            poControl = new Color();
            poControl.setGRider(foGRider);
            poControl.setBranch(fsBranchCD);
            poControl.setWithParent(fbWithParent);
            
            pnEditMode = EditMode.UNKNOWN;
        }
    }
    
    @Override
    public void setMaster(int fnCol, Object foData) {
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poData.getColumn("cRecdStat") ||
                fnCol == poData.getColumn("sModified") ||
                fnCol == poData.getColumn("dModified"))){
                
                poData.setValue(fnCol, foData);
            }
        }
    }

    @Override
    public void setMaster(String fsCol, Object foData) {
        setMaster(poData.getColumn(fsCol), foData);
    }

    @Override
    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN || poControl == null)
         return null;
      else{
         return poData.getValue(fnCol);
      }
    }

    @Override
    public Object getMaster(String fsCol) {
        return getMaster(poData.getColumn(fsCol));
    }

    @Override
    public boolean newRecord() {
        clearMessage();
                
        poData = poControl.newRecord();
        
        if (poData == null){
            showMessage();
            return false;
        }else{
            pnEditMode = EditMode.ADDNEW;
            return true;
        }
    }

    @Override
    public boolean openRecord(String fstransNox) {
        clearMessage();
        
        poData = poControl.openRecord(fstransNox);
        
        if (poData.getColorCode()== null){
            showMessage();
            return false;
        } else{
            pnEditMode = EditMode.READY;
            return true;
        }
    }

    @Override
    public boolean updateRecord() {
        if(pnEditMode != EditMode.READY) {
         return false;
      }
      else{
         pnEditMode = EditMode.UPDATE;
         return true;
      }
    }

    @Override
    public boolean saveRecord() {
        clearMessage();
        
        if(pnEditMode == EditMode.UNKNOWN){
            return false;
        }else{
            // Perform testing on values that needs approval here...
            UnitColor loResult;
            if(pnEditMode == EditMode.ADDNEW)
                loResult = poControl.saveRecord(poData, "");
            else loResult = poControl.saveRecord(poData, (String) poData.getValue(1));

            if(loResult == null){
                showMessage();
                return false;
            }else{
                pnEditMode = EditMode.READY;
                poData = loResult;
                return true;
            }
      }
    }

    @Override
    public boolean deleteRecord(String fsTransNox) {
        clearMessage();
        
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.deleteRecord(fsTransNox);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else showMessage();

            return lbResult;
        }
    }

    @Override
    public boolean deactivateRecord(String fsTransNox) {
        clearMessage();
        
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.deactivateRecord(fsTransNox);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else showMessage();

            return lbResult;
      }
    }

    @Override
    public boolean activateRecord(String fsTransNox) {
        clearMessage();
        
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.activateRecord(fsTransNox);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else showMessage();

            return lbResult;
        }
    }

    @Override
    public void setBranch(String foBranchCD) {
        psBranchCd = foBranchCD;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    //Added methods
    public void setGRider(GRider foGrider){
        this.poGRider = foGrider;
        this.psUserIDxx = foGrider.getUserID();
        
        if (psBranchCd.isEmpty()) psBranchCd = poGRider.getBranchCode();
    }
    
    public boolean browseRecord(String fsValue, boolean fbByCode){        
        JSONObject loJSON = searchColor(fsValue, fbByCode);
        
        if(loJSON == null)
            return false;
        else
            return openRecord((String) loJSON.get("sColorCde"));
    }
    
    public JSONObject searchColor(String fsValue, boolean fbByCode){
        String lsHeader = "Code»Name";
        String lsColName = "sColorCde»sDescript";
        String lsColCrit = "sColorCde»sDescript";
        String lsSQL = "SELECT " +
                            "  sColorCde" +
                            ", sDescript" + 
                            ", cRecdStat" + 
                        " FROM Color";        
        
        JSONObject loJSON = showFXDialog.jsonSearch(poGRider, 
                                            lsSQL, 
                                            fsValue, 
                                            lsHeader, 
                                            lsColName, 
                                            lsColCrit, 
                                            fbByCode ? 0 : 1);
        
        return loJSON;
    }
    
    private void showMessage(){
        psMessagex = poControl.getMessage();
        psErrorMsg = poControl.getErrMsg();
    }
    
    private void clearMessage(){
        psMessagex = "";
        psErrorMsg = "";
    }
    
    public String getMessage(){return psMessagex;}
    public String getErrMsgx(){return psErrorMsg;}
    
    private String psMessagex;
    private String psErrorMsg;
    
    //Member Variables
    private GRider poGRider;
    private Color poControl;
    private UnitColor poData;
    
    private String psBranchCd;
    private int pnEditMode;
    private String psUserIDxx;
    private boolean pbWithParent;
    
    private final String pxeModuleName = "XMColor";
}
