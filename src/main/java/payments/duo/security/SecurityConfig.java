package payments.duo.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import payments.duo.security.filters.JwtAuthorizationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String SIGNIN_ENDPOINT = "/api/auth/signin";
    public static final String SIGNUP_ENDPOINT = "/api/auth/signup";

    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // Auth end points
            SIGNIN_ENDPOINT,
            SIGNUP_ENDPOINT
    };

    private final CorsConfiguration corsConfiguration;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(CorsConfiguration corsConfiguration, JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.corsConfiguration = corsConfiguration;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//    }
//
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManager();
//    }

//    @Bean
//    public JwtAuthorizationFilter jwtAuthorizationFilter() {
//        return new JwtAuthorizationFilter();
//    }

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(12);
//    }
//
//    @Bean
//    public AuthenticationProvider daoAuthProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
//        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
//        return daoAuthenticationProvider;
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .cors().configurationSource(request -> corsConfiguration);
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
        http
                .authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/api/v1/**").hasAnyAuthority("CLIENT", "ADMIN")
                .anyRequest().authenticated().and();
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
