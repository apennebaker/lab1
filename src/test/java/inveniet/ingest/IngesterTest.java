package inveniet.ingest;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class IngesterTest {
  @Test
  public void basicTest() throws Exception {
    Ingester i = new Ingester();

    i.ingestDirectory("authors");

    Set<String> docs = i.index.getInstance().lookupTerm("macbeth");

    System.out.println("macbeth docs:" + docs);

    // ...
  }
}
