package inveniet.ingest;

import inveniet.index.MemoryIndex;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.After;

public class IngesterTest {
  @Test
  public void normalizeTest() {
    Assert.assertEquals(Ingester.normalize("abc"), "abc");

    Assert.assertEquals(Ingester.normalize("ABC"), "abc");

    Assert.assertEquals(Ingester.normalize("aBC"), "abc");

    Assert.assertEquals(Ingester.normalize(""), "");

    Assert.assertEquals(Ingester.normalize("abc "), "abc");

    Assert.assertEquals(Ingester.normalize(" abc"), "abc");

    Assert.assertEquals(Ingester.normalize(" abc "), "abc");

    Assert.assertEquals(Ingester.normalize(" ABc "), "abc");
  }

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

    // Unique docs
    // find authors/ -type f | wc -l
    Assert.assertEquals(uniqueDocs.size(), 94);

    // Unique words
    // find authors/ -type f | xargs cat | LC_ALL='C' tr '[:space:]' '\n' | LC_ALL='C' sort -uf | wc -l
    Assert.assertEquals(map.size(), 105239);

    // Results for "macbeth"
    // ack -li macbeth authors/ | wc -l
    Set<String> macbethDocs = i.index.lookupTerm("macbeth");
    Assert.assertEquals(macbethDocs.size(), 2);
  }

  @After
  public void cleanIndex() {
    MemoryIndex.getInstance().clear();
  }
}
