package inveniet;

import inveniet.ingest.Ingester;
import inveniet.query.Query;

import java.util.Scanner;

public class App
{
    private static Ingester ingester = new Ingester();
    private static Query query = new Query();

    public static void main(String [] args) throws Exception
    {
        Scanner inputScanner = new Scanner(System.in);
        printHelp(); 
        // Loop forever
        while(true)
        {
            System.out.println("Enter Command: ");
            String command = inputScanner.nextLine();
            handleCommand(command);
        }
    }
    
    private static void printHelp()     
    {
        System.out.println("type in <command>: <arguments>");
        System.out.println("id: <absolute directory path>");
        System.out.println("if: <absolute file  path>");
        System.out.println("q: <absolute file  path>");
        System.out.println("x will exit the program");
        
    }
    
    private static void handleCommand(String command) throws Exception
    {
       command = normalize(command);
       if (command.equalsIgnoreCase("x"))
       {
            System.exit(0);
       }
        
      String [] parts = command.split(":");        
      if(parts.length < 2)
      {
        throw new RuntimeException("Invalid command format");
      }

      String cmd = normalize(parts[0]);
      String arg = parts[1];
      
      if(cmd.equalsIgnoreCase("id"))
      {
        ingester.ingestDirectory(arg);
      }
      else if(cmd.equalsIgnoreCase("if"))
      {
        ingester.ingestFile(arg);
      }  
      else if(cmd.equalsIgnoreCase("q"))
      {
        Iterable<String> results = query.query(arg);
        for(String result : results)
        {
            System.out.println(result);
        }
      }
      else
      {
        printHelp();
      }
    }
    
    private static String normalize(String str)
    {
        return str.toLowerCase().trim();
    }
}
