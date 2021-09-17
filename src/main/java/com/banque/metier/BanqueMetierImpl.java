package com.banque.metier;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banque.dao.CompteRepository;
import com.banque.dao.OperationRepository;
import com.banque.entities.Compte;
import com.banque.entities.CompteCourant;
import com.banque.entities.Operation;
import com.banque.entities.Retrait;
import com.banque.entities.Versement;

@Service
@Transactional
public class BanqueMetierImpl implements IBanqueService {
	@Autowired
	private CompteRepository compteRepository;
	@Autowired
	private OperationRepository operationRepository;
	@Override
	public Compte consulterCompte(String codeCpte) {
		Compte cp = compteRepository.getOne(codeCpte);
		if (cp == null) throw new RuntimeException("Compte introuvable");
		return cp;
	}

	@Override
	public void verser(String codeCpte, double montant) {
		Compte cp = consulterCompte(codeCpte);
		Versement v = new Versement(new Date(), montant, cp);
		operationRepository.save(v);
		cp.setSolde(cp.getSolde() + montant);
		compteRepository.save(cp);
	}

	@Override
	public void retirer(String codeCpte, double montant) {
		Compte cp = consulterCompte(codeCpte);
		double facilitiesCaisse = 0;
		
		if (cp instanceof CompteCourant) {
			facilitiesCaisse = ((CompteCourant) cp).getDecouvert();
			if (cp.getSolde() + facilitiesCaisse < montant) {
				throw new RuntimeException("Solde insuffisant");
			}
			Retrait r = new Retrait(new Date(), montant, cp);
			operationRepository.save(r);
			cp.setSolde(cp.getSolde() - montant);
			compteRepository.save(cp);
		}
		
	}

	@Override
	public void virement(String codeCpte1, String codeCpte2, double montant) {
		if (codeCpte1.equals(codeCpte2)) 
				throw new RuntimeException("Operation impossible");
		retirer(codeCpte1, montant);
		verser(codeCpte2, montant);
	}

	@Override
	public Page<Operation> listOperation(String codeCpte, int page, int size) {	
		return operationRepository.listOperation(codeCpte, PageRequest.of(page, size));
	}
	
}
