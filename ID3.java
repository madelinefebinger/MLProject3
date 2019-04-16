//ID3 algorithm
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class Node{
  private label;
  private Map<String, Node> children = new HashMap<String, Node>();

  public Node(String label) {
      this.label = label;
  }

  public void addChild(String label) {
      Node child = new Node(label);
      this.children.put(label,child);
  }
}

public float informationGain(){
  return 0.0;
}

public Node ID3Alg(ArrayList<ArrayList<String>> examples, String target_attr, ArrayList<String> attrs){
  // Create a root Node

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
}

public static void main(String args[]){
    // Task 2: test on 14 PlayTennis from handout

    // Task 3: test on 4 EnjoySport examples from class

    // Task 4: test on a new set of PlayTennis data
}
