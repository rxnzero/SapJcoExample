/**
 * Example1.java
 * Property of SAP AG, Walldorf
 * (c) Copyright SAP AG, Walldorf, 2000.
 * All rights reserved.
 */
import com.sap.mw.jco.*;

/**
 * @version 1.0
 * @author  SAP AG, Walldorf
 */
public class Example1 {

  public static void main(String[] argv)
  {
    JCO.Client client = null;

    try {

      // Print the version of the underlying JCO library
      System.out.println("\n\nVersion of the JCO-library:\n" + 
                             "---------------------------\n" + JCO.getMiddlewareVersion());

      // Create a client connection to a dedicated R/3 system
      client = JCO.createClient("000",      // SAP client
								"johndoe",  // userid
								"*****",    // password
								"EN",		// language
								"appserver",// host name
								"00");		// system number

      // Open the connection
      client.connect();

      // Get the attributes of the connection and print them 

      JCO.Attributes attributes = client.getAttributes();
      System.out.println("Connection attributes:\n" + 
                         "----------------------\n" + attributes);
      // Create the input parameter list
      JCO.ParameterList input = JCO.createParameterList();

      // Set the first (and only) import parameter
      input.appendValue("REQUTEXT", JCO.TYPE_CHAR, 255, "This is my first Jayco example.");

      // Create the out parameter list
      JCO.ParameterList output = JCO.createParameterList();

      // Specify the parameters types the function will be returning
      output.addInfo("ECHOTEXT", JCO.TYPE_CHAR, 255);
      output.addInfo("RESPTEXT", JCO.TYPE_CHAR, 255);

      // Call the function
      client.execute("STFC_CONNECTION", input, output);

      // Print the result
      System.out.println("The function 'STFC_CONNECTION' returned the following parameters:\n" + 
                         "-----------------------------------------------------------------");
      for (int i = 0; i < output.getFieldCount(); i++) {
          System.out.println("Name: " +  output.getName(i) + " Value: " + output.getString(i)); 
      }//for

      // Close the connection
      client.disconnect();

      // All done
      System.out.println("\n\nCongratulations! It worked.");
    }
    catch (Exception ex) {
      System.out.println("Caught an exception: \n" + ex);
      if (client != null) client.disconnect();
    } 
  } 
}
