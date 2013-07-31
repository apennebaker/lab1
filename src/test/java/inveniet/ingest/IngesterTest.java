package inveniet.ingest;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

public class IngesterTest {
  @Test
  public void basicTest() throws Exception {
    Ingester i = new Ingester();

    i.ingestDirectory("authors");
    Map<String,Collection<String>> map = i.index.index.asMap();

    Collection<Collection<String>> docs = map.values();
    HashSet<String> uniqueDocs = new HashSet();
    for (Collection<String> results:docs) {
      uniqueDocs.addAll(results);
    }

    for (String doc:uniqueDocs) {
      System.out.println(doc);
    }

    // Unique docs
    // find authors/ -type f | wc -l
    Assert.assertEquals(uniqueDocs.size(), 94);

    // Unique words in authors/
    Assert.assertEquals(map.size(), 105243);

    Set<String> macbethDocs = i.index.lookupTerm("macbeth");
    Assert.assertEquals(macbethDocs.size(), 2);
  }
}
