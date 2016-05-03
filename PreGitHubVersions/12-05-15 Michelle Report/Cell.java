/******************************************************************************************
 * Class for a Cell object with variables for the various properties of each cell
 * and methods to gain access or change the values of select variables.
 *******************************************************************************************/
public class Cell
{
	// Instance variables
	private final int cell_id; // A unique identifier for each cell
	private int cell_gen; // Track the generation the cell belongs to
	private double last_div; // The last time this cell completed M-phase. Initially set to the timepoint it was created
	private boolean can_divide; // Indicates the state of the cell, true if cell is at G2 and can divide
    private int[][][] genome;
	
    // Constructor
	public Cell(int new_id, int new_gen, double provided_last_div, boolean division_status, int[][][] provided_genome)
	{
		cell_id = new_id;
		cell_gen = new_gen;
		last_div = provided_last_div;
		can_divide = division_status;
        genome = provided_genome;
		
	}// Constructor
	
	// Class methods
	
	/*
	 * Allows the division status of the cell to be changed to true or false
	 */
	public void updateDivisionStatus(boolean new_status)
	{
  		can_divide = new_status;
	}// updateDivisionStatus
	
	/*
	 * Get the division status of this cell
	 */
	public boolean getDivisionStatus()
	{
		return can_divide;
	}// getDivisionStatus
	
	/*
	 * Returns the generation number of this cell
	 */
	public int getGeneration()
	{
		return this.cell_gen;
	}// getGen
	
	/*
	 * Sets the generation of this cell
	 */
	public void setGeneration(int new_generation)
	{
		cell_gen = new_generation;
	}// setGen
    
    /*
     *Returns the diploid genome of this cell
     */
    public int[][][] getGenome()
    {
        return this.genome;
    }// getGenome
    
    /*
     *Set the diploid genome of this cell
     */
    public void setGenome(int[][][] new_genome)
    {
        this.genome = new_genome;
    }// setGenome
    
	
	// Return a string of key information of the cell
	public String toString()
	{
		String newStr = "Cell ID = " + this.cell_id + "; Generation = " + this.cell_gen + "; Last division timepoint = "+ this.last_div + " Division status = " + can_divide;
		return newStr;
	}// toString
}// Cell

// 1. Need a delay before setting the cell status to G2, this should be calculated from the last_div allowing for an appropriate period of time before division.
// Calculated in the setDivisionStatus method? 