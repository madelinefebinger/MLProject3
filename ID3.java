//ID3 algorithm
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Queue;
import java.util.LinkedList;

public class ID3{
public ID3(String inputFile, String outputFile){

  ArrayList<String> attrs = getAttrsFromFile(inputFile);
  String target_attr = attrs.get(attrs.size()-1);
  attrs.remove(attrs.size()-1); // Remove target_attr from attrs list

  ArrayList<ArrayList<String>> examples = getExamplesFromFile(inputFile);


  Map<String,Integer> attrToIndexMap = new HashMap<String, Integer>();
  for (int i = 0; i < attrs.size(); i++){
    attrToIndexMap.put(attrs.get(i),i);
  }

  try{
    PrintStream fileOut = new PrintStream(outputFile);
    System.setOut(fileOut);
  }
  catch(FileNotFoundException ex){
  }


  Node root = ID3Alg(examples,target_attr,attrs,attrToIndexMap);
  printTree(root);
}

class Node{
  private String label;
  private Map<String, Node> children = new HashMap<String, Node>();

  public Node(String label) {
      this.label = label;
  }

  public Node addChild(String label,Node child) {
      this.children.put(label,child);
      return child;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return this.label;
  }

  public Map<String, Node> getChildren() {
    return children;
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

  double log2p1 = 0;
  double log2p2 = 0;

  if (p1 > 0){
    log2p1 = Math.log(p1) / Math.log(2);
  }
  if (p2 > 0){
    log2p2 = Math.log(p2) / Math.log(2);
  }

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


public Node ID3Alg(ArrayList<ArrayList<String>> examples,String target_attr,ArrayList<String> attrs, Map<String, Integer> attrToIndexMap){
  // Create a root Node
  Node root = new Node("");

  // Count the number of positive and negative examples
  int positive = 0;
  int negative = 0;
  for (int i = 0; i < examples.size(); i++){
    ArrayList<String> ex = examples.get(i);
    if (ex.get(ex.size()-1).equals("Yes")){
      positive++;
    }
    else if (ex.get(ex.size()-1).equals("No")){
      negative++;
    }
  }
  // Base cases

  // If all training ex. are positive, return the single node tree root with label +
  if (negative == 0){
    return new Node("Yes");
  }
  // If all training ex. are negative, return the single node tree root with label -
  if (positive == 0){
    return new Node("No");
  }
  // If attrs is empty, return the single node tree root with label = most common value of target_attr in examples
  if (attrs.size() == 0){
    if (positive >= negative){
      return new Node("Yes");
    }
    else{
      return new Node("No");
    }
  }

  //  Otherwise
  // Select an attribute A that gives the most information gain

  String best_attr = attrs.get(0);
  double best_gain = informationGain(examples,attrs.get(0),attrToIndexMap.get(best_attr));
  int best_attr_index = attrToIndexMap.get(best_attr);

  for (int i = 1; i < attrs.size(); i++){
    String attr = attrs.get(i);
    double gain = informationGain(examples, attr, attrToIndexMap.get(attr));

    if (gain > best_gain){
      best_attr = attr;
      best_gain = gain;
      best_attr_index = attrToIndexMap.get(attr);
    }
  }

  root.setLabel(best_attr);

  // Create a map of each attribute value to its subset of examples
  Map<String, ArrayList<ArrayList<String>>> valueToExamplesMap = new HashMap<String, ArrayList<ArrayList<String>>>();
  for (int i = 0; i < examples.size(); i++){ // For each example
    ArrayList<String> ex = examples.get(i);
    String value = ex.get(best_attr_index);

    if (valueToExamplesMap.containsKey(value)){ //  If the value is in the map, append the example to the ArrayList
      valueToExamplesMap.get(value).add(ex);
    }
    else{ //  Else add that value to the map
      ArrayList<ArrayList<String>> examples_v = new ArrayList<ArrayList<String>>();
      examples_v.add(ex);
      valueToExamplesMap.put(value,examples_v);
    }
  }
  // For each possible value v_i of a
    // Add a new branch below root corresponding to the test A = v_i
    // Let examples_v_i be the subset of examples that have v_i for a
    // If examples_v_i is empty, add leaf node with label = most common value of target_attr in examples_v_i
    // Else add the subtree ID3(examples_v_i, target_attr, attrs - A)

  for (Map.Entry<String, ArrayList<ArrayList<String>>> entry : valueToExamplesMap.entrySet()) { // for each value of A
    String v_i = entry.getKey();
    ArrayList<ArrayList<String>> examples_v_i = entry.getValue();

    if (examples_v_i.size() == 0){
      if (positive >= negative){
        return new Node("Yes");
      }
      else{
        return new Node("No");
      }
    }

    ArrayList<String> new_attrs = new ArrayList<String>();
    for (int i = 0; i < attrs.size(); i++){
      if (!attrs.get(i).equals(best_attr))
        new_attrs.add(attrs.get(i));
    }

    root.addChild(v_i,ID3Alg(examples_v_i,target_attr,new_attrs,attrToIndexMap));
  }

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

public static void printTree(Node root) {
  Queue<Node> q = new LinkedList<>();
  q.add(root);

  while(!q.isEmpty()){
    Node n = q.poll();
    Map<String,Node> children = n.getChildren();

    for (Map.Entry<String, Node> entry : children.entrySet()) { // for each child node
      Node child = entry.getValue();
      q.add(child);
    }

    if(!children.isEmpty()){
      System.out.print(n.getLabel()+" has children: ");
      for (Map.Entry<String, Node> entry : children.entrySet()) { // for each child node
        String branch = entry.getKey();
        Node child = entry.getValue();
        System.out.print("["+branch+","+child.getLabel()+"] ");
      }
      System.out.println();
    }

  }
}


public static void main(String args[]){
    /*
    // Task 1: Implement ID3

    // Task 2: test on 14 PlayTennis from handout
    System.out.println("Task 2");
    ArrayList<String> task2Attrs = getAttrsFromFile("task2.txt");
    ArrayList<ArrayList<String>> task2Examples = getExamplesFromFile("task2.txt");
    ID3 task2 = new ID3(task2Examples,"PlayTennis",task2Attrs);

    // Task 3: test on 4 EnjoySport examples from class

    System.out.println("Task 3");
    ArrayList<String> task3Attrs = getAttrsFromFile("task3.txt");
    ArrayList<ArrayList<String>> task3Examples = getExamplesFromFile("task3.txt");
    ID3 task3 = new ID3(task3Examples,"EnjoySport",task3Attrs);

    // Task 4: test on a new set of PlayTennis data
    System.out.println("Task 4");
    ArrayList<String> task4Attrs = getAttrsFromFile("task4.txt");
    ArrayList<ArrayList<String>> task4Examples = getExamplesFromFile("task4.txt");
    ID3 task4 = new ID3(task4Examples,"EnjoySport",task4Attrs);
    */

    if (args.length == 2) 
    { 
      String inputDataFileLocation = args[0];
      String outputFileLocation = args[1];

      ID3 task = new ID3(inputDataFileLocation,outputFileLocation);
    } 
    else{
      System.out.println("Run the program in the following format: java ID3 inputDataFileLocation outputFileLocation"); 
    }  
}
}
