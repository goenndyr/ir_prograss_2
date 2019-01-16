package pa1;

import java.util.List;

public class Tester {

	public static void main(String[] args) {
    	new Tester().start();
		
     }
	
	private void start() {
		String[] queries = new String[]{"olympics opening ceremony", "denmark sweden bridge", "tokyo train disaster"};
        
        for(String query : queries) {
        	queryTester(query);
        }
	}
	
	
	
	private void queryTester(String query) {
		InvertedIndex ii = new InvertedIndex();
        DocumentGetter dg = new DocumentGetter();
        QueryProcessor qp = new QueryProcessor(ii);
        List<Accumulator> processes;
        int n;
		
		System.out.println("Query: " + query);
    	processes = qp.process(query, 5);
    	n = 1;
    	for(Accumulator process : processes) {
    		System.out.println(n++ + ".) -> " + dg.getTitle(process.getDId()) + " | " + process.getScore());
    		System.out.println(dg.getURL(process.getDId()));
    		System.out.println();
    	}
	}
	

}
