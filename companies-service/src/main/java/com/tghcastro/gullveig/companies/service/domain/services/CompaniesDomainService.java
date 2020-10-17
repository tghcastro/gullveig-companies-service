package com.tghcastro.gullveig.companies.service.domain.services;

import com.tghcastro.gullveig.companies.service.domain.exceptions.CompanyNotFoundException;
import com.tghcastro.gullveig.companies.service.domain.interfaces.metrics.MetricsService;
import com.tghcastro.gullveig.companies.service.domain.interfaces.repositories.CompaniesRepository;
import com.tghcastro.gullveig.companies.service.domain.interfaces.repositories.StocksRepository;
import com.tghcastro.gullveig.companies.service.domain.interfaces.services.CompaniesService;
import com.tghcastro.gullveig.companies.service.domain.models.Company;
import com.tghcastro.gullveig.companies.service.domain.models.Stock;
import com.tghcastro.gullveig.companies.service.domain.results.DomainResult;
import com.tghcastro.gullveig.companies.service.domain.results.ErrorMessagesResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompaniesDomainService implements CompaniesService<Company> {

    private final CompaniesRepository companiesRepository;
    private final StocksRepository stocksRepository;
    private final MetricsService metricsService;

    public CompaniesDomainService(CompaniesRepository companiesRepository, StocksRepository stocksRepository, MetricsService metricsService) {
        this.stocksRepository = stocksRepository;
        this.metricsService = metricsService;
        this.companiesRepository = companiesRepository;
    }

    @Override
    public List<Company> getAll() {
        return this.companiesRepository.findAllByOrderByNameAsc();
    }

    @Override
    public Optional<Company> getById(Long id) {
        Optional<Company> foundCompany = this.companiesRepository.findById(id);
        if (!foundCompany.isPresent()) {
            throw new CompanyNotFoundException(id);
        }
        return foundCompany;
    }

    @Override
    public Optional<Company> getBySectorId(Long sectorId) {
        return this.companiesRepository.findBySectorId(sectorId);
    }

    @Override
    public DomainResult<Company> create(Company companyToCreate) {
        return companyToCreate.validate()
                .onSuccess(() -> assureNotExistsWithSameName(companyToCreate))
                .onSuccess(() -> internalCreate(companyToCreate));
    }

    @Override
    public DomainResult<Company> update(Long id, Company companyToUpdate) {
        return companyToUpdate.validate()
                .onSuccess(() -> assureNotExistsWithSameName(companyToUpdate))
                .onSuccess(() -> internalUpdate(id, companyToUpdate));
    }

    @Override
    public DomainResult<Company> delete(Long id) {
        return assureExists(id)
                .onSuccess(lastResult -> internalDelete(lastResult.value()));
    }

    @Override
    public Company addStock(Long companyId, String ticker) {
        Company company = this.getById(companyId).get();
        Stock stock = new Stock(ticker);
        company.addStock(this.stocksRepository.saveAndFlush(stock));
        return internalUpdate(companyId, company).onSuccessReturnValue();
    }

    private DomainResult<Company> internalUpdate(Long id, Company dataToUpdate) {
        Company company = this.getById(id).get();
        BeanUtils.copyProperties(dataToUpdate, company, "id");

        Company updatedCompany = this.companiesRepository.saveAndFlush(dataToUpdate);
        if (updatedCompany != null) {
            this.metricsService.registerCompanyUpdated();
        }

        return new DomainResult<>(updatedCompany);
    }

    private DomainResult<Company> internalCreate(Company companyToCreate) {
        Company createdCompany = this.companiesRepository.saveAndFlush(companyToCreate);

        if (createdCompany != null) {
            this.metricsService.registerCompanyCreated();
        }

        return new DomainResult<>(companyToCreate);
    }

    private DomainResult<Company> internalDelete(Company companyToDelete) {
        companyToDelete.setEnabled(false);
        Company deletedCompany = this.companiesRepository.saveAndFlush(companyToDelete);
        if (deletedCompany != null) {
            this.metricsService.registerCompanyUpdated();
        }
        return new DomainResult<>(deletedCompany);
    }

    private DomainResult<Company> assureExists(Long id) {
        Company company = this.getById(id).get();

        if (company == null) {
            String error = ErrorMessagesResult.CompanyDoesNotExists(id);
            return new DomainResult<>(null, false, error);
        }

        return new DomainResult<>(company);
    }

    private DomainResult<Company> assureNotExistsWithSameName(Company someCompany) {
        Company alreadyExistentCompany = this.companiesRepository.findByName(someCompany.getName());

        if (alreadyExistentCompany != null && !alreadyExistentCompany.getId().equals(someCompany.getId())) {
            String error = ErrorMessagesResult.DuplicatedCompany(someCompany, alreadyExistentCompany);
            return new DomainResult<>(someCompany, false, error);
        }

        return new DomainResult<>(someCompany);
    }
}
