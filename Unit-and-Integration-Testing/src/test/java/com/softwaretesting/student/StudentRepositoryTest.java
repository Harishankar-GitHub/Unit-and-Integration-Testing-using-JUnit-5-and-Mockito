package com.softwaretesting.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// This Annotation is used to Unit Test Repositories.
// This will do the Autowiring.
// This will spin up the Database as well.
class StudentRepositoryTest
{
    @Autowired
    StudentRepository studentRepository;

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
    }

    @Test
    void itShouldCheckWhenStudentEmailExists()
    {
        // given
        String email = "abc@mail.com";
        Student student = new Student(
                "Harish",
                email,
                Gender.MALE
        );
        studentRepository.save(student);

        // when
        boolean expected = studentRepository.selectExistsEmail(email);

        // then
        assertThat(expected).isTrue();
    }

    @Test
    void itShouldCheckWhenStudentEmailDoesNotExist()
    {
        // given
        String email = "abc@mail.com";

        // when
        boolean expected = studentRepository.selectExistsEmail(email);

        // then
        assertThat(expected).isFalse();
    }
}
