/**
 * Example2.java
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000.
 * All rights reserved.
 */
import com.sap.mw.jco.*;

/**
 * @version 1.0
 * @author  SAP AG, Walldorf
 */
public class Example2 {

	// The MySAP.com system we gonna be using
	static final String SID = "R3";

	// The repository we will be using
	IRepository repository;

	public Example2()
	{
		try {
			// Add a connection pool to the specified system
			JCO.addClientPool(SID, 		 // Alias for this pool
							 10, 		 // Max. number of connections
							 "000",		 // SAP client 
							 "johndoe",  // userid 
							 "*****",	 // password 
							 "EN",   	 // language
							 "appserver",// host name
							 "00");

			// Create a new repository
			repository = JCO.createRepository("MYRepository", SID);
		}
		catch (JCO.Exception ex) {
			System.out.println("Caught an exception: \n" + ex);
		}
	}

	// Retrieves and prints information about remote system
	public void systemInfo()
	{
		try {

			// Get a function template from the repository
			IFunctionTemplate ftemplate = repository.getFunctionTemplate("RFC_SYSTEM_INFO");

			// Create a function from the template
			JCO.Function function = new JCO.Function(ftemplate);

			// Get a client from the pool
			JCO.Client client = JCO.getClient(SID);

			// We can call 'RFC_SYSTEM_INFO' directly since it does not need any input parameters
			client.execute(function);

			// The export parameter 'RFCSI_EXPORT' contains a structure of type 'RFCSI'
			JCO.Structure s = function.getExportParameterList().getStructure("RFCSI_EXPORT");

			// Use enumeration to loop over all fields of the structure
			System.out.println("System info for " + SID + ":\n" +
							   "--------------------");

			for (JCO.FieldIterator e = s.fields(); e.hasMoreElements(); ) {
				JCO.Field field = e.nextField();
				System.out.println(field.getName() + ":\t" + field.getString());
			}//for

			System.out.println("\n\n");

			// Release the client into the pool
			JCO.releaseClient(client);
		}
		catch (Exception ex) {
			System.out.println("Caught an exception: \n" + ex);
		}
	}

	// Retrieves and sales order list
	public void salesOrders()
	{
		try {

			// Get a function template from the repository
			IFunctionTemplate ftemplate = repository.getFunctionTemplate("BAPI_SALESORDER_GETLIST");

			// Create a function from the template
			JCO.Function function = new JCO.Function(ftemplate);

			// Get a client from the pool
			JCO.Client client = JCO.getClient(SID);

			// Fill in input parameters
			JCO.ParameterList input = function.getImportParameterList();

			input.setValue("0000001200", "CUSTOMER_NUMBER"   );
			input.setValue(      "1000", "SALES_ORGANIZATION");
			input.setValue(         "0", "TRANSACTION_GROUP" );

			// Call the remote system
			client.execute(function);

			// Print return message
			JCO.Structure ret = function.getExportParameterList().getStructure("RETURN");
			System.out.println("BAPI_SALES_ORDER_GETLIST RETURN: " + ret.getString("MESSAGE"));

			// Get table containing the orders
			JCO.Table sales_orders = function.getTableParameterList().getTable("SALES_ORDERS");

			// Print results
			if (sales_orders.getNumRows() > 0) {

				// Loop over all rows
				do {

					System.out.println("-----------------------------------------");

					// Loop over all columns in the current row
					for (JCO.FieldIterator e = sales_orders.fields(); e.hasMoreElements(); ) {
						JCO.Field field = e.nextField();
						System.out.println(field.getName() + ":\t" + field.getString());
					}//for
				} while(sales_orders.nextRow());

			}
			else {
				System.out.println("No results found");
			}//if

			// Release the client into the pool
			JCO.releaseClient(client);

		}
		catch (Exception ex) {
			System.out.println("Caught an exception: \n" + ex);
		}
	}

	public static void main(String[] argv)
	{
		Example2 e = new Example2();
		e.systemInfo();
		e.salesOrders();
	}
}
