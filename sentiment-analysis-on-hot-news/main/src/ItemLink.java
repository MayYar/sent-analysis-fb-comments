
import java.util.LinkedList;
import java.util.Set;

public class ItemLink {
	
	String label;
	String id;
	String description;
	LinkedList<String> knownAs;
	//Set<String> knownAs;
	
	public ItemLink(){
		this.label = null;
		this.id = null ;
		this.description = null;
		this.knownAs = new LinkedList<String>();
	}
	
	public ItemLink(String label,String id,String description){
		this.label = label ;
		this.id = id ;
		this.description = description;
	}
	
	public void setKnowAs(LinkedList<String> knonwAs){
		this.knownAs = knonwAs;
	}
	
	public void setKnowAs(String knownAs){
		this.knownAs.add(knownAs);
	}
	
	public LinkedList<String> getKnowAs(){
		return knownAs;
	}
	
	public String getLabel(){
		return label;
	}
	
	public String getId(){
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString(){
		return "label:" + label + " id:" + id + " description:" + description;
	}
}
