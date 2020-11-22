package com;

//import static org.junit.jupiter.api.Assertions.assertEquals;

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
	public void givenAddressBookInDB_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB()
			throws AddressBookException {
		List<AddressBookData> addressbookdata;
		AddressBookService addressBookService = new AddressBookService();
		addressbookdata = addressBookService.readAddressBookData();
		addressBookService.updateContactDetails("Ravina", "Jaipur", 130029);
		boolean result = addressBookService.checkAddressBookInSyncWithDB("Ravina");
		Assert.assertTrue(result);
	}
}