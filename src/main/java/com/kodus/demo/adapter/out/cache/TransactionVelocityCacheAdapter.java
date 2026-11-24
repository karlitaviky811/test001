package com.kodus.demo.adapter.out.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Casos 3 y 4: Concurrencia, Thread-Safety y Ciclo de Vida de Spring Singleton.
 * 
 * Anti-patrones introducidos deliberadamente:
 * 1. Caso 3: Map estático mutable (HashMap no sincronizado) en un Bean Singleton de Spring.
 *    Provoca condiciones de carrera (Race Conditions), pérdida de actualizaciones y fugas
 *    de memoria (Memory Leaks) progresivas por ausencia de políticas de desalojo (TTL/LRU).
 * 2. Caso 4: SimpleDateFormat estático compartido entre hilos web.
 *    SimpleDateFormat NO es thread-safe en Java y corrompe fechas en concurrencia.
 *    Debe sustituirse por java.time.format.DateTimeFormatter.
 */
@Component
public class TransactionVelocityCacheAdapter {

    private static final Logger log = LoggerFactory.getLogger(TransactionVelocityCacheAdapter.class);

    // ❌ CASO 3: HashMap estático no sincronizado compartido entre hilos en un Bean Singleton
    // Sin política de desalojo, sin LRU y sin protección ante escrituras concurrentes.
    private static final Map<String, Integer> VELOCITY_MAP = new HashMap<>();

    // ❌ CASO 4: SimpleDateFormat estático compartido entre hilos (Thread-Unsafe)
    private static final SimpleDateFormat AUDIT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void recordTransaction(String accountId) {
        // Condición de carrera (Race Condition) bajo múltiples peticiones concurrentes
        Integer count = VELOCITY_MAP.get(accountId);
        if (count == null) {
            VELOCITY_MAP.put(accountId, 1);
        } else {
            VELOCITY_MAP.put(accountId, count + 1);
        }
        log.info("Cuenta {} registra {} transacciones recientes", accountId, VELOCITY_MAP.get(accountId));
    }

    public int getTransactionCount(String accountId) {
        return VELOCITY_MAP.getOrDefault(accountId, 0);
    }

    public String formatAuditDate(Date date) {
        // Invocación concurrente en SimpleDateFormat: provoca corrupción de estado interno del Calendar
        return AUDIT_DATE_FORMAT.format(date);
    }
}
