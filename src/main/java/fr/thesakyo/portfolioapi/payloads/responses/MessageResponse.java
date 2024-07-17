package fr.thesakyo.portfolioapi.payloads.responses;

import org.springframework.http.ResponseEntity;

public class MessageResponse {

	/**********************************************************/
	/**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
	/*********************************************************/

	private String message; // Message pour les réponses http.

	/*****************************************************************/
	/*****************    ⬇️   CONSTRUCTEUR    ⬇️   *****************/
	/****************************************************************/

	/**
	 * Construit une {@link ResponseEntity réponse http}.
	 *
	 * @param message Message de la {@link ResponseEntity réponse http} en question.
	 */
	public MessageResponse(String message) { this.message = message; }

	/****************************************************************/
	/**************    ⬇️    GETTER & SETTER    ⬇️    **************/
	/***************************************************************/

	/**
	 * Récupère le {@link String message} de la {@link ResponseEntity réponse http}.
	 *
	 * @return Le {@link String message} de la {@link ResponseEntity réponse http}.
	 */
	public String getMessage() { return message; }

	/**
	 * Définit le {@link String message} de la {@link ResponseEntity réponse http}.
	 *
	 * @param message  Le {@link String message} de la {@link ResponseEntity réponse http} en question.
	 */
	public void setMessage(String message) { this.message = message; }
}
