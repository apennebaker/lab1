package inveniet.query;

import inveniet.ingest.Ingester;

import java.io.IOException;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class Query {
  /**
     * This will query the index and return the documents that match the query
     *
     * Query String will have foo AND bar OR biz NOT baz
     * The query will be parsed from left to right.
     * The following stop words (in all caps) will be defined as follows:
     * AND - will only keep document ids where both term 1 and term 2 appear
     * OR - will keep documents where either term 1 or term 2 appear
     * NOT - will keep documents which have term 1 but not term 2 
     *
     * Example:  term1 AND term2 OR term3 NOT term4
     *           This will keep all document ids that have both term1 and term2.  It will then keep
     *           all document ids which appear in the previous set (term1 AND term2) or documents
     *           which term3 appear in.  It will then keep all the document ids from the previous
     *           part of the query and remove all document ids from that set which term4 appears in.
     *
     *@return a iterable of document ids which match the query, should return an empty iterable if
     *        nothing is found.
     */

  public Ingester ingester;

  public Query(String directory) throws IOException {
    ingester = new Ingester();
    ingester.ingestDirectory(directory);
  }

  private interface Expression {
    public Set<String> reduce();
  }

  // "foo"
  private class Term implements Expression {
    private String term;

    public Term(String s) {
      term = Ingester.normalize(s);
    }

    public Set<String> reduce() {
      return ingester.index.lookupTerm(term);
    }
  }

  // ... AND ...
  private class Intersection implements Expression {
    private Expression operandX, operandY;

    public Intersection(Expression e1, Expression e2) {
      operandX = e1;
      operandY = e2;
    }

    public Set<String> reduce() {
      Set<String> x = operandX.reduce();
      Set<String> y = operandY.reduce();

      x.retainAll(y);
      return x;
    }
  }

  // ... OR ...
  private class Union implements Expression {
    private Expression operandX, operandY;

    public Union(Expression e1, Expression e2) {
      operandX = e1;
      operandY = e2;
    }

    public Set<String> reduce() {
      Set<String> x = operandX.reduce();
      Set<String> y = operandY.reduce();

      x.addAll(y);
      return x;
    }
  }

  // ... NOT ...
  private class Negation implements Expression {
    private Expression operandX, operandY;

    public Negation(Expression e1, Expression e2) {
      operandX = e1;
      operandY = e2;
    }

    public Set<String> reduce() {
      Set<String> x = operandX.reduce();
      Set<String> y = operandY.reduce();

      x.removeAll(y);
      return x;
    }
  }

  private interface LexedToken {
    public String toString();
  }

  private class LexedTerm implements LexedToken {
    private String term;

    public LexedTerm(String s) {
      term = s;
    }

    public String toString() {
      return term;
    }
  }

  public static boolean isOp(String s) {
    return s.equals("AND") || s.equals("OR") || s.equals("NOT");
  }

  private class LexedOp implements LexedToken {
    private String op;

    public LexedOp(String s) {
      op = s;
    }

    public String toString() {
      return op;
    }
  }

  public Set<String> query(String query) {
    String[] parsedTokens = query.split("\\s+");

    // Invalid query
    if (parsedTokens.length % 2 == 0) {
      return new HashSet();
    }

    ArrayList<LexedToken> lexedTokens = new ArrayList<LexedToken>();
    for (String parsedToken:parsedTokens) {
      if (Query.isOp(parsedToken)) {
        lexedTokens.add(new LexedOp(parsedToken));
      }
      else {
        lexedTokens.add(new LexedTerm(parsedToken));
      }
    }

    return query(null, lexedTokens);
  }

  public Set<String> query(Expression e, List<LexedToken> tokens) {
    int size = tokens.size();

    // Empty query
    if (e == null && size == 0) {
      return new HashSet();
    }
    // Single term or invalid query
    else if (e == null && size == 1) {
      LexedToken token = tokens.get(0);
      if (token instanceof LexedOp) {
        return new HashSet();
      }
      else {
        return new Term(token.toString()).reduce();
      }
    }
    // Invalid query
    else if (e == null && size == 2) {
      return new HashSet();
    }
    // Start of Boolean query, or invalid query
    else if (e == null && size > 2) {
      LexedToken tok1 = tokens.get(0);
      LexedToken tok2 = tokens.get(1);
      LexedToken tok3 = tokens.get(2);

      // Invalid query
      if (tok1.getClass() == LexedOp.class ||
          tok2.getClass() == LexedTerm.class ||
          tok3.getClass() == LexedOp.class) {
        return new HashSet();
      }
      else {
        String parsedTermX = tok1.toString();
        String op = tok2.toString();
        String parsedTermY = tok3.toString();

        Term termX = new Term(parsedTermX);
        Term termY = new Term(parsedTermY);

        if (op.equals("AND")) {
          e = new Intersection(termX, termY);
        }
        else if (op.equals("OR")) {
          e = new Union(termX, termY);
        }
        else {
          e = new Negation(termX, termY);
        }

        List rest = tokens.subList(3, size);

        return query(e, rest);
      }
    }
    // Completion of Boolean query
    else if (e != null && size == 0) {
      return e.reduce();
    }
    // Invalid continuation of Boolean query
    else if (e != null && size == 1) {
      return new HashSet();
    }
    // Continuation of Boolean query, or invalid query
    else if (e != null && size >= 2) {
      LexedToken tok1 = tokens.get(0);
      LexedToken tok2 = tokens.get(1);

      // Invalid query
      if (tok1.getClass() == LexedTerm.class ||
          tok2.getClass() == LexedOp.class) {
        return new HashSet();
      }
      else {
        String parsedOp = tok1.toString();
        String parsedTerm = tok2.toString();

        Term termY = new Term(parsedTerm);

        Expression expX = e;

        if (parsedOp.equals("AND")) {
          e = new Intersection(expX, termY);
        }
        else if (parsedOp.equals("OR")) {
          e = new Union(expX, termY);
        }
        else if (parsedOp.equals("NOT")) {
          e = new Negation(expX, termY);
        }

        List rest = tokens.subList(2, size);

        return query(e, rest);
      }
    }
    // ? How did you get here ?
    else {
      return new HashSet();
    }
  }
}
