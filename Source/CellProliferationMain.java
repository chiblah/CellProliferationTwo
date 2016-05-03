/**
 * This is the main class for the CellProliferation simulation code. This class
 * is responsible for dealing with the user interface and parsing data 
 * provided by the user, initiation of instances of simulation executives,
 * setting global constants and also dealing with exceptions thrown by other
 * classes.
 *
 * @author Kyata Chibalabala
 */

import java.io.*;

public class CellProliferationMain
{
    //TODO
    // 1. A string[][] to hold the data for multiple simulations
    /* A two dimensional array to store simulation arguments. First dimension
     * stores ORGANISM, SEX, INITIAL_POPULATION_SIZE, SIMULATION_DURATION
     * TIME_INTERVAL
     */
    String[][] simulation_arguments = new String[5][3];
    
    // 2. Parse the data from STDIN to the array
    public static void main(String [ ] args)
    {
        // Parse data from GUI and handle any arising exceptions
        try
        {
            
        }catch (NumberFormatException|IOException error)
        {
            //int missing_argument = Integer.parseInt(error.getMessage()) + 1;
            //print("Argument number " + missing_argument + " missing! Enter the correct number of arguments."); // Print error message on console if there are missing arguments
            //printString("Oops! Something is wrong with the input values you provided. Check that you have entered the correct number, and types of arguments. Error type => " + error.getMessage());
            //Catch null pointer exceptions!!!
        }// try-catch
        
        
    } // Method main
    

    
//TODO LATER
// Introduce concurrency so that multip[le simulation executives can be run at
// at the same time
    
}// Class CellProliferationMain