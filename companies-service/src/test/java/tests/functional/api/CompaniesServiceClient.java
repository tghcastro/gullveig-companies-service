package tests.functional.api;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import tests.functional.api.contracts.*;
import tests.functional.helpers.BaseClient;

//TODO: Add configuration for URI
public class CompaniesServiceClient extends BaseClient {

    public PostSectorResponse PostSector(PostSectorRequest sectorToCreate) {
        HttpPost request = new HttpPost("http://localhost:8080/api/v1/sectors");
        request.addHeader("Content-Type", "application/json");
        try {
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(sectorToCreate)));
            return this.execute(request, PostSectorResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PutSectorResponse PutSector(PutSectorRequest sectorToUpdate) {
        HttpPut request = new HttpPut("http://localhost:8080/api/v1/sectors/" + sectorToUpdate.getId());
        request.addHeader("Content-Type", "application/json");
        try {
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(sectorToUpdate)));
            return this.execute(request, PutSectorResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void DeleteSector(String id) {
        HttpDelete request = new HttpDelete("http://localhost:8080/api/v1/sectors/" + id);
        this.execute(request);
    }

    public GetSectorResponse GetSector(String id) {
        HttpGet request = new HttpGet("http://localhost:8080/api/v1/sectors/" + id);
        request.addHeader("Accept", "application/json");
        try {
            return this.execute(request, GetSectorResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
