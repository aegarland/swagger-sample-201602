package com.aegarland.restexample.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "USER", schema = "PUBLIC", catalog = "PUBLIC")
@ApiModel
@XmlRootElement
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class User {

	    @Column(name = "FIRST_NAME", unique = false, nullable = true, length = 64)
	    private String firstName;
	    
	    @Id
	    @Column(name = "ID")	    
	    private long id;
	 
	    @Column(name = "LAST_NAME", unique = false, nullable = false, length = 64)
	    private String lastName;
	 
	    @Transient	    
		private byte[] image;
	    
	    public User () {}	    
	 
	    @ApiModelProperty(position = 1, required = true, value = "Unique Id", example="123")
	    public long getId() {
	        return id;
	    }
	 
	    public void setId(long id) {
	        this.id = id;
	    }
	 
	    @ApiModelProperty(position = 2, required = false, example="Pooh")
	    public String getFirstName() {
	        return firstName;
	    }
	 
	    public void setFirstName(String firstName) {
	        this.firstName = firstName;
	    }

	    @ApiModelProperty(position = 3, required = true, example="Bear")
	    public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

	    @JsonIgnore
	    public byte[] getImage() {
	        return image;
	    }
	 
	    public void setImage(byte[] avatar) {
	        this.image = avatar;
	    }	    
	    
	    public static User fromString (String string){
	    	System.out.println("fromString("+string+")");
	    	(new Exception("bar")).printStackTrace();
	    	User u = new User();
	    	u.setId(31L);
	    	u.firstName = "X";
	    	u.lastName = "Man";
	    	return u;
	    }

	}