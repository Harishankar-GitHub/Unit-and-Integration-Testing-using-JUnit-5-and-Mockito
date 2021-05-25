package com.softwaretesting.student;

import com.softwaretesting.student.exception.BadRequestException;
import com.softwaretesting.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
// The above annotation initializes all the Mocks in the Test Class.
// And this also closes the resources after the Tests.
class StudentServiceTest
{
    @Mock
    private StudentRepository studentRepository;
    private StudentService service;

    @BeforeEach
    void setUp()
    {
        service = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents()
    {
        //given

        // when
        service.getAllStudents();

        // then
        verify(studentRepository).findAll();
        // Here we are verifying which method was invoked.
        // In this case, (service.getAllStudents()) findAll() is invoked.
        // So the test case is passed.
    }

    @Test
//    @Disabled   // If Disabled, these these tests won't be executed.
    void canAddStudent()
    {
        // given
        Student student = new Student(
                "Harish",
                "abc@mail.com",
                Gender.MALE
        );

        // when
        service.addStudent(student);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);

        verify(studentRepository)
                .save(studentArgumentCaptor.capture());
        // In the above line, we say, we verify the studentRepository and when save() method of studentRepository is called, we capture the student
        // object that was passed to the save() method.

        Student capturedStudent = studentArgumentCaptor.getValue();
        assertThat(capturedStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken()
    {
        // given
        Student student = new Student(
                "Harish",
                "abc@mail.com",
                Gender.MALE
        );

        // when // then

//        Mockito
//                .when(studentRepository.selectExistsEmail(Mockito.anyString()))
//                .thenReturn(true);

        // The above line can also be written as below

        given(studentRepository.selectExistsEmail(anyString()))     // anyString() is from ArgumentMatchers.
                .willReturn(true);

        assertThatThrownBy(() -> service.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
        // We verify, in studentRepository, save() method is never called.
    }

    @Test
    void canDeleteStudent()
    {
        // given
        given(studentRepository.existsById(any()))
                .willReturn(true);

        // when
        service.deleteStudent(any());

        // then
        verify(studentRepository).deleteById(any());
    }

    @Test
    void willThrowExceptionWhenStudentNotFound()
    {
        // given
        long id = 100;
        given(studentRepository.existsById(id))
                .willReturn(false);

        // when // then
        assertThatThrownBy(() -> service.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        verify(studentRepository, never()).deleteById(any());
    }
}
