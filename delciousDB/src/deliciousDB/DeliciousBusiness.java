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
                // CONNECTION
                boolean connected = false;
                do {
                    System.out.println(
                        "\nWhich database would you like to connect to?");
                    System.out.println("default:  " + DEFAULT_DB);
                    System.out.println(
                        "other:  different database (must provide " +
                        "login credentials)");
                    System.out.println(
                        "\nPlease type 'default' or 'other' at prompt");
                    System.out.print("\n>> ");
                    connected = connectToDB(keyboard.nextLine());
                } while (!connected);
                
                // ENTER MAIN MENU LOOP
                System.out.println(WELCOME_MSG);
                inMainMenu = true;
                
                while (usingProgram) {
                    if (inMainMenu)
                    	System.out.print(MAIN_MENU);
                    else {
                    	printQueryOptions();
                    }
                    System.out.print("\n\n>> ");
                    String input = keyboard.nextLine();
                    processInput(input);
                }
                
                // SHUTDOWN         
                boolean disconnected = QUERYRUNNER.Disconnect();
                if (disconnected == false) 
                	System.out.println(QUERYRUNNER.GetError());
                else
                	System.out.println("\nDisconnection successful");
                System.out.println(GOODBYE_MSG);
                keyboard.close();  
            }
        }
        
    }   
    
  
    /**
     * Manages all aspects of connecting to a database
     * @param type of connection user wants (default DB with stored credentials
     * or new DB)
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
    		System.out.println("\nConnection to " + db + " successful!");
    	else
    		System.out.println("\nError with connection:\n" +
    					QUERYRUNNER.GetError() + "\n\nPlease try again.");
    }
    
    
    /**
     * RunQuery runs a single query, including asking for input if that is
     * necessary. If it's being run as part of an iteration in autoRunQuery,
     * it will auto-fill necessary parameters with default values.
     * @param i The index of a query to be run
     * @param autoRun Boolean indicating if this function is being called by
     * autoRunQuery.
     */
    private static void runQuery(int i, boolean autoRun) {
        boolean actionStatus;
    	// Get parameters for query
    	int numParams = QUERYRUNNER.GetParameterAmtForQuery(i);
    	
    	String[] params = new String[numParams];
    	
    	// If there are params, get user input for them
    	if (numParams > 0) 
    		System.out.println("Please input query parameters, or " +
    		        "press ENTER to auto-fill\n");
    	
    	// For num params times
    	for (int j = 0; j < numParams; j++) {
    		if (autoRun == false)
    		    params[j] = getParamFromUser(i, j);
    		else
                params[j] = autoFillParam(i, j);
    	}
    	
    	// Determine proper action for query type
    	if (QUERYRUNNER.isActionQuery(i)) {
    		actionStatus = QUERYRUNNER.ExecuteUpdate(i, params);
    		// Need to print successful update 
    		printUpdateResult(actionStatus, QUERYRUNNER.GetUpdateAmount());
    	}
    	else {
    		actionStatus = QUERYRUNNER.ExecuteQuery(i, params);
    		// Gathers table header and field data.
            String[] queryHeaders = QUERYRUNNER.GetQueryHeaders();
            String[][] queryResults = QUERYRUNNER.GetQueryData();
            
            // Prints table header and field data.
            printView(actionStatus, queryHeaders, queryResults);
    	}
    	
    	if (autoRun == true || i < QUERYRUNNER.GetTotalQueries() - 2) {
            System.out.println(">> (press ENTER to continue)");
            keyboard.nextLine();
    	}
    	
    }
    
    
    //TODO reinstate "add dietary restriction" by removing the - 1 in the initial for loop
    /**
     * Runs through each query, using default values to fill in parameters.
     */
    public static void runAllQueries() {
        int numQueries = QUERYRUNNER.GetTotalQueries();
        for (int i = 0; i < numQueries - 1; i++) {
            runQuery(i, true);
        }
    }
    
    /**
     * Prompts and passes on a necessary parameter from the user.
     * @param queryNum
     * @param paramNum
     * @return
     */
    private static String getParamFromUser(int queryNum, int paramNum) {
        String paramName = QUERYRUNNER.GetParamText(queryNum, paramNum);
        System.out.print(paramName + ": ");
        String ret = keyboard.nextLine();
        if (ret.isEmpty()) {
            ret = QUERYRUNNER.GetParamDefault(queryNum, paramNum);
            System.out.print(ret + "\n");
        }
        return ret;
    }
    
    /**
     * Autofills necessary parameters. Used as part of the runAllQueries
     * function.
     * @param queryNum
     * @param paramNum
     * @return string ret
     */
    private static String autoFillParam(int queryNum, int paramNum) {
        String paramName = QUERYRUNNER.GetParamText(queryNum, paramNum);
        System.out.print(paramName + ": ");
        String ret = QUERYRUNNER.GetParamDefault(queryNum, paramNum);
        System.out.println(ret + "\n");
        return ret;
    }
    
    
    /**
     * Displays the results of queries, first using attribute names and
     * then using table fields. Gives each field 22 chars.
     * This may be expanded later.
     * @param queryResults
     */
    private static void printView(boolean executed, String[] queryHeaders, 
                                  String[][] queryResults) {
        
    	if (executed) {
    	    int width = queryHeaders.length;
    	    int totalWidth = 0;
    	    int[] widthArr = new int[width];
    	    
    	    // Looks at width of columns, headers then data.
            for (int i = 0; i < width; i++) {
                if (queryHeaders[i].length() > widthArr[i])
                    widthArr[i] = queryHeaders[i].length();
            }
    	    for (int i = 0; i < queryResults.length ; i++) {
    	        for (int j = 0; j < width; j++) {
    	            if (queryResults[i][j].length() > widthArr[j])
    	                widthArr[j] = queryResults[i][j].length();
                }
    	    }

    	    // calculates total width
    	    for (int attWidth : widthArr)
    	        totalWidth += attWidth + 3;

    	    //Printing starts here.
            for (int i = 0; i < totalWidth; i++)
                System.out.print("-");                          // dashed line
            System.out.println();
            for (int i = 0; i < width; i++) {
                System.out.printf("| %-" + widthArr[i] + "s ", 
                                            queryHeaders[i]);   // headers
            }
            System.out.println();
            for (int i = 0; i < totalWidth; i++)
                System.out.print("-");                          // dashed line
            System.out.println();
            for (int i = 0; i < queryResults.length; i++) {
                for (int j = 0; j < width; j++) {
                    System.out.printf("| %-" + widthArr[j] + "s ", 
                                            queryResults[i][j]);    // data
                }
                System.out.print("\n");
            }
            for (int i = 0; i < totalWidth; i++)
                System.out.print("-");                          // dashed line
            
            System.out.println();
    	}
    }
    
    private static void printUpdateResult(boolean updated, int numRows) {
    	if (updated) {
    		System.out.println("\n" + numRows + " row(s) successfully updated.\n");
    	}
    	else {
    		System.out.println("Update failed.");
    	}
    }
    
    private static void processInput(String input) {
    	if (inMainMenu) {
	    	switch (input) {
	        	// Help menu
                case "0" :
                    helpMenu(keyboard);
                    break;
	            case "1" :
	            	inMainMenu = false;
	                break;
	            case "2" :
	                runAllQueries();
	                break;
	            case "x" :
	            case "X" :
	            case "quit":
	            case "Quit":
	            case "Exit":
	            case "exit":
	                usingProgram = false;
	                break;
	            default :
	                System.out.println("\nInput not recognized, try again:");
	    	}        
    	}
    	else {
    		// See if input is an int
    		try {
    			int i = Integer.parseInt(input);
    			if (i >= 0 && i < queryNames.length) {
    				runQuery(i, false);
    			}
    			else {
    				System.out.println("Invalid query number, try again:");
    			}
    		}
    		catch (NumberFormatException nfe) {
    			if (input.equalsIgnoreCase("x")) {
        			inMainMenu = true;
        		}
    			else {
    				System.out.println("Input not recognized, try again:");
    			}
    		}
    	}
    }

    private static void helpMenu(Scanner keyboard) {
        System.out.println(HELP_MSG);
        keyboard.nextLine();
    }
    
    private static void printQueryOptions() {
    	System.out.println("\nAvailable queries (enter number to run): ");
    	for (int i = 0; i < queryNames.length; i++) {
    		System.out.print(i);
    		System.out.print(". ");
    		System.out.print(queryNames[i]);
    		System.out.println();
    	}
    	System.out.println("X. Back to main menu");
    }
    
    private static final QueryRunner QUERYRUNNER = new QueryRunner();
    private static String[] queryNames = QUERYRUNNER.GetQueryNames();
    private static final String MAIN_MENU = 
    		"\nMain Menu (enter option number to execute): " +
    		"\n0. Help menu" +
    		"\n1. Run single query (view options)" +
    		"\n2. Run all queries (" + QUERYRUNNER.GetTotalQueries() + 
    			" total)" +
    		"\nX. Exit program and disconnect";

    private static final String HELP_MSG =
            "\n\n=== HELP MENU ===\n\n" +
            "Delicious_DB is an application for eco and health conscious\n" +
            "restaurant managers, who need access to the data stored in the\n" +
            "Delicious Business Database. GUI and Console versions exist, and\n" +
            "you're using the console\n" +
            "version now.\n\n" +
            
            "Navigation is achieved via entering a character corresponding\n" + 
            "to the appropriate option, and pressing enter.\n\n" +
            
            "Exiting the program can be done by entering \"X\" in any\n" +
            "menu, and pressing enter.\n\n" +
            
            "Queries may need specific input in order to run.\n" +
            
            "When \"Run all queries\" is selected, specific values are\n" +
            "chosen for you." +
            "\n\n=== PRESS ENTER TO CONTINUE===\n" +
            "\n";
    
    private static final String WELCOME_MSG = "\n*** Welcome to the Delicious " +
                                  "Business Database! ***";
    private static final String GOODBYE_MSG = "\nThank you for using " +
                         "the Delicious Business Database!\n";
    
    private static boolean usingProgram = true;
    private static boolean inMainMenu;

    private static Scanner keyboard = new Scanner(System.in);
    private static final String DEFAULT_HOST = 
    		"deliciousbusiness.cespupxlvku2.us-east-1.rds.amazonaws.com";
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "cpsc5021password";
    private static final String DEFAULT_DB = "delicious_business";

}



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
//QueryRunner QUERYRUNNER = new QueryRunner();



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


//String[] noParams = new String[0];
//
//// 0 refers to the query number (index) in the queryArray
//QUERYRUNNER.ExecuteQuery(0, noParams);
//
//// Gathers table header and field data.
//String[] queryHeaders = QUERYRUNNER.GetQueryHeaders();
//String[][] queryResults = QUERYRUNNER.GetQueryData();
//
//// Prints table header and field data.
//printView(queryHeaders, queryResults);
