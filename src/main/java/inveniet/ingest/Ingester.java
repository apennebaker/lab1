package inveniet.ingest;

import inveniet.index.MemoryIndex;

import java.io.IOException;
import java.io.File;
import java.util.Scanner;

/**
 * This class will take care of ingesting files into the index.
 */
public class Ingester
{
  public MemoryIndex index;

  public Ingester() {
    index = MemoryIndex.getInstance();
  }
  
    /**
     * Ingest a directory into the index also recur to any subdirectories ingesting files
     * @param dirname a string representing the path of the directory to ingest
     * @throws IOException if there is an error with the file or parsing the file
     */
  public void ingestDirectory(String dirname) throws IOException
  {
    /*
         TODO: Ingest all the files in the directory and go to each sub directory and ingest
               their files.
    */

    File dir = new File(dirname);
    File[] files = dir.listFiles();

    for (File f: files) {
      if (f.isDirectory()) {
        ingestDirectory(f.getAbsolutePath());
      }
      else {
        ingestFile(f.getAbsolutePath());
      }
    }
  }

  /**
     * Ingest a file into the index
     * @param filename a string representing the path of the file to ingest
     * @throws IOException if there is an error with the file or parsing the file
     */
  public void ingestFile(String filename) throws IOException
  {
    
    /* 
     TODO: Add all the terms in the file to the index, make sure that terms have been 
               normalized
         */

    // System.out.println("Indexing " + filename + "...");

    Scanner scanner = new Scanner(new File(filename));

    while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        String[] words = line.split("\\s+");

        for (String word:words) {
          String normalized = normalize(word);
          
          index.addTerm(normalized, filename);
        }
    }

    scanner.close();
  }   
  
  /**
     * Poor mans version of normalizing a string (lower case and remove spaces)
     */
  private String normalize(final String str)
  {
    return str.toLowerCase().trim();
  } 
}

