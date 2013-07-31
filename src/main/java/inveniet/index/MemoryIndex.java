package inveniet.index;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.HashMultimap;

import java.util.Set;

public class MemoryIndex
{
  public SetMultimap<String, String> index;
    
  /**
   * Get a instance of the database
   * @return a instance of the database
   */
  public static MemoryIndex getInstance()
    {
      return MemoryIndexHolder.INSTANCE;        
    }
   
  /**
   * Add a term and a document to the index 
   * @param term a string to represent the term
   * @param docId the filename from which the term came from
   */
  public void addTerm(final String term, final String docId)
    {
      index.put(term, docId);    
    }
    
  /**
   * Fetch a single term from the index
   * @param term that we are looking for in the index
   * @return a set of docIds which match the term or null if the term is not in the index
   */
  public Set<String> lookupTerm(String term)
    {
      if(!index.containsKey(term))
      {
        return null;
      }    
        
      return index.get(term);
    }

  public void clear() {
    index = HashMultimap.create();
  }

  private MemoryIndex()
    {
      this.clear();
    }

  private static class MemoryIndexHolder 
  {
    private static final MemoryIndex INSTANCE = new MemoryIndex();
  }
}
