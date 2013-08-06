package inveniet.query;

// import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

public class QueryTest {
  @Test
  public void basicTest() throws Exception {
    Query q = new Query("authors");

    // Results for "macbeth"
    // ack -li macbeth authors/ | wc -l
    Set<String> macbethDocs = q.query("macbeth");
    Assert.assertEquals(macbethDocs.size(), 2);

    // Results for "romeo"
    // ack -li romeo authors/ | wc -l
    Set<String> romeoDocs = q.query("romeo");
    Assert.assertEquals(romeoDocs.size(), 1);

    // Results for "macbeth" or "romeo"
    // ack -li "macbeth|romeo" authors/ | wc -l
    Set<String> macbethOrRomeoDocs = q.query("macbeth OR romeo");
    Assert.assertEquals(macbethOrRomeoDocs.size(), 3);

    // Results for "ye" and "thou"
    Set<String> yeAndThouDocs = q.query("ye AND thou");
    Assert.assertEquals(yeAndThouDocs.size(), 36);

    // Results for "elementary AND watson"
    Set<String> elementaryAndWatsonDocs = q.query("elementary AND watson");
    Assert.assertEquals(elementaryAndWatsonDocs.size(), 7);

    // Results for "watson"
    Set<String> watsonDocs = q.query("watson");
    Assert.assertEquals(watsonDocs.size(), 30);

    // Results for "watson NOT romeo"
    Set<String> watsonNotRomeoDocs = q.query("watson NOT romeo");
    Assert.assertEquals(watsonNotRomeoDocs.size(), 30);

    // Empty query returns all documents
    // find authors/ -type f | wc -l
    Set<String> all = q.query("");
    Assert.assertEquals(all.size(), 94);

    // Bad queries
    Set<String> bad1 = q.query("AND");
    Assert.assertEquals(bad1.size(), 0);

    Set<String> bad2 = q.query("macbeth romeo");
    Assert.assertEquals(bad2.size(), 0);

    Set<String> bad3 = q.query("macbeth OR romeo AND");
    Assert.assertEquals(bad3.size(), 0);
  }
}
