package com.humingo.model;

/**
 * Created by nagarajan on 3/6/14.
 */

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collection;

import org.apache.http.Header;

import com.humingo.enumerated.CrawlStatus;
import com.humingo.util.HttpStatus;

public class CrawlContext implements Serializable {

	private static final long serialVersionUID = -1099561941062965048L;

	public static final int UNCALCULATED = -1;

	private long contentSize = UNCALCULATED;

	private String jobId;

	private URL url;

	private URL via;

	private int fetchStatus = 0; // default to unattempted

	private int fetchAttempts = 0; // the number of fetch attempts that have

	private long fetchBeginTime = -1L;

	private long fetchCompletedTime = -1L;

	private String contentType = "unknown";

	private String contentEncoding = null;

	private String contentCharset = null;

	private byte[] content = null;

	private String contentDigest = null;

	private String contentDigestScheme = null;

	private CrawlStatus crawlStatus;

	private Header[] responseHeaders;

	

	private int crawlDepth = 0;

	public CrawlContext() {

	}

	public CrawlContext(URL url) {
		this.url = url;
	}

	public static String fetchStatusCodesToString(int code) {
		return HttpStatus.valueOf(code).toString();
	}

	public CrawlStatus getCrawlStatus() {
		return crawlStatus;
	}

	public void setCrawlStatus(CrawlStatus crawlStatus) {
		this.crawlStatus = crawlStatus;
	}

	/**
	 * Return the overall/fetch status of this CrawlURI for its current trip through the processing
	 * loop.
	 * 
	 * @return a value from FetchStatusCodes
	 */
	public int getFetchStatus() {
		return fetchStatus;
	}

	/**
	 * Set the overall/fetch status of this CrawlURI for its current trip through the processing
	 * loop.
	 * 
	 * @param newStatus a value from FetchStatusCodes
	 */
	public void setFetchStatus(int newStatus) {
		fetchStatus = newStatus;
	}

	public int getFetchAttempts() {
		return fetchAttempts;
	}

	/**
	 * Increment the count of attempts (trips through the processing loop) at getting the document
	 * referenced by this URI.
	 */
	public void incrementFetchAttempts() {
		fetchAttempts++;
	}

	/**
	 * Reset fetchAttempts counter.
	 */
	public void resetFetchAttempts() {
		this.fetchAttempts = 0;
	}

	/**
	 * Get the content type of this URI.
	 * 
	 * @return Fetched URIs content type. May be null.
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Set a fetched uri's content type.
	 * 
	 * @param contentType Content type.
	 */
	public void setContentType(String contentType) {
		if (contentType == null) {
			contentType = "unknown";
		}
		this.contentType = contentType;
	}

	/**
	 * Get the size in bytes of this URI's recorded content, inclusive of things like protocol
	 * headers. It is the responsibility of the classes which fetch the URI to set this value
	 * accordingly -- it is not calculated/verified within CrawlURI.
	 * <p/>
	 * This value is consulted in reporting/logging/writing-decisions.
	 * 
	 * @return contentSize
	 */
	public long getContentSize() {
		return contentSize;
	}

	/**
	 * Sets the 'content size' for the URI, which is considered inclusive of all of all recorded
	 * material (such as protocol headers) or even material 'virtually' considered (as in material
	 * from a previous fetch confirmed unchanged with a server). (In contrast, content-length
	 * matches the HTTP definition, that of the enclosed content-body.)
	 * <p/>
	 * Should be set by a fetcher or other processor as soon as the final size of recorded content
	 * is known. Setting to an artificial/incorrect value may affect other reporting/processing.
	 */
	public void setContentSize(long l) {
		contentSize = l;
	}


	public void setContentDigest(final String scheme, final String digestValue) {
		this.contentDigest = digestValue;
		this.setContentDigestScheme(scheme);
	}

	

	public long getFetchBeginTime() {
		return this.fetchBeginTime;
	}

	public void setFetchBeginTime(long time) {
		this.fetchBeginTime = time;
	}

	public long getFetchCompletedTime() {
		return this.fetchCompletedTime;
	}

	public void setFetchCompletedTime(long time) {
		this.fetchCompletedTime = time;
	}

	public long getFetchDuration() {
		long completedTime = getFetchCompletedTime();
		long beganTime = getFetchBeginTime();
		return completedTime - beganTime;
	}

	/**
	 * @return URL
	 */
	public URL getURL() {
		return this.url;
	}

	/**
	 * @return String of URLString
	 */
	public String getURLString() {
		return this.url.toExternalForm();
	}

	/**
	 * @return URI via which this one was discovered
	 */
	public URL getVia() {
		return this.via;
	}

	public void setVia(URL via) {
		this.via = via;
	}

	/**
	 * Reset state that that should not persist when a URI is rescheduled for a specific future
	 * time.public void setResponseHeaders(org.apache.http.Header[] responseHeaders) {
	 * this.responseHeaders = responseHeaders; }
	 */
	public void resetForRescheduling() {

	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}


	public String getContentString() {
		String content = null;
		if (getContent() != null) {
			if (getContentCharset() == null) {
				content = new String(getContent());
			} else {
				try {
					content = new String(getContent(), getContentCharset());
				} catch (UnsupportedEncodingException e) {
					content = new String(getContent());
				}
			}
		}
		return content;
	}


	public String getContentDigest() {
		return contentDigest;
	}

	public void setContentDigest(String contentDigest) {
		this.contentDigest = contentDigest;
	}

	public String getContentDigestScheme() {
		return contentDigestScheme;
	}

	public void setContentDigestScheme(String contentDigestScheme) {
		this.contentDigestScheme = contentDigestScheme;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}


	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Header[] responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

	public String getContentCharset() {
		return contentCharset;
	}

	public void setContentCharset(String contentCharset) {
		this.contentCharset = contentCharset;
	}

	public int getCrawlDepth() {
		return crawlDepth;
	}

	public void setCrawlDepth(int crawlDepth) {
		this.crawlDepth = crawlDepth;
	}

	@Override
	public String toString() {
		return getURLString();
	}

	
}
