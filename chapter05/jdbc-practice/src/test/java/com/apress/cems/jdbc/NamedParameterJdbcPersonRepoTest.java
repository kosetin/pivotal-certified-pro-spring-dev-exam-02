/*
Freeware License, some rights reserved

Copyright (c) 2019 Iuliana Cosmina

Permission is hereby granted, free of charge, to anyone obtaining a copy 
of this software and associated documentation files (the "Software"), 
to work with the Software within the limits of freeware distribution and fair use. 
This includes the rights to use, copy, and modify the Software for personal use. 
Users are also allowed and encouraged to submit corrections and modifications 
to the Software for the benefit of other users.

It is not allowed to reuse,  modify, or redistribute the Software for 
commercial use in any way, or for a user's educational materials such as books 
or blog articles without prior permission from the copyright holder. 

The above copyright notice and this permission notice need to be included 
in all copies or substantial portions of the software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS OR APRESS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.apress.cems.jdbc;

import com.apress.cems.jdbc.config.TestDbConfig;
import com.apress.cems.repos.PersonRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Iuliana Cosmina
 * @since 1.0
 */
@SpringJUnitConfig(classes = {TestDbConfig.class, JdbcConfig.class})
class NamedParameterJdbcPersonRepoTest {
    private Logger logger = LoggerFactory.getLogger(JdbcPersonRepoTest.class);

    static final Long PERSON_ID = 1L;

    @Qualifier("jdbcNamedPersonRepo")
    @Autowired
    PersonRepo jdbcNamedPersonRepo;

    @BeforeEach
    void setUp(){
        assertNotNull(jdbcNamedPersonRepo);
    }

    @Test
    void testFindByIdPositive(){
        jdbcNamedPersonRepo.findById(PERSON_ID).ifPresentOrElse(
                p -> assertEquals("Sherlock", p.getFirstName()),
                Assertions:: fail
        );
    }

    @Test
    void testFindByIdNegative(){
        assertThrows( EmptyResultDataAccessException.class, () -> jdbcNamedPersonRepo.findById(99L));
    }

    @Test
    @SqlGroup({
        @Sql(statements = "INSERT INTO PERSON(ID, USERNAME, FIRSTNAME, LASTNAME, PASSWORD, HIRINGDATE, VERSION, CREATED_AT, MODIFIED_AT) " +
                    "VALUES (123, 'userX', 'First', 'Last', '123ss12sh', '1983-08-18 00:01', 1, '1983-08-18 00:01', '1999-03-18 00:02' );",
             executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),

        @Sql(statements = "DELETE FROM PERSON WHERE ID=123",
             executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        }
    )
    void testChangePassword() {
        long id = 123L;
        String oldPass = "123ss12sh";
        String newPass = "new_password";

        jdbcNamedPersonRepo.findById(id).ifPresentOrElse(
                p -> assertEquals(oldPass, p.getPassword()),
                Assertions::fail
        );

        jdbcNamedPersonRepo.updatePassword(id, newPass);

        jdbcNamedPersonRepo.findById(id).ifPresentOrElse(
                p -> assertEquals(newPass, p.getPassword()),
                Assertions::fail
        );
    }

    @Test
    @Sql(statements = "INSERT INTO PERSON(ID, USERNAME, FIRSTNAME, LASTNAME, PASSWORD, HIRINGDATE, VERSION, CREATED_AT, MODIFIED_AT) " +
                    "VALUES (123, 'userX', 'First', 'Last', '123ss12sh', '1983-08-18 00:01', 1, '1983-08-18 00:01', '1999-03-18 00:02' );",
                    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testDeleteById() {
        long id = 123L;
        var username = "userX";

        jdbcNamedPersonRepo.findById(id).ifPresentOrElse(
                p -> assertEquals(username, p.getUsername()),
                Assertions::fail
        );

        jdbcNamedPersonRepo.deleteById(id);

        assertThrows( EmptyResultDataAccessException.class,
                () -> jdbcNamedPersonRepo.findById(id));
    }
}
