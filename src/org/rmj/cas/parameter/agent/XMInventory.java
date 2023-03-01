/**
 * @author  Michael Cuison
 * @date    2018-05-03
 */
package org.rmj.cas.parameter.agent;

import org.json.simple.JSONObject;
import org.rmj.appdriver.constants.EditMode;
import org.rmj.appdriver.GRider;
import org.rmj.appdriver.iface.XMRecord;
import org.rmj.appdriver.agentfx.ShowMessageFX;
import org.rmj.appdriver.agentfx.ui.showFXDialog;
import org.rmj.cas.parameter.base.Inventory;
import org.rmj.cas.parameter.pojo.UnitInventory;

public class XMInventory implements XMRecord{
    public XMInventory(GRider foGRider, String fsBranchCD, boolean fbWithParent){
        this.poGRider = foGRider;
        if (foGRider != null){
            this.pbWithParent = fbWithParent;
            this.psBranchCd = fsBranchCD;
            
            poControl = new Inventory();
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
            if(!(fnCol == poData.getColumn("sStockIDx") ||
                fnCol == poData.getColumn("cRecdStat") ||
                fnCol == poData.getColumn("sModified") ||
                fnCol == poData.getColumn("dModified"))){
                
                if (fnCol == poData.getColumn("nUnitPrce") ||
                    fnCol == poData.getColumn("nSelPrice") ||
                    fnCol == poData.getColumn("nDiscLev1") ||
                    fnCol == poData.getColumn("nDiscLev2") ||
                    fnCol == poData.getColumn("nDiscLev3") ||
                    fnCol == poData.getColumn("nDealrDsc")){
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
        poData = poControl.newRecord();
        
        if (poData == null){
            ShowMessageFX();
            return false;
        }else{
            pnEditMode = EditMode.ADDNEW;
            return true;
        }
    }

    @Override
    public boolean openRecord(String fstransNox) {
        poData = poControl.openRecord(fstransNox);
        
        if (poData.getStockID()== null){
            ShowMessageFX();
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
        if(pnEditMode == EditMode.UNKNOWN){
            return false;
        }else{
            // Perform testing on values that needs approval here...
            UnitInventory loResult;
            if(pnEditMode == EditMode.ADDNEW)
                loResult = poControl.saveRecord(poData, "");
            else loResult = poControl.saveRecord(poData, (String) poData.getValue(1));

            if(loResult == null){
                ShowMessageFX();
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
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.deleteRecord(fsTransNox);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else ShowMessageFX();

            return lbResult;
        }
    }

    @Override
    public boolean deactivateRecord(String fsTransNox) {
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.deactivateRecord(fsTransNox);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else ShowMessageFX();

            return lbResult;
      }
    }

    @Override
    public boolean activateRecord(String fsTransNox) {
        if(pnEditMode != EditMode.READY){
            return false;
        } else{
            boolean lbResult = poControl.activateRecord(fsTransNox);
            if (lbResult)
                pnEditMode = EditMode.UNKNOWN;
            else ShowMessageFX();

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
    
    public boolean browseRecord(String fsValue, boolean fbByCode, boolean fbSearch){        
        JSONObject loJSON = searchInventory(fsValue, fbByCode, fbSearch);
        
        if(loJSON == null)
            return false;
        else
            return openRecord((String) loJSON.get("sStockIDx"));
    }
    
    public JSONObject searchInventory(String fsValue, boolean fbByCode, boolean fbSearch){
       String lsHeader = "Barcode»Description»Inv. Type»Brand»Model»Stock ID";
        String lsColName = "sBarCodex»sDescript»xInvTypNm»xBrandNme»xModelNme»sStockIDx";
        String lsColCrit = "a.sBarCodex»a.sDescript»d.sDescript»b.sDescript»c.sDescript»a.sStockIDx";
        String lsSQL = "SELECT " +
                            "  a.sStockIDx" +
                            ", a.sBarCodex" + 
                            ", a.sDescript" + 
                            ", a.sBriefDsc" + 
                            ", a.sAltBarCd" + 
                            ", a.sCategCd1" + 
                            ", a.sCategCd2" + 
                            ", a.sCategCd3" + 
                            ", a.sCategCd4" + 
                            ", a.sBrandCde" + 
                            ", a.sModelCde" + 
                            ", a.sColorCde" + 
                            ", a.sInvTypCd" + 
                            ", a.nUnitPrce" + 
                            ", a.nSelPrice" + 
                            ", a.nDiscLev1" + 
                            ", a.nDiscLev2" + 
                            ", a.nDiscLev3" + 
                            ", a.nDealrDsc" + 
                            ", a.cComboInv" + 
                            ", a.cWthPromo" + 
                            ", a.cSerialze" + 
                            ", a.cUnitType" + 
                            ", a.cInvStatx" + 
                            ", a.sSupersed" + 
                            ", a.cRecdStat" + 
                            ", b.sDescript xBrandNme" + 
                            ", c.sDescript xModelNme" + 
                            ", d.sDescript xInvTypNm" + 
                        " FROM Inventory a" + 
                            " LEFT JOIN Brand b" + 
                                " ON a.sBrandCde = b.sBrandCde" + 
                            " LEFT JOIN Model c" + 
                                " ON a.sModelCde = c.sModelCde" + 
                            " LEFT JOIN Inv_Type d" + 
                                " ON a.sInvTypCd = d.sInvTypCd"; 
        
        JSONObject loJSON;
        if (fbByCode){
            loJSON = showFXDialog.jsonSearch(poGRider, 
                                            lsSQL, 
                                            fsValue, 
                                            lsHeader, 
                                            lsColName, 
                                            lsColCrit, 
                                            5);
        } else {
            loJSON = showFXDialog.jsonSearch(poGRider, 
                                            lsSQL, 
                                            fsValue, 
                                            lsHeader, 
                                            lsColName, 
                                            lsColCrit, 
                                            fbSearch ? 0 : 1);
        }
        
        return loJSON;
    }
    
    private void ShowMessageFX(){
        if (!poControl.getErrMsg().isEmpty()){
            if (!poControl.getMessage().isEmpty())
                ShowMessageFX.Error(poControl.getErrMsg(), pxeModuleName, poControl.getMessage());
            else ShowMessageFX.Error(poControl.getErrMsg(), pxeModuleName, null);
        }else ShowMessageFX.Information(null, pxeModuleName, poControl.getMessage());
    }
    
    //Member Variables
    private GRider poGRider;
    private Inventory poControl;
    private UnitInventory poData;
    
    private String psBranchCd;
    private int pnEditMode;
    private String psUserIDxx;
    private boolean pbWithParent;
    
    private final String pxeModuleName = "XMInventory";
}
