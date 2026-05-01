package com.ws101.gallamora.torres.EcommerceApi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS (Cross-Origin Resource Sharing) configuration.
 * <p>
 * Task 7: Configures the backend to accept requests from the frontend dev server
 * running on {@code http://localhost:5500} (VS Code Live Server default port).
 * Without this, the browser would block all cross-origin Fetch API calls with a
 * CORS error in the console.
 * </p>
 * <p>
 * By defining CORS here globally (instead of using {@code @CrossOrigin} on each
 * controller), every endpoint in the application automatically inherits these rules.
 * </p>
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registers CORS mappings for all API endpoints.
     *
     * <ul>
     *   <li>{@code addMapping("/**")} – applies the rule to every route in the app.</li>
     *   <li>{@code allowedOrigins} – only the listed origin may send cross-origin
     *       requests. The wildcard {@code *} cannot be used together with
     *       {@code allowCredentials(true)}, so the exact origin is listed.</li>
     *   <li>{@code allowedMethods} – permits the HTTP verbs the frontend will use.</li>
     *   <li>{@code allowedHeaders} – allows the custom headers the frontend sends,
     *       including {@code Authorization} for Bearer-token auth and
     *       {@code Content-Type} for JSON bodies.</li>
     *   <li>{@code allowCredentials(true)} – lets the browser include cookies or
     *       HTTP auth headers in cross-origin requests.</li>
     * </ul>
     *
     * @param registry the Spring MVC CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5500",   // VS Code Live Server
                        "http://127.0.0.1:5500"    // alternate Live Server address
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept")
                .allowCredentials(true);
    }
}
