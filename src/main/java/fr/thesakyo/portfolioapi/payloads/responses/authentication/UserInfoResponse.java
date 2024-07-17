package fr.thesakyo.portfolioapi.payloads.responses.authentication;

import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.models.entities.User;
import fr.thesakyo.portfolioapi.models.DTO.ProjectDTO;
import fr.thesakyo.portfolioapi.models.DTO.RoleDTO;

import java.util.Set;

public class UserInfoResponse {

	/**********************************************************/
	/**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
	/*********************************************************/

	private Long id; // L'Identifiant de l'utilisateur connecté en réponse pour les cookies.

	private String name; // Le nom de l'utilisateur connecté en réponse pour les cookies.

	private String email; // L'adresse e-mail de l'utilisateur connecté en réponse pour les cookies.

	private Set<ProjectDTO> projects; // Liste des projet(s) de l'utilisateur connecté en réponse pour les cookies.

	private Set<RoleDTO> roles; // La liste des rôles de l'utilisateur connecté en réponse pour les cookies.


	/*****************************************************************/
	/*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
	/****************************************************************/

	/**
	 * Construit un utilisateur connecté en cookie.
	 */
	public UserInfoResponse() {}

	/**
	 * Construit un utilisateur connecté en cookie.
	 *
	 * @param id L'{@link Long Identifiant} de l'{@link User utilisateur} connecté en cookie.
	 * @param name Le {@link String nom} de l'{@link User utilisateur} connecté en cookie.
	 * @param email L'{@link String adresse e-mail} de l'{@link User utilisateur} connecté en cookie.
	 * @param roles La {@link Set liste} des {@link RoleDTO rôle}s associé à l'{@link User utilisateur} connecté en cookie.
	 */
	public UserInfoResponse(Long id, String uuid, String name, String email, Set<ProjectDTO> projects, Set<RoleDTO> roles) {

		this.id = id;
		this.name = name;
		this.email = email;
		this.projects = projects;
		this.roles = roles;
	}

	/****************************************************************/
	/**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
	/***************************************************************/

	/**
	 * Récupère l'{@link Long identifiant} de l'{@link User utilisateur} connecté en cookie.
	 *
	 * @return L'{@link Long Identifiant} de l'{@link User utilisateur} connecté en cookie.
	 */
	public Long getId() { return id; }

	/**
	 * Récupère le {@link String nom} de l'{@link User utilisateur} connecté en cookie.
	 *
	 * @return Le {@link String nom} de l'{@link User utilisateur} connecté en cookie.
	 */
	public String getName() { return name; }

	/**
	 * Récupère l'{@link String adresse e-mail} de l'{@link User utilisateur} connecté en cookie.
	 *
	 * @return L'{@link String adresse e-mail} de l'{@link User utilisateur} connecté en cookie.
	 */
	public String getEmail() { return email; }

	/**
	 * Récupère la {@link Set liste} des {@link String nom}s des {@link RoleDTO rôle}s associé à l'{@link User utilisateur} connecté en cookie.
	 *
	 * @return La {@link Set liste} des {@link String nom}s des {@link RoleDTO rôle}s associé à l'{@link User utilisateur} connecté en cookie.
	 */
	public Set<RoleDTO> getRoles() { return roles; }

					/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
					/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
					/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

	/**
	 * Définit l'{@link Long identifiant} de l'{@link User utilisateur} connecté en cookie.
	 *
	 * @param id L'{@link Long Identifiant} de l'{@link User utilisateur} connecté en cookie.
	 */
	public void setId(Long id) { this.id = id; }

	/**
	 * Définit le {@link String nom} de l'{@link User utilisateur} connecté en cookie.
	 *
	 * @param name Le {@link String nom} de l'{@link User utilisateur} connecté en cookie.
	 */
	public void setName(String name) { this.name = name; }

	/**
	 * Définit l'{@link String adresse e-mail} de l'{@link User utilisateur} connecté en cookie.
	 *
	 * @param email L'{@link String adresse e-mail} de l'{@link User utilisateur} connecté en cookie.
	 */
	public void setEmail(String email) { this.email = email; }

	/**
	 * Définit la {@link Set liste} des {@link String nom}s des {@link RoleDTO rôle}s associé à l'{@link User utilisateur} connecté en cookie.
	 *
	 * @param roles La {@link Set liste} des {@link String nom}s des {@link RoleDTO rôle}s associé à l'{@link User utilisateur} connecté en cookie.
	 */
	public void setRoles(Set<RoleDTO> roles) { this.roles = roles; }

	/******************************************************************************************************************/
	/******************************************************************************************************************/
	/******************************************************************************************************************/

	/**
	 * Convertit l'{@link Object objet} de la {@link UserInfoResponse réponse d'authentification} en {@link String chaîne de caractère}.
	 *
	 * @return Une {@link String chaîne de caractère} de notre {@link UserInfoResponse réponse d'authentification}.
	 */
	@Override
	public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
