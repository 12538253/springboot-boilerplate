package com.devtoolkit.boilerplate.common.config;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.DefaultBackoffStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {

    // 커넥션 풀 관련 설정값
    private static final int MAX_TOTAL_CONNECTIONS = 200; // 최대 커넥션 수 (전체)
    private static final int MAX_CONNECTIONS_PER_ROUTE = 20; // 호스트별 최대 커넥션 수
    private static final int MAX_IDLE_TIME = 60; // 유휴 상태 커넥션 유지 시간 (초)

    // 타임아웃 설정값 ( 연결 타임아웃 < 쓰기/읽기 타임아웃 ≤ 응답 타임아웃 )
    private static final long CONNECT_TIMEOUT = 5L; // 서버 연결 시도 시간 (초) - 연결이 성립되지 않으면 발생
    private static final long CONNECTION_REQUEST_TIMEOUT = 10L; // 커넥션 풀에서 커넥션을 가져오는 대기 시간 (초) - 커넥션 풀이 꽉 찬 경우 발생
    private static final long RESPONSE_TIMEOUT = 60L; // 서버 응답 대기 시간 (초) - 연결 후 응답을 기다리는 시간

    // 재시도 관련 설정값
    private static final int MAX_RETRIES = 3; // 요청 실패 시 최대 재시도 횟수
    private static final long INITIAL_RETRY_INTERVAL = 5; // 첫 번째 재시도 대기 시간 (초) - 이후 점진적 증가

    /**
     * Spring에서 사용할 RestClient 빈을 생성합니다.
     * 내부적으로 Apache HttpClient를 사용하도록 구성되어 있습니다.
     *
     * @param httpClient 커스터마이징된 Apache HttpClient
     * @return RestClient 인스턴스
     */
    @Bean
    public RestClient restClient(HttpClient httpClient) {
        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    /**
     * 커넥션 풀, 재시도 전략, 타임아웃 등을 포함한 Apache HttpClient를 생성합니다.
     *
     * @return 설정된 HttpClient 인스턴스
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClients.custom()
                .setConnectionManager(buildConnectionManager())
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setRetryStrategy(buildRetryStrategy())
                .setConnectionBackoffStrategy(new DefaultBackoffStrategy())
                .setDefaultRequestConfig(requestConfig())
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.ofSeconds(MAX_IDLE_TIME))
                .build();
    }

    /**
     * 커넥션 풀을 구성하고 최대 커넥션 수와 라우트별 커넥션 수를 설정합니다.
     *
     * @return PoolingHttpClientConnectionManager 인스턴스
     */
    private PoolingHttpClientConnectionManager buildConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        return connectionManager;
    }

    /**
     * 요청 타임아웃 설정을 구성합니다.
     * - 응답 대기 시간
     * - 커넥션 풀에서 커넥션 획득 시간
     * - 연결 타임아웃 < 쓰기/읽기 타임아웃 ≤ 응답 타임아웃
     * @return RequestConfig 인스턴스
     */
    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) // 연결 타임아웃 3초
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT, TimeUnit.SECONDS) // 쓰기/읽기 타임아웃 5초
                .setResponseTimeout(RESPONSE_TIMEOUT, TimeUnit.SECONDS) // 응답 타임아웃 20초
                .build();
    }

    /**
     * 선형 증가 재시도 전략
     *
     * @return DefaultHttpRequestRetryStrategy 인스턴스
     */
    private DefaultHttpRequestRetryStrategy buildRetryStrategy() {
        return new DefaultHttpRequestRetryStrategy(MAX_RETRIES, TimeValue.ofSeconds(INITIAL_RETRY_INTERVAL)) {
            @Override
            public boolean retryRequest(HttpRequest request, IOException exception, int execCount, HttpContext context) {
                if (execCount > MAX_RETRIES) {
                    log.warn("재시도 횟수 초과 - 요청 실패: {}", request.getRequestUri());
                    return false;
                }
                log.info("재시도 {}회차: {} - 이유: {}", execCount, request.getRequestUri(), exception.getMessage());
                return true;
            }

            @Override
            public TimeValue getRetryInterval(HttpResponse response, int execCount, HttpContext context) {
                long delay = INITIAL_RETRY_INTERVAL * execCount; // 선형 증가 (5, 10, 15초)
                log.info("재시도 대기 시간: {}초", delay);
                return TimeValue.ofSeconds(delay);
            }
        };
    }
}