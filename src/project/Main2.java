package project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/*
 * File Name: Main2.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */

public class Main2 extends Object
{
  
  private static final ArrayList<String> header = new ArrayList<>();
  private static final ArrayList<String> panes = new ArrayList<>();
  
  private static ArrayList<double[]> loadData(String directory, int... exclude)
  {
    ArrayList<Integer> excludeColumns = new ArrayList<>();
    for (int a = 0; a < exclude.length; a++)
    {
      excludeColumns.add(exclude[a]);
    }
    
    File dir = new File("./build/classes/project/"+directory);
                        
    ArrayList<double[]> data = new ArrayList<>();    
    for (File subDir : dir.listFiles())
    {
      for (File f : subDir.listFiles())
      {
        try
        {
          //System.out.println(f.getName());
          Scanner in = new Scanner(f);
          String head = in.nextLine();
          if (Main2.header.size() == 0)
          {
            head = head.replace("\"", "").replace(",", " ");
            Scanner in2 = new Scanner(head);
            int colNum = 0;
            while (in2.hasNext() == true)
            {
              String a = in2.next();
              if (excludeColumns.contains(colNum) == false)
              {
                Main2.header.add(a);
              }
              colNum++;
            }
          }
          while (in.hasNextLine())
          {
            String line = in.nextLine();
            line = line.replace("\"", "").replace(",", " ");
            Scanner in2 = new Scanner(line);
            double[] row = new double[Main2.header.size()];
            
            String company = in2.next();
            if (company.endsWith("B"))
            {
              row[0] = 1;
            }
            String pane = in2.next();
            if (Main2.panes.contains(pane) == false)
            {
              Main2.panes.add(pane);
            }
            row[1] = Main2.panes.indexOf(pane);

            int m = 2;
            int colNum = 2;
            while (in2.hasNextDouble() == true)
            {
              double a = in2.nextDouble();
              if (excludeColumns.contains(colNum) == false)
              {
                row[m++] = a;
              }
              colNum++;
            }
            data.add(row);
           // System.out.println(java.util.Arrays.toString(row));
          }
        }
        catch (IOException ioe)
        {
          ioe.printStackTrace();
          System.exit(-1);
        }
      }
    }
    System.out.println(Main2.header);
    System.out.println(java.util.Arrays.toString(data.get(0)));
    return data;
  }
  
  private static void scaleData(ArrayList<double[]> vals)
  {
    double[] min = new double[vals.get(0).length];
    double[] max = new double[vals.get(0).length];
    int numRows = vals.size();
    int numCols = vals.get(0).length;
    
    System.arraycopy(vals.get(0), 4, min, 4, numCols - 4);
    System.arraycopy(vals.get(0), 4, max, 4, numCols - 4);
    
    for (double[] row : vals)
    {
      for (int c = 4; c < numCols; c++)
      {
        double x = row[c];
        if (x < min[c])
        {
          min[c] = x;
        }
        if (x > max[c])
        {
          max[c] = x;
        }
      }
    }
        
    for (int c = 4; c < numCols; c++)
    {
      double range = max[c] - min[c];
      if (range != 0)
      {
        for (int r = 0; r < numRows; r++)
        {
          vals.get(r)[c] = (vals.get(r)[c] - min[c]) / range;
        }
      }
    }    
  }
  
  private static ArrayList<Double> getColumn(int col, ArrayList<double[]> x)
  {
    ArrayList<Double> column = new ArrayList<>();
    for (double[] row : x)
    {
      column.add(row[col]);
    }
    return column;
  }
  
  
  private static ArrayList<double[]> getColumns(int startCol, ArrayList<double[]> x)
  {
    ArrayList<double[]> data = new ArrayList<>();
    int numCols = x.get(0).length - startCol;
    for (int r = 0; r < x.size(); r++)
    {
      double[] row = new double[numCols];
      for (int c = 0; c < row.length; c++)
      {
        row[c] = x.get(r)[c + startCol];
      }
      data.add(row);
    }
    return data;
  }
  
  public static void main(String[] args) 
  {
    // Step 1: load all glass data into an array list
       ArrayList<double[]> allData = Main2.loadData("temp02_glass_data");
    
    // Step 2: scale data (all but the first four columns) into [0.0, 1.0]
       Main2.scaleData(allData);
    
    // Step 3: split data into X and y
       //Column 1 is the pane AA-AQ and BA-BQ
       ArrayList<Double> y = getColumn(1, allData);
       ArrayList<double[]> X = getColumns(4, allData);
      
    // Step 4: separate data randomly into a training set and a testing set
       Random rng = new Random();
       //Random rng = new Random(042771);
       int trainingPercent = 80;

       ArrayList<double[]> trainX = new ArrayList<>();
       ArrayList<Double> trainY = new ArrayList<>();

       ArrayList<double[]> testX = new ArrayList<>();
       ArrayList<Double> testY = new ArrayList<>();

       int trainingSize = X.size() * trainingPercent / 100;

       for (int m = 0; m < trainingSize; m++)
       {
         int index = rng.nextInt(X.size());
         trainX.add(X.remove(index));
         trainY.add(y.remove(index));
       }

       while (X.size() > 0)
       {
         int index = rng.nextInt(X.size());
         testX.add(X.remove(index));
         testY.add(y.remove(index));
       }
    
    // Step 5: Create a FFNN and train it on the training data
       //inputSize should be 18 for the 18 isotopes recorded in the dataset
       int inputSize = trainX.get(0).length;
       System.out.println("inputSize = "+inputSize);
      
       //Number of panes
       int outputSize = Main2.panes.size();
       int[] hiddenLayersSizes = {66};
       int numEpochs = 100;
            
       FFNN network = new FFNN(inputSize, hiddenLayersSizes, outputSize, new ReluHN(1, 1));
       network.setLearnRate(0.05);
       network.setMomentumFactor(0.9);
      
      
       System.out.println(network);
       double[] actual;
       long timeOld = System.currentTimeMillis();
       for (int e = 0; e < numEpochs; e++)
       {
         for (int m = 0; m < trainX.size(); m++)
         {
           double[] inputs = trainX.get(m);
           double act = trainY.get(m);
           actual = new double[Main2.panes.size()];
           actual[(int)act] = 1;
           network.trainNetwork(inputs, actual);
         }
         long timeNew = System.currentTimeMillis();
         System.out.printf("Epoch "+e+" of "+numEpochs+" taking %.2f seconds\n", (timeNew-timeOld)/1000.0);
         timeOld = timeNew;
       }
      
       double correct = 0.0;
       for (int m = 0; m < testX.size(); m++)
       {
         double[] inputs = testX.get(m);
         double act = testY.get(m);
         double[] out = network.makePrediction(inputs);
         int predict = 0;
        
         for (int n = 1; n < out.length; n++)
         {
           if (out[n] > out[predict])
           {
             predict = n;
           }
         }
         if (act == predict) correct++;
       }
       System.out.println("\n\nAccuracy on test set: "+correct/testX.size());
      
      
      //System.out.println(testY);
       Scanner in = new Scanner(System.in);
       while (true)
       {
         System.out.print("Enter test number [0 to "+(testX.size()-1)+"] : ");
         int index = in.nextInt();
         String actualPane = Main2.panes.get((int)(double)testY.get(index));
         System.out.println("   Actual: pane "+actualPane);
         double[] out = network.makePrediction(testX.get(index));
         System.out.println("Predicted: pane "+Main2.panes.get(Main2.indexOfMax(out)));
         System.out.println("");
       }    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  private static int indexOfMax(double[] x)
  {
    int index = 0;
    for (int m = 1; m < x.length; m++)
    {
      if (x[m] > x[index])
      {
        index = m;
      }
    }
    return index;
  }
}
