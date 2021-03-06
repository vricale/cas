package org.apereo.cas;

import org.apereo.cas.authentication.MongoDbAuthenticationHandlerTests;
import org.apereo.cas.authentication.MongoDbConnectionFactoryTests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite to run all MongoDb tests.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@SelectClasses({
    MongoDbAuthenticationHandlerTests.class,
    MongoDbConnectionFactoryTests.class
})
@Suite
public class AllMongoDbTestsSuite {
}
