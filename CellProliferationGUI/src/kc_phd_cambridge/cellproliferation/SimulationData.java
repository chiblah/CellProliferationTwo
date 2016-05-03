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

/**
 * Class for the storage of user input parameters.
 * 
 * Stores parameters provided by the user via the user interface and is passed 
 * to the Simulation object's constructor upon initiation of said object.
 * 
 * @see kc_phd_cambridge.cellproliferation.Simulation
 * @author Kyata Chibalabala
 */
public class SimulationData 
{
  // Instance variable
  private final String organism;
  private final int sex, initial_population_size, simulation_duration, time_interval, haploid_number;
  
  // Constructor
  public SimulationData(String new_org, int new_sex, int new_init_pop_size, int new_sim_dur, int new_interval, int new_haploid_number)
  {  
    this.organism = new_org;
    this.sex = new_sex;
    this.initial_population_size = new_init_pop_size;
    this.simulation_duration = new_sim_dur;
    this.time_interval = new_interval;
    this.haploid_number = new_haploid_number;  
  }// Constructor
  
  //*** Access methods ***//
  
  /**
   * Provides read access to the 'organism' value stored in this input parameter set
   *
   * @return the value of 'organism'. 
   */
  public String getOrganism()
  {
    return this.organism;
  }// getOrganism
  
  /**
   * Provides read access to the 'sex' value stored in this input parameter set
   *
   * @return the value of 'sex'.  
   */
  public int getSex()
  {
    return this.sex;
  }// getSex
  
  /**
   * Provides read access to the 'initial population size' value stored in this input parameter set
   *
   * @return the value of 'initial population size'.
   */
  public int getInitialPopulationSize()
  {
    return this.initial_population_size;
  }// getInitialPopulationSize
  
  /**
   * Provides read access to the 'simulation duration' value stored in this input parameter set
   *
   * @return the value of 'Simulation Duration'.
   */
  public int getSimulationDuration()
  {
    return this.simulation_duration;
  }// getSimulationDuration
  
  /**
   * Provides read access to the integer value of 'Time Interval' stored in this input parameter set
   *
   * @return the integer value of 'Time Interval'.
   */
  public int getTimeInterval()
  {
    return this.time_interval;
  }// getTimeInterval
  
  public int getHaploidNumber() 
  {
    return this.haploid_number;
  }// getHaploidNumber() 
  
  /**
   * Provide read access to a string representation of this input parameter set
   *
   * @return a string representation of the entire input parameter set.
   */
  @Override
  public String toString()
  {
    String sex_string = this.sex == 1 ? "Female" : "Male";
    return "Organism=" + this.organism + ", Sex=" + sex_string + ", Init_Pop=" + this.initial_population_size + ", Sim_Duration=" + this.simulation_duration + ", Interval="+ this.time_interval + ", Haploid#=" + haploid_number; 
  }// toString
}// SimulationData