package inveniet.ingest;

import org.junit.Assert;
import org.junit.Test;

public class IngesterTest {
  @Test
  public void basicTest() throws Exception {
    Ingester i = new Ingester();

    i.ingestDirectory("authors");

    // ...
  }
}
