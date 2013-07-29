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
	public Iterable<String> query(String query)
	{
		// TODO: Implement query logic
		return null; // TODO: Make this return something proper 
	}
}
