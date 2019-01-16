package pa1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class QueryProcessor {

	private InvertedIndex ii;

    public QueryProcessor(InvertedIndex ii) {
    	this.ii = ii;
    }

    private List<String> parseQuery(String query) {
        // remove HTML tags
        query = query.replaceAll("<[^<>]+>", " ");

        // remove non-alphanumeric characters
        query = query.replaceAll("[^a-zA-Z0-9]", " ");

        // convert to lower case
        query = query.toLowerCase();

        // split at sequences of white spaces
        String[] terms = query.split("[\\s]+");

        // return query terms
        return Arrays.asList(terms);
    }
    
    
    private ArrayList<Accumulator> getProcess(String query) {
    	int df, size;
    	double idf;
    	ArrayList<Accumulator> allProcesses = new ArrayList<Accumulator>();
    	List<String> terms = parseQuery(query);
    	HashMap<Long, Accumulator> accs = new HashMap<Long, Accumulator>();
    	
    	for(String term : terms) {
    		df= ii.getDF(term);
    		size = ii.getSize();
    		idf = Math.log((double)size / (double)df);
    		for(Posting p : ii.getIndexList(term)) {
    			Accumulator acc = accs.get(p.getDid());
                if (acc == null) {
                    acc = new Accumulator(p.getDid());
                    accs.put(p.getDid(), acc);
                }

                // update document's score
                acc.setScore(acc.getScore() + idf * p.getTf());
    			
    		}
    	}
    	allProcesses.addAll(accs.values());
    	return allProcesses;
    }
    
    public List<Accumulator> process(String query) {
    	ArrayList<Accumulator> allProcesses = getProcess(query);
    	allProcesses.sort((Accumulator a1, Accumulator a2) -> Double.compare(a2.getScore(), a1.getScore()));
    	return allProcesses;
    }
    
    
    
    public List<Accumulator> process(String query, int k) {
    	ArrayList<Accumulator> allProcesses = getProcess(query);
    	ArrayList<Accumulator> result = new ArrayList<Accumulator>();
    	allProcesses.sort((Accumulator a1, Accumulator a2) -> Double.compare(a2.getScore(), a1.getScore()));
    	
    	
    	for (int i=0; i<k; i++) {
    		result.add(allProcesses.remove(0));
    	}
    	
    	return result;
    }
    
    
    
    
    
    
    
    
    
    
    
    
}
