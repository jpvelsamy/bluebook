package com.humingo.crawl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.humingo.model.CrawlContext;
import com.humingo.util.HtmlContentHandler;
import com.humingo.util.URLCanonicalizer;

/**
 * Created by nagarajan on 3/6/14.
 */
public class URLExtractorUtil 
{

	
	private static final Logger logger = LoggerFactory.getLogger(URLExtractorUtil.class);
	
	
	private final HtmlParser htmlParser;
	private final ParseContext parseContext;
	
	/*private final List<DecideRule> decideRules;
	private static RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
	private static RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, new PageFetcher());*/

	/*public URLExtractor(List<DecideRule> decideRules) 
	{
		this.decideRules = decideRules;
		parseContext = new ParseContext();
		htmlParser = new HtmlParser();
	}*/
	
	public URLExtractorUtil()
	{
		parseContext = new ParseContext();
		htmlParser = new HtmlParser();
	}

	public List<CrawlContext> extractOutgoingUrls(CrawlContext crawlContext) {

		if (ArrayUtils.isEmpty(crawlContext.getContent())) 
		{
			return null;
		}
		List<CrawlContext> outgoingUrls = findOutGoingUrls(crawlContext);		
		//List<CrawlContext> rejectedUrls= concludeOutgoingCandidateUrls(outgoingUrls);		
		return outgoingUrls;
	}

	/*private List<CrawlContext> concludeOutgoingCandidateUrls(List<CrawlContext> outgoingUrls) 
	{
		List<CrawlContext> rejectedUrls = new ArrayList<CrawlContext>();
		if (CollectionUtils.isNotEmpty(outgoingUrls)) 
		{
			Iterator<CrawlContext> iterator = outgoingUrls.iterator();
			while (iterator.hasNext()) 
			{
				CrawlContext outgoingUrl = (CrawlContext) iterator.next();
				DecideResult decideResult = innerDecide(outgoingUrl);
				if (decideResult == DecideResult.REJECT) 
				{
					if(this.info.shouldPrint())
						logger.info("Removed By Rule =" + outgoingUrl.getURLString());
					iterator.remove();
					rejectedUrls.add(outgoingUrl);
				} else if (!robotstxtServer.allows(outgoingUrl)) 
				{
					logger.debug("Removed By robot rules=" + outgoingUrl.getURLString());
					iterator.remove();
					rejectedUrls.add(outgoingUrl);
				}
			}
			
		}
		//logger.info(String.valueOf(outgoingUrls.size()));
		return rejectedUrls;
		
	}*/

	private List<CrawlContext> findOutGoingUrls(CrawlContext crawlContext) 
	{
		HtmlContentHandler contentHandler = loadContentHandler(crawlContext);
		String contextURL = initContenxtUrl(crawlContext, contentHandler);
		List<CrawlContext> outgoingUrls = new ArrayList<CrawlContext>();
		
		for (String href : contentHandler.getOutgoingUrls()) 
		{
			try 
			{
				String url = URLCanonicalizer.getCanonicalURL(href, contextURL);
				if (url != null) 
				{
					CrawlContext context = new CrawlContext(new URL(url));
					context.setCrawlDepth(crawlContext.getCrawlDepth() + 1);
					outgoingUrls.add(context);
				}
			} catch (Exception e) 
			{
				logger.error("Unable to parse Url " + href);
			}
		}
		//logger.info("Outgoing url candidates count="+String.valueOf(outgoingUrls.size()));
		return outgoingUrls;
	}

	private String initContenxtUrl(CrawlContext crawlContext, HtmlContentHandler contentHandler) 
	{
		String contextURL = crawlContext.getURLString();
		String baseURL = contentHandler.getBaseUrl();
		if (baseURL != null) 
		{
			contextURL = baseURL;
		}
		return contextURL;
	}

	private HtmlContentHandler loadContentHandler(CrawlContext crawlContext) 
	{
		Metadata metadata = new Metadata();
		HtmlContentHandler contentHandler = new HtmlContentHandler();
		InputStream inputStream = null;
		try 
		{
			inputStream = new ByteArrayInputStream(crawlContext.getContent());
			htmlParser.parse(inputStream, contentHandler, metadata, parseContext);
		} catch (Exception e) 
		{
			logger.error(e.getMessage() + ", while parsing: " + crawlContext.getURLString());
		} 
		finally 
		{
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage() + ", while parsing: " + crawlContext.getURLString());
			}
		}
		return contentHandler;
	}

	/*public DecideResult innerDecide(CrawlContext crawlContext) 
	{
		DecideResult result = DecideResult.NONE;
		if (CollectionUtils.isNotEmpty(decideRules)) 
		{
			int max = decideRules.size();
			for (int i = 0; i < max; i++) 
			{
				DecideRule rule = decideRules.get(i);
				if (rule.onlyDecision(crawlContext) != result)
				{
					DecideResult r = rule.decisionFor(crawlContext);
					if (r != DecideResult.NONE) 
					{
						result = r;
					}
				}
			}
		}
		return result;
	}*/


}
