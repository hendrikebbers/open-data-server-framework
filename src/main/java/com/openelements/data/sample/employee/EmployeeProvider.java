package com.openelements.data.sample.employee;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.openelements.data.db.I18nStringEntity;
import com.openelements.data.provider.DataProvider;
import io.helidon.webclient.WebClient;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class EmployeeProvider implements DataProvider<Employee> {

    @Override
    public Set<Employee> loadUpdateData(ZonedDateTime lastUpdate) {
        final WebClient client = WebClient.builder()
                .baseUri("https://raw.githubusercontent.com")
                .build();
        return client.get()
                .path("/OpenElements/open-elements-website/refs/heads/main/data/en/team.json")
                .request(String.class)
                .map(body -> {
                    final JsonElement jsonElement = JsonParser.parseString(body);
                    if (jsonElement.isJsonArray()) {
                        return jsonElement.getAsJsonArray();
                    } else {
                        throw new IllegalArgumentException("Expected a JSON array");
                    }
                }).map(jsonArray -> {
                    Set<Employee> employees = new HashSet<>();
                    for (JsonElement element : jsonArray) {
                        if (element.isJsonObject()) {
                            final String id = element.getAsJsonObject().get("id").getAsString();
                            final String firstName = element.getAsJsonObject().get("firstName").getAsString();
                            final String lastName = element.getAsJsonObject().get("lastName").getAsString();
                            final String role =
                                    element.getAsJsonObject().has("role") ? element.getAsJsonObject().get("role")
                                            .getAsString()
                                            : null;
                            String gitHubUsername = null;
                            if (element.getAsJsonObject().has("socials") && element.getAsJsonObject().get("socials")
                                    .isJsonArray()) {
                                final JsonArray socials = element.getAsJsonObject().get("socials").getAsJsonArray();
                                for (JsonElement socialElement : socials) {
                                    if (socialElement.isJsonObject() && socialElement.getAsJsonObject().has("name")) {
                                        if (Objects.equals(socialElement.getAsJsonObject().get("name"), "GitHub")) {
                                            gitHubUsername = socialElement.getAsJsonObject().get("link").getAsString()
                                                    .substring("https://github.com/".length());
                                        }
                                    }
                                }
                            }
                            Employee employee = new Employee();
                            employee.setUuid(id);
                            employee.setFirstName(firstName);
                            employee.setLastName(lastName);
                            employee.setRole(new I18nStringEntity(role));
                            employee.setGitHubUsername(gitHubUsername);
                            employees.add(employee);
                        }
                    }
                    return employees;
                })
                .await(10, TimeUnit.SECONDS);
    }
}
