import com.sap.mw.jco.*;
/**
 * @author Thomas G. Schuessler, ARAsoft GmbH
 * http://www.arasoft.de
 */
public class TutorialBapi1 extends Object {
  JCO.Client mConnection;
  JCO.Repository mRepository;
  public TutorialBapi1() {
    try {
      // Change the logon information to your own system/user
      mConnection =
        JCO.createClient("001", // SAP client
          "<userid>",           // userid
          "****",               // password
          null,                 // language
          "<hostname>",         // application server host name
          "00");                // system number
      mConnection.connect();
      mRepository = new JCO.Repository("ARAsoft", mConnection);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    JCO.Function function = null;
    JCO.Table codes = null;
    try {
      function = this.createFunction("BAPI_COMPANYCODE_GETLIST");
      if (function == null) {
        System.out.println("BAPI_COMPANYCODE_GETLIST" +
                           " not found in SAP.");
        System.exit(1);
      }
      mConnection.execute(function);
      JCO.Structure returnStructure =
        function.getExportParameterList().getStructure("RETURN");
      if (! (returnStructure.getString("TYPE").equals("") ||
             returnStructure.getString("TYPE").equals("S")) ) {
        System.out.println(returnStructure.getString("MESSAGE"));
        System.exit(1);
      }
      codes =
        function.getTableParameterList().getTable("COMPANYCODE_LIST");
      for (int i = 0; i < codes.getNumRows(); i++) {
        codes.setRow(i);
        System.out.println(codes.getString("COMP_CODE") + '\t' +
                           codes.getString("COMP_NAME"));
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    try {
      codes.firstRow();
      for (int i = 0; i < codes.getNumRows(); i++, codes.nextRow()) {
        function = this.createFunction("BAPI_COMPANYCODE_GETDETAIL");
        if (function == null) {
          System.out.println("BAPI_COMPANYCODE_GETDETAIL" +
                             " not found in SAP.");
          System.exit(1);
        }
        function.getImportParameterList().
          setValue(codes.getString("COMP_CODE"), "COMPANYCODEID");
        function.getExportParameterList().setActive(false, "COMPANYCODE_ADDRESS");
        mConnection.execute(function);
        JCO.Structure returnStructure =
          function.getExportParameterList().getStructure("RETURN");
        if (! (returnStructure.getString("TYPE").equals("") ||
               returnStructure.getString("TYPE").equals("S") ||
               returnStructure.getString("TYPE").equals("W")) ) {
          System.out.println(returnStructure.getString("MESSAGE"));
        }
        JCO.Structure detail =
          function.getExportParameterList().
          getStructure("COMPANYCODE_DETAIL");
        System.out.println(detail.getString("COMP_CODE") + '\t' +
                           detail.getString("COUNTRY") + '\t' +
                           detail.getString("CITY"));
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
    mConnection.disconnect();
  }
  public JCO.Function createFunction(String name) throws Exception {
    try {
      IFunctionTemplate ft = mRepository.getFunctionTemplate(name.toUpperCase());
      if (ft == null)
        return null;
      return ft.getFunction();
    }
    catch (Exception ex) {
      throw new Exception("Problem retrieving JCO.Function object.");
    }
  }
  public static void main (String args[]) {
    TutorialBapi1 app = new TutorialBapi1();
  }
}
