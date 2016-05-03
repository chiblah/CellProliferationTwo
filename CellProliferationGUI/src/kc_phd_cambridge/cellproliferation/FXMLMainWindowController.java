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

import java.io.File;
import java.io.IOException;
import java.util.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import static kc_phd_cambridge.cellproliferation.CellProliferationGUI.displayAlert;


/**
 * Main class responsible for drawing and controlling the user interface,
 * receiving input data from the user, storing this data in SimulationData 
 * objects and creating threads on which separate instances of Simulations are 
 * run.
 * 
 * Takes validated user input(s) and stores them in an ArrayList as a set  
 * containing unique SimulationData objects. For each SimulatedData object in 
 * the ArrayList, a new thread is created and a Simulation evaluated.
 * 
 * @author Kyata Chibalabala
 * @see GenomeData
 * @see Simulation
 * @see SimulationData
 */
public class FXMLMainWindowController  
{ 
  // FXML variables for GUI objects
  @FXML
  private Button addSimulationButton, beginSimulationButton, chooseGenomeDataFileButton, clearDatasets;
  @FXML
  private RadioButton sexRadioButtonF, sexRadioButtonM; // Sex radio buttons
  @FXML
  private RadioButton organismRadioButtonHum, organismRadioButtonMou, organismRadioButtonTest; // Organism radio buttons
  @FXML
  private TextField initPopSizeField, simDurationField, timeIntervalField;
  @FXML
  private ToggleGroup organismToggleGroup, sexToggleGroup; // Toggle groups to make mutually exclusive selevtions for organism and sex
  @FXML
  private TextArea outputTextArea;
  @FXML
  private TitledPane simTitledPane;

  private boolean organism_input_valid = false, sex_input_valid = false, init_pop_input_valid = false, sim_dur_input_valid = false, interval_input_valid = false;
  
  public static final boolean CLEAR_CONTENT = true, DONT_CLEAR_CONTENTS = false; // Whether to clear the contents of the TextArea   
  public static final String new_line = System.lineSeparator(), tab = "\t";
  
  // An array list to store the input data for each unique simulation
  private final List<SimulationData> input_data_for_simulations = new ArrayList<>();
  
  // Get the location of the Genome Data file and declare a variable to store a 
  // corresponding GenomeData object 
  private File genome_data_file;

  /**
   * The array of entire genome data imported from a user selected genome data file.
   * 
   * Only one instance of this object is required by the program as the object 
   * contains all genome data. Simulations call the {@link kc_phd_cambridge.cellproliferation.GenomeData#getGenomeData(String, int) getGenomeData}
   * method in the GenomeData object to obtain chromosome sizes for a chosen organism and sex.
   * 
   */
  public static GenomeData genome_data; 
  
  boolean validate;
  
  public FXMLMainWindowController() 
  {
    
  }
  /**
   * A validation helper method.
   * 
   * Runs a few checks to enable or disable some input fields based on what
   * data the user has already provided. Ensure that the user first selects a 
   * genome data file before they can do anything else.
   */
  private boolean enableDisableInputFields()
  {
    if(genome_data !=null && GenomeData.getImportStatus())
    {// a valid genome data file has been provided
      addSimulationButton.setDisable(false);
      simTitledPane.setDisable(true);
    } 
    
    if(input_data_for_simulations.isEmpty())
    {//no input data added yet  
      clearDatasets.setDisable(true);
    }
    else
    {// a valid genome data file has been imported and at least one set of valid input data has been provided
      simTitledPane.setDisable(false);
      clearDatasets.setDisable(false);
    }
    
    if(input_data_for_simulations == null && genome_data == null)
    {// no valid genome data file or valid input data
      beginSimulationButton.setDisable(true); 
    }
    return true;
  }// enableDisableInputFields
  /** 
   * Evaluated when the user decides to add a simulation dataset to the queue.
   * 
   * Verifies input data when the user decides to create a SimulationData object
   * by pushing the Add Dataset to Queue button. Each unique input data field is verified and a SimulationData object is
   * created and stored if and only if all input fields contain valid data.
   * 
   * @param temp_organism the organism provided by the user. Empty string if none received (invalid input).
   * @param temp_sex the sex provided by the user. 0 if none received, 1 = Female, 2 = Male (invalid input).
   * @param temp_initial_population_size the initial population size provided by the user. 0 if none received (invalid input).
   * @param temp_simulation_duration the duration of the simulation as provided by the user. 0 if none received (invalid input).
   * @param temp_time_interval the time interval provided by the user. 0 if none received (invalid input).
   * @see SimulationData
   */
  @FXML
  private void handleAddSimulationButtonEvent(ActionEvent event) 
  {
    // Temp variables to store input data as validation is completed. When all
    // field are validated, the temp values are then used to create a SimData
    // object which is then added to the list of input datasets.
    String temp_organism = "";
    int temp_sex = 0, temp_initial_population_size = 0, temp_simulation_duration = 0, temp_time_interval = 0;

    StringBuilder validation_message = new StringBuilder();// Store validation messages

    // TODO
    // 1. Perform validation on each data entry field

    // Extract organism information froim user input
    if(organismRadioButtonHum.isSelected())
    {
      temp_organism = "Homo sapiens";
      organism_input_valid = true;
    }
    else if(organismRadioButtonMou.isSelected())
    {
      temp_organism = "Mus musculus";
      organism_input_valid = true;
    }
    else if(organismRadioButtonTest.isSelected())
    {
      temp_organism = "Test testulus";
      organism_input_valid = true;
    }
    else
    {
      organism_input_valid = false;
      validation_message.append("No organism selected.").append(new_line);
    }

    // Extract sex information from user input
    if(sexRadioButtonF.isSelected())
    {
      temp_sex = Simulation.FEMALE;
      sex_input_valid = true;
    }
    else if(sexRadioButtonM.isSelected())
    {
      temp_sex = Simulation.MALE;
      sex_input_valid = true;
    }
    else
    {
      sex_input_valid = false;
      validation_message.append("No sex selected.").append(new_line);
    }

    // Extract population size from user input
    if(initPopSizeField.getText() != null && ! initPopSizeField.getText().trim().isEmpty()) 
    {
      temp_initial_population_size = Integer.parseInt(initPopSizeField.getText());
      init_pop_input_valid = true;
    }else
    {
      init_pop_input_valid = false;
      validation_message.append("No population size provided.").append(new_line);
    }

    // Extract simulation duration from user input
    if(simDurationField.getText() != null && ! simDurationField.getText().trim().isEmpty()) 
    {
      temp_simulation_duration = Integer.parseInt(simDurationField.getText());
      sim_dur_input_valid = true;
    }else
    {
      sim_dur_input_valid = false;
      validation_message.append("No simulation duration provided.").append(new_line);
    }

    // Extract time interval from user input
    if(timeIntervalField.getText() != null && ! timeIntervalField.getText().trim().isEmpty()) 
    {
      temp_time_interval = Integer.parseInt(timeIntervalField.getText());
      interval_input_valid = true;
    }else
    {
      interval_input_valid = false;
      validation_message.append("No time interval provided.").append(new_line);
    }

    // Create a SimulationData object from the temp values and add it to the 
    // list of input datasets. However, only do this if all inputes are valid
    if(organism_input_valid && sex_input_valid && init_pop_input_valid && sim_dur_input_valid && interval_input_valid)
    {
      int haploid_number = GenomeData.getHaploidNumber(temp_organism);
      input_data_for_simulations.add(new SimulationData(temp_organism, temp_sex, temp_initial_population_size, temp_simulation_duration, temp_time_interval, haploid_number));
      String to_print = "The simulation dataset has been successfuly added to the list of simulations." + new_line + input_data_for_simulations.get(input_data_for_simulations.size() - 1).toString() + new_line + new_line;
      printToTextArea(DONT_CLEAR_CONTENTS,to_print);
      //displayAlert("",to_print);
    }
    else
      validation_message.insert(0,"Sorry, this dataset was not added as there exists  =< 1  incorrect inputs" + new_line + new_line+ "ERRORS: " + new_line);
    
    // If validation errors present, display them in an alert box
    if(!validation_message.toString().isEmpty())
      displayAlert("INVALID INPUTS!", validation_message.toString());
    // 3. Based on which option has been selected, wipe input field
    
    validate = enableDisableInputFields();
    
  }// handleAddSimulationButtonEvent

  
  /** 
   * Evaluated when the user chooses to run simulations from the data they have 
   * provided. 
   * 
   * Creates threads on which Simulations are performed from user input data. 
   * Goes through the list of SimulationData objects and for each object, passes
   * the SimulationData object to a Simulation object's constructor which then
   * initiates and performs the simulation.
   * 
   * @param input_data_for_simulations the list of SimulationData objects stored
   * in an ArrayList.
   * @see Simulation
   */
  @FXML
  private void handleBeginSimulationEvent(ActionEvent event) 
  {
    // TODO
    // 1. Present a confirmation prompt for the user
    // 2. For each unique row in the input data array, create a new thread
    //    to run a simulation object.
    // Go through each element in the input_data_for_simulations array and create
    
    //3 No simulation data added, button inactive
    //4 Disable butto when simulation begins runnin, enable when complete.
    
    // Verify that a genome data file has been successfully imported
    if (genome_data != null && genome_data.getImportStatus()) 
    {
      for(SimulationData current_simulation_input_dataset:input_data_for_simulations)
      {
        new Thread(new Simulation(current_simulation_input_dataset)).start();
      }
      
    } else// Genome data import was not successful
    {
      displayAlert("ALERT!","Failed to import genome data file, check that you have selected a valid genome data file.");
    }
  }// handleBeginSimulationEvent

  /** 
   * Evaluated when the user selects a Genome Data file.
   * 
   * Takes a user selected file and imports the genome data into a GenomeData object
   * 
   * @param genome_data_file the selected file containing genome data.
   * @param genome_data the GenomeData object created from the selected file
   * @see GenomeData
   */
  @FXML
  private void handleChooseGenomeDataFileButtonEvent(ActionEvent event) 
  {
    try
    {
      genome_data_file = CellProliferationGUI.getGenomeDataFile();
      genome_data = new GenomeData(genome_data_file);
      
      if(genome_data.getImportStatus())
      {// Display success message
      
        String confirmation_title = "File Selection Confirmed";// Title of alert box
        StringBuilder confirmation_contents = new StringBuilder();// Contents of alert box
        
        // Add some preamble to explain the message in the alert box
        confirmation_contents.append("Newly selected Genome Data File: ").append(genome_data_file.getAbsoluteFile()).append(new_line).append(new_line).append("File contains the following header lines(s):").append(new_line);
     
        // Append the header lines to the message to be displayed, placing each on a new line
        genome_data.getGetHeaderLines().stream().forEach((String header_line) -> 
        {
          confirmation_contents.append(header_line).append(new_line);
        });
        
        // Draw the alert box, with specified title and contents
        displayAlert(confirmation_title, confirmation_contents.toString());
      }else
      {
        displayAlert("ERROR", "Failed to import genome data file, check that you have selected a valid file.");
      }
    }catch(NullPointerException|IOException|ArrayIndexOutOfBoundsException error)
    {
      displayAlert("ERROR", "Failed to import genome data file, check that you have selected a valid file." + new_line +  "ERROR: " + error.getMessage() +new_line + "CAUSE:" + error.toString());
    }// try catch 
    
    this.validate = enableDisableInputFields();
  }//handleChangeGenomeDataFileButtonEvent  
  
  /** 
   * Evaluated when the user selects the "Clear Datasets" button.
   * 
   * 
   * 
   */
  @FXML
  private void handleClearDatasetsEvent(ActionEvent event) 
  {
    input_data_for_simulations.clear();
    outputTextArea.clear();
    enableDisableInputFields();
  }
  
  /**
   *
   * @param clear_contents
   * @param contents
   */
  public void printToTextArea(boolean clear_contents, String contents)
  {
    // Clear the contents of the text area if specified
    if(clear_contents)
      outputTextArea.clear();
    outputTextArea.appendText(contents); // Add string to the text area
  }// printToTextArea
}
