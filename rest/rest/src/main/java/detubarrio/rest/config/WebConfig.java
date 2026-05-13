package detubarrio.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // --- NUEVA SECCIÓN PARA IMÁGENES ---
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Esto permite que al acceder a http://localhost:8080/uploads/nombre.jpg
        // Spring busque el archivo en tu carpeta de Windows
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    // --- TU CONFIGURACIÓN DE CORS EXISTENTE ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}