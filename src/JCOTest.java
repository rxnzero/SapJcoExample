import com.sap.mw.jco.*;


public class JCOTest extends JCO.BasicRepository {
	
	public JCOTest(String name) {
		super(name);	
	}
	
	public static void main(String args[]) throws Exception {
		new JCOTest("test").run();
	}	
	
	public void run() {
		JCO.Client con = JCO.createClient("910", "EAI01", "EAI01", "KO", "42.1.55.49", "00");
		con.connect();
        JCO.Repository rep = new JCO.Repository("SapClientRespository", con);
        //JCO.MetaData md = (JCO.MetaData)rep.getFunctionInterface("ZEAI_ECHOCLI");
        //IFunctionTemplate temp = rep.getFunctionTemplate("ZEAI_ECHOCLI");
        //System.out.println(temp);
        JCO.MetaData md = (JCO.MetaData)rep.getStructureDefinition("ZEAI_ECHOCLI");
        
        //JCO.MetaData md = (JCO.MetaData)rep.getFunctionInterface("RFC_SYSTEM_INFO");
        
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        System.out.println(md);
        System.out.println("+++++++++++++++++++++++++++++++++++++++");
        this.addFunctionInterfaceToCache(md); 
    	for(int i=0;i<md.getFieldCount();i++) {
    		if(md.getType(i)==JCO.TYPE_STRUCTURE) {
    			String strName = md.getTabName(i);
    			if(strName!=null) {
    				if(getStructureDefinition(strName)!=null) continue;
    				IMetaData mdata = rep.getStructureDefinition(strName);
    				if(mdata!=null) {
    					this.addStructureDefinitionToCache(mdata);
    					System.out.println("[SapRepository] cache the meta data of structure. - "+strName);	
    				} else {
    					System.out.println("[SapRepository] fail to cache the meta of structure. - "+strName);	
    				}
    			}
    		} else if(md.getType(i)==JCO.TYPE_TABLE) {
    			String tabName = md.getTabName(i);
    			if(tabName!=null) {   
    				if(getTableDefinition(tabName)!=null) continue;
    				IMetaData mdata = rep.getTableDefinition(tabName);
    				if(mdata!=null) {
    					this.addStructureDefinitionToCache(mdata);
    					System.out.println("[SapRepository] cache the meta data of table. - "+tabName);	
    				} else {
    					System.out.println("[SapRepository] fail to cache the meta of table. - "+tabName);	
    				}
    			}
    		}	
		}
    	con.disconnect();
		
		
	}
}