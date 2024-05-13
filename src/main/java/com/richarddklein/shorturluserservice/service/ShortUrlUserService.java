/**
 * The Short URL User Service
 * (Copyright 2024 by Richard Klein)
 */

package com.richarddklein.shorturluserservice.service;

/**
 * The Short URL User Service interface.
 *
 * <p>Specifies the methods that must be implemented by any class that
 * provides service-layer functionality to the Short URL User Service.</p>
 */
public interface ShortUrlUserService {
    /**
     * Initialize the Short URL User repository.
     *
     * <p>This is a synchronous method. It will return only when the
     * initialization has completed successfully, or has failed.</p>
     */
    void initializeShortUrlUserRepository();
}
