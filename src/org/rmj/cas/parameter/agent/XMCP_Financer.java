/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rmj.cas.parameter.agent;

import org.json.simple.JSONObject;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.agentfx.ui.showFXDialog;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.iface.XMRecord;
import org.rmj.cas.parameter.base.CP_Financer;
import org.rmj.cas.parameter.pojo.UnitCPFinancer;

/**
 *
 * @author jovanalic
 * since 2021-07-12
 */
public class XMCP_Financer implements XMRecord{
    public XMCP_Financer(GRider foGRider, String fsBranchCD, boolean fbWithParent){
        this.poGRider = foGRider;
        if (foGRider != null){
            this.pbWithParent = fbWithParent;
            this.psBranchCd = fsBranchCD;
            
            poControl = new CP_Financer();
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
        
        poData = (UnitCPFinancer) poControl.newRecord();
        
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
        
        poData = (UnitCPFinancer) poControl.openRecord(fstransNox);
        
        if (poData.getsCompnyNm()== null){
            showMessage();
            return false;
        } else{
            pnEditMode = EditMode.READY;
            return true;
        }
    }
    
     public boolean SearchMaster(int fnCol, String fsValue, boolean fbByCode){
        switch(fnCol){
            case 7: //sTermCode               
                XMTerm loTerm = new XMTerm(poGRider, psBranchCd, true);
                if (loTerm.browseRecord(fsValue, fbByCode)){
                    setMaster(fnCol, loTerm.getMaster("sTermCode"));
                    p_sTermNme = (String) loTerm.getMaster("sDescript");
                    return true;
                }
                break;
        }
        
        return false;
    }
    
    public boolean SearchMaster(String fsCol, String fsValue, boolean fbByCode){
        return SearchMaster(poData.getColumn(fsCol), fsValue, fbByCode);
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
            UnitCPFinancer loResult;
            if(pnEditMode == EditMode.ADDNEW)
                loResult = (UnitCPFinancer) poControl.saveRecord(poData, "");
            else loResult = (UnitCPFinancer) poControl.saveRecord(poData, (String) poData.getValue(1));

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
        JSONObject loJSON = searchCPFinancer(fsValue, fbByCode);
        
        if(loJSON == null)
            return false;
        else
            return openRecord((String) loJSON.get("sFnancrID"));
    }
    
    
    
    public JSONObject searchCPFinancer(String fsValue, boolean fbByCode){
        String lsHeader = "Financer ID»Company Name»Term";
        String lsColName = "sFnancrID»sCompnyNm»sTermCode";
        String lsColCrit = "a.sFnancrID»a.sCompnyNm»b.sDescript";
        
        String lsSQL = "SELECT " +
                            "  a.sFnancrID" +
                            ", a.sCompnyNm" + 
                            ", b.sDescript" + 
                            ", b.sTermCode" +
                            ", a.cRecdStat" +
                        " FROM CP_Financer a" +
                            " LEFT JOIN Term b" + 
                        " ON a.sTermIDxx = b.sTermCode" +
                        " WHERE a.cRecdStat = '1'" +
                            " AND a.cEPayment = '1'";
        
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
    private CP_Financer poControl;
    private UnitCPFinancer poData;
    private String p_sTermNme;

    public String getTerm() {
        return p_sTermNme;
    }

    public void setTerm(String p_sTermNme) {
        this.p_sTermNme = p_sTermNme;
    }
    
    private String psBranchCd;
    private int pnEditMode;
    private String psUserIDxx;
    private boolean pbWithParent;
    
    private final String pxeModuleName = this.getClass().getSimpleName();
    
}
