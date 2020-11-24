package AB.System;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class AddressBookTest {

	@Test
	public void givenAddressBookDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<PersonInfo> contactList = addressBookService.readContactData();
		Assert.assertEquals(6, contactList.size());
	}

	@Test
	public void givenAddressBookInDB_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB() {
		AddressBookService addressBookService = new AddressBookService();
		List<PersonInfo> contactList = addressBookService.readContactData();
		addressBookService.updateContactDetails("Surabhi", "Panvel");
		boolean result = addressBookService.checkConatctDetailsInSyncWithDB("Surabhi");
		Assert.assertTrue(result);
	}

	@Test
	public void givenAddressBookDataInDB_WhenRetrievedForGivenDate_ShouldSyncWithDB() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		LocalDate startDate = LocalDate.parse("2018-01-01");
		LocalDate endDate = LocalDate.now();
		List<PersonInfo> contactList = addressBookService.readContactDataForDateRange(startDate, endDate);
		Assert.assertEquals(6, contactList.size());
	}

	@Test
	public void givenContacts_RetrieveNumberOfContacts_ByCityOrState() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		Map<String, Integer> contactByCityMap = addressBookService.readContactByCityOrState();
		Integer count = 1;
		Assert.assertEquals(count, contactByCityMap.get("Amravati"));
	}

	@Test
	public void givenNewContact_WhenAdded_ShouldSyncWithDB() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		LocalDate date = LocalDate.now();
		addressBookService.addContactToAddressBook("Simson", "Toppo", "BG Road", "Delhi", "Delhi", 123456, 761169904,
				"sim123@gmail.com", "AB Dairy", "Teacher", date);
		boolean result = addressBookService.checkConatctDetailsInSyncWithDB("Simson");
		Assert.assertTrue(result);
	}

	@Test
	public void givenAddressBookMultipleData_ShouldAddToAdddressBook() {
		PersonInfo[] arrayOfContacts = {
				new PersonInfo("Shivam", "Singh", "Ashok Nagar", "Allahabad", "UP", 120022, 901999215,
						"shivam123@gmail.com", "Contact Book", "Colleague", LocalDate.now()),
				new PersonInfo("Farida", "Bano", "Janakpuri", "Delhi", "Delhi", 100305, 619369212,
						"faridaBano@gmail.com", "Contact Book", "friend", LocalDate.now()) };
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readContactData();
		Instant start = Instant.now();
		addressBookService.addEmployeeToPayrollWithThreads(Arrays.asList(arrayOfContacts));
		Instant end = Instant.now();
		System.out.println("Duration with thread: " + Duration.between(start, end));
		List<PersonInfo> contactList = addressBookService.readContactData();
		Assert.assertEquals(6, contactList.size());
	}
}