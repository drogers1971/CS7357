package project;

/*
 * File Name: TanhHN.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */

public class TanhHN extends HiddenNeuron 
{
  public TanhHN(int numInputs, int numOutputs)
  {
    super(numInputs, numOutputs);
  }
  
  @Override
  public TanhHN newInstance(int numInputs, int numOutputs)
  {
    return new TanhHN(numInputs, numOutputs);
  }
  
  @Override
  public String activationName()
  {
    return "Hyperbolic Tangent";
  }
  
  @Override
  public double activationFunction(double x)
  {
    return Math.tanh(x);
  }

  /*
    The derivative of the tanh function is (1 + y)(1 - y)
    Using the stochastic gradient descent to correct future values
    The local gradient is the derivative times the dotproduct
    of the output neurons value with the output weights
  */
  @Override
  public void computeLocalGradient()
  {
    double dp = 0;
    for (Neuron n : outputNeurons)
    {
      dp += n.localGradient * weights.get(n);
    }
    localGradient = (1 - neuronValue)*(1 + neuronValue) * dp;
  }
}
