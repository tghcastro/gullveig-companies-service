package tests.unit.domain.models;

import com.tghcastro.gullveig.companies.service.domain.exceptions.DomainException;
import com.tghcastro.gullveig.companies.service.domain.models.Company;
import com.tghcastro.gullveig.companies.service.domain.results.DomainResult;
import org.junit.Test;
import tests.unit.domain.UnitTestDataHelper;

import static org.junit.jupiter.api.Assertions.*;

public class CompaniesTests {
    @Test
    public void validate_ShouldNotThrowError_WithValidData() {
        Company company = UnitTestDataHelper.companyWithValidDataWithoutStocks();
        assertDoesNotThrow(company::validate);
    }

    @Test
    public void validate_ShouldThrowError_WithEmptyName() {
        Company company = UnitTestDataHelper.companyWithValidDataWithoutStocks();
        company.setName("");
        DomainResult<Company> result = company.validate();
        assertTrue(result.failed());
        assertEquals("Company's name should not be empty", result.error());
    }

    @Test
    public void validate_ShouldThrowError_WithNullName() {
        Company company = UnitTestDataHelper.companyWithValidDataWithoutStocks();
        company.setName(null);
        DomainResult<Company> result = company.validate();
        assertTrue(result.failed());
        assertEquals("Company's name should not be empty", result.error());
    }

    @Test
    public void validate_ShouldThrowError_WithNullSector() {
        Company company = UnitTestDataHelper.companyWithValidDataWithoutStocks();
        company.setSector(null);
        DomainResult<Company> result = company.validate();
        assertTrue(result.failed());
        assertEquals("Company's sector should not be null", result.error());
    }

    @Test
    public void addStock_ShouldThrowError_WhenTickerIsNull() {
        Company company = UnitTestDataHelper.companyWithValidDataWithoutStocks();
        String ticker = null;
        assertThrows(DomainException.class, () -> company.addStock(ticker));
    }

    @Test
    public void addStock_ShouldThrowError_WhenTickerIsEmpty() {
        Company company = UnitTestDataHelper.companyWithValidDataWithoutStocks();
        String ticker = "";
        assertThrows(DomainException.class, () -> company.addStock(ticker));
    }

    @Test
    public void addStock_ShouldThrowError_WhenTickerHasOnlySpaces() {
        Company company = UnitTestDataHelper.companyWithValidDataWithoutStocks();
        String ticker = " ";
        assertThrows(DomainException.class, () -> company.addStock(ticker));
    }

    @Test
    public void addStock_ShouldConvertToUpperCase_WhenTickerHasLowerCaseChars() {
        Company company = UnitTestDataHelper.companyWithValidDataWithoutStocks();
        String ticker = "Abbv";
        company.addStock(ticker);
        assertEquals("ABBV", company.getStocks().get(0).getTicker());
    }
}
