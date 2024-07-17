package fr.thesakyo.portfolioapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;

public class SerializableResponseEntity<T> extends ResponseEntity<T> implements Serializable {

    /**********************************************************/
    /**************   ⬇️    PROPRIÉTÉS    ⬇️   **************/
    /*********************************************************/

    @JsonIgnore
    private static final long serialVersionUIDLONG = 1L;

    @JsonIgnore
    private Object object;

    /***********************************************************************/
    /**************   ⬇️    MÉTHODES DE SÉRIALISATION   ⬇️   **************/
    /***********************************************************************/

    public SerializableResponseEntity(HttpStatus status) { super(status); }

    public SerializableResponseEntity(T body, HttpStatus status) {

        super(body, status);
        this.object = body;
    }

    public SerializableResponseEntity(MultiValueMap<String, String> headers, HttpStatus status) { super(headers, status); }

    public SerializableResponseEntity(T body, MultiValueMap<String, String> headers, HttpStatus status) {

        super(body, headers, status);
        this.object = body;
    }

    public SerializableResponseEntity(ResponseEntity<T> responseEntity) {

        super(responseEntity.getBody(), responseEntity.getHeaders(), responseEntity.getStatusCode());
        this.object = responseEntity.getBody();
    }
}
