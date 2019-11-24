package deliciousDB;

import java.util.Scanner;

public class DeliciousBusiness {

    /**
     * 
     * @param args A string array containing the command line arguments.
     */
    public static void main(String[] args) {
        // TODO code application logic here

        final QueryRunner queryrunner = new QueryRunner();
        
        if (args.length == 0)
        {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {

                    new QueryFrame(queryrunner).setVisible(true);
                }            
            });
        }
        else
        {
            if (args[0].equals ("-console"))
            {
                System.out.println("Nothing has been implemented yet. Please implement the necessary code");
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
                QueryRunner consoleQR = new QueryRunner();
                
                // Prints out welcome message
                String welcomeMessage = "Welcome to QueryRunner.";
                System.out.println(welcomeMessage);
                
                
                // Could include the option to use stored login or new login 
                // (Stored being our DB params kept in constant variables)
                // Connect - get parameters needed for Connect() function
                Scanner keyboard = new Scanner(System.in);
                
                System.out.println("CONNECTING TO DATABASE ...");
                // System.out.print("Enter hostname: ");
                final String hostname = "deliciousbusiness.cespupxlvku2.us-east-1.rds.amazonaws.com";
                // System.out.print("Enter username: ");
                final String username = "admin";
                // System.out.print("Enter password: ");
                final String password = "cpsc5021password";
                // System.out.print("Enter name of database: ");
                final String database = "delicious_business";
                
                boolean connected = consoleQR.Connect(hostname, username, password, database);
                
                
                // Indicate whether connection was successful 
                if (connected == true) {
                    System.out.println("Connection to '" + database + "' successful.");
                }
                else {
                    System.out.println("Error with connection: " + consoleQR.GetError());
                }
                
                
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
                
                

                
                
                String[] noParams = new String[0];
                
                // 0 refers to the query number (index) in the queryArray
                consoleQR.ExecuteQuery(0, noParams);
                
                // Gathers table header and field data.
                String[] queryHeaders = consoleQR.GetQueryHeaders();
                String[][] queryResults = consoleQR.GetQueryData();
                
                // Prints table header and field data.
                printView(queryHeaders, queryResults);
                
                
                
                
                // end line.
                keyboard.close();               
                // TODO figure out why disconnect throws an error.
                // consoleQR.Disconnect();
            }
        }
        
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
            System.out.printf("|  %-20s ", attribute);
        }
        
        System.out.println();
        
        for (String[] row : queryResults) {
            for (String field : row) {
                System.out.printf("|  %-20s", field);
            }
            System.out.println();
        }
    }

}
