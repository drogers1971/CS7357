package project;

/*
 * File Name: InputNeuron.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */



/*
  The input neuron has the basic functionality of accepting an input value
  but not needing an activation function or a local gradient.
*/
public class InputNeuron extends Neuron
{
  public InputNeuron(double inputValue, int numOutputs)
  {
    super(1, numOutputs);
    neuronValue = inputValue;
  }

  public void setInputValue(double inputValue)
  {
    neuronValue = inputValue;
  }

  @Override
  public double activationFunction(double x)
  {
    return neuronValue;
  }
  
  @Override
  public void computeLocalGradient()
  {
    localGradient = 0.0;
  }
  
  @Override
  public String toString()
  {
    return super.toString("Input ", "");
  }
}