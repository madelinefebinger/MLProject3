//ID3 algorithm
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.Scanner;
import java.util.Arrays;

public class ID3{

class Node{
  private String label;
  private Map<String, Node> children = new HashMap<String, Node>();

  public Node(String label) {
      this.label = label;
  }

  public void addChild(String label) {
      Node child = new Node(label);
      this.children.put(label,child);
  }
}

public static double calculateEntropy(ArrayList<ArrayList<String>> examples){
  // Get proportions of positive and negative examples
  double positive = 0; // positive examples count
  double negative = 0; // negative examples count
  for (int i = 0; i < examples.size(); i++){ // for each training example
    ArrayList<String> ex = examples.get(i);
    String sign = ex.get(ex.size()-1);
    if (sign.equals("Yes")){
      positive += 1;
    }
    else if (sign.equals("No")){
      negative += 1;
    }
  }
  double p1 = positive / (positive + negative);
  double p2 = negative / (positive + negative);

  double log2p1 = Math.log(p1) / Math.log(2);
  double log2p2 = Math.log(p2) / Math.log(2);


  double entropy = (-1)*p1*log2p1 - p2*log2p2;

  return entropy;
}

public static double informationGain(ArrayList<ArrayList<String>> examples, String attr, int attr_index){

  double entropy_s = calculateEntropy(examples);
    
  // Find the index of that attribute is i 
  // Make a map that maps the attribute value to an array list of examples with that attribute value
  Map<String, ArrayList<ArrayList<String>>> valueToExamplesMap = new HashMap<String, ArrayList<ArrayList<String>>>();
  for (int i = 0; i < examples.size(); i++){ // For each example
    ArrayList<String> ex = examples.get(i);
    String value = ex.get(attr_index);
    
    if (valueToExamplesMap.containsKey(value)){ //  If the value is in the map, append the example to the ArrayList
      valueToExamplesMap.get(value).add(ex);
    }
    else{ //  Else add that value to the map
      ArrayList<ArrayList<String>> examples_v = new ArrayList<ArrayList<String>>();
      examples_v.add(ex);
      valueToExamplesMap.put(value,examples_v);
    }   
  }

  double infoGain = 0;

  for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : valueToExamplesMap.entrySet()) { // for each value of A
    String value = entry.getKey();
    ArrayList<ArrayList<String>> examples_v = entry.getValue();

    double entropy_v = calculateEntropy(examples_v); // calculate the entropy of examples_v
    infoGain += ((double)examples_v.size() / examples.size()) * entropy_v; // info gain = |examples_v| / |examples| * entropy(examples_v)
  }
  infoGain = entropy_s - infoGain;

  return infoGain;
}


public Node ID3Alg(ArrayList<ArrayList<String>> examples, String target_attr, ArrayList<String> attrs){
  // Create a root Node
  Node root = new Node("Root");

  // Base cases
  // If all training ex. are positive, return the single node tree root with label +
  // If all training ex. are negative, return the single node tree root with label -
  // If attrs is empty, return the single node tree root with label = most common value of target_attr in examples

  //  Otherwise

  // Select an attribute A that gives the most information gain

  // For each possible value v_i of a
    // Add a new branch below root corresponding to the test A = v_i
    // Let examples_v_i be the subset of examples that have v_i for a
    // If examples_v_i is empty, add leaf node with label = most common value of target_attr in examples_v_i
    // Else add the subtree ID3(examples_v_i, target_attr, attrs - A)

  // return root
  return root;
}

// Gets the attrs from the first line of the file
public static ArrayList<String> getAttrsFromFile(String fileName){
  File file = new File(fileName);
  ArrayList<String> attrs = new ArrayList<String>();
  try{
    Scanner scan = new Scanner(file); // create scanner to read from file
    String line = scan.nextLine();
    attrs = new ArrayList<String>(Arrays.asList(line.split(",")));
  }
  catch(FileNotFoundException ex){
  }
  return attrs;
}


// Get examples from file 
// The first row of the file should have a list of the attrs
// File should have 1 training example per line, each attribute separated by a comma
// see PlayTennisSampleDataFormat.txt as an example of how to format the file
public static ArrayList<ArrayList<String>> getExamplesFromFile(String fileName){
  File file = new File(fileName);

  ArrayList<ArrayList<String>> examples = new ArrayList<ArrayList<String>>(); // create list of training examples
  try{
    Scanner scan = new Scanner(file); // create scanner to read from file
    scan.nextLine(); // don't use the first line of the file (the attribute names)
    while (scan.hasNextLine()) {
      String line = scan.nextLine();
      ArrayList<String> ex = new ArrayList<String>(Arrays.asList(line.split(",")));
      examples.add(ex);
    }
  }
  catch(FileNotFoundException ex){
  }


  return examples;
}

public static void main(String args[]){
    ArrayList<String> playTennisAttrs = getAttrsFromFile("PlayTennisSampleDataFormat.txt"); // Gets the attr names from the first line of the file
    ArrayList<ArrayList<String>> playTennisExamples = getExamplesFromFile("PlayTennisSampleDataFormat.txt"); // Creates an ArrayList where each example is an ArrayList<String>

    ArrayList<String> infoGainTestAttrs = getAttrsFromFile("informationGainTest.txt");
    ArrayList<ArrayList<String>> infoGainTestExamples = getExamplesFromFile("informationGainTest.txt");   

    System.out.println(informationGain(infoGainTestExamples,"Wind",0));

    // Task 2: test on 14 PlayTennis from handout

    // Task 3: test on 4 EnjoySport examples from class

    // Task 4: test on a new set of PlayTennis data
}
}
