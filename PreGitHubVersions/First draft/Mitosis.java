/*
 * The main class responsible for inititaing the simulation, setting constants and tracking
 * the different states and properties of the cell population.
 */
public class Mitosis
{
	public final static int INITIAL_CELL_COUNT = 500; // The number of cells to start with, 3.7x10^6 ***hard coded for now
	public final static int START_TIME = 0; // N(0) *** hard coded for now
	public final static double DIV_DELAY = 1.8; /* Delay as the cell proceeds through interphase before it is allowed to enter M-Phase again ***hardcoded for now*/
	public static int current_timepoint = START_TIME; // Track the current time in this simulation, initialise at 0.
	private static int generation_tracker = 1; // Keep track of the most recent generation of cells
	private static int id_of_last_created_cell = -1; // The cell ID of the last cell which was created. Tracked to set the ID of the next.

	public static void main(String [ ] args)
	{	
		Cell[] initial_population = new Cell[INITIAL_CELL_COUNT]; //Initiates a new array to store the new population
		// Create the starting population of cells, all as generation 1, AND all able to divide.
		for (int i = 0; i < INITIAL_CELL_COUNT; i++)
		{
			initial_population[i] = new  Cell(i, 1, -1.8); // Value for last div hard coded to -1.8 so that check in setDivisionStatus method of cell object returns true
			//id_of_last_created_cell++; // Increment in cell id after a new one is created
			print(initial_population[i].toString());
		}// for(int i = 0; i > INITIAL_CELL_COUNT; i++)
		
		
		for(int i =0; i < initial_population.length; i++)
		{
			initial_population[i].updateDivisionStatus();
			print(initial_population[i].toString());
		}
		
		//***Maybe store all generations in single rows/columns of a two dimensional array?
		
	} // Method main
		
	// Get the current timepoint in the simulation
	public int getCurrentTimepoint()
	{
		return current_timepoint;
	}// getCurrentTimepoint
		
	//Method to print a string passed to it
	public static void print(String print_this)
	{
		System.out.println(print_this);
	}// print
}// Class Mitosis
	