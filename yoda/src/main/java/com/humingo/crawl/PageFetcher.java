package com.humingo.crawl;


import com.humingo.model.CrawlContext;
import com.humingo.util.HttpStatus;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.InMemoryDnsResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by nagarajan on 3/6/14.
 */
public class PageFetcher {
	public static final String BEGIN_TIME = "beginTime";

	private final CloseableHttpClient httpClient;

	private static final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0";

	public static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss.SSS zzz";

	private static final String TTL_SECS = "120";

	protected final Object mutex = new Object();

	private static Map<String, Long> lastFetchTime = new HashMap<String, Long>();
	private static Map<String, Long> politenessPerHost = new HashMap<String, Long>();

	private static Logger logger = LoggerFactory.getLogger(PageFetcher.class);


	static {
		java.security.Security.setProperty("networkaddress.cache.ttl", TTL_SECS);
	}

	//private final CrawlConfig config;

	private PoolingHttpClientConnectionManager cm;

	public PageFetcher() {
		
		httpClient = httpClient();
	}

	

	private CloseableHttpClient httpClient() {
		PlainConnectionSocketFactory sf = PlainConnectionSocketFactory.getSocketFactory();
		Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create().register("http", sf).build();
		Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create().register("http", new MyConnectionSocketFactory()).build();
		cm = new PoolingHttpClientConnectionManager(reg);

		RequestConfig globalConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).setCookieSpec(CookieSpecs.BEST_MATCH).build();
		cm.setMaxTotal(100);
		cm.setDefaultMaxPerRoute(100);
		ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).setMessageConstraints(MessageConstraints.DEFAULT).build();
		CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).setDefaultConnectionConfig(connectionConfig).setDefaultCookieStore(new BasicCookieStore()).setConnectionManager(cm).setDefaultRequestConfig(globalConfig).addInterceptorFirst(new HttpRequestInterceptor() {
			public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {

                //InetSocketAddress socksaddr = new InetSocketAddress("96.237.61.39", 32554);
               // context.setAttribute("socks.address", socksaddr);
				long beginTime = System.currentTimeMillis();
				context.setAttribute("beginTime", beginTime);

				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
				if (!request.containsHeader("Date")) {
					request.addHeader("BeginTime", DateUtils.formatDate(new Date(System.currentTimeMillis()), DATE_FORMAT));
				}

			}
		}).addInterceptorFirst(new HttpResponseInterceptor() {
			public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
				Object value = context.getAttribute(BEGIN_TIME);
				HttpEntity entity = response.getEntity();
				response.addHeader(BEGIN_TIME, String.valueOf(value));
				if (entity != null) {
					Header ceheader = entity.getContentEncoding();
					if (ceheader != null) {
						HeaderElement[] codecs = ceheader.getElements();
						for (int i = 0; i < codecs.length; i++) {
							if (codecs[i].getName().equalsIgnoreCase("gzip")) {
								response.setEntity(new GzipDecompressingEntity(response.getEntity()));
								return;
							}
						}
					}
				}
			}

		}).build();

		return client;
	}

	public byte[] getContent(String url, Map<String, String> headers) throws IOException {
		CloseableHttpResponse response = null;
		InputStream inputStream = null;
		byte[] content = null;
		try {
			HttpGet request = new HttpGet(url);
			if (headers != null) {
				Set<Map.Entry<String, String>> entries = headers.entrySet();
				for (Map.Entry<String, String> entry : entries) {
					request.addHeader(entry.getKey(), entry.getValue());
				}
			}
			response = httpClient.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpURLConnection.HTTP_OK) {
				content = EntityUtils.toByteArray(response.getEntity());
			}
		} finally {
			IOUtils.closeQuietly(inputStream);
			HttpClientUtils.closeQuietly(response);
		}
		return content;
	}

	public CloseableHttpResponse getResponse(String url, Map<String, String> headers) throws IOException {
		HttpGet request = new HttpGet(url);
		if (headers != null) {
			Set<Map.Entry<String, String>> entries = headers.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				request.addHeader(entry.getKey(), entry.getValue());
			}
		}
		return httpClient.execute(request);
	}

	public byte[] getContent(String url) throws IOException {
		return getContent(url, null);
	}

	public byte[] getContent(URL url) throws IOException {
		return getContent(url.toExternalForm(), null);
	}

	public void fetch(CrawlContext context) {
		String toFetchURL = context.getURLString();
		HttpGet get = null;
		CloseableHttpResponse response = null;
		long startTime=System.currentTimeMillis();
		try {
			String host = context.getURL().getHost();
			get = new HttpGet(toFetchURL);
			//synchronized (mutex) {
				long now = (new Date()).getTime();
				long lastfetchtime = getLastFetchTime(host);
				long politeness = getPolitenessDelay(host);
				if (now - lastfetchtime < politeness) 
				{
					long delay = politeness - (now - lastfetchtime);
					logger.debug("Polite delay : " + delay);
					//Thread.sleep(delay);
				}
			//}
			logger.debug("Fetching Content for the url {}", context.toString());
			response = httpClient().execute(get);
			if (response == null) {
				context.setFetchStatus(HttpStatus.UNKNOWN_ERROR.value());
				return;
			}
			long getEndTime=System.currentTimeMillis();
			long timeTaken=getEndTime-startTime;
			
			int statusCode = response.getStatusLine().getStatusCode();
			context.setFetchStatus(response.getStatusLine().getStatusCode());
			context.setResponseHeaders(response.getAllHeaders());

			if (statusCode == HttpStatus.OK.value()) {
				if (response.getEntity() != null) {
					HttpEntity entity = response.getEntity();
					long size = entity.getContentLength();
					if (size == -1) {
						Header length = response.getLastHeader("Content-Length");
						if (length == null) {
							length = response.getLastHeader("Content-length");
						}
						if (length != null) {
							size = Integer.parseInt(length.getValue());
						} else {
							size = -1;
						}
					}
					byte[] content = EntityUtils.toByteArray(entity);
					context.setContentDigest(DigestUtils.md5Hex(content));
					context.setContent(content);

					Header type = response.getEntity().getContentType();
					if (type != null) {
						context.setContentType(type.getValue());
					}

					Header encoding = entity.getContentEncoding();
					if (encoding != null) {
						context.setContentEncoding(type.getValue());
					}

					Charset charset = ContentType.getOrDefault(entity).getCharset();
					if (charset != null) {
						context.setContentCharset(type.getValue());
					}
				}
			} else {
				logger.info("Failed: " + response.getStatusLine().toString() + ", while fetching " + toFetchURL);
			}

			long fetchEndTime = System.currentTimeMillis();
			lastFetchTime.put(host, fetchEndTime);
			Header requestTime = response.getFirstHeader(BEGIN_TIME);
			if (requestTime != null) {
				Long fetchBeginTime = Long.valueOf(requestTime.getValue());
				context.setFetchBeginTime(fetchBeginTime);
				logger.debug("Fetch Duration : " + (fetchEndTime - fetchBeginTime));
			}
			context.setFetchCompletedTime(fetchEndTime);
			long politenessDelay = getPolitenessDelay(context);
			politenessPerHost.put(host, politenessDelay);
			long totalTimeTaken=fetchEndTime-startTime;
			logger.info("[page-download-frontier] content-download for the url {} in a total time span(ms) {}, with pagefetch alone taking {}", context.toString(),totalTimeTaken,timeTaken);
		} catch (IOException e) {
			logger.error("Fatal transport error: " + e.getMessage() + " while fetching " + toFetchURL + " (link found in doc #" + context.getVia() + ")", e);
			context.setFetchStatus(HttpStatus.FATAL_TRANSPORT_ERROR.value());
		} catch (IllegalStateException e) {
			// ignoring exceptions that occur because of not registering https
			// and other schemes
		} catch (Exception e) {
			if (e.getMessage() == null) {
				logger.error("Error while fetching " + context.getURL(), e);
			} else {
				logger.error(e.getMessage() + " while fetching " + context.getURL(), e);
			}
			context.setFetchStatus(HttpStatus.FATAL_TRANSPORT_ERROR.value());
		} finally {
			cm.closeExpiredConnections();
			//logger.info("Closing Http Response.  Available Http connection pool{}", cm.getTotalStats().getAvailable());
			if (get != null) {
				get.abort();
			}
			HttpClientUtils.closeQuietly(response);
		}
	}

	private long getPolitenessDelay(String host) {
		Long politenessDelay = politenessPerHost.get(host);
		if (politenessDelay == null) {
			politenessDelay = 0L;
		}
		return politenessDelay.longValue();
	}

	private long getPolitenessDelay(CrawlContext context) {
		long durationToWait = 0;
		long completeTime = context.getFetchCompletedTime();
		long durationTaken = (completeTime - context.getFetchBeginTime());
		long delayFactor = 1;
		long minPolitenessDelay=1;
		long maxPolitenessDelay=2;
		durationToWait = (long) (delayFactor * durationTaken);

		long minDelay =minPolitenessDelay;
		if (minDelay > durationToWait) {
			// wait at least the minimum
			durationToWait = minDelay;
		}

		long maxDelay = maxPolitenessDelay;
		if (durationToWait > maxDelay) {
			// wait no more than the maximum
			durationToWait = maxDelay;
		}
		return durationToWait;
	}

	private long getLastFetchTime(String host) {
		Long fetchTime = lastFetchTime.get(host);
		if (fetchTime != null) {
			return fetchTime.longValue();
		}
		return 0;
	}

	static class MyConnectionSocketFactory implements ConnectionSocketFactory {

		@Override
		public Socket createSocket(final HttpContext context) throws IOException {
			InetSocketAddress socksaddr = (InetSocketAddress) context.getAttribute("socks.address");
            if(socksaddr != null){
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
                return new Socket(proxy);
            }else{
                return new Socket();
            }

		}

		@Override
		public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context) throws IOException, ConnectTimeoutException {
			Socket sock;
			if (socket != null) {
				sock = socket;
			} else {
				sock = createSocket(context);
			}
			if (localAddress != null) {
				sock.bind(localAddress);
			}
			try {
				sock.connect(remoteAddress, connectTimeout);
			} catch (SocketTimeoutException ex) {
				throw new ConnectTimeoutException(ex, host, remoteAddress.getAddress());
			}
			return sock;
		}

	}

	public static void main(String[] args) throws MalformedURLException {
		PageFetcher pageFetcher = new PageFetcher();
		CrawlContext crawlURI1 = new CrawlContext(new URL("http://www.flipkart.com/apple-16gb-ipad-2-wi-fi/p/itmdfyjgphytdyfe"));
		CrawlContext crawlURI2 = new CrawlContext(new URL("http://www.flipkart.com/moto-x-16-gb/p/itmdwgffrgc885qt"));
		CrawlContext crawlURI3 = new CrawlContext(new URL("http://www.flipkart.com/lenovo-essential-g500s-59-383022-laptop-3rd-gen-ci3-2gb-1tb-dos-1gb-graph/p/itmdtjnp7twuz78j"));
		pageFetcher.fetch(crawlURI1);
		pageFetcher.fetch(crawlURI2);
		pageFetcher.fetch(crawlURI3);
		System.out.println(crawlURI1.getContentString());
		System.out.println(crawlURI2);
		System.out.println(crawlURI3);
	}
}
