package com.resteflex.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SupabaseClient {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;

    private final RestTemplate restTemplate;

    public HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseAnonKey);
        headers.set("Authorization", "Bearer " + supabaseAnonKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Prefer", "return=representation");
        return headers;
    }

    public <T> List<T> getList(String table, String query, Class<T> clazz) {
        String url = supabaseUrl + "/rest/v1/" + table + (query != null ? "?" + query : "");
        ResponseEntity<List<T>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers()),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public <T> T getSingle(String table, String query, Class<T> clazz) {
        String url = supabaseUrl + "/rest/v1/" + table + "?" + query;
        HttpHeaders h = headers();
        h.set("Accept", "application/vnd.pgrst.object+json");
        ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(h),
                clazz
        );
        return response.getBody();
    }

    public <T> T insert(String table, Object body, Class<T> clazz) {
        String url = supabaseUrl + "/rest/v1/" + table;
        ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(body, headers()),
                clazz
        );
        return response.getBody();
    }

    public <T> T update(String table, String query, Object body, Class<T> clazz) {
        String url = supabaseUrl + "/rest/v1/" + table + "?" + query;
        ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers()),
                clazz
        );
        return response.getBody();
    }

    public void delete(String table, String query) {
        String url = supabaseUrl + "/rest/v1/" + table + "?" + query;
        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers()), Void.class);
    }
}
