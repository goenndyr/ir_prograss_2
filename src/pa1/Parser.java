package pa1;

import com.nytlabs.corpus.NYTCorpusDocument;
import com.nytlabs.corpus.NYTCorpusDocumentParser;

import java.io.File;
import java.util.Arrays;

/**
 * @author Klaus Berberich (klaus.berberich@htwsaar.de)
 */
public class Parser {

    // Parser for documents from The New York Times Annotated Corpus
    private NYTCorpusDocumentParser nytParser;

    public Parser() {
        this.nytParser = new NYTCorpusDocumentParser();
    }

    /**
     * Parses the given .xml file and returns a document to be indexed.
     *
     * @param file File to be parsed
     * @return Document ready to be indexed.
     */
    public Document parse(File file) {
        Document doc = new Document();

        // parse the given file
        NYTCorpusDocument nytDoc = nytParser.parseNYTCorpusDocumentFromFile(file, false);

        // copy attributes
        doc.setId(nytDoc.getGuid());
        doc.setURL(nytDoc.getUrl().toString());
        doc.setTitle(nytDoc.getHeadline());

        // get document content
        String body = (nytDoc.getBody() == null ? "" : nytDoc.getBody());

        // remove HTML tags
        body = body.replaceAll("<[^<>]+>", " ");

        // remove all characters that are neither a letter, a number or a full stop
        body = body.replaceAll("[^a-zA-Z0-9\\.]", " ");

        // remove all full stops before a white space that are not preceded by another full stop in the same word
        char[] bodyChars = body.toCharArray();
        int stopSeen = 0;
        for (int i = 0; i < bodyChars.length; i++) {
            if(bodyChars[i] == ' ') {
                if (i > 0 && bodyChars[i-1] == '.') {
                    if (stopSeen < 2) {
                        bodyChars[i-1] = ' ';
                    }
                }
                stopSeen = 0;
            } else if (bodyChars[i] == '.') {
                stopSeen++;
            }
        }

        // convert to lower case
        body = (new String(bodyChars)).toLowerCase();

        // split at sequences of white spaces
        String[] content = body.split("[\\s]+");

        // copy content
        doc.setContent(content);

        return doc;
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        Document doc = parser.parse(new File("/Users/kberberi/Downloads/nyt/data/2000/01/05/1166233.xml"));
        System.out.println(Arrays.toString(doc.getContent()));
    }

}
