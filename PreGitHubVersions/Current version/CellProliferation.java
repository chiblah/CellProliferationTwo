/*
 * The main class responsible for inititaing the simulation executive, setting constants and tracking
 * the different states and properties of the cell population.
 */

import java.io.*;
import java.lang.Math.*;
import java.util.*;

public class CellProliferation
{
    /*
     * Global variables
     */
    private static int current_timepoint = 0; // Track the current time in this simulation, initialise at 0.
	private static int newest_generation = -1; // Keep track of the most recent generation of cells
	private static int id_of_last_created_cell = -1; // The cell ID of the last cell that was created. Tracked to set the ID of the next.
    private static int haploid_number; // Used to determine the size of the first dimension in the temp_genome_data array
    private static final int FEMALE = 1, MALE = 2, STRAND_LABELLED = 1, STRAND_UNLABELLED = 0;
    private static final boolean CAN_DIVIDE = true, CANT_DIVIDE = false;
    private static List<String>genome_data = new ArrayList<String>();
	
    /******************************************************
                  PART ONE : MAIN METHOD
    *******************************************************/
    public static void main(String [ ] args)
	{
		try
		{
			//Parse command line arguments
            String organism = null; //The target organism for the simulation, determines the genome data that will be parsed
            if(args[0].equals("Hum"))
                organism = "Homo sapiens";
            else if(args[0].equals("Mou"))
                organism = "Mus musculus";
            else if(args[0].equals("Test"))
                organism = "Test test";
            else
                printString("Please check and provide a valid organism name as the first input argument!");
            
            int sex = 0;
            if(args[1].equals("F"))
            sex = FEMALE;
            else if(args[1].equals("M"))
                sex = MALE;
            else
                printString("Please check that you provided 'F' or 'M' for gender!");
            
            final int INITIAL_POPULATION_SIZE = Integer.parseInt(args[2]);
            final int SIM_DURATION = Integer.parseInt(args[3]);
            final int TIME_INTERVAL = 24;//Integer.parseInt(args[4]);
            
            
            /******************************************************
             * Read the genome_data file to obtain data on the size 
             * of each chromosome. Store this data in a String list.
             * Each line stores the integer sizes of each chromosome 
             * pair, e.g for an organism of haploid# = 3;
             *  chr1 133797422,133797422
             *  chr2 242508799,242508799
             *  XY   198450956,130786757  <==The last line will have
             *                               two different values for
             *                               males (XY instead of XX)
             *
             */
            File genome_data_file = new File ("Genome_data.txt");
            genome_data = importGenomeData(genome_data_file, organism, sex);
           
            //printString(Integer.toString(genome_data.size()));
            //for(Iterator<String> i = genome_data.iterator(); i.hasNext();)
            //{
                //String item = i.next();
                //System.out.println(item);
            //}
            /******************************************************/
            
            List<Cell> cell_population = initiatePopulation(INITIAL_POPULATION_SIZE); //Initialise a population to be used at the start of the simulation
            
            //Run the simulation executive, providing the duration to run the simulation for, the time intervals at which events are evaluated and the initial cell population to perform the simulation of proliferation on
            cell_population = runSimulationExecutive(SIM_DURATION, TIME_INTERVAL, cell_population);
            //printLabelDistribOfPopulation(cell_population);
            generateOutput(cell_population, genome_data);


        }
		catch (NumberFormatException|IOException error)
		{
			//int missing_argument = Integer.parseInt(error.getMessage()) + 1;
			//print("Argument number " + missing_argument + " missing! Enter the correct number of arguments."); // Print error message on console if there are missing arguments
			printString("Oops! Something is wrong with the input values you provided. Check that you have entered the correct number, and types of arguments. Error type => " + error.getMessage());
            //Catch null pointer exceptions!!!
		}// try-catch

		//****************************************
		//1. Set a max time to run simulation
		//2. Set time intervals
		//3. Vars for fraction_dividing_cells, rate_cell_death, doubling_time/division_rate
		//4. Don't use a static variable for current_timepoint. Provide this value each time you deal with a cell, i.e in the for loop
		//5. cell_cycle_duration
	    //6. cell_cycle_frequency
	  
		
		//***Maybe store all generations in single rows/columns of a two dimensional array?
		
	} // Method main
    
    
    
    /*******************************************************
                  PART TWO : OUTPUT AND ANALYSIS
     *******************************************************/
    
    /*
     *
     */
    private static void generateOutput(List<Cell> population, List<String>genome_data)
    {
        String newLine = System.getProperty("line.separator");
        printString("===================" + newLine + "GENERATE OUTPUT" + newLine + "===================");
        int generation = -1;
        int[][] chromosome_sizes = new int[haploid_number][2]; //The size of each chromosome (deploid) stored in a 2D array
        int diploid_genome_size = 0; //The sum of all chromosome sizes
        
        //Produce array of chromosome sizes, garbage clean genome_data
        //For each element in genome_data, split by comma and place into two elements of chromosome sizes array
        int current_chromosome = 0;
        for(Iterator<String> i = genome_data.iterator(); i.hasNext();current_chromosome++)
        {
            String item = i.next();
            String[] homologous_pair_sizes = item.split(",");
            chromosome_sizes[current_chromosome][0] = Integer.parseInt(homologous_pair_sizes[0]);
            chromosome_sizes[current_chromosome][1] = Integer.parseInt(homologous_pair_sizes[1]);
            diploid_genome_size += chromosome_sizes[current_chromosome][0];
            diploid_genome_size += chromosome_sizes[current_chromosome][1];
            System.out.println(item);
        }
        
        //Print chromosome sizes array
        /*for(int chromosome_count = 0; chromosome_count < chromosome_sizes.length; chromosome_count++)
        {
            for(int homologous_pair_count= 0; homologous_pair_count < chromosome_sizes[chromosome_count].length; homologous_pair_count++)
            {
                printString(Integer.toString(chromosome_sizes[chromosome_count][homologous_pair_count]));
            }
        }*/
    
        printString("Size of diploid genome in base pairs = " + Integer.toString(diploid_genome_size));
        genome_data = null; // Garbage collection

        
        //new  Cell(cell_id, cell_generation, last_div, CAN_DIVIDE, diploid_genome));
        //For each cell in population 1)Track each generation and push cells to 2D array
        //2)Track % new DNA of each cell and total % new DNA of each generation
        
        double [] generation_label_percentage = new double[newest_generation+1];
        int [] cells_in_each_generation = new int[newest_generation+1];
        for (int this_generation : cells_in_each_generation)
        {
            this_generation = 0;
        }
        //Gather information from each cell

        //Go through each cell and obtain its generation number and update the corresponding % new DNA
        for(int cell_count = 0; cell_count < population.size(); cell_count++)
        {
            Cell current_cell = population.get(cell_count);
            int [][][] this_cells_genome = current_cell.getGenome();
            int generation_of_this_cell = current_cell.getGeneration();
            double cell_percentage_labelled = 0.0;
            
            // For each chromosome
            for(int chromosome_count = 0; chromosome_count < this_cells_genome.length; chromosome_count++)
            {
                double chromo_percentage_labelled = 0;// Label status of each chromosome
                for(int homologous_pair_count= 0; homologous_pair_count < this_cells_genome[chromosome_count].length; homologous_pair_count++)
                {
                    int homolog_size = chromosome_sizes[chromosome_count][homologous_pair_count]; //Chromosome size in base pairs
                    double chromosome_proportion_of_genome = (double)homolog_size/diploid_genome_size; // The size of the chromosome relative to the diploid genome
                    double strand_percentage_labelled = 0;
                    for(int dna_strand_count = 0; dna_strand_count < this_cells_genome[chromosome_count][homologous_pair_count].length; dna_strand_count++)
                    {
                        int label_status = this_cells_genome[chromosome_count][homologous_pair_count][dna_strand_count];
                        switch (label_status)
                        {
                            case STRAND_UNLABELLED:
                                //double percentage_calc =
                                //strand_percentage_labelled =

                                
                            break;
                            case STRAND_LABELLED:
                                strand_percentage_labelled = 1.0;
                                chromo_percentage_labelled = (chromo_percentage_labelled + strand_percentage_labelled) / 2;
                                strand_percentage_labelled = 0.0;
                                //printString(Double.toString(chromo_percentage_labelled));

                            break;
                        }
                    }
                    cell_percentage_labelled += chromo_percentage_labelled * chromosome_proportion_of_genome;
                }
            }// For each chromosome
            generation_label_percentage[generation_of_this_cell] += (generation_label_percentage[generation_of_this_cell] + cell_percentage_labelled) / 2;
            
            int temp_number_cells = cells_in_each_generation[generation_of_this_cell] + 1;
            cells_in_each_generation[generation_of_this_cell] = temp_number_cells;
            //printString(generation_of_this_cell + "   " + cell_percentage_labelled);
            
            //population_by_generations.add(new  Cell(current_generation),null);
        }// for
        
        //Go through the population and sort cells into the 2D array by their generation number
        //for(int current_cell = 0; current_cell < cells_in_each_generation.length; current_cell++)
        //{
           // printString(current_cell + "   " + cells_in_each_generation[current_cell]);

        //}// for
        
        for(int current_generation = 0; current_generation < generation_label_percentage.length; current_generation++)
        {
            printString("Generation: " + current_generation + "     % labelled: " + generation_label_percentage[current_generation]);
            
        }// for
        
        //printLabelDistribOfPopulation(population);
        
    }// generateOutput()
    
    /*
     *
     */
    private static void printLabelDistribOfCell(Cell cell)
    {
        int[][][] genome = cell.getGenome();
        
        for(int chromosome_count = 0; chromosome_count < genome.length; chromosome_count++)
        {
            for(int homologous_pair_count= 0; homologous_pair_count < genome[chromosome_count].length; homologous_pair_count++)
            {
                for(int dna_strand_count = 0; dna_strand_count < genome[chromosome_count][homologous_pair_count].length; dna_strand_count++)
                {
                    printString("Chromosome " + Integer.toString(chromosome_count+1) + "; Homologous Pair " + Integer.toString(homologous_pair_count+1) + "; Strand " + Integer.  toString(dna_strand_count+1) + " - Label status = " + Integer.toString(genome[chromosome_count][homologous_pair_count][dna_strand_count]));
                }
            }
        }
    }// printLabelDistribOfPopulation
    
    /*
     *
     */
    private static void printLabelDistribOfPopulation(List<Cell> population)
    {
        for(int cell_count = 0; cell_count < population.size(); cell_count++)
        {
            printString(cell_count + " "+ population.get(cell_count).toString());
            
            int[][][] blank_genome = new int[haploid_number][2][2];
            
            for(int chromosome_count = 0; chromosome_count < blank_genome.length; chromosome_count++)
            {
                for(int homologous_pair_count= 0; homologous_pair_count < blank_genome[chromosome_count].length; homologous_pair_count++)
                {
                    for(int dna_strand_count = 0; dna_strand_count < blank_genome[chromosome_count][homologous_pair_count].length; dna_strand_count++)
                    {
                        printString("Chromosome " + Integer.toString(chromosome_count+1) + "; Homologous Pair " + Integer.toString(homologous_pair_count+1) + "; Strand " + Integer.  toString(dna_strand_count+1) + " - Label status = " + Integer.toString(population.get(cell_count).getGenome()[chromosome_count][homologous_pair_count][dna_strand_count]));
                    }
                }
            }
        }// for population
    }// printLabelDistribOfPopulation
    
    /*
     * Method to print a string passed to it
     */
    public static void printString(String print_this)
    {
        System.out.println(print_this);
    }// print
    
    
    
    /*******************************************************
             PART THREE : MAIN METHOD SUB ROUTINES
     *******************************************************/
    
	/*
	 *
	 */
	private static List<Cell> runSimulationExecutive(int sim_duration, int simulation_time_interval, List<Cell> cell_population)
	{
		int population_size = cell_population.size();
        int[][][] blank_genome = new int[haploid_number][2][2]; // A blank diploid genome - The karyotype of the cell, 3 dimensional array to store discrete values for each  chromosome -> each homologous pair of chromosomes -> 2 complementary DNA strands'
		
        //Progress through time at selected intervals for a specified duration
		for(int current_time = 0; current_time < sim_duration; current_time += simulation_time_interval)
        {
            printString(current_time + " " + population_size);
            //Perform division of all cells that can divide as determined by a random number generator and the set division threshhold
            //As new cells are being added to the list, only go through the cells present in the population before new cells were added in this round i.e use the previous population size as max number of iterations.
            for(int current_element = 0; current_element < population_size; current_element++)
            {
                Cell current_cell = cell_population.get(current_element);
                boolean current_cell_can_divide = current_cell.getDivisionStatus();
                if(current_cell_can_divide)
                {
                    
                    //Produce a random number, R, between ***********????!!!!!!!!!
                    double between_zero_and_one = randomDouble();
                    if(between_zero_and_one < 0.2) //The cell can divide
                    {
                        //Remove one cell from the current generation and add two to the next
                        //by changing the generation of the original cell to current_gen + 1 and
                        //creating a new cell object also in generation current_gen + 1
                        
                        final int daughter_cell_one = current_element; //The original cell will become daughter cell one
                        
                        // Change the orginal cell to daughter cell 1 by increasing its generation value by 1, from its current generation
                        //printString("Generation of parent cell = " + daughter_cell_one.getGeneration());
                        //printString("Cell ID of parent cell = " + daughter_cell_one.getId());
                        int next_generation = cell_population.get(daughter_cell_one).getGeneration() + 1;
                        cell_population.get(daughter_cell_one).setGeneration(next_generation);
                        //This is now a new cell, change its ID to a new unique one???????
                        
                        //Create a new cell object which will become daughter cell 2, with a blank diploid genome, of same generation as the newly created daughter cell 1
                        cell_population.add(new  Cell(id_of_last_created_cell + 1, next_generation, -1, CAN_DIVIDE, newEmptyDeploidGenome()));
                        id_of_last_created_cell++;
                        printString("Population size after division = " + cell_population.size());
                        final int daughter_cell_two = (cell_population.size() - 1); //Create and set daughter cell 2 to the newly created cell object
                        printLabelDistribOfCell(cell_population.get(daughter_cell_two));
                        /*********************************************************/
                        // Track the latest generation of cells.
                        if(cell_population.get(daughter_cell_one).getGeneration() > newest_generation)
                        {newest_generation++;}
                        /*********************************************************/
                        
                        
                        //CREATE AN EMPTY GENOME
                        int[][][] empty_diploid_genome = newEmptyDeploidGenome();
                        
                        int[][][] temp_genome_d1 = cell_population.get(daughter_cell_one).getGenome(), temp_genome_d2 = empty_diploid_genome;

                        
                        printString("MITOTIC EVENT  Index of daughter cell two --->" + (cell_population.size()-1));
                        
                        //printString(Integer.toString(current_element));
                        printString("Daughter cell 1 --->" + cell_population.get(current_element).toString());
                        printLabelDistribOfCell(cell_population.get(current_element));
                        printString("Daughter cell 2 --->" + cell_population.get(cell_population.size() - 1).toString());
                        printLabelDistribOfCell(cell_population.get(cell_population.size() - 1));
                    }// if(between_zeroANDone >= 0.5)
                    
                    else //Cell doesn't divided
                    {
                        printString("Didn't divide --->" + current_cell.toString());
                        printLabelDistribOfCell(cell_population.get(current_element));

                    }

                    
                } // if the cell can divide
                
                //printString(Integer.toString(cell_population.get(current_element).getGeneration()));
            
            } // for all cells in the main population
            population_size = cell_population.size(); // Update the population size after the round of division
            printString(current_time + " " + population_size);
        }// for
        printString("Latest generation = " + (newest_generation));
        printLabelDistribOfPopulation(cell_population);

        return cell_population;
	}// runSimulationExecutive
	
    
    /*
     *
     */
    private static double randomDouble()
    {
        Random rand = new Random();
        return rand.nextDouble();
    }// randomDouble
    
     /*
      *
      */
    private static List<String> importGenomeData(File genome_text_file, String target_organism, int sex) throws IOException
    {
        
        List<String> temp_genome_data = new ArrayList<String>(); // The genome data list that contains the sizes of each chromosome on a line. Format
        //int haploid_number; // Used to determine the size of the first dimension in the temp_genome_data array
        boolean found_target_organism = false; // Used to determine what action to take when a new header line in the file is found; close if true, keep reading if false
        boolean end_of_genome = false; // True when the Y chromosome has been dealt with, for this to work the file being imported has to have the Y chr line after not before the X chr line
        
        // Construct BufferedReader from FileReader; search for header line of target organism and
        // obtain the haploid number then create a three dimensional array(size of the first dimension
        // equals the haploid number; size of second dimension equals two) i.e two chromosomes to form deploid organism.
        // At this point, the next lines correspond to the size of each chromosome so populate the newly created array
        // with this information. Stop reading when no more new lines or when a new header line is found
        BufferedReader genome_file_reader = new BufferedReader(new FileReader(genome_text_file));
        
        String line = null;
        StringBuilder organism_name;
        while ((line = genome_file_reader.readLine()) != null)
        {
            String[] split_line = line.split(" ");
            
            if(split_line[0].equals(">")) // If this is a first header line
            {
                organism_name = new StringBuilder().append(split_line[1]).append(" ").append(split_line[2]); // Recreate the genus and species of the organism from the strings that were separated during the splitting of the line
                if(organism_name.toString().equals(target_organism)) // If this line refers to the organism of interest
                {
                    found_target_organism = true;
                    haploid_number = Integer.parseInt(split_line[4]); // Get the haploid number stored in the header line
                }
                else
                {
                    found_target_organism = false;
                    continue; //This is a header line but it is not the organism of interest
                }
            }
            else if(found_target_organism && !end_of_genome)// This is not a header line so we evaluate whether we want to import this line
            {
                //boolean autosome = true;
                switch(sex)
                {
                    case 1://Female
                    {
                        if(split_line[0].equals("chrX"))
                        {
                            temp_genome_data.add(split_line[1] + "," + split_line[1]);
                        }
                        else if(split_line[0].equals("chrY"))
                        {
                            end_of_genome = true;
                        }// Ignore the Y chromosome
                        else
                            temp_genome_data.add(split_line[1] + "," + split_line[1]);// The current line is an autosome
                    }break;
                    case 2://Male
                    {
                        if(split_line[0].equals("chrX"))
                        {
                            temp_genome_data.add(split_line[1] + ",");
                        }
                        else if(split_line[0].equals("chrY"))
                        {
                            String temp = temp_genome_data.get(temp_genome_data.size() - 1); // Store the value already there
                            temp_genome_data.remove(temp_genome_data.size()-1);
                            
                            temp = temp + split_line[1];
                            temp_genome_data.add(temp);
                            
                            end_of_genome = true;
                        }
                        else
                            temp_genome_data.add(split_line[1] + "," + split_line[1]);// The current line is an autosome
                    }break;
                }
            }
        }// while ((line = genome_file_reader.readLine()) != null)
        genome_file_reader.close();
        return temp_genome_data;
    }// importGenomeData
    
    /*
     * Creates and returns an array list of the initial population of cells.
     * The total number of cells in the population is determined by the integer 'population_size'
     */
    private static List<Cell> initiatePopulation(int required_population_size)
    {
        List<Cell> population = new ArrayList<Cell>(); //Define an arraylist to hold the initial population of cells
        int[][][] diploid_genome = newEmptyDeploidGenome();
        int cell_generation = newest_generation + 1, last_div = -1;
        
        //Set the label status of all DNA strands in the genome to unlabelled
        for(int chromosome_count = 0; chromosome_count < diploid_genome.length; chromosome_count++)
        {
            for(int homologous_pair_count= 0; homologous_pair_count < diploid_genome[chromosome_count].length; homologous_pair_count++)
            {
                for(int dna_strand_count = 0; dna_strand_count < diploid_genome[chromosome_count][homologous_pair_count].length; dna_strand_count++)
                {
                    diploid_genome[chromosome_count][homologous_pair_count][dna_strand_count] = STRAND_UNLABELLED; //Set each DNA strand to unlabelled
                }
            }
        }
        
        // !****!Create the starting population of cells, all as generation 0!****!
        for (int counter = 0; counter < required_population_size; counter++)
        {
            int cell_id = counter;
            population.add(new  Cell(cell_id, cell_generation, last_div, CAN_DIVIDE, diploid_genome)); // Create a new Cell object with the following values.
            id_of_last_created_cell = cell_id; // Track the id of the last created cell
        }// for
        newest_generation++;
        return population;
    }// initiate_first_population()
	
    /*
     * Creates and returns an empty three dimensional array to store a deploid genome
     */
    private static int[][][] newEmptyDeploidGenome()
    {
        int[][][] diploid_genome = new int[haploid_number][2][2]; // The karyotype of the cell, 3 dimensional array to store discrete values for each  chromosome -> each homologous pair of chromosomes -> 2 complementary DNA strands
        return diploid_genome;
    }// newEmptyDeploidGenome()
    
	/*
	 * Method to access the current timepoint in the simulation executive
	 */
	public int getCurrentTimepoint()
	{
		return current_timepoint;
	}// getCurrentTimepoint
    
}// Class Mitosis
	