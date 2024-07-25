package project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/*
 * File Name: Main1.java
 *    Author: David Rogers
 *     Email: droge113@students.kennesaw.edu
 */

public class Main1 extends Object
{
  
  private static ArrayList<String> header = new ArrayList<>();
  
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
          Scanner in = new Scanner(f);
          String head = in.nextLine();
          if (header.size() == 0)
          {
            head = head.replace("\"", "").replace(",", " ");
            Scanner in2 = new Scanner(head);
            int colNum = 0;
            while (in2.hasNext() == true)
            {
              String a = in2.next();
              if (excludeColumns.contains(colNum) == false)
              {
                header.add(a);
              }
              colNum++;
            }
          }
          while (in.hasNextLine())
          {
            String line = in.nextLine();
            line = line.replace("\"", "").replace(",", " ");
            Scanner in2 = new Scanner(line);
            double[] row = new double[header.size()];
            
            String company = in2.next();
            if (company.endsWith("B"))
            {
              row[0] = 1;
            }
            row[1] = in2.next().hashCode();

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
       ArrayList<double[]> allData = Main1.loadData("glass_data", 
              4, 5, 6, 7, 8, 9, 10, 11, 13, 14, 15, 16, 17, 18, 19, 20, 21);
    
    // Step 2: scale data (all but the first four columns) into [0.0, 1.0]
      Main1.scaleData(allData);
    
    // Step 3: split data into  
       ArrayList<Double> y = getColumn(0, allData);
       ArrayList<double[]> X = getColumns(4, allData);
      
    // Step 4: separate data randomly into a training set and a testing set
       Random rng = new Random();
       //Random rng = new Random(042771);
       int trainingPercent = 5;

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
    
       System.out.println(Main1.header+"\n");
       
    // Step 5: Create a FFNN and train it on the training data
       int inputSize = trainX.get(0).length;
       int outputSize = 2;
       int[] hiddenLayersSizes = {4};
       int numEpochs = 10;
     
       System.out.print("Press Enter to build network...");
       (new Scanner(System.in)).nextLine();
      
       FFNN network = new FFNN(inputSize, hiddenLayersSizes, outputSize, new ReluHN(10, 2));
       System.out.println(network);
       System.out.print("Press Enter to train network...");
       (new Scanner(System.in)).nextLine();
       double[] actual;
       for (int e = 0; e < numEpochs; e++)
       {
         for (int m = 0; m < trainX.size(); m++)
         {
           double[] inputs = trainX.get(m);
           double act = trainY.get(m);
           actual = new double[] {0, 1};
           if (act == 0) actual = new double[] {1, 0};
           network.trainNetwork(inputs, actual);
         }
       }
      
       System.out.println(network);
       double correct = 0.0;
       for (int m = 0; m < testX.size(); m++)
       {
         double[] inputs = testX.get(m);
         double act = testY.get(m);
         double[] out = network.makePrediction(testX.get(m));
         int predict = 0;
         if (out[1] > out[0]) predict = 1;
         if (act == predict) correct++;
       }
       System.out.println("Accuracy on test set: "+correct/testX.size());

       Scanner in = new Scanner(System.in);
       while (true)
       {
         System.out.print("Enter test number [0 to "+(testX.size()-1)+"] : ");
         int index = in.nextInt();
         System.out.println("   Actual: Company "+(int)(double)testY.get(index));
         double[] out = network.makePrediction(testX.get(index));
         System.out.println("Predicted: Company "+Main1.indexOfMax(out));
         System.out.print("See network? (y/n) ");
         in.nextLine();//consume EOL marker
         String ans = in.nextLine();
         if (ans.startsWith("y")) System.out.println(network+"\n");
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
