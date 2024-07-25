package project;

/*
 * File Name: Neuron.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/*
  The base abstract class for all future neuron subclasses.
  A neuron object maintains a list of input neurons and a list of output
  neurons.  
*/
public abstract class Neuron extends Object
{
//  private static final Random rng = new Random();
  private static final Random rng = new Random(042771);
  protected final int numInputs, numOutputs;
  protected double bias, neuronValue, localGradient;
  
  protected ArrayList<Neuron> inputNeurons, outputNeurons;
  protected HashMap<Neuron, Double> weights;
  protected HashMap<Neuron, Double> deltaValues;
  
  private static int ID_GENERATOR = 99;
  private int ID;

  public Neuron(int numInputs, int numOutputs)
  {
    if (numInputs < 1)
    {
      throw new Error("First explicit parameter (numInputs) must be positive");
    }
    if (numOutputs < 1) 
    {
       throw new Error("Second explicit parameter (numOutputs) must be positive");
    }
    ID = ID_GENERATOR++;
    weights =  new HashMap<Neuron, Double>();
    deltaValues =  new HashMap<Neuron, Double>();
    inputNeurons = new ArrayList<Neuron>();
    outputNeurons = new ArrayList<Neuron>();
    this.numInputs = numInputs;
    this.numOutputs = numOutputs;
    bias = 0.011;
  }

  protected double preActivationValue()
  {
    double cVal = bias;
    for (Neuron n : inputNeurons)
    {
      Double d = n.weights.get(this);
      if (d == null) d = 0.0;
      cVal += n.neuronValue * d;
    }
    return cVal;
  }

  
  public void updateWeightsAndBias(double learnRate, double momentumFactor)
  {
    boolean firstTimeThrough = true;
    for (Neuron n : inputNeurons)
    {
      double oldDelta = n.deltaValues.get(this);
      double oldWeight = n.weights.get(this);
      
      double newDelta = learnRate * localGradient * n.neuronValue;
      double momentum = momentumFactor * oldDelta;

      double newWeight = oldWeight + newDelta + momentum;

      if (firstTimeThrough)
      {
        firstTimeThrough = false;
        bias += learnRate * localGradient + momentum;
      }
      n.weights.put(this, newWeight);
      n.deltaValues.put(this, newDelta);
    }    
  }

  public void randomizeBias(double min, double max)
  {
    bias = min + rng.nextDouble() * (max - min);
  }
  
  public void randomizeWeights(double min, double max)
  {      
    for (Neuron n : weights.keySet())
    {
      weights.put(n, min + rng.nextDouble() * (max - min));
    }
  }
  
  public void addOutput(Neuron n)
  {
    if (n == this)
    {
      throw new Error("Self-referential error: cannot add self to set of output neurons");
    }
    if (outputNeurons.size() == numOutputs)
    {
      throw new Error("Too many output neurons have been added, max = "+numOutputs);
    }
    if (outputNeurons.contains(n))
    {
      throw new Error("Attempt to add neuron "+n.ID+" twice to outputs");
    }
    outputNeurons.add(n);
    weights.put(n, 0.0);
    deltaValues.put(n, 0.01);
    n.inputNeurons.add(this);
  }

  public void feedForward()
  {
    neuronValue = activationFunction(preActivationValue());
    for (Neuron n : outputNeurons) n.feedForward();
  }

  protected String toString(String className, String spaces)
  {
    String s = spaces + className+"Neuron: "+ID+"\n";
    s += spaces + "----------------------------------\n";
    s += spaces + "          bias = "+String.format("%6f",bias)+"\n";
    s += spaces + " current value = "+String.format("%6f",neuronValue)+"\n";
    s += spaces + "local gradient = "+String.format("%6f",localGradient)+"\n";
    if (inputNeurons.size() > 0)
    {
      s += spaces + " Input neurons =";
      for (Neuron n : inputNeurons)
      {
        s += String.format("%4d",n.ID);
      }
      if (this instanceof OutputNeuron == false) s += "\n\n";
    }
       
    if (outputNeurons.size() > 0)
    { 
      s += spaces + "Output neurons =";
      for (Neuron n : outputNeurons)
      {
        s += String.format("%11d",n.ID);
      }    
      s += "\n";
      s += spaces + "       weights =";
      for (Neuron n : outputNeurons)
      {
        s += String.format("%11f", weights.get(n));
      }    
      s += "\n";
      s += spaces + "  delta values =";      
      for (Neuron n : outputNeurons)
      {
        s += String.format("%11f", deltaValues.get(n));
      }
    }
    return s +"\n";
  }

  public abstract double activationFunction(double x);
  public abstract void computeLocalGradient();
}
