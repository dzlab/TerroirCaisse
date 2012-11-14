package com.terroir.caisse.data;

public class Producer {

	public String raison_social; // raison social
	public String sous_type; // sous type
	public String code_postal;
	public String ville;	
	public String address;
	public String telephone;
	public String mail;
	public String addresse_web;
	public double latitude;
	public double longitude;
	public double distance;
	
	public Producer() {}
	
	public Producer(String name, String type, String address, String telephone) {
		this.raison_social = name;
		this.sous_type = type;
		this.address = address;
		this.telephone = telephone;
	}
	
	/** Check whether this instance is null (i.e. contains no information) */
	public boolean isNull() {
		return (raison_social==null);
	}
	
	public String toString() {
		return "{raison_social: '"+raison_social+"', sous_type:'"+sous_type+"', address:'"+address+"', ville: '"+ville+"', code_postal: '"+code_postal+"', telephone:'"+telephone+"', mail:'"+mail+"', latitude:'"+latitude+"', longitude:'"+longitude+"'}";
	}
}
