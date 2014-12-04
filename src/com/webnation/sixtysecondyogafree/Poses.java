package com.webnation.sixtysecondyogafree;

public class Poses {
	private long id;
	private String graphic;
	private String text;
	private String title;
	private int difficulty;
	private int skip;
	private String yoga_name;
	private int repeat;
	
	public Poses() { 
		this.id = 0;
		this.graphic = "";
		this.text = "";
		this.title = "";
		this.difficulty = 0;
		this.skip = 0;
		this.yoga_name = "";
		this.repeat = 0;
		
	}
	
	public Poses(long id,
			String graphic, String text, 
    		String title, int difficulty,
    		int skip) { 
		this.id = id;
    	this.graphic = graphic;
    	this.text = text;
    	this.title = title;
    	this.difficulty = difficulty;
    	this.skip = skip;
		
	}
	
	public Poses(long id,
			String graphic, String text, 
    		String title, int difficulty,
    		int skip, String yoga_name) { 
		this.id = id;
    	this.graphic = graphic;
    	this.text = text;
    	this.title = title;
    	this.difficulty = difficulty;
    	this.skip = skip;
    	this.yoga_name = yoga_name;
		
	}
	
	public Poses(long id,
			String graphic, String text, 
    		String title, int difficulty,
    		int skip, String yoga_name, int repeat) { 
		this.id = id;
    	this.graphic = graphic;
    	this.text = text;
    	this.title = title;
    	this.difficulty = difficulty;
    	this.skip = skip;
    	this.yoga_name = yoga_name;
    	this.repeat = repeat;
		
	}
	
    public Poses(String graphic, String text, 
    		String title, int difficulty,
    		int skip, String yoga_name) { 
    	this.graphic = graphic;
    	this.text = text;
    	this.title = title;
    	this.difficulty = difficulty;
    	this.skip = skip;
    	this.yoga_name = yoga_name;
		
	}
    
    public Poses(String graphic, String text, 
    		String title, int difficulty,
    		int skip, String yoga_name, int repeat) { 
    	this.graphic = graphic;
    	this.text = text;
    	this.title = title;
    	this.difficulty = difficulty;
    	this.skip = skip;
    	this.yoga_name = yoga_name;
    	this.repeat = repeat;
		
	}
    
    public Poses(String graphic, String text, 
    		String title, int difficulty,
    		int skip) { 
    	this.graphic = graphic;
    	this.text = text;
    	this.title = title;
    	this.difficulty = difficulty;
    	this.skip = skip;
    	
    }
    
    public Poses(String graphic, String text, 
    		String title) { 
    	this.graphic = graphic;
    	this.text = text;
    	this.title = title;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGraphic() {
		return graphic;
	}

	public void setGraphic(String graphic) {
		this.graphic = graphic;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	
	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}
	
	public String getYogaName() {
		return yoga_name;
	}

	public void setYogaName(String yoga_name) {
		this.yoga_name = yoga_name;
	}
	
	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

}