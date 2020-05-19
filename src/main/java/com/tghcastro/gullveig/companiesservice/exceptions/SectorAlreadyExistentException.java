package com.tghcastro.gullveig.companiesservice.exceptions;

import com.tghcastro.gullveig.companiesservice.models.Sector;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class SectorAlreadyExistentException extends RuntimeException {
    public SectorAlreadyExistentException(Sector foundSector) {
        super(MessageFormat.format("Sector already exists [Id: {0}][Name: {1}]",
                foundSector.getId(),
                foundSector.getName()));
    }
}
