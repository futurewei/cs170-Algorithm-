/**
 * Created by Lily on 11/26/16.
 */
import java.util.*;
import java.io.*;
public class Main {
    public static final double NUM_ITERATIONS = 1000;

    public static void main(String[] args) {
        try {
            PrintWriter writer = new PrintWriter("cs170_final_outputs2/" + 82 + ".out", "UTF-8");
            
            PrintWriter writer2 = new PrintWriter("cs170_final_outputs2/" + 82 + "score.out", "UTF-8"); //CHANGED
            
//            for (int filename = 1; filename<601; filename++) {
                //PARSE INPUT FILE
                String file = "cs170_final_inputs/" + 228 + ".in";
                List<String> inputs = readFile(file);
                int size = Integer.parseInt(inputs.get(0));
                Graph mainGraph = new Graph(size);
                for (int row = 1; row < size + 1; row++) {
                    String[] line = inputs.get(row).split("\\s+");
                    for (int col = 0; col < size; col++) {
                        if (row - 1 == col) {
                            mainGraph.addProb(row - 1, Integer.parseInt(line[col]));
                        } else {
                            String digit = line[col];
                            if (digit.equals("1")) {
                                mainGraph.addEdge(row - 1, col);
                            }
                        }
                    }
                }

//                    System.out.println("THE FOLLOWING IS GREEDY");

                int bestGREEDYScore = -1;
                Set<List<Integer>> bestGREEDYPermutation = null;
                
                for (int i = 0; i < NUM_ITERATIONS; i++) {
                    Graph G = new Graph(mainGraph);
                    Set<List<Integer>> teams = G.getAPermutation("LONG");
                    int totalScore = G.totalScore();
                    if (totalScore > bestGREEDYScore) {
                        bestGREEDYScore = totalScore;
                        bestGREEDYPermutation = teams;
                    }
                }
                
                writer2.print(bestGREEDYScore); //CHANGED
   
                
//                System.out.println(bestGREEDYScore);
//                System.out.println(bestGREEDYPermutation);

//                if (bestGREEDYPermutation.size()!=1) {
//                    writer.println();
//                    continue;
//                }

                Iterator<List<Integer>> iter = bestGREEDYPermutation.iterator();
                List<Integer> perm = iter.next();
                Iterator<Integer> permIter = perm.iterator();
                writer.print(permIter.next());
                while (permIter.hasNext()) {
                    writer.print(" ");
                    writer.print(permIter.next());
                }

                while (iter.hasNext()) {
                    perm = iter.next();
                    writer.print("; ");
                    permIter = perm.iterator();
//                    if (permIter.hasNext()) {
//                        System.out.print(" ");
//                    }
                    writer.print(permIter.next());
                    while (permIter.hasNext()) {
                        writer.print(" ");
                        writer.print(permIter.next());
                    }

                }

                writer.println();
                writer2.println(); //CHANGED
//            }
            writer.close();
            writer2.close(); //CHANGED
        } catch (IOException e) {
           // do something
        } catch (Exception e) {
            System.out.print("An error has occured");
        }
    }




    /**
     * Open and read a file, and return the lines in the file as a list
     * of Strings.
     * (Demonstrates Java FileReader, BufferedReader, and Java5.)
     */
    private static List<String> readFile(String filename)
    {
        List<String> records = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null)
            {
                records.add(line);
            }
            reader.close();
            return records;
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }


}
