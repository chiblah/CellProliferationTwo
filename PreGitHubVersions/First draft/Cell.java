/******************************************************************************************
 * This class creates a Cell object with variables for the various properties of each cell
 * and methods to gain access or change the values of some variables.
 *******************************************************************************************/
public class Cell
{
	// Instance variables
	private int cell_id; // A unique identifier for each cell
	private int cell_gen; // Track the generation the cell belongs to
	private double last_div; // The last time this cell completed M-phase. Initially set to the timepoint it was created
	private boolean can_div; // Indicates the state of the cell, true if cell is at G2 and can divide
	
	// Constructor
	public Cell(int new_id, int new_gen, double new_last_div)
	{
		cell_id = new_id;
		cell_gen = new_gen;
		last_div = new_last_div;
		can_div = false;
		
	}// Constructor
	
	// Class methods
	
	/*
	 * Allows the division status of this cell to be changed
	 */
	public void updateDivisionStatus()
	{
		if (Mitosis.current_timepoint >= (this.last_div + Mitosis.DIV_DELAY)) // Has the cell spent enough time in interphase?
  	{
			//Yes
			can_div = true;
  	}
  	else
  	{
  		// No
  		can_div = false;
  	} 
	}// updateDivisionStatus
	
	/*
	 * Returns the generation number of this cell
	 */
	public int getGen()
	{
		return this.cell_gen;
	}// getGen
	
	// Return a string of key information of the cell
	public String toString()
	{
		String newStr = "Cell ID = " + this.cell_id + "; Generation = " + this.cell_gen + "; Last division timepoint = "+ this.last_div + " Division status = " + can_div;
		return newStr;
	}// toString
}// Cell

// 1. Need a delay before setting the cell status to G2, this should be calculated from the last_div allowing for an appropriate period of time before division.
// Calculated in the setDivisionStatus method? 