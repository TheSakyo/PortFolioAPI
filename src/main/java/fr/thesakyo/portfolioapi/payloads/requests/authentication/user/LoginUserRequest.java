package fr.thesakyo.portfolioapi.payloads.requests.authentication.user;

import fr.thesakyo.portfolioapi.helpers.MapperHelper;
import fr.thesakyo.portfolioapi.payloads.requests.authentication.BaseRequest;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class LoginUserRequest implements BaseRequest, Serializable {

	/**********************************************************/
	/**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
	/*********************************************************/

	@NotBlank
	private String email; // Adresse e-mail depuis le formulaire de connexion.

	@NotBlank
	private String password; // Mot de passe (crypté) depuis le formulaire de connexion.

	/****************************************************************/
	/**************   ⬇️    GETTERS & SETTERS    ⬇️   **************/
	/***************************************************************/

	/**
	 * Récupère l'{@link String adresse e-mail} depuis le formulaire de connexion.
	 *
	 * @return L'{@link String adresse e-mail} depuis le formulaire de connexion.
	 */
	@Override
	public String getEmail() { return email; }

	/**
	 * Récupère le {@link String mot de passe} (crypté) depuis le formulaire de connexion.
	 *
	 * @return Le {@link String mot de passe} (crypté) depuis le formulaire de connexion.
	 */
	@Override
	public String getPassword() { return password; }

					/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
					/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
					/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

	/**
	 * Définit l'{@link String adresse e-mail} depuis le formulaire de connexion.
	 *
	 * @param email L'{@link String adresse e-mail} depuis le formulaire de connexion.
	 */
	@Override
	public void setEmail(String email) { this.email = email; }

	/**
	 * Définit le {@link String mot de passe} (crypté) depuis le formulaire de connexion.
	 *
	 * @param password Le {@link String mot de passe} (crypté) depuis le formulaire de connexion.
	 */
	@Override
	public void setPassword(String password) { this.password = password; }

	/******************************************************************************************************************/
	/******************************************************************************************************************/
	/******************************************************************************************************************/

	/**
	 * Convertit l'{@link Object objet} de la {@link LoginUserRequest requête} en {@link String chaîne de caractère}.
	 *
	 * @return Une {@link String chaîne de caractère} de notre {@link LoginUserRequest objet de requête}.
	 */
	@Override
	public String toString() { return MapperHelper.readJsonFromObjectAsString(this); }
}
