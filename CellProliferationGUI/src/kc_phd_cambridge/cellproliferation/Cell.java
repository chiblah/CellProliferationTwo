/*
 * Copyright 2015 Kyata Chibalabala.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kc_phd_cambridge.cellproliferation;

import java.util.List;

/**
 * Representation of a Cell object.
 * 
 * Store the variables for the various properties of each cell as well as
 * methods to gain read or modify the values of editable variables.
 * 
 * @author Kyata Chibalabala
 */
public class Cell 
{
  // Instance variables
	private final int cell_id, cell_lineage_id; // A unique identifier for each cell, and an identifier for the cell's lineage
	private int cell_gen; // Track the generation the cell belongs to
	private double last_div; // The last time this cell completed M-phase. Initially set to the timepoint it was created
	private boolean can_divide; // Indicates the state of the cell, true if cell is at G2 and can divide
  private double[][][] genome;
  private double fraction_genome_labelled;
	
  // Constructor
	public Cell(int new_id, int new_lineage_id, int new_gen, double provided_last_div, boolean division_status, double[][][] provided_genome)
	{
		this.cell_id = new_id;
    this.cell_lineage_id = new_lineage_id;
		this.cell_gen = new_gen;
		this.last_div = provided_last_div;
		this.can_divide = division_status;
    this.genome = provided_genome;
    this.fraction_genome_labelled = 0;
	}// Constructor
	
  /**
   * Copy constructor
   * @param source the Cell instance being copied
   */
  public Cell(Cell source) 
  {
    cell_id = source.cell_id;
    cell_lineage_id = source.cell_lineage_id;
		cell_gen = source.cell_gen;
		last_div = source.last_div;
		can_divide = source.can_divide;
    genome = source.genome;
    fraction_genome_labelled = source.fraction_genome_labelled;
  }
  
  
	//*** Access methods ***//
	
	/**
   * Allows the division status of this cell to be changed to true or false
   * True = cell can divide, False = Cell can not divide
   *
   * @param new_status the new division status of this cell.
   */
	public void changeDivisionStatus(boolean new_status)
	{
    this.can_divide = new_status;
	}// updateDivisionStatus
	
	/**
   * Provides access to the current division status of this cell
   *
   * @return the division status of this cell
   */
	public boolean getDivisionStatus()
	{
    return this.can_divide;
	}// getDivisionStatus
	
  /**
   * Provides read access for the unique ID of this cell
   *
   * @return the cellId of this cell object
   */
  public int getCellId()
  {
    return this.cell_id;
  }// getId
  
   /**
   * Provides read access for the lineage ID of this cell
   *
   * @return the cellLineageId of this cell object
   */
  public int getLineageId()
  {
    return this.cell_lineage_id;
  }// getLineageId
    
  /**
   * Provides read access for the generation number of this cell
   *
   * @return the generation of cells that this cell belongs to
   */
	public int getGeneration()
	{
		return this.cell_gen;
	}// getGen
	
	/**
   * Allows the generation number of this cell to be changed
   *
   * @param new_generation the generation of cells that this cell now belongs to
   */
	public void setGeneration(int new_generation)
	{
		this.cell_gen = new_generation;
	}// setGen
    
  /**
   * Provides read access for the diploid genome of this cell
   *
   * @return  the genome of this cell
   */
  public double[][][] getGenome()
  {
      return this.genome;
  }// getGenome
    
  /**
   * Allows the diploid genome of this cell to be changed
   *
   * @param new_genome the new genome object that this cell will now carry
   */
  public void setGenome(double[][][] new_genome)
  {
    this.genome = new_genome;
  }// setGenome
  
  /**
   * Allows the label status of this cell's genome to be changed.
   *
   * @param new_fraction_genome_labelled the new double representing fractional labelling of this cell's genome.
   * @see kc_phd_cambridge.cellproliferation.Simulation#calculateCellFractionLabelled(int index_of_cell)
   */
  public void setFractionGenomeLabelled(double new_fraction_genome_labelled)
  {
    this.fraction_genome_labelled = new_fraction_genome_labelled;
  }// setFractionGenomeLabelled()
  
  /**
   * Provides read access to the label status of this cell's genome.
   *
   * @return  the double representing fractional labelling of this cell's genome.
   */
  public double getFractionGenomeLabelled()
  {
      return this.fraction_genome_labelled;
  }// getFractionGenomeLabelled()
  

  public void printGenomeStatus()
  {
    System.out.println(this.toString());
    for (double[][] genome1 : this.genome) {
      // foreach homologous pair of the genome
      for (double[] genome11 : genome1) {
        // for each chromosome in each homologous pair
        for (int dna_strand_count = 0; dna_strand_count < genome11.length; dna_strand_count++) {
          // for each DNA strand in the chromosome
          System.out.println(Double.toString(genome11[dna_strand_count]));
        } // for each DNA strand in the chromosome
      }
    }
  }
	/**
   * Returns a string of key information about this cell.
   *
   * @return newStr a String representation of key information about this cell object
   */
  @Override
	public String toString()
	{
    String newStr = "Cell ID = " + this.cell_id + "; Cell Lineage = " + this.cell_lineage_id +"; Generation = " + this.cell_gen + "; Fraction labelled = " + this.fraction_genome_labelled * 100;//+ " Division status = " + can_divide;
		return newStr;
	}// toString
} // Class Cell
