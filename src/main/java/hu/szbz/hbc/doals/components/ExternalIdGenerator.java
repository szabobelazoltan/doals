package hu.szbz.hbc.doals.components;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExternalIdGenerator {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
