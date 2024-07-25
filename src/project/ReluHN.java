package project;

/*
 * File Name: ReluHN.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */


public class ReluHN extends HiddenNeuron 
{
  public ReluHN(int numInputs, int numOutputs)
  {
    super(numInputs, numOutputs);
  }
  
  @Override
  public ReluHN newInstance(int numInputs, int numOutputs)
  {
    return new ReluHN(numInputs, numOutputs);
  }
  
  @Override
  public String activationName()
  {
    return "ReLU";
  }
  
  @Override
  public double activationFunction(double x)
  {
    if (x > 0) return x;
    return 0;
  }

  /*
    The derivative of the ReLU function is 0 when x <= 0 and 1 when x > 0
    Using the stochastic gradient descent to correct future values
    The local gradient is the derivative times the dotproduct
    of the output neurons value with the output weights
  */
  @Override
  public void computeLocalGradient()
  {
    if (neuronValue > 0)
    {
      double dp = 0;
      for (Neuron n : outputNeurons)
      {
        dp += n.localGradient * weights.get(n);
      }
      localGradient = 1 * dp;
    }
    else
    {
      localGradient = 0;
    }
  }
}
