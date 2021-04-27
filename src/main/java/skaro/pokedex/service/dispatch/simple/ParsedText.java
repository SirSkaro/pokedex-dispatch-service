package skaro.pokedex.service.dispatch.simple;

import java.util.List;

public class ParsedText {

	private String commmand;
	private List<String> arguments;
	
	public String getCommmand() {
		return commmand;
	}
	public void setCommmand(String commmand) {
		this.commmand = commmand;
	}
	public List<String> getArguments() {
		return arguments;
	}
	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
	
}
