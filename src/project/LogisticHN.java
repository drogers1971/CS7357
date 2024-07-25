package project;

/*
 * File Name: LogisticHN.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */



public class LogisticHN extends HiddenNeuron 
{
  public LogisticHN(int numInputs, int numOutputs)
  {
    super(numInputs, numOutputs);
  }
  
  @Override
  public LogisticHN newInstance(int numInputs, int numOutputs)
  {
    return new LogisticHN(numInputs, numOutputs);
  }
  
  @Override
  public String activationName()
  {
    return "Logistic";
  }
  
  @Override
  public double activationFunction(double x)
  {
    return 1 / (1 + Math.exp(-x));
  }

  /*
    The derivative of the logistic function is y(1 - y)
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
    localGradient = neuronValue * (1 - neuronValue) * dp;
  }
}
