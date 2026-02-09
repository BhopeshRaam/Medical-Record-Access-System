package com.mras.config;

import java.util.Map;

import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Applies MongoDB JSON Schema validators (collection-level).
 *
 * NOTE: On MongoDB Atlas, your user must have privileges for collMod/createCollection.
 * To avoid startup failures in restricted environments, this runner is disabled by default.
 *
 * Enable with: mras.mongo.applyValidators=true
 */
@Component
public class MongoCollectionValidatorRunner implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final Environment env;

    public MongoCollectionValidatorRunner(MongoTemplate mongoTemplate, Environment env) {
        this.mongoTemplate = mongoTemplate;
        this.env = env;
    }

    @Override
    public void run(String... args) {
        boolean enabled = Boolean.parseBoolean(env.getProperty("mras.mongo.applyValidators", "false"));
        if (!enabled) return;

        apply("patients", patientSchema());
        apply("records", recordSchema());
        apply("users", userSchema());
        apply("access_grants", accessGrantSchema());
        apply("audit_logs", auditLogSchema());
    }

    private void apply(String collection, Document jsonSchema) {
        try {
            boolean exists = mongoTemplate.collectionExists(collection);
            if (!exists) {
                Document cmd = new Document("create", collection)
                        .append("validator", new Document("$jsonSchema", jsonSchema))
                        .append("validationLevel", "moderate")
                        .append("validationAction", "error");
                mongoTemplate.getDb().runCommand(cmd);
                return;
            }

            Document cmd = new Document("collMod", collection)
                    .append("validator", new Document("$jsonSchema", jsonSchema))
                    .append("validationLevel", "moderate")
                    .append("validationAction", "error");
            mongoTemplate.getDb().runCommand(cmd);
        } catch (Exception e) {
            // Don't crash the app if the DB user can't apply validators
            System.err.println("[MRAS] Could not apply validator for collection '" + collection + "': " + e.getMessage());
        }
    }

    private Document patientSchema() {
        return new Document(Map.of(
                "bsonType", "object",
                "required", java.util.List.of("mrn", "name", "dob", "gender", "phone", "status"),
                "properties", Map.of(
                        "mrn", new Document(Map.of("bsonType", "string", "minLength", 6, "maxLength", 30)),
                        "name", new Document(Map.of("bsonType", "string", "minLength", 2, "maxLength", 120)),
                        "dob", new Document(Map.of("bsonType", "date")),
                        "gender", new Document(Map.of("bsonType", "string")),
                        "phone", new Document(Map.of("bsonType", "string")),
                        "status", new Document(Map.of("bsonType", "string"))
                )
        ));
    }

    private Document recordSchema() {
        return new Document(Map.of(
                "bsonType", "object",
                "required", java.util.List.of("patientId", "encounterDate", "authorId", "status"),
                "properties", Map.of(
                        "patientId", new Document(Map.of("bsonType", "string")),
                        "authorId", new Document(Map.of("bsonType", "string")),
                        "encounterDate", new Document(Map.of("bsonType", "date")),
                        "chiefComplaint", new Document(Map.of("bsonType", "string")),
                        "notes", new Document(Map.of("bsonType", "string")),
                        "status", new Document(Map.of("bsonType", "string")),
                        "attachments", new Document(Map.of("bsonType", "array"))
                )
        ));
    }

    private Document userSchema() {
        return new Document(Map.of(
                "bsonType", "object",
                "required", java.util.List.of("email", "passwordHash", "roles", "status"),
                "properties", Map.of(
                        "email", new Document(Map.of("bsonType", "string")),
                        "passwordHash", new Document(Map.of("bsonType", "string")),
                        "roles", new Document(Map.of("bsonType", "array")),
                        "status", new Document(Map.of("bsonType", "string"))
                )
        ));
    }

    private Document accessGrantSchema() {
        return new Document(Map.of(
                "bsonType", "object",
                "required", java.util.List.of("patientId", "granteeUserId", "status", "grantedAt"),
                "properties", Map.of(
                        "patientId", new Document(Map.of("bsonType", "string")),
                        "granteeUserId", new Document(Map.of("bsonType", "string")),
                        "status", new Document(Map.of("bsonType", "string")),
                        "grantedAt", new Document(Map.of("bsonType", "date")),
                        "revokedAt", new Document(Map.of("bsonType", "date"))
                )
        ));
    }

    private Document auditLogSchema() {
        return new Document(Map.of(
                "bsonType", "object",
                "required", java.util.List.of("action", "resourceType", "status", "timestamp"),
                "properties", Map.of(
                        "actorId", new Document(Map.of("bsonType", "string")),
                        "action", new Document(Map.of("bsonType", "string")),
                        "resourceType", new Document(Map.of("bsonType", "string")),
                        "resourceId", new Document(Map.of("bsonType", "string")),
                        "status", new Document(Map.of("bsonType", "string")),
                        "timestamp", new Document(Map.of("bsonType", "date"))
                )
        ));
    }
}
