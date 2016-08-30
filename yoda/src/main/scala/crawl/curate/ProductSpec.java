package crawl.curate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductSpec implements Serializable 
{

	private static final long serialVersionUID = -8448973261671987233L;
	private String specType;
	private String url;
	private String name;
	private Map<String, Collection<AtomicSpec>> atomicSpecList =new HashMap<String, Collection<AtomicSpec>>();
	
	public String getSpecType() {
		return specType;
	}
	public void setSpecType(String specType) {
		this.specType = specType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void add(String templateTokenKey, String placeholder, String description, float rank)
	{
		AtomicSpec spec = new AtomicSpec(placeholder, description, rank);		
		if(atomicSpecList.containsKey(templateTokenKey)){
			Collection<AtomicSpec> atomicList = atomicSpecList.get(templateTokenKey);
			atomicList.add(spec);
		}
		else	{
			Collection<AtomicSpec> atomicList = new ArrayList<AtomicSpec>();
			atomicList.add(spec);
			atomicSpecList.put(templateTokenKey, atomicList);
		}
	}
	@Override
	public String toString() {
		return "ProductSpec [specType=" + specType + ", url=" + url + ", name="
				+ name + ", atomicSpecList=" + atomicSpecList + "]";
	}
	
	public String toJson(){
		 ObjectMapper mapper = new ObjectMapper();
	     try {
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(atomicSpecList);			
			return json;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return null;
	}
	
}
