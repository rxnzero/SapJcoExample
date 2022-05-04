
import java.util.*;
import java.io.*;

import com.sap.mw.jco.*;

public class JCOSvr extends JCO.Server implements JCO.ServerExceptionListener, JCO.ServerStateChangedListener {
	
	private static Repository repository;
	
	public JCOSvr() {
		super("","","");
	}
	
	public JCOSvr(String gwhost, String gwserv, String progid)
	{
		super(gwhost,gwserv,progid,repository);
		// Yes, we're interested in server exceptions
		JCO.addServerExceptionListener(this);
		// And we also want to know when the server(s) change their states
		JCO.addServerStateChangedListener(this);
	}

	static public class Repository extends JCO.BasicRepository implements com.sap.mw.jco.IRepository {

		/**
		 * Creates a new empty repository
		 */
		public Repository(String name)
		{
			super(name);
		}
	}
	
	public static void init() throws Exception {
		repository = new Repository("JCOSvrRepository");
		JCO.Client con = JCO.createClient("230","eai01","eai123","KO","42.6.7.23","00");
		con.connect();
        JCO.Repository rep = new JCO.Repository("JCOSvrRepository", con);
        JCO.MetaData md = (JCO.MetaData)rep.getFunctionInterface("RFC_SYSTEM_INFO");
        repository.addFunctionInterfaceToCache(md); 
        for(int i=0;i<md.getFieldCount();i++) {
    		if(md.isStructure(i)) {
    			String strName = md.getTabName(i);
    			if(strName!=null) {
    				IMetaData mdata = rep.getStructureDefinition(strName);
    				if(mdata!=null) {
    					repository.addStructureDefinitionToCache(mdata);
    					System.out.println(strName);
    				}
    			}
    		} else if(md.isTable(i)) {
    			String tabName = md.getTabName(i);
    			if(tabName!=null) {   
    				IMetaData mdata = rep.getTableDefinition(tabName);
    				if(mdata!=null) {
    					repository.addStructureDefinitionToCache(mdata);
    					System.out.println(tabName);
    				}
    			}
    		}	
		}  
        con.disconnect();
        
	}
	
	public static void main(String args[]) throws Exception {
		init();	
		JCOSvr svr = new JCOSvr("42.6.7.23","sapgw00","YEAISRV");
		svr.setTrace(true);
		svr.start();
	}
	
	protected void handleRequest(JCO.Function function)
	{
		JCO.ParameterList imports = function.getImportParameterList();
        
        System.out.println("imports >> "+imports);
        System.out.println("length >> "+imports.getFieldCount());
	}

	public void serverStateChangeOccurred(JCO.Server server, int old_state, int new_state)
	{
		System.out.print("Server " + server.getProgID() + " changed state from [");
		if ((old_state & JCO.STATE_STOPPED    ) != 0) System.out.print(" STOPPED ");
		if ((old_state & JCO.STATE_STARTED    ) != 0) System.out.print(" STARTED ");
		if ((old_state & JCO.STATE_LISTENING  ) != 0) System.out.print(" LISTENING ");
		if ((old_state & JCO.STATE_TRANSACTION) != 0) System.out.print(" TRANSACTION ");
		if ((old_state & JCO.STATE_BUSY       ) != 0) System.out.print(" BUSY ");

		System.out.print("] to [");
		if ((new_state & JCO.STATE_STOPPED    ) != 0) System.out.print(" STOPPED ");
		if ((new_state & JCO.STATE_STARTED    ) != 0) System.out.print(" STARTED ");
		if ((new_state & JCO.STATE_LISTENING  ) != 0) System.out.print(" LISTENING ");
		if ((new_state & JCO.STATE_TRANSACTION) != 0) System.out.print(" TRANSACTION ");
		if ((new_state & JCO.STATE_BUSY       ) != 0) System.out.print(" BUSY ");
		System.out.println("]");
	}
	
	/**
	 *  Simply prints the text of the exception and a stack trace
	 */
	public void serverExceptionOccurred(JCO.Server server, Exception ex)
	{
		System.out.println("Exception in server " + server.getProgID() + ":\n" + ex);
		ex.printStackTrace();
	}
}