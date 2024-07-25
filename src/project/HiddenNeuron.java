package project;

/*
 * File Name: HiddenNeuron.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */


/*
  This is the abstract base class for future HiddenNeuron concrete classes as
  described in the type enumeration
*/
public abstract class HiddenNeuron extends Neuron
{
  protected HiddenNeuron(int numInputs, int numOutputs)
  {
    super(numInputs, numOutputs);
  }
  
  public abstract HiddenNeuron newInstance(int numInputs, int numOutputs);

  public abstract String activationName();
  
  @Override
  public String toString()
  {
    return super.toString("Hidden "+activationName()+" ", "   ");
  }
}