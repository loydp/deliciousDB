package deliciousDB;

import java.util.Scanner;

public class DeliciousBusiness {

    /**
     * 
     * @param args A string array containing the command line arguments.
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        if (args.length == 0)
        {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {

                    new QueryFrame(QUERYRUNNER).setVisible(true);
                }            
            });
        }
        else
        {
            if (args[0].equals ("-console"))
            {
           
               // TODO 
                // You should code the following functionality:

                //    You need to determine if it is a parameter query. If it is, then
                //    you will need to ask the user to put in the values for the Parameters in your query
                //    you will then call ExecuteQuery or ExecuteUpdate (depending on whether it is an action query or regular query)
                //    if it is a regular query, you should then get the data by calling GetQueryData. You should then display this
                //    output. 
                //    If it is an action query, you will tell how many row's were affected by it.
                // 
                //    This is Psuedo Code for the task:  
                //    Connect()
                //    n = GetTotalQueries()
                //    for (i=0;i < n; i++)
                //    {
                //       Is it a query that Has Parameters
                //       Then
                //           amt = find out how many parameters it has
                //           Create a paramter array of strings for that amount
                //           for (j=0; j< amt; j++)
                //              Get The Paramater Label for Query and print it to console. Ask the user to enter a value
                //              Take the value you got and put it into your parameter array
                //           If it is an Action Query then
                //              call ExecuteUpdate to run the Query
                //              call GetUpdateAmount to find out how many rows were affected, and print that value
                //           else
                //               call ExecuteQuery 
                //               call GetQueryData to get the results back
                //               print out all the results
                //           end if
                //      }
                //    Disconnect()


                // NOTE - IF THERE ARE ANY ERRORS, please print the Error output
                // NOTE - The QueryRunner functions call the various JDBC Functions that are in QueryJDBC. If you would rather code JDBC
                // functions directly, you can choose to do that. It will be harder, but that is your option.
                // NOTE - You can look at the QueryRunner API calls that are in QueryFrame.java for assistance. You should not have to 
                //    alter any code in QueryJDBC, QueryData, or QueryFrame to make this work.
                
                // Need to make a QueryRunner object to use its methods
                //QueryRunner consoleQR = new QueryRunner();
                
                // Prints out welcome message
                System.out.println(WELCOME_MSG);
                boolean connected = false;
                do {
                System.out.println("\nWhich database would you like to connect to?");
                System.out.println("default:  " + DEFAULT_DB);
                System.out.println("other:  different database (must provide "
                		+ "login credentials)");
                System.out.println("\nPlease type 'default' or 'other' at prompt");
                System.out.print("\n>> ");
                connected = connectToDB(keyboard.nextLine());
                } while (!connected);
               
                
                // Create some kind of help menu 
                // Show commands:
                // - Show queries (Number, name, params)
                // - Run single query (based on number)
                // - Run all queries (use loop to go through all)
                // - Disconnect 
                
                // When executing queries with parameters, could also store
                // "default" parameter values for easy testing/demos 
                // E.g., each query has its own array of param values, then we
                // just loop through the array to input all params for a given query
                
                // ExecuteQuery must take an array of parameters and will get
                // the length of that array in QueryJDBC.ExecuteQuery
                // If there are no params, create an array size 0 and pass in
                
                

                
                
//                String[] noParams = new String[0];
//                
//                // 0 refers to the query number (index) in the queryArray
//                QUERYRUNNER.ExecuteQuery(0, noParams);
//                
//                // Gathers table header and field data.
//                String[] queryHeaders = QUERYRUNNER.GetQueryHeaders();
//                String[][] queryResults = QUERYRUNNER.GetQueryData();
//                
//                // Prints table header and field data.
//                printView(queryHeaders, queryResults);
                
                
                
                
                // end line.
                keyboard.close();               
                // TODO figure out why disconnect throws an error.
                // consoleQR.Disconnect();
            }
        }
        
    }   
    
  
    /**
     * Manages all aspects of connecting to a database
     * @param type of connection user wants (default DB with stored credentials
     * or new DB)
     * @return true if connected, false if not connected
     */
    private static boolean connectToDB(String type) {
    	boolean connected = false;
    	String host, user, pw, dbName;
    	String typeLC = type.toLowerCase();
    	switch (typeLC) {
    		case "default":
    			connected = QUERYRUNNER.Connect(DEFAULT_HOST, DEFAULT_USER, 
    					DEFAULT_PASSWORD, DEFAULT_DB);
    			printConnectionStatus(connected, DEFAULT_DB);
    			return connected;
    		case "other":
    			System.out.print("Enter hostname: ");
    			host = keyboard.nextLine();
    			System.out.print("Enter username: ");
    			user = keyboard.nextLine();
    			System.out.print("Enter password: ");
    			pw = keyboard.nextLine();
    			System.out.print("Enter name of database: ");
    			dbName = keyboard.nextLine();
    			connected = QUERYRUNNER.Connect(host, user, pw, dbName);
    			printConnectionStatus(connected, dbName);
    			return connected;
    		default:
    			System.out.println("Command not recognized. Please try again.");
    			return connected;
    	}
    }
    
    /**
     * Displays whether or not connection was successful.
     */
    private static void printConnectionStatus(boolean status, String db) {
    	if (status == true)
    		System.out.println("Connection to " + db + " successful!");
    	else
    		System.out.println("\nError with connection:\n" +
    					QUERYRUNNER.GetError() + "\n\nPlease try again.");
    }
    
    /**
     * Displays the results of queries, first using attribute names and
     * then using table fields. Gives each field 20 chars.
     * This may be expanded later.
     * @param queryResults
     */
    public static void printView( String[] queryHeaders, 
                                  String[][] queryResults) {
   
        for (String attribute : queryHeaders) {
            System.out.printf("|  %-20s", attribute);
            // Add in dashed horizontal line
        }
        
        System.out.println();
        
        for (String[] row : queryResults) {
            for (String field : row) {
                System.out.printf("|  %-20s", field);
            }
            System.out.println();
            // Maybe not have output with space between each row?
        }
    }

    private static final QueryRunner QUERYRUNNER = new QueryRunner();
    private static Scanner keyboard = new Scanner(System.in);
    
    private static final String WELCOME_MSG = "Welcome to QueryRunner!";
    private static final String DEFAULT_HOST = 
    		"deliciousbusiness.cespupxlvku2.us-east-1.rds.amazonaws.com";
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "cpsc5021password";
    private static final String DEFAULT_DB = "delicious_business";

}
