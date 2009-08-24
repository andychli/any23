package org.deri.any23.writer;

import java.util.Collection;
import java.util.HashSet;

import org.deri.any23.extractor.ExtractionContext;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * A {@link TripleHandler} that collects various information
 * about the extraction process, such as the extractors used
 * and the total number of triples.
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class ReportingTripleHandler implements TripleHandler {
	private final TripleHandler wrapped;
	private final Collection<String> extractorNames = new HashSet<String>();
	private int totalTriples = 0;
	private int totalDocuments = 0;
	
	public ReportingTripleHandler(TripleHandler wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public void startDocument(URI documentURI) {
		totalDocuments++;
		wrapped.startDocument(documentURI);
	}

	@Override
	public void openContext(ExtractionContext context) {
		wrapped.openContext(context);
	}

	@Override
	public void receiveNamespace(String prefix, String uri,
			ExtractionContext context) {
		wrapped.receiveNamespace(prefix, uri, context);
	}

	@Override
	public void receiveTriple(Resource s, URI p, Value o,
			ExtractionContext context) {
		extractorNames.add(context.getExtractorName());
		totalTriples++;
		wrapped.receiveTriple(s, p, o, context);
	}

	@Override
	public void setContentLength(long contentLength) {
		wrapped.setContentLength(contentLength);
	}

	@Override
	public void closeContext(ExtractionContext context) {
		wrapped.closeContext(context);
	}
	
	@Override
	public void endDocument(URI documentURI) {
		wrapped.endDocument(documentURI);
	}
	
	@Override
	public void close() {
		wrapped.close();
	}
	
	public Collection<String> getExtractorNames() {
		return extractorNames;
	}
	
	public int getTotalTriples() {
		return totalTriples;
	}
	
	public int getTotalDocuments() {
		return totalDocuments;
	}
}
