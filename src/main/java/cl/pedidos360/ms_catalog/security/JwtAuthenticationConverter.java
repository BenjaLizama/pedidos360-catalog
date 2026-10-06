package cl.pedidos360.ms_catalog.security;

import cl.pedidos360.ms_catalog.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtProperties jwtProperties;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<?> roles = extractRoles(jwt);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(Object::toString)
                .map(role -> jwtProperties.getRolesPrefix() + role)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<?> extractRoles(Jwt jwt) {
        String rolesClaim = jwtProperties.getRolesClaim();

        if ("realm_access.roles".equals(rolesClaim)) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            if (realmAccess == null) {
                return List.of();
            }

            Object roles = realmAccess.get("roles");

            if (roles instanceof Collection<?> collection) {
                return collection;
            }

            return List.of();
        }

        Object roles = jwt.getClaim(rolesClaim);

        if (roles instanceof Collection<?> collection) {
            return collection;
        }

        return List.of();
    }
}
