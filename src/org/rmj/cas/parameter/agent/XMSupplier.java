/**
 * @author  Michael Cuison
 * @date    2018-04-19
 */
package org.rmj.cas.parameter.agent;

import org.json.simple.JSONObject;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.SQLUtil;
import org.rmj.appdriver.iface.XMRecord;
import org.rmj.appdriver.agentfx.ui.showFXDialog;
import org.rmj.cas.parameter.base.Supplier;
import org.rmj.cas.parameter.pojo.UnitSupplier;

public class XMSupplier implements XMRecord{
    public XMSupplier(GRider foGRider, String fsBranchCD, boolean fbWithParent){
        this.poGRider = foGRider;
        if (foGRider != null){
            this.pbWithParent = fbWithParent;
            this.psBranchCd = fsBranchCD;
            
            poControl = new Supplier();
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
                
                if (fnCol == poData.getColumn("nDiscount") ||
                    fnCol == poData.getColumn("nCredLimt") ||
                    fnCol == poData.getColumn("nABalance")){
                    if (foData instanceof Number){
                        poData.setValue(fnCol, foData);
                    }else poData.setValue(fnCol, null);
                } else poData.setValue(fnCol, foData);   
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
            poData.setClientID("");
            poData.setBranchCode("");
            poData.setClientSince(poGRider.getSysDate());
            poData.setLedgerNo("0");
                    
            pnEditMode = EditMode.ADDNEW;
            return true;
        }
    }

    public boolean openRecord(String fsTransNox, String fsBranchCd) {
        clearMessage();
        
        poData = poControl.openRecord(fsTransNox, fsBranchCd);
        
        if (poData.getClientID() == null || poData.getBranchCode()== null){
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
            UnitSupplier loResult;
            if(pnEditMode == EditMode.ADDNEW)
                loResult = poControl.saveRecord(poData, "", "");
            else loResult = poControl.saveRecord(poData, (String) poData.getValue(1), (String) poData.getValue(2));

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

    public boolean deleteRecord(String fsTransNox, String fsBranchCd) {
        clearMessage();
        
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.deleteRecord(fsTransNox, fsBranchCd);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else showMessage();

            return lbResult;
        }
    }

    public boolean deactivateRecord(String fsTransNox, String fsBranchCd) {
        clearMessage();
        
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.deactivateRecord(fsTransNox, fsBranchCd);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else showMessage();

            return lbResult;
      }
    }

    public boolean activateRecord(String fsTransNox, String fsBranchCd) {
        clearMessage();
        
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.activateRecord(fsTransNox, fsBranchCd);
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
       
    public boolean browseRecord(String fsValue, String fsBranchCd, boolean fbByCode){        
        JSONObject loJSON = searchSupplier(fsValue, fsBranchCd, fbByCode);
        
        if(loJSON == null)
            return false;
        else
            return openRecord((String) loJSON.get("sClientID"), (String) loJSON.get("sBranchCd"));
    }
    
    public JSONObject searchSupplier(String fsValue, String fsBranchCd, boolean fbByCode){
        String lsHeader = "ID»Name»Branch";
        String lsColName = "sClientID»sClientNm»sBranchNm";
        String lsColCrit = "a.sClientID»b.sClientNm»c.sBranchNm";
        String lsSQL = "SELECT " +
                            "  a.sClientID" +
                            ", b.sClientNm" + 
                            ", c.sBranchNm" + 
                            ", a.sBranchCd" +
                        " FROM Supplier a" + 
                            ", Client_Master b" + 
                            ", Branch c" + 
                        " WHERE a.sClientID = b.sClientID" +
                            " AND a.sBranchCd = c.sBranchCd" + 
                            " AND a.sBranchCD = " + SQLUtil.toSQL(fsBranchCd);  
        
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
    private Supplier poControl;
    private UnitSupplier poData;
    
    private String psBranchCd;
    private int pnEditMode;
    private String psUserIDxx;
    private boolean pbWithParent;
    
    private final String pxeModuleName = "XMSupplier";

    @Override
    public boolean openRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deactivateRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean activateRecord(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
