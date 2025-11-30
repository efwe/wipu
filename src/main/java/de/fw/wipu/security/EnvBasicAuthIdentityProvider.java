package de.fw.wipu.security;

import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * As we do not need real persistence, we skip the official integration for now
 * and implement a version which relies on the environment variables.
 * </p>
 * According to: <a href="https://quarkus.io/guides/security-identity-providers">identity provider</a>
 */
@ApplicationScoped
public class EnvBasicAuthIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    @Inject
    WipuBasicAuth wipuBasicAuth;


    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public int priority() {
        return IdentityProvider.super.priority();
    }

    @Override
    public Uni<SecurityIdentity> authenticate(UsernamePasswordAuthenticationRequest usernamePasswordAuthenticationRequest, AuthenticationRequestContext authenticationRequestContext) {
        if (wipuBasicAuth.user() == null || wipuBasicAuth.password() == null) {
            throw new ForbiddenException("Authentication configuration is missing");
        }

        String givenUser = usernamePasswordAuthenticationRequest.getUsername();
        if (givenUser == null) {
            throw new AuthenticationFailedException("Username is required");
        }

        String givenPass = usernamePasswordAuthenticationRequest.getPassword() != null &&
                usernamePasswordAuthenticationRequest.getPassword().getPassword() != null ?
                new String(usernamePasswordAuthenticationRequest.getPassword().getPassword()) : null;
        if (givenPass == null) {
            throw new AuthenticationFailedException("Password is required");
        }

        if (wipuBasicAuth.user().equals(givenUser) && givenPass.equals(wipuBasicAuth.password())) {
            return Uni.createFrom().item(QuarkusSecurityIdentity.builder()
                    .setPrincipal(new QuarkusPrincipal(givenUser))
                    .addCredential(usernamePasswordAuthenticationRequest.getPassword())
                    .setAnonymous(false)
                    .addRole("infra")
                    .build());
        }
        throw new AuthenticationFailedException("Invalid credentials");
    }

}
