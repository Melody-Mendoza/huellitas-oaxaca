package com.huellitasoaxaca.backend.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Configuration
@ConditionalOnProperty(
        name = "firebase.enabled",
        havingValue = "true"
)
public class FirebaseAdminConfig
{
    @Bean(destroyMethod = "delete")
    public FirebaseApp firebaseApp(
            @Value("${firebase.project-id:}") String projectId
    )
    {
        if (projectId == null || projectId.isBlank())
        {
            throw new IllegalStateException(
                    "FIREBASE_PROJECT_ID es obligatorio cuando Firebase está habilitado"
            );
        }

        try
        {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials.getApplicationDefault()
                    )
                    .setProjectId(projectId.trim())
                    .build();

            return FirebaseApp.initializeApp(options);
        }
        catch (IOException exception)
        {
            throw new IllegalStateException(
                    "No fue posible cargar las credenciales ADC de Firebase",
                    exception
            );
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp)
    {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
