 package project;

/*
 * File Name: FFNN.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */

/*
  A feed forward, multi-layer artificial neural network with stochastic 
  gradient of descent back propagation with momentum.  
*/
public class FFNN extends Object
{
  private InputNeuron[] inputLayer;
  private HiddenNeuron[][] hiddenLayers;
  private OutputNeuron[] outputLayer;
  
  private double learnRate = 0.05, momentumFactor = 0.01;

  /**
   * 
   * @param nI number of features / inputs
   * @param hLayers an array indicating the number of neurons in each hidden layer
   * @param nO number of outputs
   * @param archetype type of hidden neuron to be added
   */
  public FFNN(int nI, int[] hLayers, int nO, HiddenNeuron archetype)
  {
    int lastHiddenLayerSize = hLayers[hLayers.length - 1];
    outputLayer = new OutputNeuron[nO];
    for (int m = 0; m < nO; m++)
    {
      outputLayer[m] = new OutputNeuron(lastHiddenLayerSize, Double.NaN);
      outputLayer[m].randomizeBias(0, 1);
    }

    hiddenLayers = new HiddenNeuron[hLayers.length][];
    int numInputs, numOutputs;
    for (int r = hLayers.length - 1; r >= 0; r--)
    {
      if (r == 0)
      {
        numInputs = nI;
      }
      else
      {
        numInputs = hLayers[r-1];
      }
      
      if (r == hLayers.length - 1)
      {
        numOutputs = nO;
      }
      else
      {
        numOutputs = hLayers[r+1];
      }
      int numNeuronsInLayer = hLayers[r];
      hiddenLayers[r] = new HiddenNeuron[numNeuronsInLayer];
      
      Neuron[] nextLayer;
      if (r == hLayers.length - 1)
      {
        nextLayer = outputLayer;
      }
      else
      {
        nextLayer = hiddenLayers[r+1];
      }
      
      for (int c = 0; c < numNeuronsInLayer; c++)
      {
        hiddenLayers[r][c] = archetype.newInstance(numInputs, numOutputs);
        hiddenLayers[r][c].randomizeBias(0, 1);
        hiddenLayers[r][c].randomizeWeights(-1, 1);
        for (Neuron n : nextLayer)
        {
          hiddenLayers[r][c].addOutput(n);
        }
      }  
    }
       
    inputLayer = new InputNeuron[nI];
    for (int m = 0; m < nI; m++)
    {
      inputLayer[m] = new InputNeuron(Double.NaN, hLayers[0]);
      inputLayer[m].randomizeWeights(-1, 1);
      for (Neuron h : hiddenLayers[0])
      {
        inputLayer[m].addOutput(h);
      }
    }

  }
  
  private String inputLayerToString()
  {
    StringBuilder s = new StringBuilder();
    for (InputNeuron i : inputLayer) 
    {
      s.append(i).append("\n\n");
    }
    return s.toString();
  }
  
  
  private String hiddenLayersToString()
  {
    StringBuilder s = new StringBuilder();
    for (int r = 0; r < hiddenLayers.length; r++)
    {
      s.append("============================");
      s.append("Hidden Layer #"+r);
      s.append("============================\n\n");
      for (HiddenNeuron h : hiddenLayers[r])
      {
        s.append(h.toString(h.activationName()+" ", "")).append("\n\n");
      }
      
    }
    return s.toString();
  }
  
  
  private String outputLayerToString()
  {
    StringBuilder s = new StringBuilder();
    for (OutputNeuron o : outputLayer)
    {
      s.append(o.toString("Output", ""));
      s.append("\n  Final output = ");
      s.append(o.getFinalOutputValue());
      s.append("\n\n\n");
    }

    return s.toString();
  }
    
    
  public void setLearnRate(double r)
  {
    learnRate = r;
  }
  
  public void setMomentumFactor(double r)
  {
    momentumFactor = r;
  }

  private void setInputValues(double[] inputs)
  {
    for (int m = 0; m < inputLayer.length; m++)
    {
      inputLayer[m].setInputValue(inputs[m]);
    }
  }

  private void setActualValues(double[] out)
  {
    for (int m = 0; m < outputLayer.length; m++)
    {
      outputLayer[m].setActualValue(out[m]);
    }
  }

  private double[] getOutputs()
  {
    double[] outputs = new double[outputLayer.length];
    for (int m = 0; m < outputs.length; m++)
    {
      outputs[m] = outputLayer[m].getFinalOutputValue();
    }
    return outputs;
  }
  
  public void trainNetwork(double[] inputs, double actual[])
  {
    setInputValues(inputs);
    setActualValues(actual);
    feedForward();
    computeOutputGradients();
    computeHiddenGradients();
    updateAllWeightsAndBiases();    
  }
  
  public double[] makePrediction(double[] inputs)
  {
    setInputValues(inputs);
    feedForward();
    return getOutputs();
  }

  private void feedForward()
  {
    for (Neuron n : inputLayer)
    {
      n.feedForward();
    }
  }

  private void computeOutputGradients()
  {
    for (Neuron o : outputLayer)
    {
      o.computeLocalGradient();
    }
  }
  
  private void computeHiddenGradients()
  {
    for (int r = hiddenLayers.length - 1; r >= 0; r--) 
    {
      HiddenNeuron[] hiddenLayer = hiddenLayers[r];
      for (Neuron h : hiddenLayer) 
      {
        h.computeLocalGradient();
      }
    }
  }

  private void updateAllWeightsAndBiases()
  {
    for (Neuron o : outputLayer) 
    {
      o.updateWeightsAndBias(learnRate, momentumFactor);
    }
    
    for (int r = hiddenLayers.length - 1; r >= 0; r--) 
    {
      HiddenNeuron[] hiddenLayer = hiddenLayers[r];
      for (Neuron h : hiddenLayer) 
      {
        h.updateWeightsAndBias(learnRate, momentumFactor);
      }
    }
  }

  @Override 
  public String toString()
  {
    StringBuilder s = new StringBuilder();
    s.append(inputLayerToString());
    s.append(hiddenLayersToString());
    s.append(outputLayerToString());
    return s.toString();
  }
 
  public static void main(String[] args) 
  {
//    FFNN net = new FFNN(5, new int[] {3, 7, 6}, 2, new ReluHN(6, 2));
    FFNN net = new FFNN(3, new int[] {1, 4}, 2, new ReluHN(4, 2));
    System.out.println(net);
  }
}