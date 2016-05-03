/******************************************************************************************
 * Class for a Cell object with variables for the various properties of each cell
 * and methods to gain access or change the values of select variables.
 *******************************************************************************************/
public class Cell
{
	// Instance variables
	private int cell_id; // A unique identifier for each cell
	private int cell_gen; // Track the generation the cell belongs to
	private double last_div; // The last time this cell completed M-phase. Initially set to the timepoint it was created
	private boolean can_divide; // Indicates the state of the cell, true if cell is at G2 and can divide
    private int[][][] genome;
    private int generation_number;
	
    // Constructor
	public Cell(int new_id, int new_gen, double provided_last_div, boolean division_status, int[][][] provided_genome)
	{
		this.cell_id = new_id;
		this.cell_gen = new_gen;
		this.last_div = provided_last_div;
		this.can_divide = division_status;
        this.genome = provided_genome;
		
	}// Constructor
	
	// Class methods
	
	/*
	 * Allows the division status of the cell to be changed to true or false
	 */
	public void changeDivisionStatus(boolean new_status)
	{
  		this.can_divide = new_status;
	}// updateDivisionStatus
	
	/*
	 * Get the division status of this cell
	 */
	public boolean getDivisionStatus()
	{
		return this.can_divide;
	}// getDivisionStatus
	
    /*
     * Returns the ID of this cell
     */
    public int getId()
    {
        return this.cell_id;
    }// getId
    
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
		this.cell_gen = new_generation;
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
        String newStr = "Cell ID = " + this.cell_id + "; Generation = " + this.cell_gen; // + "; Last division timepoint = "+ this.last_div + " Division status = " + can_divide;
		return newStr;
	}// toString
}// Cell

// 1. Need a delay before setting the cell status to G2, this should be calculated from the last_div allowing for an appropriate period of time before division.
// Calculated in the setDivisionStatus method? 