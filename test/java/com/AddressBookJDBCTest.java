package com;

import java.time.LocalDate;
import java.util.List;
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
}