/*
 * Copyright 2015 Kyata.
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static kc_phd_cambridge.cellproliferation.FXMLMainWindowController.new_line;
import static kc_phd_cambridge.cellproliferation.FXMLMainWindowController.tab;


/**
 *
 * @author Kyata Chibalabala
 */
public class DataAnalysis 
{
  //private final List<Cell> cell_population;
  private final String organism;
  private final int sex;
  private final List<String> genome_data;
  private final int  haploid_number;
  private long total_number_of_bases_in_genome;
  private double fraction_of_genome_labelled;
  private int total_labelled_bases_in_genome, total_number_of_lineages;
  private final int[] highest_generations;// An array of the highest generation reached from each cell lineage
  
  /**
   *
   * @param first_results_file the String value of the first results file !!!!!*&^&^%%
   * @param new_genome_data the string array of chromosome sizes required to perform fractional DNA synthesis calculations.
   * @param new_organism the string representation of the organism.
   * @param new_sex the integer value of the sex of the organism.
   * @param new_highest_generations
   * @param initial_population_size
   */
  public DataAnalysis(String first_results_file, List<String> new_genome_data, String new_organism, int new_sex, int[] new_highest_generations, int initial_population_size)
	{
    //this.cell_population = new_population;
    this.genome_data = new_genome_data;
    this.organism = new_organism;
    this.sex = new_sex;
    this.total_number_of_bases_in_genome = GenomeData.getGenomeSize(this.organism, this.sex);
    this.haploid_number = GenomeData.getHaploidNumber(this.organism);
    this.highest_generations = new_highest_generations;
    this.total_number_of_lineages = initial_population_size;
    
    //Find the global highest generation reached in any of the lineages 
    int highest_generation_tracker = 0;
    for(int highest_gen: highest_generations)
    {
      if(highest_generation_tracker < highest_gen)
        highest_generation_tracker = highest_gen;
    }

    // Calculate the expected number of cells up to the global highest generation of all lineage
    int[] expected_number_of_cells_in_each_generation = new int[highest_generation_tracker+1];// + 1 to tak einto account generation 0
    int cells_in_generation = 1;
    expected_number_of_cells_in_each_generation[0] = cells_in_generation;// Set generation 0 expected number of cells to 1
    for(int count = 1; count < highest_generation_tracker+1; count++)// + 1 to tak einto account generation 0
    {// For all generations
      cells_in_generation = cells_in_generation * 2;
      expected_number_of_cells_in_each_generation[count] = cells_in_generation;
    }  // For all generations      
    
    // Initiate an array to store the percentage labelled in each generation for each lineage
    // First dimension = cell lineages; second dimension = generations arising from that lineage
    StringBuilder[][] generation_percentages = new StringBuilder[total_number_of_lineages][];
    
    // Populate the second dimension of the array, jagged array if need be
    for(int lineage_count = 0; lineage_count < total_number_of_lineages; lineage_count++)
    {// For each lineage
      int generations_in_this_lineage = highest_generations[lineage_count];
      generation_percentages[lineage_count] = new StringBuilder[generations_in_this_lineage+1];
    }// for each lingeage
    
    
    
    // TEST EXPECTED NUMBER OF CELLS 
    int count = 0;
    for(StringBuilder[] lineage:generation_percentages)
    {
      System.out.println("# of generations in lineage " + count + " = "+ lineage.length);
      count++;
    }
    

    FileReader file_reader; 
    try 
    {
      file_reader = new FileReader(first_results_file);
      BufferedReader buffered_reader = new BufferedReader(file_reader); 
      String s; 
      while((s = buffered_reader.readLine()) != null) 
      { 
        String[] split_line = s.split(tab + tab);
        int cell_id, cell_generation, cell_lineage; 
        double percentage_labelled;
        cell_id = Integer.parseInt(split_line[0]);
        cell_generation = Integer.parseInt(split_line[1]);
        cell_lineage = Integer.parseInt(split_line[2]);
        percentage_labelled = Double.parseDouble(split_line[3]);
        
        String to_add = Double.toString(percentage_labelled) + ",";
        if(generation_percentages[cell_lineage][cell_generation] == null)
        {// If nothing is stored at this index 
          generation_percentages[cell_lineage][cell_generation]= new StringBuilder(to_add);
        }else
        {
          generation_percentages[cell_lineage][cell_generation].append(to_add) ;
        }
        System.out.println(cell_lineage +" " + cell_generation);
      } 
      file_reader.close();
    } catch (FileNotFoundException ex) 
    {
      Logger.getLogger(DataAnalysis.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) 
    {
      Logger.getLogger(DataAnalysis.class.getName()).log(Level.SEVERE, null, ex);
    }
   
    // Calculate string of results to write to file
    List<String> final_generation_to_output = new ArrayList<>();
    List<String> individual_distributions = new ArrayList<>();
    for(int lineage = 0; lineage < generation_percentages.length; lineage++)
    {// for each lineage
      final_generation_to_output.add("LINEAGE," + lineage);
      individual_distributions.add("LINEAGE," + lineage);
      
      generation_percentages[lineage][0] = new StringBuilder("0.0");//Set all generation 0 values to 0
      for(int generation = 0; generation < generation_percentages[lineage].length; generation++)
      {// for each generation in the current lineage
        //final_generation_to_output.add();
        String this_line = generation_percentages[lineage][generation].toString();
        String[] split_line = this_line.split(",");
        System.out.println("Line " + this_line + " Elements = " + split_line.length);
        individual_distributions.add("Generation," + generation);
        individual_distributions.add("# of Cells," + split_line.length + "," + this_line);
        double percentage_label_accumalator = 0;
        for(String percentage:split_line)
        {// for each element in the split line
          percentage_label_accumalator += Double.parseDouble(percentage);
        }// for each element in the split line
        int number_of_cells_in_this_generation = split_line.length;
        final_generation_to_output.add("GENERATION," + generation + "," + Double.toString(percentage_label_accumalator/number_of_cells_in_this_generation));
      }// for each generation in the current lineage
    }// for each lineage   
    writeToFile(final_generation_to_output, "Final Label Percentages - " + first_results_file + ".csv");
    writeToFile(individual_distributions, "Individual Cell Percentages - " + first_results_file + ".csv");
    /*TODO
    1. Calculate B, the toal number of bases in the genome
    2. Calculated L, the number of bases in the genome thats are labelled
    3. Calculate F, the fraction of the genome that is labelled
    */
  } 
  
  private void getCellLabelDistribution(Cell new_cell)
  {
    double[][] chromosome_labelled_bases = new double[haploid_number][2];
    double[][][] genome = new_cell.getGenome();
    total_labelled_bases_in_genome = 0;
    //System.out.println("Cell " + new_cell.getId() + ": Generation " + new_cell.getGeneration());
    for(int chromosome_count = 0; chromosome_count < genome.length; chromosome_count++)
    {// For each homologous pair
      double chromo_labelled_bases = 0;
      for(int homologous_pair_count= 0; homologous_pair_count < genome[chromosome_count].length; homologous_pair_count++)
      {// For each chromosome in a homologous pair
        int chromosome_size;
        
        String[] split_chromosome_sizes = genome_data.get(chromosome_count).split(",");
        if(homologous_pair_count==0)
        {//Homologous chromosome one
          chromosome_size = Integer.parseInt(split_chromosome_sizes[0]);
        }else
        {//Homologous chromosome two
          chromosome_size = Integer.parseInt(split_chromosome_sizes[1]);
        }
        for(int dna_strand_count = 0; dna_strand_count < genome[chromosome_count][homologous_pair_count].length; dna_strand_count++)
        {// For each DNA strand in the chromosome 
          
          //System.out.println(genome[chromosome_count][homologous_pair_count][dna_strand_count]);
                  
          double bases_labelled_on_strand = genome[chromosome_count][homologous_pair_count][dna_strand_count];
          //System.out.println("Strand bases labelled " + bases_labelled_on_strand);
          total_labelled_bases_in_genome += bases_labelled_on_strand*(double)chromosome_size; 
          
          chromo_labelled_bases =(bases_labelled_on_strand*(double)chromosome_size);          
        }// For each DNA strand
        chromosome_labelled_bases[chromosome_count][homologous_pair_count] = chromo_labelled_bases;
      }// For each chromosome in a homologous pair
    }// For each homologous pair
    fraction_of_genome_labelled = (total_labelled_bases_in_genome/(double)total_number_of_bases_in_genome);
    new_cell.setFractionGenomeLabelled(fraction_of_genome_labelled);
  }// getCellLabelDistribution
  
  private void writeToFile(List<String> file_contents, String file_name)
  {
    try
    {
      try(FileWriter writer = new FileWriter(file_name)) 
      {
        for(String line : file_contents)
        {
          writer.append(line);
          writer.append(new_line);
        }
        //generate whatever data you want
        writer.flush();
        writer.close();
      }
    }
    catch(IOException e)
    {
    }
  }
}// DataAnalysis
