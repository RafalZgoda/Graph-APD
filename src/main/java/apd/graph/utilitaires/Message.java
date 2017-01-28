package apd.graph.utilitaires;

public class Message {

	private String typeMessage;
	private int id;
	
	
	public Message(String typeMessage, int id) {
		this.typeMessage = typeMessage;
		this.id = id;
	}
	public String getTypeMessage() {
		return typeMessage;
	}
	public void setTypeMessage(String typeMessage) {
		this.typeMessage = typeMessage;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
