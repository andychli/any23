package org.deri.any23.writer;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Triple handler decorator useful for logging purposes.
 */
public class LoggingTripleHandler implements TripleHandler {

    /**
     * Decorated.
     */
    private final TripleHandler underlyingHandler;

    private final Map<String, Integer> contextTripleMap = new HashMap<String, Integer>();
    private long startTime     = 0;
    private long contentLength = 0;
    private final PrintWriter destination;

    public LoggingTripleHandler(TripleHandler tripleHandler, PrintWriter destination) {
        if(tripleHandler == null) {
            throw new NullPointerException("tripleHandler cannot be null.");
        }
        if(destination == null) {
            throw new NullPointerException("destination cannot be null.");
        }
        underlyingHandler = tripleHandler;
        this.destination = destination;
    }

    public void startDocument(URI documentURI) {
        underlyingHandler.startDocument(documentURI);
        startTime = System.currentTimeMillis();
    }

    public void close() {
        underlyingHandler.close();
        destination.flush();
        destination.close();
    }

    public void closeContext(ExtractionContext context) {
        underlyingHandler.closeContext(context);
    }

    public void openContext(ExtractionContext context) {
        underlyingHandler.openContext(context);
    }

    public void receiveTriple(Resource s, URI p, Value o, ExtractionContext context) {
        underlyingHandler.receiveTriple(s, p, o, context);
        Integer i = contextTripleMap.get(context.getExtractorName());
        if (i == null) i = 0;
        contextTripleMap.put(context.getExtractorName(), (i + 1));
    }

    public void receiveNamespace(String prefix, String uri, ExtractionContext context) {
        underlyingHandler.receiveNamespace(prefix, uri, context);
    }

    public void endDocument(URI documentURI) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        boolean success = true;
        StringBuffer sb = new StringBuffer("[");
        for (Entry<String, Integer> ent : contextTripleMap.entrySet()) {
            sb.append(" ").append(ent.getKey()).append(":").append(ent.getValue());
            if (ent.getValue() > 0) {
                success = true;
            }
        }
        sb.append("]");
        destination.println(
                documentURI + "\t" + contentLength + "\t" + elapsedTime + "\t" + success + "\t" + sb.toString()
        );
        contextTripleMap.clear();
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }
}