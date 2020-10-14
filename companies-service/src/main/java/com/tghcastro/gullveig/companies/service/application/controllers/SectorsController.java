package com.tghcastro.gullveig.companies.service.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tghcastro.gullveig.companies.service.domain.exceptions.DomainException;
import com.tghcastro.gullveig.companies.service.domain.interfaces.services.SectorsService;
import com.tghcastro.gullveig.companies.service.domain.models.Sector;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/sectors")
public class SectorsController {
    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private SectorsService sectorsService;

    @GetMapping
    public List<Sector> list() {
        return sectorsService.getAll();
    }

    @GetMapping
    @RequestMapping("{id}")
    public Sector get(@PathVariable Long id) {
        return sectorsService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sector create(@Valid @RequestBody Sector sector) {
        return sectorsService.create(sector);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws DomainException {
        sectorsService.delete(id);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public Sector update(@PathVariable Long id, @RequestBody Sector sector) {
        return sectorsService.update(id, sector);

    }

    // TODO: Improve error handling
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String responseBody = mapper.writeValueAsString(errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody);
    }
}
