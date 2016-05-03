/*
 * The main class responsible for inititaing the simulation, setting constants and tracking
 * the different states and properties of the cell population.
 */

import java.lang.Math.*;
import java.util.*;

public class Mitosis
{
	public final static double DIV_DELAY = 1.8; // Delay as the cell proceeds through interphase before it is allowed to enter M-Phase again ***hardcoded for now
	private static int current_timepoint = 0; // Track the current time in this simulation, initialise at 0.
	private static int newest_generation = 1; // Keep track of the most recent generation of cells
	private static int id_of_last_created_cell = -1; // The cell ID of the last cell that was created. Tracked to set the ID of the next.

	public static void main(String [ ] args)
	{
		try
		{
			//Parse command line arguments
  	  int INITIAL_POPULATION_SIZE = Integer.parseInt(args[0]);
  	  int SIM_DURATION = Integer.parseInt(args[1]);
  	  int TIME_INTERVAL = 1;//Integer.parseInt(args[2]);
  	  double percentage_of_dividing_cells = Double.parseDouble(args[2]); // Validate this value such that it is always between 0-100%
  	  int doubling_rate = Integer.parseInt(args[3]);
  	  //int cell_death_rate = Integer.parseInt(args[1]);
  	  
  	  List<Cell> cell_population = new ArrayList<Cell>();
  	  cell_population = initiatePopulation(cell_population, INITIAL_POPULATION_SIZE, percentage_of_dividing_cells);
  	  
  	  runMitosisSimulation(SIM_DURATION, TIME_INTERVAL, cell_population, doubling_rate); 
  	  
  	  //************************************************** 
  		//Print the details of the initial population
    	//************************************************** 
  		for(int i =0; i < cell_population.size(); i++)
  		{
  			//print(cell_population.toString());
  			//print("/n");
  			//initial_population[i].updateDivisionStatus();
  			//print(initial_population[i].toString());
  		}
  		//**************************************************
		}
		catch (ArrayIndexOutOfBoundsException|NumberFormatException error)
		{
			//int missing_argument = Integer.parseInt(error.getMessage()) + 1;
			//print("Argument number " + missing_argument + " missing! Enter the correct number of arguments."); // Print error message on console if there are missing arguments
			print("Oops! Something is wrong with the input values you entered. Check that you have provided the correct number, and types of input values. Error message => " + error.getMessage());
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
	 * Create the initial population of cells
	 */
	private static List<Cell> initiatePopulation(List<Cell> population, int population_size, double percentage_of_dividing_cells)
	{
		int fraction_of_dividing_cells = (int)Math.round((percentage_of_dividing_cells / 100.0 * (double) population_size)); // The number of cells able to divide
		
		// Create the starting population of cells, all as generation 1, AND all able to divide.
		for (int counter = 0; counter < population_size; counter++)
		{
			if(counter <= fraction_of_dividing_cells) //For the number of cells that can divide
			{
				population.add(new  Cell(counter, newest_generation + 1, -1, true)); // Create a new Cell object with the following values. 
			}
			else if(counter > fraction_of_dividing_cells) // For those cells that can't divide
			{
				population.add(new  Cell(counter, newest_generation + 1, -1, false));
			} // if-else if
			id_of_last_created_cell = counter; // Track the id of the lastcreated cell
		}// for
		return population;
	}// initiate_first_population()

	/*
	 * 
	 */
	private static void runMitosisSimulation(int sim_duration, int simulation_time_interval, List<Cell> cell_population, int cell_doubling_rate)
	{
		int population_size = cell_population.size(); 
		int next_div_time = cell_doubling_rate;
		//Progress through time at selected intervals for a specified duration
		for(int current_time = 0; current_time < sim_duration; current_time += simulation_time_interval)
		{
			//If the current time is a division time point as determined by the cell doubling rate
			if(current_time == next_div_time)
			{
				//Perform division of all cells that can divide
				//As new cells are being added to the list, only go through the cells in the population before new cells are added i.e use the previous population size as max number of iterations
				for(int counter = 0; counter < population_size; counter++)
				{
					if(cell_population.get(counter).getDivisionStatus())
					{
						//Remove one cell from the current generation and add two to the next
						//by changing the generation of the dividing cell to current_gen + 1 and
						//create a new cell object for the next generation
						cell_population.get(counter).setGen(cell_population.get(counter).getGen() + 1);
						cell_population.add(new  Cell(id_of_last_created_cell + 1, newest_generation + 1, -1, true));
						id_of_last_created_cell++;
					} // if the cell can divide
				}	// for all items in main population array	
				
				population_size = cell_population.size(); // Update the population size after the round of division
				newest_generation++; // Update the value of the most recent generation after the division round
				next_div_time += cell_doubling_rate; // Set the next time of division by adding the doubling rate to the current time
			}// if it is time to divide	
		
			System.out.println("Population size at time " + current_time + " = " + population_size);
		}// for
	}// runSimulation
	
	
	/*
	 * Get the current timepoint in the simulation
	 */
	public int getCurrentTimepoint()
	{
		return current_timepoint;
	}// getCurrentTimepoint
		
	/*
	 * Method to print a string passed to it
	 */
	public static void print(String print_this)
	{
		System.out.println(print_this);
	}// print
}// Class Mitosis
	