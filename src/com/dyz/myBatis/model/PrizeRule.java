package com.dyz.myBatis.model;

public class PrizeRule {
    private Integer id;

    private String content;

    private Integer precount;
    
    private String status;
    
    
    

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getPrecount() {
        return precount;
    }

    public void setPrecount(Integer precount) {
        this.precount = precount;
    }
}