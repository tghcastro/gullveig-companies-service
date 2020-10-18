package com.tghcastro.gullveig.companies.service.domain.interfaces.services;

import com.tghcastro.gullveig.companies.service.domain.models.Sector;

import java.util.List;

public interface SectorsService {
    Sector getById(Long id);

    List<Sector> getAll();

    Sector create(Sector sector);

    void delete(Long id);

    Sector update(Long id, Sector sector);
}
