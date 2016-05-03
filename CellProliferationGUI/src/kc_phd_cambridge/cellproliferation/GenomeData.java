/*
 * Copyright 2015 Kyata Chibalabala .
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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Read a genome data file and and stores the size of each chromosome in an array
 * list.
 * 
 * Each line stores the integer sizes of each chromosome pair, e.g for an 
 * organism of haploid number = 3;
 * 
 *  chr1 133797422,133797422
 *  chr2 242508799,242508799
 *  XY   198450956,130786757  
 * 
 * The last line will have two different values for males (XY instead of XX).
 * This is handled by specifying a sex of the organism to determine the 
 * combination of the sex chromosomes. When imported into the list, the 'chr'
 * chromosome identifier is discarded and array indices used to track chromosome
 * number and the chromosome sizes stored as a single string of two comma 
 * separated integers (one for each chromosome in a homologous pair).
 *
 * @author Kyata Chibalabala
 */
public class GenomeData 
{
  // Class variables, static as the contents of this object will be shared
  private static boolean successful_genome_import = false;
  private static File genome_data_file;
  private static List<String> all_genome_data_contents, header_lines; 
  
  /**
   * Constructs a GenomeData object used to store all Genome Data from a user 
   * selected file.
   *
   * @param new_genome_data_file the File, selected by the user and passed to this constructor  
   * @throws java.io.IOException
   */
  public GenomeData(File new_genome_data_file) throws IOException
	{
    // Garbage collection in case the user has previously created a GenomeData object
    all_genome_data_contents = null;
    header_lines = null;
		genome_data_file = null;
    
    all_genome_data_contents = new ArrayList<>();// Store the genome data in a list
    header_lines = new ArrayList<>();// Separately store the headers found in the file
		genome_data_file = new_genome_data_file;// The genome data file provided by the user
 
    // Attempt to import the genome data file and note the boolean result of the attempt
    successful_genome_import = importGenomeData(); 
	}// Constructor

  
  private static boolean importGenomeData() throws IOException, FileNotFoundException, ArrayIndexOutOfBoundsException
  {
    boolean no_glitches = false; 
    try (BufferedReader genome_file_reader = new BufferedReader(new FileReader(genome_data_file))) 
    {
      String line;
      
      // Read all lines of the file into a List<String> object
      while ((line = genome_file_reader.readLine()) != null)
      {
        if(line.isEmpty() || "".equals(line))// Empty lines will cause problems later so ignore them
        {}
        else// Not an empty line
        {
          String[] split_line = line.split(" ");
          if(split_line[0].equals(">")) // If this is a header line
          {
            // Store the header line
            header_lines.add(line);
            no_glitches = true;// Dirty trick, accepts the provided file because a header line has been found. Will cause problems if user selects an invalid file that contins any line beginning with a '>' character
          }
          all_genome_data_contents.add(line); 
        }  
      }// while ((line = genome_file_reader.readLine()) != null)
      genome_file_reader.close();
    }// try
    return no_glitches;
  }// importGenomeData
 
  /**
   * Allows access to the boolean result of the genome data import operation.
   * 
   * Returns true if genome data import was successful and false if unsuccessful.
   * 
   * @return successful_genome_import whether the import operation was successful or not
   */
  public static boolean getImportStatus() 
  {
    return successful_genome_import;
  }// getImportStatus
  
  /**
   * Allows access to the integer value of haploid number for a specified organism.
   * 
   * @param target_organism_name the target organism for which the haploid number is required.
   * @return haploid_number the haploid number of the specified organism.
   */
  public static int getHaploidNumber(String target_organism_name)
  {
    int haploid_number = 0;
    StringBuilder this_organism_name;
    for(String this_header:header_lines)
    {
      String[] split_header = this_header.split(" ");// Split the header line into constituent parts
      this_organism_name = new StringBuilder().append(split_header[1]).append(" ").append(split_header[2]); // Recreate the genus and species of the organism from the strings
      if(target_organism_name.equals(this_organism_name.toString()))
        haploid_number = Integer.parseInt(split_header[4]);
    }
    return haploid_number;
  }// getHaploidNumber
  
  /**
   * Provides access to a subset of the genome data.
   * 
   * Access to the chromosome sizes of a chosen organism, male or female.
   * 
   * @param target_organism the string value representing the organism for which chromosome sizes are required.
   * @param sex integer value for sex, gives the combination of sex chromosome sizes to return, XX (female) or XY (male)
   * @return genome_data_contents_subset the subset of genome data of the specified organism.
   */
  public static List<String> getGenomeData(String target_organism, int sex)
  {
    List<String> genome_data_contents_subset = new ArrayList<>();
    
    // Used to determine what action to take when a header line is encountered; 
    // close the file if true (a previous header has been encountered so this 
    // must be the beginning of a new organism), keep reading if false
    boolean found_target_organism = false;
    
    // True when the Y chromosome has been dealt with, for this to work the file 
    // being imported has to have the Y chr line after not before the X chr line
    boolean end_of_genome = false; 
    // Evaluate each line imported from the file and add the chromosome sizes of 
    // interest to the genome data subset array
    
    StringBuilder organism_name;
    for(String chromosome_sizes : all_genome_data_contents) 
    {
      String[] split_line = chromosome_sizes.split(" ");
      
      if(split_line[0].equals(">")) // If this is a first header line
      {
        organism_name = new StringBuilder().append(split_line[1]).append(" ").append(split_line[2]); // Recreate the genus and species of the organism from the strings that were separated during the splitting of the line
        if(organism_name.toString().equals(target_organism)) // If this line refers to the organism of interest
        {
          found_target_organism = true;
        }
        else
        {
          found_target_organism = false;
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
              genome_data_contents_subset.add(split_line[1] + "," + split_line[1]);
            }
            else if(split_line[0].equals("chrY"))
            {
              end_of_genome = true;
            }// Ignore the Y chromosome
            else
              genome_data_contents_subset.add(split_line[1] + "," + split_line[1]);// The current line is an autosome
          }break;
          case 2://Male
          {
            if(split_line[0].equals("chrX"))
            {
              genome_data_contents_subset.add(split_line[1] + ",");
            }
            else if(split_line[0].equals("chrY"))
            {
              String temp = genome_data_contents_subset.get(genome_data_contents_subset.size() - 1); // Store the value already there
              genome_data_contents_subset.remove(genome_data_contents_subset.size()-1);

              temp = temp + split_line[1];
              genome_data_contents_subset.add(temp);

              end_of_genome = true;
            }
            else
              genome_data_contents_subset.add(split_line[1] + "," + split_line[1]);// The current line is an autosome
          }break;
        }
      }
    }
    return genome_data_contents_subset;
  }

  /**
   * Returns the size of a diploid genome in bases.
   * 
   * @param target_organism the string value representing the organism for which total genome size is required.
   * @param sex integer value for sex, gives the combination of sex chromosome sizes to return, XX (female) or XY (male).
   * @return total_number_of_bases_in_genome the integer value for the sum of all bases on all chromosomes.
   */ 
  public static long getGenomeSize(String target_organism, int sex)
  {
    long number_of_bases_in_genome = 0;
    final List<String> genome_data = getGenomeData(target_organism, sex);
    final int haploid_number = getHaploidNumber(target_organism);
    
    for(int chromosome_count = 0; chromosome_count < haploid_number; chromosome_count++)
    {
      String[] split_chromosome_sizes = genome_data.get(chromosome_count).split(",");
      // The size of each chromosome in bases = the chromosome size (base pairs) x 2
      
      long this_chromosome_size = Integer.parseInt(split_chromosome_sizes[0])*2 + Integer.parseInt(split_chromosome_sizes[1])*2;
      //System.out.println("This chromo size " + this_chromosome_size);

      number_of_bases_in_genome += this_chromosome_size;
    }
    
    //System.out.println(target_organism + "" + total_number_of_bases_in_genome);
    return number_of_bases_in_genome;  
  }// getGenomeSize
  
  /**
   * Provides access to the header lines found in the imported genome data file.
   * 
   * Used to inform the user of the header lines in the genome file they have imported.
   * @return header_lines the header lines present in the imported genome data file.
   */
  public static List<String> getGetHeaderLines() 
  {
    return header_lines; 
  }// getGetHaderLines
}// GenomeData
