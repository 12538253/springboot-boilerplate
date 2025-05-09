package com.devtoolkit.boilerplate.common.utill;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * RestClient를 활용하여 HTTP 요청(GET, POST, PUT, DELETE)을 처리하는 유틸리티 클래스입니다.
 * 헤더/토큰/로깅/예외 일관 처리 필요할 때 사용, 현재 미사용 유틸
 */
@Slf4j
@AllArgsConstructor
@Component
public class HttpUtil {
    private final RestClient restClient;

    /**
     * GET 요청을 보내고 응답을 ResponseEntity 형태로 반환합니다.
     *
     * @param targetUrl    요청을 보낼 대상 URL
     * @param headers      요청에 포함할 헤더 (null 가능)
     * @param responseType 응답 본문을 매핑할 타입
     * @param <T>          응답 타입
     * @return 매핑된 응답 본문을 포함한 ResponseEntity 객체
     */
    public <T> ResponseEntity<T> sendGet(String targetUrl, MultiValueMap<String, String> headers,
                                         Class<T> responseType) {
        try {
            log.debug("GET Request: URL={}, Headers={}", targetUrl, headers);
            ResponseEntity<T> response = restClient.get()
                    .uri(targetUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> {
                        if (headers != null && !headers.isEmpty()) {
                            httpHeaders.addAll(headers);
                        }
                    })
                    .retrieve()
                    .toEntity(responseType);
            log.debug("GET Response: Status={}, Body={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (Exception e) {
            log.error("GET Request failed: URL={}, Error={}", targetUrl, e.getMessage(), e);
            throw new RuntimeException("GET request failed: " + targetUrl, e);
        }
    }

    /**
     * POST 요청을 보내고 응답을 ResponseEntity 형태로 반환합니다.
     *
     * @param targetUrl    요청을 보낼 대상 URL
     * @param headers      요청에 포함할 헤더 (null 가능)
     * @param body         요청 본문 객체
     * @param responseType 응답 본문을 매핑할 타입
     * @param <T>          응답 타입
     * @return 매핑된 응답 본문을 포함한 ResponseEntity 객체
     */
    public <T> ResponseEntity<T> sendPost(String targetUrl, MultiValueMap<String, String> headers,
                                          Object body, Class<T> responseType) {
        try {
            log.debug("POST Request: URL={}, Headers={}, Body={}", targetUrl, headers, body);
            ResponseEntity<T> response = restClient.post()
                    .uri(targetUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> {
                        if (headers != null && !headers.isEmpty()) {
                            httpHeaders.addAll(headers);
                        }
                    })
                    .body(body)
                    .retrieve()
                    .toEntity(responseType);
            log.debug("POST Response: Status={}, Body={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (Exception e) {
            log.error("POST Request failed: URL={}, Error={}", targetUrl, e.getMessage(), e);
            throw new RuntimeException("POST request failed: " + targetUrl, e);
        }
    }

    /**
     * 멀티파트 POST 요청을 보내고 응답을 ResponseEntity 형태로 반환합니다.
     *
     * @param targetUrl    요청을 보낼 대상 URL
     * @param headers      요청에 포함할 헤더 (null 가능)
     * @param body         요청 본문 객체
     * @param responseType 응답 본문을 매핑할 타입
     * @param <T>          응답 타입
     * @return 매핑된 응답 본문을 포함한 ResponseEntity 객체
     *
     * 사용시에는 아래처럼 하시길!!!
     * ByteArrayResource resource = new ByteArrayResource(fileBytes) {
     *     @Override
     *     public String getFilename() {
     *         return "image.gif"; // ← 반드시 있어야 함
     *     }
     *
     *     @Override
     *     public long contentLength() {
     *         return fileBytes.length;
     *     }
     * };
     *
     * MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
     * body.add("file", resource);
     * body.add("file_path", "testpromotion");
     * body.add("width", "100");
     * body.add("height", "100");
     *
     * ResponseEntity<String> response = restClient.post()
     *         .uri("http://spring2-server/file/upload/single")
     *         .contentType(MediaType.MULTIPART_FORM_DATA)
     *         .body(body)
     *         .retrieve()
     *         .toEntity(String.class);
     */
    public <T> ResponseEntity<T> sendMultipartPost(String targetUrl,
                                                   MultiValueMap<String, String> headers,
                                                   MultiValueMap<String, Object> body,
                                                   Class<T> responseType) {
        try {
            log.debug("Multipart POST Request: URL={}, Headers={}, Body={}", targetUrl, headers, body);
            ResponseEntity<T> response = restClient.post()
                    .uri(targetUrl)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .headers(httpHeaders -> {
                        if (headers != null && !headers.isEmpty()) {
                            httpHeaders.addAll(headers);
                        }
                    })
                    .body(body)
                    .retrieve()
                    .toEntity(responseType);
            log.debug("Multipart POST Response: Status={}, Body={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (Exception e) {
            log.error("Multipart POST Request failed: URL={}, Error={}", targetUrl, e.getMessage(), e);
            throw new RuntimeException("Multipart POST request failed: " + targetUrl, e);
        }
    }


    /**
     * PUT 요청을 보내고 응답을 ResponseEntity 형태로 반환합니다.
     *
     * @param targetUrl    요청을 보낼 대상 URL
     * @param headers      요청에 포함할 헤더 (null 가능)
     * @param body         요청 본문 객체
     * @param responseType 응답 본문을 매핑할 타입
     * @param <T>          응답 타입
     * @return 매핑된 응답 본문을 포함한 ResponseEntity 객체
     */
    public <T> ResponseEntity<T> sendPut(String targetUrl, MultiValueMap<String, String> headers,
                                         Object body, Class<T> responseType) {
        try {
            log.debug("PUT Request: URL={}, Headers={}, Body={}", targetUrl, headers, body);
            ResponseEntity<T> response = restClient.put()
                    .uri(targetUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> {
                        if (headers != null && !headers.isEmpty()) {
                            httpHeaders.addAll(headers);
                        }
                    })
                    .body(body)
                    .retrieve()
                    .toEntity(responseType);
            log.debug("PUT Response: Status={}, Body={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (Exception e) {
            log.error("PUT Request failed: URL={}, Error={}", targetUrl, e.getMessage(), e);
            throw new RuntimeException("PUT request failed: " + targetUrl, e);
        }
    }

    /**
     * DELETE 요청을 보내고 응답을 ResponseEntity 형태로 반환합니다.
     *
     * @param targetUrl    요청을 보낼 대상 URL
     * @param headers      요청에 포함할 헤더 (null 가능)
     * @param responseType 응답 본문을 매핑할 타입
     * @param <T>          응답 타입
     * @return 매핑된 응답 본문을 포함한 ResponseEntity 객체
     */
    public <T> ResponseEntity<T> sendDelete(String targetUrl, MultiValueMap<String, String> headers,
                                            Class<T> responseType) {
        try {
            log.debug("DELETE Request: URL={}, Headers={}", targetUrl, headers);
            ResponseEntity<T> response = restClient.delete()
                    .uri(targetUrl)
                    .headers(httpHeaders -> {
                        if (headers != null && !headers.isEmpty()) {
                            httpHeaders.addAll(headers);
                        }
                    })
                    .retrieve()
                    .toEntity(responseType);
            log.debug("DELETE Response: Status={}, Body={}", response.getStatusCode(), response.getBody());
            return response;
        } catch (Exception e) {
            log.error("DELETE Request failed: URL={}, Error={}", targetUrl, e.getMessage(), e);
            throw new RuntimeException("DELETE request failed: " + targetUrl, e);
        }
    }

    public <T> List<T> getListFromJson(String url, Class<T[]> clazz) {
        try {
            log.debug("Fetching JSON from URL={}", url);
            ResponseEntity<T[]> response = sendGet(url, null, clazz);
            T[] body = response.getBody();
            log.debug("Received JSON: Status={}, Body={}", response.getStatusCode(), Arrays.toString(body));
            return body != null ? Arrays.asList(body) : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching JSON from URL={}, Error={}", url, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
