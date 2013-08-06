package inveniet.query;

public class Query
{
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

  public Ingester i;

  public Query(String directory) {
    i = new Ingester();
    i.ingestDirectory(directory);
  }

  private interface Expression {
    public Set<String> reduce();
  }

  // "foo"
  private class Term implements Expression {
    private String term;

    public Term(String s) {
      term = s;
    }

    public Set<String> reduce() {
      return i.lookupTerm(term);
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
    public String toString() {}
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

  private class LexedOp implements LexedToken {
    private String op;

    public static isOp(String s) {
      return s == "AND" || s == "OR" || s == "NOT";
    }

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
      return new Set();
    }

    ArrayList<LexedToken> lexedTokens = new ArrayList<LexedToken>();
    for (String parsedToken:parsedTokens) {
      if (LexedOp.isOp(parsedToken)) {
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
      return new Set();
    }
    // Single term or invalid query
    else if (e == null && size == 1) {
      LexedToken token = tokens.get(0);
      if (token.getClass() == LexedOp) {
        return new Set();
      }
      else {
        return new Expression(token.toString()).reduce();
      }
    }
    // Invalid query
    else if (e == null && size == 2) {
      return new Set();
    }
    // Start of Boolean query, or invalid query
    else if (e == null && size > 2) {
      LexedToken tok1 = tokens.get(0);
      LexedToken tok2 = tokens.get(1);
      LexedToken tok3 = tokens.get(2);

      // Invalid query
      if (tok1.getClass() == LexedOp ||
          tok2.getClass() == LexedTerm ||
          tok3.getClass() == LexedOp) {
        return new Set();
      }
      else {
        String parsedTermX = tok1.toString();
        String op = tok2.toString();
        String parsedTermY = tok3.toString();

        Term termX = new Term(parsedTermX);
        Term termY = new Term(parsedTermY);

        if (op == "AND") {
          e = new Intersection(termX, termY);
        }
        else if (op == "OR") {
          e = new Union(termX, termY);
        }
        else {
          e = new Negation(termX, termY);
        }

        List rest = tokens.subList(3, size - 3);

        return query(e, rest);
      }
    }
    // Completion of Boolean query
    else if (e != null && size == 0) {
      return e.reduce();
    }
    // Invalid continuation of Boolean query
    else if (e != null && size == 1) {
      return new Set();
    }
    // Continuation of Boolean query, or invalid query
    else if (e != null && size >= 2) {
      LexedToken tok1 = tokens.get(0);
      LexedToken tok2 = tokens.get(1);

      // Invalid query
      if (tok1.getClass() == LexedTerm || tok2.getClass() == LexedOp) {
        return new Set();
      }
      else {
        String parsedOp = tok1.toString();
        String parsedTerm = tok2.toString();

        Term termY = new Term(parsedTerm);

        Expression expX = e;

        if (parsedOp == "AND") {
          e = new Intersection(expX, termY);
        }
        else if (parsedOp == "OR") {
          e = new Union(expX, termY);
        }
        else if (parsedOp == "NOT") {
          e = new Negation(expX, termY);
        }

        List rest = tokens.subList(2, size - 2);

        return query(e, rest);
    }
  }
}
