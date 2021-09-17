package com.banque.entities;

import java.util.Collection;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("CE")
public class CompteEpargne extends Compte {
	private double taux;

	public CompteEpargne(String codeCompte, Date dateCreation, double montant, Client client, double taux) {
		super(codeCompte, dateCreation, montant, client);
		this.taux = taux;
	}

	public CompteEpargne() {
		super();
	}

	public double getTaux() {
		return taux;
	}

	public void setTaux(double taux) {
		this.taux = taux;
	}
	
	
}
