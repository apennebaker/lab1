package inveniet.index;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class MemoryIndexTest
{
  @Test
  public void basicTest() throws Exception
  {
    MemoryIndex.getInstance().addTerm("term1", "doc1");
    MemoryIndex.getInstance().addTerm("term2", "doc1");
    MemoryIndex.getInstance().addTerm("term3", "doc1");
    MemoryIndex.getInstance().addTerm("term4", "doc1");
    MemoryIndex.getInstance().addTerm("term2", "doc2");
    MemoryIndex.getInstance().addTerm("term2", "doc3");
    MemoryIndex.getInstance().addTerm("term3", "doc2");
    
    ImmutableSet<String> docIds = new ImmutableSet.Builder<String>().add("doc1", "doc2", "doc3")
                                                                        .build();
        Set<String> docs = MemoryIndex.getInstance().lookupTerm("term2");
        Assert.assertEquals(docIds.size(), docs.size());
        for(String docId : docIds)
        {
            Assert.assertTrue(docs.contains(docId));
        }
  }
}
