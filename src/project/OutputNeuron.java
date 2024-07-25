package project;

/*
 * File Name: OutputNeuron.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */

import java.util.ArrayList;

/*
  Uses the soft max function applied to all the output neurons in the network
  to determine the output value 
*/
public class OutputNeuron extends Neuron
{
  private double actualValue;
  
  public OutputNeuron(int numInputs, double v)
  {
    super(numInputs, 1);
    actualValue = v;
  }

  public void setActualValue(double v)
  {
    actualValue = v;
  }
  
  @Override
  public double activationFunction(double x)
  {
    return x;
  }

  

  /*
    The local derivative of softmax is (1 - y)*y
  */
  @Override
  public void computeLocalGradient()
  {
    double nv = getFinalOutputValue();
    localGradient = ((1 - nv)*nv)*(actualValue - nv);
  }

  public double getFinalOutputValue()
  {
    double denominator = 0.0;
    ArrayList<Neuron> allOutputNeurons = inputNeurons.get(0).outputNeurons; 
    for (Neuron n : allOutputNeurons)
    {
      denominator += Math.exp(n.neuronValue);
    }
    return Math.exp(neuronValue) / denominator;
  }

  @Override
  public String toString()
  {
    String s = super.toString("Output ", "      ");
    s += "        Final output = "+getFinalOutputValue()+"\n\n";
    return s;    
  }
}