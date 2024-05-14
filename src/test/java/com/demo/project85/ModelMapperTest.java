package com.demo.project85;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;

@Slf4j
public class ModelMapperTest {

    @Test
    public void test_inlineCall() {
        Person person = getPerson();
        ModelMapper modelMapper = new ModelMapper();
        TypeMap<Person, PersonView> typeMap = modelMapper.createTypeMap(Person.class, PersonView.class);
        typeMap.addMappings(mapper -> {
            mapper.map(Person::getFirstName, PersonView::setFirstName);
            mapper.map(Person::getFamilyName, PersonView::setLastName);
        });
        PersonView personView = modelMapper.map(person, PersonView.class);
        log.info("personView: {}", personView);
        Assertions.assertEquals(personView.getFirstName(), person.getFirstName());
        Assertions.assertEquals(personView.getLastName(), person.getFamilyName());
    }

    @Test
    public void test_directCall() {
        Person person = getPerson();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(personMap);
        PersonView personView = modelMapper.map(person, PersonView.class);
        log.info("personView: {}", personView);
        Assertions.assertEquals(personView.getFirstName(), person.getFirstName());
        Assertions.assertEquals(personView.getLastName(), person.getFamilyName().toUpperCase());
    }

    @Test
    public void test_directCallJava8() {
        Person person = getPerson();
        ModelMapper modelMapper = new ModelMapper();
        PersonView personView = modelMapper
                .typeMap(Person.class, PersonView.class)
                .addMappings(mapper -> {
                    mapper.using(toUppercase)
                        .map(src -> src.getFamilyName(), PersonView::setLastName);
                    mapper.using(workStatusConvertor)
                            .map(src -> src.getWorkStatus(), PersonView::setWorkStatusName);
                    mapper.map(Person::getAge, PersonView::setAgeStr);
                })
                .map(person);
        log.info("personView: {}", personView);
        Assertions.assertEquals(personView.getFirstName(), person.getFirstName());
        Assertions.assertEquals(personView.getLastName(), person.getFamilyName().toUpperCase());
        Assertions.assertEquals(personView.getAgeStr(), String.valueOf(person.getAge()));
    }

    @Test
    public void test_genericHelperClass() {
        Person person = getPerson();
        MapperHelper<Person, PersonView> entityMapperHelper = new MapperHelper<>(Person.class, PersonView.class);
        entityMapperHelper.mapper.typeMap(Person.class, PersonView.class)
                .addMappings(mapper -> {
                    mapper.using(toUppercase)
                            .map(src -> src.getFamilyName(), PersonView::setLastName);
                    mapper.using(workStatusConvertor)
                            .map(src -> src.getWorkStatus(), PersonView::setWorkStatusName);
                    mapper.map(Person::getAge, PersonView::setAgeStr);
                });
        PersonView personView = entityMapperHelper.toModel(person);
        log.info("personView: {}", personView);
        Assertions.assertEquals(personView.getFirstName(), person.getFirstName());
    }

    @Test
    public void test_listConvertGenericHelperClass() {
        List<Person> personList = getListOfPersons();
        MapperHelper<Person,PersonView> entityMapperHelper = new MapperHelper<>(Person.class, PersonView.class);
        List<PersonView> personViewList = entityMapperHelper.toListModel(personList);
        log.info("personView: {}", personViewList);
        Assertions.assertEquals(personViewList.size(), personList.size());
    }

    PropertyMap<Person, PersonView> personMap = new PropertyMap<>() {
        protected void configure() {
            using(toUppercase).map().setLastName(source.getFamilyName());
        }
    };

    Converter<String, String> toUppercase =
            context -> context.getSource() == null ? null : context.getSource().toUpperCase();

    Converter<WorkStatus, String> workStatusConvertor =
            context -> context.getSource() == null ? null : context.getSource().getStatus();

    public static List<Person> getListOfPersons() {
        List<Person> personList = new ArrayList<>();
        personList.add(Person.builder()
                .firstName("luke")
                .familyName("skywalker")
                .age(30)
                .workStatus(WorkStatus.EMPLOYEED)
                .build());
        personList.add(Person.builder()
                .firstName("han")
                .familyName("solo")
                .age(35)
                .workStatus(WorkStatus.EMPLOYEED)
                .build());
        return personList;
    }

    public static Person getPerson() {
        return Person.builder()
                .firstName("luke")
                .familyName("skywalker")
                .workStatus(WorkStatus.EMPLOYEED)
                .age(30)
                .build();
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Person {
    String firstName;
    String familyName;
    WorkStatus workStatus;
    int age;
}

enum WorkStatus {
    EMPLOYEED("Employeed"),
    UN_EMPLOYEED("Unemployeed");

    String status;
    WorkStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PersonView {
    String firstName;
    String lastName;
    WorkStatus workStatus;
    String workStatusName;
    String ageStr;
}
