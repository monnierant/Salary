package me.monnierant.salary;

import java.util.ArrayList;

public class Translator
{
	// la liste des textes séparer en sections
	private ArrayList<ArrayList<String>> m_texts;
	
	//le nom des sections
	private ArrayList<String> m_sections;
	
	public Translator(String _lng)
	{
		if(_lng==null)
		{
			_lng="EN";
		}
		// le numéro de la langue
		int numLng=0;
		ArrayList<String> lng=new ArrayList<String>();
		lng.add("FR");
		lng.add("EN");
		
		for(int t=0;t<lng.size();t++)
		{
			if(_lng.equals(lng.get(t)))
			{
				numLng=t;
			}
		}
				
		m_sections=new ArrayList<String>();
		m_sections.add("help");
		m_sections.add("error");
		m_sections.add("reload");
		m_sections.add("pay");
		
		m_texts=new ArrayList<ArrayList<String>>();
		
		switch(numLng)
		{
			// FR
			case 0:
				m_texts.add(new ArrayList<String>());
				m_texts.get(0).add("§8-------------- §6Salary §8- §aAide §8--------------");
				m_texts.get(0).add("§6/salary reload");
				m_texts.get(0).add("Recharge la configuration.");
				m_texts.get(0).add("§6/salary list");
				m_texts.get(0).add("Liste les groupes existant.");
				m_texts.get(0).add("§6/salary pay");
				m_texts.get(0).add("Verser les salaires (si ils n'ont pas déjà été versés).");
				m_texts.get(0).add("§6/salary add/remove groupe joueur");
				m_texts.get(0).add("Ajoute/Retire un joueur du groupe. (nécessite §6settings.Salary.withPermissions=false§f).");
				m_texts.add(new ArrayList<String>());
				m_texts.get(1).add("§4Vous n'avez pas les droit pour effectuer cette opération.");
				m_texts.get(1).add("§4Fichier de config incomplet.");
				m_texts.get(1).add("§4Erreur avec vault.");
				m_texts.get(1).add("§4Impossible d'ajouter ou de retirer un joueur du groupe car le plugin utilise les permissions.");
				m_texts.get(1).add("§4Changer §6settings.Salary.withPermissions§4 pour §6false§4 pour utiliser les groupes.");
				m_texts.get(1).add("§4Ce groupe n'existe pas.");
				m_texts.get(1).add("§4Ce joueur est déjà dans le groupe.");
				m_texts.get(1).add("§4Ce joueur est déjà retiré du groupe.");
				m_texts.add(new ArrayList<String>());
				m_texts.get(2).add("§8-------------- §6Salary §8- §aConfiguration §8--------------");
				m_texts.get(2).add("§8Configuration recharger avec succès.");
				m_texts.add(new ArrayList<String>());
				m_texts.get(3).add("§6Payement effectué.");
				m_texts.get(3).add("§6Joueur bien ajouté dans le groupe.");
				m_texts.get(3).add("§6Joueur bien retiré dans le groupe.");
				m_texts.get(3).add("§8-------------- §6Salary §8- §aList des groupes §8--------------");
			
				break;
			// EN
			case 1:
			default:
				m_texts.add(new ArrayList<String>());
				m_texts.get(0).add("§8-------------- §6Salary §8- §aHelp §8--------------");
				m_texts.get(0).add("§6/salary reload");
				m_texts.get(0).add("Reload configuration.");
				m_texts.get(0).add("§6/salary list");
				m_texts.get(0).add("List existing groups.");
				m_texts.get(0).add("§6/salary pay");
				m_texts.get(0).add("Pay salaries (if they have not already been paid).");
				m_texts.get(0).add("§6/salary add/remove group player");
				m_texts.get(0).add("Add/Remove a player from group. (need §6settings.Salary.withPermissions=false§f).");
				m_texts.add(new ArrayList<String>());
				m_texts.get(1).add("§4You do not have permission to do that.");
				m_texts.get(1).add("§4Uncomplet config file.");
				m_texts.get(1).add("§4Error with vault.");
				m_texts.get(1).add("§4Unable to add or remove a player from group beacause plugin use permissions.");
				m_texts.get(1).add("§4Change §6settings.Salary.withPermissions§4 to §6false§4 to use groups.");
				m_texts.get(1).add("§4This group don't exist.");
				m_texts.get(1).add("§4This joueur is already in the group.");
				m_texts.get(1).add("§4This joueur is already remove from the group.");
				m_texts.add(new ArrayList<String>());
				m_texts.get(2).add("§8-------------- §6Salary §8- §aConfiguration §8--------------");
				m_texts.get(2).add("§8Successfully reloaded the configuration.");
				m_texts.add(new ArrayList<String>());
				m_texts.get(3).add("§6Pay done.");
				m_texts.get(3).add("§6Player well added.");
				m_texts.get(3).add("§6Player well removed.");
				m_texts.get(3).add("§8-------------- §6Salary §8- §aList of groups §8--------------");
				
				break;
		}
		
		
		
	}
	
	public String get(String _sections,int _num)
	{
		
		for(int t=0;t<m_sections.size();t++)
		{
			if(m_sections.get(t).equals(_sections))
			{
				if(m_texts.get(t).size()>_num && _num>=0)
				{
					return m_texts.get(t).get(_num);
				}
				else
				{
					return "";
				}
			}
		}
		
		return "";
	}
	
	public ArrayList<String> get(String _sections)
	{
		
		for(int t=0;t<m_sections.size();t++)
		{
			if(m_sections.get(t).equals(_sections))
			{
				return m_texts.get(t);
			}
		}
		
		return new ArrayList<String>();
	}
	
	
}
