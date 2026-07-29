package com.huellitasoaxaca.backend.security;

public interface FirebaseTokenVerifier
{
    FirebaseIdentity verify(String idToken);

    record FirebaseIdentity(
            String uid,
            String correo,
            String nombre
    )
    {}
}
