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
	private static int newest_generation = 0; // Keep track of the most recent generation of cells
	private static int id_of_last_created_cell = -1; // The cell ID of the last cell that was created. Tracked to set the ID of the next.
    private static int haploid_number; // Used to determine the size of the first dimension in the temp_genome_data array
    private static final int FEMALE = 1, MALE = 2, STRAND_LABELLED = 1, STRAND_UNLABELLED =0;
    private static final boolean CAN_DIVIDE = true, CANT_DIVIDE = false;
    private static List<String>genome_data = new ArrayList<String>();
	public static void main(String [ ] args)
	{
		try
		{
			//Parse command line arguments
            String organism = null; //The target organism for the simulation, determines the genome data that will be parsed by the simulation executive.
            if(args[0].equals("Hum"))
                organism = "Homo sapiens";
            else if(args[0].equals("Mou"))
                organism = "Mus musculus";
            else if(args[0].equals("Test"))
                organism = "Test test";
            else
                printString("Please check that you provided a valid organism name as the first input argument!");
            
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
            
            List<Cell> cell_population = new ArrayList<Cell>(); //Define an arraylist to hold the initial population of cells
            cell_population = initiatePopulation(cell_population, INITIAL_POPULATION_SIZE); //Initialise a population to be used at the start of the simulation
            
            //Run the simulation executive, providing the duration to run the simulation for, the time intervals at which events are evaluated and the initial cell population to perform the simulation of proliferation on
            cell_population = runSimulationExecutive(SIM_DURATION, TIME_INTERVAL, cell_population);
            printLabelDistribOfPopulation(cell_population);

            
            //for (String s : genome_data) //PRINT CONTENTS OF GLOBAL GENOME DATA ARRAY
              //  printString(s);
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
            //Perform division of all cells that can divide as determined by a random number generator and the set division threshhold
            //As new cells are being added to the list, only go through the cells present in the population before new cells were added in this round i.e use the previous population size as max number of iterations.
            for(int current_element = 0; current_element < population_size; current_element++)
            {
                if(cell_population.get(current_element).getDivisionStatus())
                {
                    
                    //Random number generator, 0 = divide, 1 = don't divide
                    
                    double between_zero_and_one = randomDouble();
                    //printString(Double.toString(between_zeroANDone));
                    if(between_zero_and_one < 0.1)
                    {
                        //printString(""+ cell_population.get(current_element).toString());
                        /*Remove one cell from the current generation and add two to the next
                          by changing the generation of the original cell to current_gen + 1 and
                          creating a new cell object also in generation current_gen + 1
                         */

                        Cell daughter_cell_one = cell_population.get(current_element); //The original cell will become daughter cell one
                        
                        // Change the orginal cell to daughter cell 1 by increasing its generation value by 1, from its current generation
                        daughter_cell_one.setGeneration(daughter_cell_one.getGeneration() + 1);
                        
                        //Create a new cell object which will become daughter cell 2, with a blank diploid genome, of same generation as the newly created daughter cell 1
                        cell_population.add(new  Cell(id_of_last_created_cell + 1, daughter_cell_one.getGeneration(), -1, CAN_DIVIDE, daughter_cell_one.getGenome()));
                        id_of_last_created_cell++;
                        
                        Cell daughter_cell_two = cell_population.get(cell_population.size() - 1); //Create and set daughter cell 2 to the newly created cell object
                        
                        /*********************************************************/
                                  // Track the latest generation of cells.
                        if(daughter_cell_one.getGeneration() >= newest_generation)
                        {newest_generation++;}
                        /*********************************************************/
                        
                        int[][][] temp_genome_d1 = daughter_cell_one.getGenome(), temp_genome_d2 = daughter_cell_two.getGenome();

                        
                        for(int chromosome_count = 0; chromosome_count < temp_genome_d1.length; chromosome_count++)
                        {

                            for(int homologous_pair_count= 0; homologous_pair_count < temp_genome_d1[chromosome_count].length; homologous_pair_count++)
                            {
                                for(int dna_strand_count = 0; dna_strand_count < temp_genome_d1[chromosome_count][homologous_pair_count].length; dna_strand_count++)
                                {
                                    if(homologous_pair_count == 0 && dna_strand_count==0)
                                        temp_genome_d2[chromosome_count][homologous_pair_count][dna_strand_count] = STRAND_LABELLED; //Set cell 2 DNA strand to labelled
                                    else if(homologous_pair_count == 0 && dna_strand_count==1)
                                    {
                                        temp_genome_d2[chromosome_count][homologous_pair_count][dna_strand_count] = temp_genome_d1[chromosome_count][homologous_pair_count][dna_strand_count]; //This strand in cell 2 came from the original cell
                                        temp_genome_d1[chromosome_count][homologous_pair_count][dna_strand_count] = STRAND_LABELLED; //Label cell 1s strand as it's new
                                    }
                                    else if(homologous_pair_count == 1 && dna_strand_count==0)
                                        temp_genome_d2[chromosome_count][homologous_pair_count][dna_strand_count] = STRAND_LABELLED; //Label cells 2's strand, new
                                    else if(homologous_pair_count == 1 && dna_strand_count==1)
                                    {
                                        temp_genome_d2[chromosome_count][homologous_pair_count][dna_strand_count] = temp_genome_d1[chromosome_count][homologous_pair_count][dna_strand_count]; //This strand in cell 2 came from the original cell
                                        temp_genome_d1[chromosome_count][homologous_pair_count][dna_strand_count] = STRAND_LABELLED; //Label cell 1s strand as it's new
                                    }
                                //    printString("Chromosome " + Integer.toString(chromosome_count+1) + "; Homologous Pair " + Integer.toString(homologous_pair_count+1) + "; Strand " + Integer.  toString(dna_strand_count+1) + " - Label status = " + Integer.toString(blank_genome[chromosome_count][homologous_pair_count][dna_strand_count]));

                                    //printString("Chromosome " + Integer.toString(chromosome_count+1) + "; Homologous Pair " + Integer.toString(homologous_pair_count+1) + "; Strand " + Integer.toString(dna_strand_count+1) + " - Label status = " + Integer.toString(temp_genome_d1[chromosome_count][homologous_pair_count][dna_strand_count]));
                                }// for every DNA strand

                                /****/
                                //Model stochastic nature of chromosome migration. Each chromosome could end up in either cell one or cell two.
                                double another_between_zeroANDone = randomDouble();
                                if(another_between_zeroANDone >= 0.5)
                                {}
                                else
                                {
                                    int[][] temp_chromosome_1 = temp_genome_d1[chromosome_count]; int[][] temp_chromosome_2 = temp_genome_d2[chromosome_count];

                                    temp_genome_d1[chromosome_count] = temp_chromosome_2;
                                    temp_genome_d2[chromosome_count] = temp_chromosome_1;
                                    
                                    //printString("Chromosome " + Integer.toString(chromosome_count+1) + "; Homologous Pair " + Integer.toString(homologous_pair_count+1) + "; Strand 1" + " - Label status = " + Integer.toString(temp_genome_d1[chromosome_count][homologous_pair_count][0]));
                                    //printString("Chromosome " + Integer.toString(chromosome_count+1) + "; Homologous Pair " + Integer.toString(homologous_pair_count+1) + "; Strand 1" + " - Label status = " + Integer.toString(temp_genome_d1[chromosome_count][homologous_pair_count][1]));
                                    /******/
                                    //Update the genomes of both cells
                                    daughter_cell_one.setGenome(temp_genome_d1);
                                    daughter_cell_two.setGenome(temp_genome_d2);
                                    
                                }
                                
                            }// for every homologous pair
                            
                        }// for each chromosome
                        
                        
                        cell_population.set(current_element, daughter_cell_one);
                        cell_population.set(cell_population.size() - 1, daughter_cell_two);
                        
                        
                        
                        /*
                        for(int chromosome_count = 0; chromosome_count < daughter_cell_two.getGenome().length; chromosome_count++)
                        {
                            for(int homologous_pair_count= 0; homologous_pair_count < daughter_cell_two.getGenome()[chromosome_count].length; homologous_pair_count++)
                            {
                                for(int dna_strand_count = 0; dna_strand_count < daughter_cell_two.getGenome()[chromosome_count][homologous_pair_count].length; dna_strand_count++)
                                {
                                    printString("Chromosome " + Integer.toString(chromosome_count+1) + "; Homologous Pair " + Integer.toString(homologous_pair_count+1) + "; Strand " + Integer.  toString(dna_strand_count+1) + " - Label status = " + Integer.toString(blank_genome[chromosome_count][homologous_pair_count][dna_strand_count]));
                                }
                            }
                        }
                        */
                        
                    }// if(between_zeroANDone >= 0.5)
                    
                    // Population level logic complete, now deal with the molecular level changes during mitotic division
                    // BOTH CELLS NOW HAVE IDENTICAL GENOMES
                    
                    
                    //printString(Integer.toString(location_of_d1) + "  " + Integer.toString(location_of_d2));
                    
                    
                } // if the cell can divide
                //printString(Integer.toString(cell_population.get(current_element).getGeneration()));
                //printLabelDistribOfCell(cell_population.get(current_element));
            }	// for all cells in the main population
            population_size = cell_population.size(); // Update the population size after the round of division
            printString(current_time + " " + population_size);
		}// for
        printString("Latest generation = " + (newest_generation - 1));
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
     * Method to that creates and returns and array list of the initial population of cells.
     * The number of cells in the population is determined by the integer 'population_size'
     */
    private static List<Cell> initiatePopulation(List<Cell> population, int required_population_size)
    {
        int[][][] diploid_genome = new int[haploid_number][2][2]; // The karyotype of the cell, 3 dimensional array to store discrete values for each  chromosome -> each homologous pair of chromosomes -> 2 complementary DNA strands
        
        //Set the label status of all DNA strands in the genome to unlabelled
        for(int chromosome_count = 0; chromosome_count < diploid_genome.length; chromosome_count++)
        {
            for(int homologous_pair_count= 0; homologous_pair_count < diploid_genome[chromosome_count].length; homologous_pair_count++)
            {
                for(int dna_strand_count = 0; dna_strand_count < diploid_genome[chromosome_count][homologous_pair_count].length; dna_strand_count++)
                {
                    diploid_genome[chromosome_count][homologous_pair_count][dna_strand_count] = STRAND_UNLABELLED; //Set each DNA strand to unlabelled
                    
                    //printString("Chromosome " + Integer.toString(chromosome_count+1) + "; Homologous Pair " + Integer.toString(homologous_pair_count+1) + "; Strand " + Integer.toString(dna_strand_count+1) + " - Label status = " + Integer.toString(diploid_genome[chromosome_count][homologous_pair_count][dna_strand_count]));
                }
            }
        }
        
        // Create the starting population of cells, all as generation 1
        for (int counter = 0; counter < required_population_size; counter++)
        {
            population.add(new  Cell(counter, newest_generation + 1, -1, CAN_DIVIDE, diploid_genome)); // Create a new Cell object with the following values.
            
            id_of_last_created_cell = counter; // Track the id of the lastcreated cell
        }// for
        
        return population;
    }// initiate_first_population()
	
	/*
	 * Method to access the current timepoint in the simulation executive
	 */
	public int getCurrentTimepoint()
	{
		return current_timepoint;
	}// getCurrentTimepoint
    
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
    
    
    private static void printLabelDistribOfPopulation(List<Cell> population)
    {
        for(int current_element = 0; current_element < population.size(); current_element++)
        {
            printString(""+ population.get(current_element).toString());

            int[][][] blank_genome = new int[haploid_number][2][2];
            
            for(int chromosome_count = 0; chromosome_count < blank_genome.length; chromosome_count++)
            {
                for(int homologous_pair_count= 0; homologous_pair_count < blank_genome[chromosome_count].length; homologous_pair_count++)
                {
                    for(int dna_strand_count = 0; dna_strand_count < blank_genome[chromosome_count][homologous_pair_count].length; dna_strand_count++)
                    {
                        printString("Chromosome " + Integer.toString(chromosome_count+1) + "; Homologous Pair " + Integer.toString(homologous_pair_count+1) + "; Strand " + Integer.  toString(dna_strand_count+1) + " - Label status = " + Integer.toString(population.get(current_element).getGenome()[chromosome_count][homologous_pair_count][dna_strand_count]));
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
}// Class Mitosis
	