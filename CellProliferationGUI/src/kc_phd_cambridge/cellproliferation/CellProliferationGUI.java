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
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Kyata Chibalabala
 */
public class CellProliferationGUI extends Application 
{
    
  @Override
  public void start(final Stage main_stage) throws Exception 
  {
    // Display the main window
    Parent root = FXMLLoader.load(getClass().getResource("FXMLMainWindow.fxml"));
    Scene scene = new Scene(root);
    main_stage.setTitle("Cell Proliferation Simulator");
    main_stage.setScene(scene);
    main_stage.show();   
  }

  /**
   * Displays a file chooser window that allows the user to select a file 
   * containing Genome Data required for data analysis.
   * 
   * @param selected_file the file selected by the user
   * @return selected_file the file selected by the user
   */
  private static File chooseGenomeDataFile() throws NullPointerException
  {
    // Display a file chooser for single file selection
    Stage file_chooser_stage = new Stage();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Select a Genome_Data file");
    File selected_file = fileChooser.showOpenDialog(file_chooser_stage);
    
    return selected_file;
  }// ChooseGenomeDataFile
  
  /**
   * Provides access to the genome data file selected by the user. 
   * 
   * @return user_selected_genome_file the genome data file selected by the user.
   */
  public static File getGenomeDataFile()
  {
    File user_selected_genome_file = chooseGenomeDataFile();
    return user_selected_genome_file;
  }// GetGenomeDataFile
  
  /**
   * Creates an alert dialogue that displays a title and contents passed to this 
   * method.
   * 
   * @param dialogue_title the title of the alert dialogue.
   * @param dialogue_content the contents of the alert dialogue.
   */
  public static void displayAlert(String dialogue_title, String dialogue_content)
  {
    // Display a dialogue containing the received title and content
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(dialogue_title);
    alert.setHeaderText(null);
    alert.setContentText(dialogue_content);
    alert.showAndWait();
  }// displayAlert
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) 
  {
    launch(args);
  }   
}
