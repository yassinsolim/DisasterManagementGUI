package edu.ucalgary.oop;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    CLIHandlerTest.class,
    DatabaseAdapterTest.class,
    DisasterVictimTest.class,
    ErrorLoggerTest.class,
    ExternalInquiryTest.class,
    FamilyGroupTest.class,
    InputHandlerTest.class,
    InquiryTest.class,
    ItemTest.class,
    ItemTypeTest.class,
    LanguageLoaderTest.class,
    LocationTest.class,
    MedicalRecordTest.class,
    SupplyTest.class,
    VictimInquiryTest.class
})
public class AllTests {
    // This class remains empty, it's just a holder for the above annotations
} 