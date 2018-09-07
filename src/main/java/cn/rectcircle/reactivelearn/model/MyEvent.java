package cn.rectcircle.reactivelearn.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "event")
public class MyEvent {
	@Id
	private Long id;
	private String message;

	public MyEvent(){}
	
	public MyEvent(String message) {
		this.message = message;
	}

	public MyEvent(long id, String message) {
		this.id = id;
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}