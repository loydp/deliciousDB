package deliciousDB;

import java.util.Scanner;

/**
 * Driver class for running both GUI and console portions of QueryRunner application
 * @author Alisa Wallace, Doug Herstad, Peter Loyd
 * @version 1.0
 */
public class DeliciousBusiness {

    /**
     * Main method.  Runs GUI setup unless "-console" provided as command line argument at runtime
     * @param args A string array containing the command line arguments.
     */
    public static void main(String[] args) {
       
        if (args.length == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new QueryFrame(QUERYRUNNER).setVisible(true);
                }            
            });
        }
        else {
            if (args[0].equals ("-console")) {
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
    
    
    /**
     * Runs through each query, using default values to fill in parameters.
     */
    private static void runAllQueries() {
        int numQueries = QUERYRUNNER.GetTotalQueries();
        for (int i = 0; i < numQueries; i++) {
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
     * then using table fields. Dynamically adjusts column width based on contents.
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
                                    queryResults[i][j].trim());    // data
                }
                System.out.print("\n");
            }
            for (int i = 0; i < totalWidth; i++)
                System.out.print("-");                          // dashed line
            
            System.out.println();
    	}
    }
    
    /**
     * Displays the outcome of an action query.
     * @param updated	outcome of action (successful or not)
     * @param numRows	number of rows the action impacted
     */
    private static void printUpdateResult(boolean updated, int numRows) {
    	if (updated) {
    		System.out.println("\n" + numRows + " row(s) successfully updated.\n");
    	}
    	else {
    		System.out.println("Update failed.");
    	}
    }
    
    /**
     * Processes user input from command line prompt 
     * @param input		user input
     */
    private static void processInput(String input) {
    	if (inMainMenu) {
	    	switch (input) {
                case "0" :
                    aboutMessage();
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

    /**
     * Displays "about" information to user and returns to main menu when
     * user hits "enter"
     */
    private static void aboutMessage() {
        System.out.println(ABOUT_MSG);
        keyboard.nextLine();
    }
    
    /**
     * Displays all available queries by name and order they are stored in
     * the QueryData object
     */
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
    
    // *** FIELDS ***
    
    // Query function
    private static final QueryRunner QUERYRUNNER = new QueryRunner();
    private static String[] queryNames = QUERYRUNNER.GetQueryNames();
    
    // Menu function
    private static boolean usingProgram = true;
    private static boolean inMainMenu;

    private static Scanner keyboard = new Scanner(System.in);
    
    // Printing and output
    private static final String MAIN_MENU = 
    		"\nMain Menu (enter option number to execute): " +
    		"\n0. About" +
    		"\n1. Run single query (view options)" +
    		"\n2. Run all queries (" + QUERYRUNNER.GetTotalQueries() + 
    			" total)" +
    		"\nX. Exit program and disconnect";

    private static final String ABOUT_MSG =
            "\n\n=== ABOUT ===\n\n" +
            "Delicious_DB is an application for eco and health conscious\n" +
            "restaurant managers who use the Delicious Business Database. \n\n" +
            "Developed by: Peter Loyd, Alisa Wallace, Doug Herstad\n" +
            "Website: deliciousbusinessdb.com\n" +
            "Version: 1.0\n\n" +
            
            "To use the menus, type the number/character corresponding to your\n" + 
            "selection in the command prompt and press enter.\n\n" +
            
            "Exiting the program can be done by entering \"X\" in any\n" +
            "menu, and pressing enter.\n\n" +
            "Notes:\n" +
            "- Queries may need specific input in order to run.\n" +
            "- When \"Run all queries\" is selected, specific values are\n" +
            "chosen for you." +
            "\n\n=== PRESS ENTER TO CONTINUE ===\n" +
            "\n";
    
    private static final String WELCOME_MSG = "\n*** Welcome to the Delicious " +
                                  "Business Database! ***";
    private static final String GOODBYE_MSG = "\nThank you for using " +
                         "the Delicious Business Database!\n";
    
    // Database defaults
    private static final String DEFAULT_HOST = 
    		"deliciousbusiness.cespupxlvku2.us-east-1.rds.amazonaws.com";
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "cpsc5021password";
    private static final String DEFAULT_DB = "delicious_business";

}
