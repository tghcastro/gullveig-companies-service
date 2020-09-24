package tests.functional.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import tests.functional.CompaniesServiceAdministrator;
import tests.functional.ScenarioDataContext;
import tests.functional.api.contracts.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

public class SectorsSteps {

    private final ScenarioDataContext scenarioDataContext;
    private final CompaniesServiceAdministrator companiesServiceAdministrator;

    public SectorsSteps(ScenarioDataContext scenarioDataContext, CompaniesServiceAdministrator companiesServiceAdministrator) {
        this.scenarioDataContext = scenarioDataContext;
        this.companiesServiceAdministrator = companiesServiceAdministrator;
    }

    @Given("an existent sector")
    public void anExistentSector() {
        PostSectorResponse sector = companiesServiceAdministrator.createValidSector();
        scenarioDataContext.put("createdSector", sector);
    }

    @And("another existent sector")
    public void anotherExistentSector() {
        PostSectorResponse sector = companiesServiceAdministrator.createValidSector();
        scenarioDataContext.put("anotherCreatedSector", sector);
    }

    @Given("^an unregistered sector$")
    public void anUnregisteredSector() {
        PostSectorRequest postSectorRequest = companiesServiceAdministrator.GetSectorInstanceWithValidData();
        scenarioDataContext.put("sector", postSectorRequest);
    }

    @When("^a client tries to register this sector$")
    public void aClientTriesToRegisterThisSector() {
        PostSectorRequest sector = scenarioDataContext.get("sector");
        PostSectorResponse response = companiesServiceAdministrator.createSector(sector);
        scenarioDataContext.put("createdSector", response);
    }

    @Then("^the sector is correctly created$")
    public void theSectorIsCorrectlyCreated() {
        PostSectorRequest sector = scenarioDataContext.get("sector");
        PostSectorResponse createdSector = scenarioDataContext.get("createdSector");
        assertThat(createdSector.getName(), is(sector.getName()));
        assertThat(createdSector.getId(), is(notNullValue()));
    }

    @And("^it is enabled to use$")
    public void itIsEnabledToUse() {
        PostSectorResponse createdSector = scenarioDataContext.get("createdSector");
        assertTrue(createdSector.isEnabled());
    }

    @Given("a registered sector")
    public void aRegisteredSector() {
        PostSectorRequest postSectorRequest = companiesServiceAdministrator.GetSectorInstanceWithValidData();
        scenarioDataContext.put("sectorToCreate", postSectorRequest);
        PostSectorResponse createdSector = companiesServiceAdministrator.createSector(postSectorRequest);
        assertThat(createdSector.getId(), is(notNullValue()));
        scenarioDataContext.put("createdSector", createdSector);
    }

    @When("a client tries to update this sector data")
    public void aClientTriesToUpdateThisSectorData() {
        PostSectorResponse createdSector = scenarioDataContext.get("createdSector");
        PutSectorRequest sectorToUpdate = PutSectorRequest.cloneFrom(createdSector);
        sectorToUpdate.setName("NEW " + System.currentTimeMillis());
        PutSectorResponse updatedSector = companiesServiceAdministrator.updateSector(sectorToUpdate);
        scenarioDataContext.put("sectorToUpdate", sectorToUpdate);
        scenarioDataContext.put("updatedSector", updatedSector);
    }

    @Then("the sector is correctly updated")
    public void theSectorIsCorrectlyUpdated() {
        PutSectorRequest sectorToUpdate = scenarioDataContext.get("sectorToUpdate");
        PutSectorResponse updatedSector = scenarioDataContext.get("updatedSector");
        assertThat(updatedSector.getId(), is(sectorToUpdate.getId()));
        assertThat(updatedSector.getName(), is(sectorToUpdate.getName()));
    }

    @When("a client tries to delete this sector")
    public void aClientTriesToDeleteThisSector() {
        PostSectorResponse createdSector = scenarioDataContext.get("createdSector");
        companiesServiceAdministrator.deleteSector(createdSector.getId());
        scenarioDataContext.put("deletedSectorId", createdSector.getId());
    }

    @Then("^this sector (.*?) deleted")
    public void thisSectorIsDeleted(String what) {
        String sectorId = scenarioDataContext.get("deletedSectorId");
        GetSectorResponse sector = companiesServiceAdministrator.getSectorById(sectorId);
        assertThat(sector.getId(), is(sectorId));
        if (what.equals("was not")) {
            assertTrue(sector.isEnabled());
        } else {
            assertFalse(sector.isEnabled());
        }
    }

    @When("a client tries to register this sector again")
    public void aClientTriesToRegisterThisSectorAgain() {
        PostSectorRequest sectorToCreate = scenarioDataContext.get("sectorToCreate");
        PostSectorResponse response = companiesServiceAdministrator.createSector(sectorToCreate);
        scenarioDataContext.put("createdSector", response);
    }

    @Then("this sector was not created")
    public void thisSectorWasNotCreated() {
        assertNull(scenarioDataContext.get("createdSector"));
    }
}
