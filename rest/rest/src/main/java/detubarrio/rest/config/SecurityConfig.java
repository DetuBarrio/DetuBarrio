package detubarrio.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; 
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import detubarrio.rest.repository.UsuarioRepository;
import detubarrio.rest.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UsuarioRepository usuarioRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .cors(Customizer.withDefaults())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos y documentación pública
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/*.html",
                    "/images/**",
                    "/css/**",
                    "/js/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/api-docs/**"
                ).permitAll()
                
                // Endpoints de autenticación y salud
                .requestMatchers("/api/health", "/api/categorias").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/disponibilidades/**").permitAll()

                // 🔐 REGLA CORREGIDA (SUBIDA): Protege el POST de opiniones ANTES de dar permisos generales
                .requestMatchers(HttpMethod.POST, "/api/comercios/{comercioId}/resenas").authenticated()

                // Consulta de comercios (ahora el comodín general no pisará la seguridad del POST)
                .requestMatchers("/api/comercios/**").permitAll()
                
                // 🔒 PROTECCIÓN DE RESERVAS
                .requestMatchers(HttpMethod.POST, "/api/reservas").authenticated()       
                .requestMatchers(HttpMethod.PUT, "/api/reservas/**").authenticated()     
                .requestMatchers(HttpMethod.GET, "/api/reservas/usuario/**").authenticated() 
                
                // Otras rutas protegidas de tu app
                .requestMatchers("/api/comentarios").authenticated()
                .requestMatchers("/api/dashboard/**", "/api/auth/me").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Cualquier otra petición se procesa según este flujo
                .anyRequest().permitAll()
            )
            // Filtro JWT que lee los tokens antes de procesar la petición
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByEmailIgnoreCase(username)
            .map(usuario -> (UserDetails) User.withUsername(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}