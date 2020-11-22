package com;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class AddressBookJDBCTest {

	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws AddressBookException {
		List<AddressBookData> addressbookdata;
		AddressBookService addressBookService = new AddressBookService();
		addressbookdata = addressBookService.readAddressBookData();
		Assert.assertEquals(7, addressbookdata.size());
	}

	@Test
	public void givenAddressBookInDB_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB() throws AddressBookException {
		List<AddressBookData> addressbookdata;
		AddressBookService addressBookService = new AddressBookService();
		addressbookdata = addressBookService.readAddressBookData();
		addressBookService.updateContactDetails("Ravina", "Jaipur", 130029);
		boolean result = addressBookService.checkAddressBookInSyncWithDB("Ravina");
		Assert.assertTrue(result);
	}

	@Test
	public void givenAddressBookDataInDB_WhenRetrievedForGivenDate_ShouldSyncWithDB() throws AddressBookException {
		List<AddressBookData> addressbookdata;
		AddressBookService addressBookService = new AddressBookService();
		addressbookdata = addressBookService.readAddressBookData();
		LocalDate startDate = LocalDate.parse("2018-01-01");
		LocalDate endDate = LocalDate.now();
		List<AddressBookData> matchingRecords = addressBookService.getAddressBookDataByStartDate(startDate, endDate);
		Assert.assertEquals(matchingRecords.get(0), addressBookService.getAddressBookData("Sippora"));
	}

	@Test
	public void givenContacts_RetrieveNumberOfContacts_ByCityOrState() throws AddressBookException {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readAddressBookData();
		Map<String, Integer> contactByCityOrStateMap = addressBookService.readContactByCityOrState();
		Assert.assertEquals(true, contactByCityOrStateMap.get("Maharashtra").equals(2));
	}

	@Test
	public void givenNewContact_WhenAdded_ShouldSyncWithDB() throws AddressBookException {
		AddressBookJDBCService addressBookJDBCService = new AddressBookJDBCService();
		AddressBookService addressBookService = new AddressBookService();
		AddressBookJDBCService.addContact("Shivani", "Singh", "MG Road", "MP", "Bhopal", 100012, 99887701,
				"shivani123@gmail.com", "addressBook2020", "Friend", Date.valueOf("2020-11-19"));
		boolean result = addressBookService.checkAddressBookInSyncWithDB("Shivani");
		Assert.assertTrue(result);
	}

	@Test
	public void givenAddressBookMultipleData_ShouldAddToAdddressBook() throws AddressBookException {
		AddressBookData[] arrayOfDetails = {
				new AddressBookData("Bhupesh", "Singh", "9A Block", "Mumbai", "Maharashtra", 170091, 78008712,
						"bhupesh123@gmail.com", "addressbook2020", "Friends", Date.valueOf("2020-07-12")),
				new AddressBookData("Raina", "David", "MG Marg", "New Delhi", "Delhi", 110010, 871829191,
						"ravina123@gmail.com", "addressBook2020", "Friends", Date.valueOf("2020-11-19")) };
		AddressBookJDBCService addressBookJDBCService = new AddressBookJDBCService();
		addressBookJDBCService.readData();
		Instant startThread = Instant.now();
		addressBookJDBCService.addContactToDBWithThreads(Arrays.asList(arrayOfDetails));
		Instant endThread = Instant.now();
		System.out.println("Duration With Thread:" + java.time.Duration.between(startThread, endThread));
		Assert.assertEquals(10, addressBookJDBCService.countEntries());
	}
}