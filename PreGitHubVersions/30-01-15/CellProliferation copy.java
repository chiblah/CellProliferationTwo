/*
 * The main class responsible for inititaing the simulation executive, setting constants and tracking
 * the different states and properties of the cell population.
 */

import java.io.*;
import java.lang.Math.*;
import java.util.*;

public class CellProliferation
{
    
    private static int current_timepoint = 0; // Track the current time in this simulation, initialise at 0.
	private static int newest_generation = 1; // Keep track of the most recent generation of cells
	private static int id_of_last_created_cell = -1; // The cell ID of the last cell that was created. Tracked to set the ID of the next.

	public static void main(String [ ] args)
	{
		try
		{
            
			//Parse command line arguments
            String ORGANISM = " "; //The target organism for the simulation, determines the genome data that will be parsed by the simulation executive.
            if(args[0].equals("Hum"))
            {
                ORGANISM = "Homo sapiens";
                //printString("Human");
            }
            else if (args[0].equals("Mou"))
            {
                ORGANISM = "Mus musculus";//printString("Mouse");
            }
            
            else
            {printString("Please check that you provided a valid organism name as the first input argument!");printString("NOPE");}
               
            final int INITIAL_POPULATION_SIZE = Integer.parseInt(args[1]);
            final int SIM_DURATION = Integer.parseInt(args[2]);
            final int TIME_INTERVAL = 1;//Integer.parseInt(args[2]);
            final List<String> genome_data;
            
            /******************************************************
             * Read the genome_data file to obtain data on the size 
             * of each chromosome. Store this data in a two 
             * dimensional array
             */
            File genome_data_file = new File ("Genome_data.txt");
        
            try
            {
                genome_data = importGenomeData(genome_data_file, ORGANISM);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            /******************************************************/
            
            List<Cell> cell_population = new ArrayList<Cell>(); //Define an arraylist to hold the initial population of cells
            cell_population = initiatePopulation(cell_population, INITIAL_POPULATION_SIZE); //Initialise the initial population
            
            //Run the simulation executive, providing the duration to run the simulation for, the time intervals at which events are determined, the initial cell population
            runSimulationExecutive(SIM_DURATION, TIME_INTERVAL, cell_population, 2);
        }
		catch (ArrayIndexOutOfBoundsException|NumberFormatException error)
		{
			//int missing_argument = Integer.parseInt(error.getMessage()) + 1;
			//print("Argument number " + missing_argument + " missing! Enter the correct number of arguments."); // Print error message on console if there are missing arguments
			printString("Oops! Something is wrong with the input values you provided. Check that you have entered the correct number, and types of arguments. Error message => " + error.getMessage());
		}// try-catch
	  
	  
		//****************************************
		//1. Set a max time to run simulation
		//2. Set time intervals
		//3. Vars for fraction_dividing_cells, rate_cell_death, doubling_time/division_rate235
		//4. Don't use a static variable for current_timepoint. Provide this value each time you deal with a cell, i.e in the for loop
		//5. cell_cycle_duration
	    //6. cell_cycle_frequency
	  
		
		//***Maybe store all generations in single rows/columns of a two dimensional array?
		
	} // Method main
	
    /*
     *
     */
    private static List<String> importGenomeData(File genome_text_file, String target_organism) throws IOException
    {
        
        List<String> temp_genome_data = new ArrayList<String>(); // The genome data array that contains the sizes of each chromosome
        //int haploid_number; // Used to determine the size of the first dimension in the temp_genome_data array
        boolean found_target_organism = false; // Used to determine what action to take when a new header line in the file is found; close if true, keep reading if false
        
        // Construct BufferedReader from FileReader; search for header line of target organism and
        // obtain the haploid number then create a two dimensional array, size of the first dimension
        // equals the haploid number, size of second dimension equals two (two chromosomes to form deploid organism)
        // At this point, the next lines correspond to the size of each chromosome so populate the newly created array
        // with this information. Stop reading when no more new lines or when a new header line is found
        BufferedReader genome_file_reader = new BufferedReader(new FileReader(genome_text_file));
        
        String line = null;
        StringBuilder organism_name;
        while ((line = genome_file_reader.readLine()) != null)
        {
            String[] split_line = line.split(" ");
            //printString(split_line[0]);
            if(split_line[0].equals(">") && found_target_organism==false) // If this is the first header line that has been discovered
            {
                printString(String.valueOf(found_target_organism));
                printString(line);
                organism_name = new StringBuilder().append(split_line[1]).append(" ").append(split_line[2]); // Recreate the genus and species of the organism from the strings that were separated during the splitting of the line
                //printString(organism_name.toString());
                //printString(Integer.toString(organism_name.length()) + "__" + Integer.toString(target_organism.length()));
                //printString(organism_name.toString());
                if(organism_name.toString().equals(target_organism)) // If this is the organism of interest
                {
                    found_target_organism = true;
                    printString(organism_name.toString());
                    
                    //haploid_number = Integer.parseInt(split_line[4]); // Get the haploid number stored in the header line
                    //temp_genome_data = new int[haploid_number][2]; // Initialise the genome data array now that we know the required size
                }
                
            }
            else if (split_line[0].equals(">") && found_target_organism) // This is not the first header line and is therefore the end of data import for the target organism, close the bufferedreader
            {
                printString("first CLOSED!!!");
                //genome_file_reader.close();
            }
            temp_genome_data.add(line + "," + line);
            printString(temp_genome_data.get(temp_genome_data.size() - 1));
        }
        genome_file_reader.close();
        
        return temp_genome_data;
    }
    
	/*
	 * Method to that creates and returns and array list of the initial population of cells. 
     * The number of cells in the population is determined by the integer 'population_size'
	 */
	private static List<Cell> initiatePopulation(List<Cell> population, int population_size)
	{
    		
		// Create the starting population of cells, all as generation 1
		for (int counter = 0; counter < population_size; counter++)
		{
            population.add(new  Cell(counter, newest_generation + 1, -1, true)); // Create a new Cell object with the following values.
			
			id_of_last_created_cell = counter; // Track the id of the lastcreated cell
		}// for
		return population;
	}// initiate_first_population()

	/*
	 * 
	 */
	private static void runSimulationExecutive(int sim_duration, int simulation_time_interval, List<Cell> cell_population, int cell_doubling_rate)
	{
		int population_size = cell_population.size(); 
		int next_div_time = cell_doubling_rate;
		//Progress through time at selected intervals for a specified duration
		for(int current_time = 0; current_time < sim_duration; current_time += simulation_time_interval)
        {
            Random rand = new Random();
            //Perform division of all cells that can divide as determined by a random number generator and the set division threshhold
            //As new cells are being added to the list, only go through the cells in the population before new cells are added i.e use the previous population size as max number of iterations
            for(int counter = 0; counter < population_size; counter++)
            {
                if(cell_population.get(counter).getDivisionStatus())
                {
                    //Random number generator, 0 = divide, 1 = don't divide
                    
                    double coin_flip = rand.nextDouble();
                    //printString(Double.toString(coin_flip));
                    if(coin_flip >= 0.5)
                    {
                        //Remove one cell from the current generation and add two to the next
                        //by changing the generation of the dividing cell to current_gen + 1 and
                        //create a new cell object for the next generation
                        cell_population.get(counter).setGen(cell_population.get(counter).getGen() + 1);
                        cell_population.add(new  Cell(id_of_last_created_cell + 1, newest_generation + 1, -1, true));
                        id_of_last_created_cell++;
                    }
                        
                } // if the cell can divide
            }	// for all items in main population array
				
            population_size = cell_population.size(); // Update the population size after the round of division
            newest_generation++; // Update the value of the most recent generation after the division round
            next_div_time += cell_doubling_rate; // Set the next time of division by adding the doubling rate to the current time
			
		
			printString(current_time + " " + population_size);
		}// for
	}// runSimulationExecutive
	
	
	/*
	 * Method to access the current timepoint in the simulation executive
	 */
	public int getCurrentTimepoint()
	{
		return current_timepoint;
	}// getCurrentTimepoint
		
	/*
	 * Method to print a string passed to it
	 */
	public static void printString(String print_this)
	{
		System.out.println(print_this);
	}// print
}// Class Mitosis
	